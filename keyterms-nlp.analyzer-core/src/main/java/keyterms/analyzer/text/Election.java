/*
 * NOTICE
 * This software was produced for the U.S. Government and is subject to the
 * Rights in Data-General Clause 5.227-14 (May 2014).
 * Copyright 2018 The MITRE Corporation. All rights reserved.
 *
 * “Approved for Public Release; Distribution Unlimited” Case  18-2165
 *
 * This project contains content developed by The MITRE Corporation.
 * If this code is used in a deployment or embedded within another project,
 * it is requested that you send an email to opensource@mitre.org
 * in order to let us know where this software is being used.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package keyterms.analyzer.text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import keyterms.util.collect.Keyed;
import keyterms.util.math.Statistics;
import keyterms.util.text.Strings;

/**
 * An election represents the necessary information for a voting ensemble to classify inputs.
 */
public class Election<C> {
    /**
     * The election result sorter.
     */
    private static final Comparator<Keyed<?, Double>> HIGH_SCORE;

    // Initialize the election result sorter.
    static {
        Comparator<Keyed<?, Double>> highScore = Comparator.comparing(Keyed::getValue);
        highScore = highScore.reversed();
        HIGH_SCORE = highScore;
    }

    /**
     * The maximum rank to consider for the election.
     */
    private final int maxRank;

    /**
     * The weighted results from the ensemble's classifiers.
     */
    private final List<WeightedVote> weightedVotes = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param maxRank The maximum rank to consider for the election.
     */
    public Election(int maxRank) {
        super();
        this.maxRank = maxRank;
    }

    /**
     * Add a value to the election with a weight of {@code 1.0}.
     *
     * @param value The value.
     * @param rank The rank of the value.
     */
    public void add(C value, int rank) {
        add(value, rank, 1.0);
    }

    /**
     * Add a value to the election.
     *
     * @param value The value.
     * @param rank The rank of the value.
     * @param weight The weight to assign to the value.
     */
    public void add(C value, int rank, double weight) {
        weightedVotes.add(new WeightedVote(value, rank, weight));
    }

    /**
     * Tally the weighted votes in the election under the simple rank voting scheme.
     *
     * @return The election results in the form of sorted result-score pairs.
     */
    public List<Keyed<C, Double>> getResults() {
        // Group the votes by output.
        Map<C, List<WeightedVote>> byClass = new LinkedHashMap<>();
        weightedVotes.forEach((wv) -> {
            C value = wv.value;
            byClass.computeIfAbsent(value, (k) -> new ArrayList<>()).add(wv);
        });
        // Compute the raw scores.
        List<Keyed<C, Double>> scored = new ArrayList<>();
        AtomicReference<Double> total = new AtomicReference<>(0.0);
        byClass.forEach((value, weightedVotes) -> {
            AtomicReference<Double> classScore = new AtomicReference<>(0.0);
            weightedVotes.forEach((wv) -> {
                if (wv.rank <= maxRank) {
                    double voteScore = wv.weight * (maxRank + 1 - wv.rank);
                    classScore.accumulateAndGet(voteScore, (d1, d2) -> d1 + d2);
                    total.accumulateAndGet(voteScore, (d1, d2) -> d1 + d2);
                }
            });
            scored.add(new Keyed<>(value, classScore.get()));
        });
        for (Keyed<C, Double> result : scored) {
            result.setValue(result.getValue() / total.get());
        }
        scored.sort(HIGH_SCORE);
        if (!scored.isEmpty()) {
            // Best effort tie resolution.
            double topScore = scored.get(0).getValue();
            List<Keyed<C, Double>> topScored = scored.stream()
                    .filter((k) -> k.getValue() == topScore)
                    .collect(Collectors.toList());
            if (topScored.size() > 1) {
                Map<Keyed<C, Double>, Statistics> stats = new HashMap<>();
                topScored.forEach((k) ->
                        byClass.get(k.getKey()).forEach((wv) ->
                                stats.computeIfAbsent(k, (ts) -> new Statistics())
                                        .add(wv.weight * (maxRank + 1 - wv.rank))));
                // Highest mean scorer
                Keyed<C, Double> winner = getBest(stats, Statistics::getMean, true);
                // Highest maximum scorer
                winner = (winner != null) ? winner : getBest(stats, Statistics::getMaximum, true);
                // Most consistent scorer.
                winner = (winner != null) ? winner : getBest(stats, Statistics::getStandardDeviation, false);
                if (winner != null) {
                    // Boost the winner's score by 0.1%
                    winner.setValue(winner.getValue() + 0.001);
                    scored.sort(HIGH_SCORE);
                }
            }
        }
        return scored;
    }

    /**
     * Get the winner in a tie breaker based on the weighted voting statistics.
     *
     * @param stats The statistics for the weighted votes.
     * @param score The function which obtains scores from the statistics.
     * @param highWins A flag indicating whether the high score wins.
     *
     * @return The winner if one can be determined.
     */
    private Keyed<C, Double> getBest(Map<Keyed<C, Double>, Statistics> stats,
            Function<Statistics, Double> score, boolean highWins) {
        Keyed<C, Double> winner = null;
        Comparator<Keyed<Keyed<C, Double>, Statistics>> statsSort =
                Comparator.comparing((k) -> score.apply(k.getValue()));
        if (highWins) {
            statsSort = statsSort.reversed();
        }
        List<Keyed<Keyed<C, Double>, Statistics>> statsList = stats.entrySet().stream()
                .map(Keyed::new)
                .sorted(statsSort)
                .collect(Collectors.toList());
        if (!score.apply(statsList.get(0).getValue()).equals(score.apply(statsList.get(1).getValue()))) {
            winner = statsList.get(0).getKey();
        }
        return winner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + weightedVotes.size() + " votes" + ']';
    }

    /**
     * A container for weighted votes.
     */
    private class WeightedVote {
        /**
         * The value.
         */
        private final C value;

        /**
         * The rank of the value.
         */
        private final double rank;

        /**
         * The weight for the vote.
         */
        private final double weight;

        /**
         * Constructor.
         *
         * @param value The value.
         * @param rank The rank of the value.
         * @param weight The weight of the vote.
         */
        private WeightedVote(C value, int rank, double weight) {
            super();
            this.value = value;
            this.rank = rank;
            this.weight = weight;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return getClass().getSimpleName() + "[" + rank + " - " + Strings.toString(value) + " (" +
                    String.format("%.2f", weight) + ")]";
        }
    }
}