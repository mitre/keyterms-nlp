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

package keyterms.analyzer.profiles;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import keyterms.analyzer.AnalyzerId;
import keyterms.util.collect.Unique;
import keyterms.util.io.IO;
import keyterms.util.text.Strings;

/**
 * Metadata regarding a WekaAnalyzer instance.
 */
public class WekaProfile
        extends Unique<String>
        implements Serializable {
    /**
     * The profile name.
     */
    private final String name;

    /**
     * The identifiers for analyzers that must be present in the core analyzer pools for the associated analyzer to
     * function correctly.
     */
    private final Set<String> requiredAnalyzers;

    /**
     * The creation date of the profile.
     */
    private final Date createDate;

    /**
     * The user name of the trainer.
     */
    private final String trainer;

    /**
     * The name of the training index file.
     */
    private final String trainingFile;

    /**
     * Get the number of training records used.
     */
    private final long trainingInstances;

    /**
     * The date of the last update to the training index file.
     */
    private final long lastTrainingFileUpdate;

    /**
     * Constructor.
     *
     * @param name The name of the profile.
     * @param analyzer The analyzer for which the profile is being created.
     * @param trainingFile The training file.
     * @param trainingInstances The number of training records used.
     */
    WekaProfile(String name, WekaAnalyzer analyzer, Path trainingFile, long trainingInstances) {
        super(Strings.trim(name));
        this.name = Strings.trim(name);
        this.requiredAnalyzers = analyzer.getRequiredAnalyzers().stream()
                .map(Strings::toString)
                .collect(Collectors.toCollection(HashSet::new));
        createDate = Calendar.getInstance().getTime();
        trainer = System.getProperty("user.name");
        this.trainingFile = (trainingFile != null) ? IO.getName(trainingFile) : "null";
        this.trainingInstances = trainingInstances;
        lastTrainingFileUpdate = (trainingFile != null)
                ? trainingFile.toFile().lastModified()
                : Calendar.getInstance().getTimeInMillis();
    }

    /**
     * Get the name of the profile.
     *
     * @return The profile name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the creation date of the profile.
     *
     * @return The creation date of the profile.
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Get the identifiers for analyzers that must be present in the core analyzer pools for the associated analyzer to
     * function correctly.
     *
     * @return The identifiers for analyzers that must be present in the core analyzer pools.
     */
    public Set<AnalyzerId> getRequiredAnalyzers() {
        return requiredAnalyzers.stream()
                .map(AnalyzerId::valueOf)
                .collect(Collectors.toSet());
    }

    /**
     * Get the user name of the trainer.
     *
     * @return The user name of the trainer.
     */
    public String getTrainer() {
        return trainer;
    }

    /**
     * Get the name of the training index file.
     *
     * @return The name of the training index file.
     */
    public String getTrainingFile() {
        return trainingFile;
    }

    /**
     * Get the number of training records used.
     *
     * @return The number of training records used.
     */
    public long getTrainingInstances() {
        return trainingInstances;
    }

    /**
     * Get the date of the last update to the training index file.
     *
     * @return The date of the last update to the training index file.
     */
    public long getLastTrainingFileUpdate() {
        return lastTrainingFileUpdate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + name + "]";
    }
}