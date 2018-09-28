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

import java.text.Normalizer;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import keyterms.nlp.interfaces.INormalizer;
import keyterms.util.collect.ITrie;
import keyterms.util.text.Strings;

public class StringSegmenter {

    public static final boolean SUPPRESS_SUBSUMED = true;
    public static final int MAX_RESULTS = 20;

    public static Vector<Vector<String>> segmentString(String input, ITrie termStore, INormalizer normalizer) {
        return segmentString(input, termStore, normalizer, true, true, 20);
    }

    public static Vector<Vector<String>> segmentString(String input, ITrie termStore, INormalizer normalizer, boolean
            supressSubsumed, boolean filterSegments, int maxResults) {
        if (maxResults <= 0) {
            maxResults = 20;
        }
        Hashtable<Integer, Vector<String>> results = StringSegmenter.getAllTerms(input, termStore, null);
        Vector<Vector<String>> fullSegments = StringSegmenter.assembleSegments(results, supressSubsumed, maxResults);
        if (filterSegments) {
            return filterSegments(fullSegments);
        }
        return fullSegments;
    }

    public static Hashtable<Integer, Vector<String>> getAllTerms(String input, ITrie termStore, INormalizer
            normalizer) {
        int ngramLength = 2;
        Hashtable<Integer, Vector<String>> termsAtEachIndex = new Hashtable<>();
        if (Strings.isBlank(input)) {
//                String errMessage = "Error: null input to GetAllTerms.";
//                System.err.write(errMessage);
            return null;
        }
        if (normalizer == null) {
            input = StringNormalizer.normalize(input, true/*removeNewLine*/, true/*removeSpace*/, true/*removeControl*/,
                    true/*removePunctuation*/, true/*normalizePunctuation*/, true/*transliteratePunctuation*/,
                    true/*removeDiacritics*/,
                    true/*normalizeCase*/, Normalizer.Form.NFKC);
        } else {
            input = normalizer.normalizeForDisplay(input);
        }
        // XXX to do:  input lengths greater than 150?
        if (input.length() > 150) {
            input = input.substring(0, 150);
        }
        boolean inSpecialSequence = false;
        Hashtable<Integer, StringBuilder> specialStrings = new Hashtable<>();
        char prevSpecialChar = '\0';

        for (int i = 0; i < input.length(); i++) {
            String curChar = input.substring(i, i + 1);
            char curCharChar = curChar.charAt(0);
            String pfx = "";
            if (i <= input.length() - ngramLength) {
                pfx = input.substring(i, i + ngramLength);
            } else {
                pfx = curChar;
                Vector<String> termsAtThisIndex = new Vector<>();
                if (!termsAtThisIndex.contains(curChar)) {
                    termsAtThisIndex.add(curChar);
                    termsAtEachIndex.put(i, termsAtThisIndex);
                }
                continue;
            }
            //
            List<String> candidates = termStore.searchPrefix(pfx);
            if (candidates == null) {
                // Note, this is not perfect, since it doesn't really look to see whether this is an logograph
                //if (Char.GetUnicodeCategory(curChar[0]) == UnicodeCategory.OtherLetter)

                if (Characters.isLatinLetter(curCharChar) || Character.isDigit(curCharChar) ||
                        ((curCharChar == ',' || curCharChar == '.') && Character.isDigit(prevSpecialChar))) {
                    inSpecialSequence = true;
                    prevSpecialChar = curCharChar;
                    addToSpecialStrings(curCharChar, i, specialStrings);
                } else {
                    Vector<String> termsAtThisIndex = new Vector<>();
                    if (!termsAtThisIndex.contains(curChar)) {
                        termsAtThisIndex.add(curChar);
                        termsAtEachIndex.put(i, termsAtThisIndex);
                    }
                    if (inSpecialSequence) {
                        addSpecialStringsToTermsAtEachIndex(specialStrings, termsAtEachIndex);
                        inSpecialSequence = false;
                        specialStrings = new Hashtable<>();
                        prevSpecialChar = '\0';
                    }
                }
            } else {
                if (inSpecialSequence) {
                    addSpecialStringsToTermsAtEachIndex(specialStrings, termsAtEachIndex);
                    inSpecialSequence = false;
                    specialStrings = new Hashtable<>();
                    prevSpecialChar = '\0';
                }
                Vector<String> termsAtThisIndex = new Vector<>();
                //termsAtThisIndex.Add(curChar);
                String curInputString = input.substring(i);
                for (String curCandidate : candidates) {
                    if ((!termsAtThisIndex.contains(curCandidate)) && curInputString.startsWith(curCandidate)) {
                        termsAtThisIndex.add(curCandidate);
                    }
                }
                if (termsAtThisIndex.size() < 1) {
                    termsAtThisIndex.add(curChar);
                }
                termsAtEachIndex.put(i, termsAtThisIndex);
            }
        }
        for (Vector<String> termVector : termsAtEachIndex.values()) {
            termVector.sort(Comparator.comparing(String::length).reversed());
        }
        return termsAtEachIndex;
    }

