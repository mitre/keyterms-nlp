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

package keyterms.nlp.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextSpan
        implements Comparable<TextSpan> {

    public static final int BEFORE = -3;
    public static final int END_CONTAINED = -2;
    public static final int FULLY_CONTAINS = -1;
    public static final int EQUAL = 0;
    public static final int FULLY_CONTAINED_BY = 1;
    public static final int START_CONTAINED = 2;
    public static final int AFTER = 3;

    public String Text;
    public int Start;
    public int End;
    public int Length;

    public TextSpan(String text, int start, int end) {
        this.Text = text;
        this.Start = start;
        this.End = end;
    }

    public String getText() {
        return this.Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public int getStart() {
        return Start;
    }

    public void setStart(int start) {
        Start = start;
    }

    public int getEnd() {
        return End;
    }

    public void setEnd(int end) {
        End = end;
    }

    public int getLength() {
        return End - Start + 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean equals = (this == obj);
        if ((!equals) && (obj instanceof TextSpan)) {
            TextSpan that = (TextSpan)obj;
            equals = (compareTo(that) == EQUAL);
        }
        return equals;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        String hashBase = " " + Start + End;
        return hashBase.hashCode();
    }

    public int compareTo(TextSpan that) {
        if (that == null) {
            throw new NullPointerException("Cannot compare null text spans.");
        }
        if (this.Start == that.Start && this.End == that.End) {
            return EQUAL;
        }
        // if this is not overlapping, but is before
        if (this.End < that.Start) {
            return BEFORE;
        }
        if (this.Start > that.End) {
            return AFTER;
        }
        if (this.Start <= that.Start && this.End >= that.End) {
            return FULLY_CONTAINS;
        }
        if (this.Start >= that.Start) {
            if (this.End <= that.End) {
                return FULLY_CONTAINED_BY;
            }
            return START_CONTAINED;
        }
        return END_CONTAINED;
    }

    public static boolean IsFullySubsumed(List<TextSpan> currentSegments, List<TextSpan> seenSegments) {
        if (currentSegments == null || seenSegments == null || seenSegments.size() < 1) {
            return false;
        }
        for (TextSpan newSpan : currentSegments) {
            boolean segFoundInPrev = false;
            for (TextSpan seenSpan : seenSegments) {
                int relationship = newSpan.compareTo(seenSpan);
                if (relationship == TextSpan.EQUAL || relationship == TextSpan.FULLY_CONTAINED_BY) {
                    segFoundInPrev = true;
                    break;
                }
            }
            if (!segFoundInPrev) {
                return false;
            }
        }
        return true;
    }

    public String ToString() {
        return this.Text;
    }

    // This can be improved, but it is _a_ decision for now
    public static List<TextSpan> SortAndFilter(List<TextSpan> seenSpans) {
        if (seenSpans == null || seenSpans.size() < 2) {
            return seenSpans;
        }
        Collections.sort(seenSpans);
        List<TextSpan> results = new ArrayList<>();
        for (int i = 0; i < seenSpans.size(); i++) {

            TextSpan curSpan = seenSpans.get(i);
            TextSpan nextSpan;
            TextSpan followingSpan = null;

            if (results.size() > 0) {
                TextSpan prevSpan = results.get(results.size() - 1);
                int relToPrev = curSpan.compareTo(prevSpan);
                if (relToPrev == TextSpan.FULLY_CONTAINED_BY || relToPrev == TextSpan.START_CONTAINED) {
                    continue;
                }
            }
            if (i + 1 < seenSpans.size()) {
                nextSpan = seenSpans.get(i + 1);
            } else {
                results.add(curSpan);
                break;
            }
            if (i + 2 < seenSpans.size()) {
                followingSpan = seenSpans.get(i + 2);
            }

            int relToNext = curSpan.compareTo(nextSpan);
            boolean nextSpanAlreadyAdded = false;

            switch (relToNext) {
                case TextSpan.BEFORE:
                    results.add(curSpan);
                    break;  // this span is entirely before, so leave it be
                case TextSpan.FULLY_CONTAINS:
                case TextSpan.END_CONTAINED:
                    TextSpan spanToAdd = ChooseSpan(curSpan, nextSpan,
                            followingSpan);  // if the end of this is contained within the next one, choose one
                    results.add(spanToAdd);
                    if (spanToAdd.equals(nextSpan)) {
                        nextSpanAlreadyAdded = true;
                        i++;
                    }
                    break;
                //case TextSpan.FULLY_CONTAINS:                       // this span fully contains the next one, so
                // add this one and skip ahead past the next
                //        results.Add(curSpan);
                //        i++;
                //        break;
                case TextSpan.EQUAL:
                    break;                        // this span is a duplicate of the next, so don't add it
                case TextSpan.FULLY_CONTAINED_BY:                   // if this span is fully contained by the next
                    // span, add the next one and skip ahead
                    results.add(nextSpan);
                    nextSpanAlreadyAdded = true;
                    i++;
                    break;
            }
            if (i == seenSpans.size() - 1 && !nextSpanAlreadyAdded) {
                results.add(nextSpan);
                i++;
            }
        }
        return results;
    }

    /**
     * Pick the longest or if the same length, take the
     */
    private static TextSpan ChooseSpan(TextSpan firstSpan, TextSpan secondSpan, TextSpan followingSpan) {
        if (firstSpan == null) {
            return secondSpan;
        }
        if (secondSpan == null) {
            return firstSpan;
        }
        if (followingSpan != null) {
            int followingSpanRelationship = followingSpan.compareTo(firstSpan);
            if (followingSpanRelationship == TextSpan.START_CONTAINED) {
                return secondSpan;
            }
        }
        if (firstSpan.Length >= secondSpan.Length) {
            return firstSpan;
        }
        return secondSpan;
    }
}