    public static Vector<Vector<String>> assembleSegments(Hashtable<Integer, Vector<String>> termsAtEachIndex) {
        return assembleSegments(termsAtEachIndex, SUPPRESS_SUBSUMED, MAX_RESULTS);
    }

    public static Vector<Vector<String>> assembleSegments(Hashtable<Integer, Vector<String>> termsAtEachIndex,
            boolean supressSubsumed, int maxResults) {
        String errMessage = "";
        if (termsAtEachIndex == null || termsAtEachIndex.size() < 1) {
            errMessage = "Error:  No terms provided to AssembleSegments.";
            System.err.println(errMessage);
            return null;
        }
        Vector<Vector<String>> finalResults = new Vector<>();
        Vector<TextSpan> seenSpans = new Vector<>();
        Vector<String> starterVector = termsAtEachIndex.get(0);
        if (starterVector == null) {
            errMessage = "Error:  Unable to AssembleSegments.";
            System.err.println(errMessage);
            return null;
        }

        try {
            boolean done = false;
            Vector<Vector<String>> tempResults = new Vector<>();
            for (String startString : starterVector) {
                Vector<String> currentVector = new Vector<>();
                currentVector.add(startString);
                tempResults.add(currentVector);
            }
            int numResults = 0;
            while (!done) {
                Vector<Vector<String>> newResults = new Vector<>();
                if (tempResults == null || tempResults.size() < 1) {
                    break;
                }
                for (Vector<String> currentVector : tempResults) {
                    String joinedSegs = String.join("", currentVector);
                    int nextSegLoc = joinedSegs.length();
                    if (termsAtEachIndex.containsKey(nextSegLoc)) {
                        Vector<String> nextSegs = termsAtEachIndex.get(nextSegLoc);
                        if (nextSegs != null && nextSegs.size() > 0) {
                            for (String nextSegString : nextSegs) {
                                Vector<String> newPath = new Vector<>(currentVector);
                                newPath.add(nextSegString);
                                newResults.add(newPath);
                            }
                        } else {
                            Vector<String> completePath = new Vector<>(currentVector);
                            Vector<TextSpan> currentSpans = getSpansForStrings(completePath);
                            // If the whole swiped text is one segment, add it and do not add its spans to seen spans
                            if (currentSpans.size() == 1) {
                                finalResults.add(completePath);
                                numResults++;
                            } else {
                                boolean isFullySubsumed = TextSpan.IsFullySubsumed(currentSpans, seenSpans);
                                if (supressSubsumed && !isFullySubsumed) {
                                    finalResults.add(completePath);
                                    numResults++;
                                    for (TextSpan newSpan : currentSpans) {
                                        if (!seenSpans.contains(newSpan)) {
                                            seenSpans.add(newSpan);
                                        }
                                    }

                                } else {
                                    if (!supressSubsumed) {
                                        finalResults.add(completePath);
                                        numResults++;
                                    }
                                }
                            }
                        }
                    } else {
                        Vector<String> completePath = new Vector<String>(currentVector);
                        Vector<TextSpan> currentSpans = getSpansForStrings(completePath);
                        if (currentSpans.size() == 1) {
                            finalResults.add(completePath);
                            numResults++;
                        } else {
                            boolean isFullySubsumed = TextSpan.IsFullySubsumed(currentSpans, seenSpans);
                            if (supressSubsumed && !isFullySubsumed) {
                                finalResults.add(completePath);
                                numResults++;
                                for (TextSpan newSpan : currentSpans) {
                                    if (!seenSpans.contains(newSpan)) {
                                        seenSpans.add(newSpan);
                                    }
                                }
                            } else {
                                if (!supressSubsumed) {
                                    finalResults.add(completePath);
                                    numResults++;
                                }
                            }
                        }
                        if (numResults >= maxResults) {
                            return finalResults;
                        }
                    }
                }
                tempResults = newResults;
            }
        } catch (Exception eek) {
            errMessage = eek.getMessage();
        }
        return finalResults;
    }

    public static Vector<TextSpan> getSpansForStrings(Vector<String> pathStrings) {
        if (pathStrings == null) {
            return null;
        }
        Vector<TextSpan> results = new Vector<>();
        if (pathStrings.size() < 1) {
            return results;
        }
        int lastEnd = -1;
        for (String pathString : pathStrings) {
            int start = lastEnd + 1;
            int end = start + pathString.length() - 1;
            TextSpan newSpan = new TextSpan(pathString, start, end);
            lastEnd = end;
            results.add(newSpan);
        }
        return results;
    }

    public static Vector<Vector<String>> filterSegments(Vector<Vector<String>> segmentationVector) {
        if (segmentationVector == null || segmentationVector.size() < 1) {
            return segmentationVector;
        }
        Vector<Vector<String>> cleaned = new Vector<>();
        for (Vector<String> curSegmentation : segmentationVector) {
            Vector<String> newCurrentVector = new Vector<>();
            for (String curSegText : curSegmentation) {
                if (!shouldBeFiltered(curSegText)) {
                    newCurrentVector.add(curSegText);
                }
            }
            cleaned.add(newCurrentVector);
        }
        return cleaned;
    }

    public static boolean shouldBeFiltered(String text) {
        return Strings.isBlank(text);
    }

    private static Vector<String> getTermsStartingWithPrefix(String curPrefix, Vector<ITrie> tries) {
        return getTermsStartingWithPrefix(curPrefix, tries, true);
    }

    private static Vector<String> getTermsStartingWithPrefix(String curPrefix, Vector<ITrie> tries, boolean
            eliminateSingleCharTokens) {
        if (curPrefix == null || tries == null) {
            return null;
        }
        Vector<String> terms = new Vector<String>();
        for (ITrie curTrie : tries) {
            if (curTrie == null) {
                continue;
            }
            Vector<String> curPfxs = curTrie.searchPrefix(curPrefix, 200);
            if (curPfxs != null && curPfxs.size() > 0) {
                for (String pfx : curPfxs) {
                    if (!terms.contains(pfx)) {
                        if (!(pfx.length() == 1 && eliminateSingleCharTokens)) {
                            terms.add(pfx);
                        }
                    }
                }
            }
        }
        if (terms.size() < 1) {
            return null;
        }
        terms.sort(Comparator.comparing(String::length).reversed());
        return terms;
    }

    private static Vector<String> getTermsStartingWithPrefix_VectVect(String curPrefix, Vector<Vector<String>>
            termCandidates, boolean eliminateSingleCharTokens) {
        if (curPrefix == null || termCandidates == null) {
            return null;
        }
        Vector<String> terms = new Vector<>();
        for (Vector<String> curList : termCandidates) {
            if (curList == null || curList.size() < 1) {
                continue;
            }
            for (String candidate : curList) {
                if (!terms.contains(candidate)) {
                    if (!(candidate.length() == 1 && eliminateSingleCharTokens)) {
                        terms.add(candidate);
                    }
                }
            }

        }
        if (terms.size() < 1) {
            return null;
        }
        terms.sort(Comparator.comparing(String::length).reversed());
        return terms;
    }

    // XXX TO DO:  This should really use indexes rather than strings, or there could be spurious false positives
    public static boolean isFullySubsumed(Vector<String> currentSegmenation, Vector<Vector<String>> seenSegmentations) {
        if (seenSegmentations == null || seenSegmentations.size() < 1 || currentSegmenation == null) {
            return false;
        }
        for (String curNewSeg : currentSegmenation) {
            if (Strings.hasText(curNewSeg)) {
                for (Vector<String> seenSegments : seenSegmentations) {
                    String newSeg = curNewSeg.toLowerCase();
                    boolean segFoundInPrev = false;
                    for (String seenSeg : seenSegments) {
                        if (seenSeg.indexOf(newSeg) != -1) {
                            segFoundInPrev = true;
                            break;
                        }
                    }
                    if (!segFoundInPrev) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //* This is promising, but not fully implemented
    //*
    //* public static Vector<Vector<String>> SegmentString_GraphBased(String input, Vector<ITrie> tries, INormalizer
    // normalizer, out String errMessage, bool filterSegments = true, int maxResults = 100000)
    //  {
    //      Hashtable<int, Vector<String>> results = StringSegmenter.GetAllTerms(input, tries, null, out errMessage);
    //      Vector<Vector<String>> fullSegments = StringSegmenter.AssembleSegments(results, out errMessage, maxResults);
    //      if (filterSegments)
    //      {
    //          return FilterSegments(fullSegments);
    //      }
    //      return fullSegments;
    //  }

    private static void addToSpecialStrings(char curCharChar, int i, Hashtable<Integer, StringBuilder> specialStrings) {
        if (specialStrings == null) {
            specialStrings = new Hashtable<>();
        }
        StringBuilder currentSpecialString = new StringBuilder();
        if (specialStrings.containsKey(i)) {
            specialStrings.remove(i);
            currentSpecialString.append(curCharChar);
            specialStrings.put(i, currentSpecialString);

        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(curCharChar);
            specialStrings.put(i, sb);
        }
    }

    private static void addSpecialStringsToTermsAtEachIndex(Hashtable<Integer, StringBuilder> specialStrings,
            Hashtable<Integer, Vector<String>> termsAtEachIndex) {
        if (specialStrings == null || specialStrings.size() < 1) {
            return;
        }
        if (termsAtEachIndex == null) {
            termsAtEachIndex = new Hashtable<>();
        }
        Enumeration<Integer> specialKeys = specialStrings.keys();
        while (specialKeys.hasMoreElements()) {
            Integer specialIndex = specialKeys.nextElement();
            Vector<String> stringsAlreadyAtSpecialIndex;
            if (termsAtEachIndex.containsKey(specialIndex)) {
                stringsAlreadyAtSpecialIndex = termsAtEachIndex.get(specialIndex);
                stringsAlreadyAtSpecialIndex.add(specialStrings.get(specialIndex).toString());
            } else {
                Vector<String> newStringsAtSpecialIndex = new Vector<>();
                newStringsAtSpecialIndex.add(specialStrings.get(specialIndex).toString());
                termsAtEachIndex.put(specialIndex, newStringsAtSpecialIndex);
            }
        }
    }
}