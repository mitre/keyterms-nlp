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

package keyterms.nlp.transliterate;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import keyterms.util.text.Strings;

/**
 * A text transformation which modifies the input text provisionally based on a map of inputs to desired outputs.
 */
public class MapTransform
        implements Predicate<CharSequence>, Function<CharSequence, String> {
    /**
     * The map of inputs to replace and their replacements.
     */
    private final Map<String, String> map = new HashMap<>();

    /**
     * Constructor.
     */
    public MapTransform() {
        super();
    }

    /**
     * Get the transformation map.
     *
     * @return The transformation map.
     */
    public Map<String, String> getTransforms() {
        return new HashMap<>(map);
    }

    /**
     * Add a mapping from input to output texts.
     *
     * <p> Note: Both the input and output will be trimmed. </p>
     * <p> Note: Blank or {@code null} inputs are ignored. </p>
     *
     * @param input The input text.
     * @param output The output text.
     */
    public void add(CharSequence input, CharSequence output) {
        map.put(Strings.trim(input), Strings.trim(output));
    }

    /**
     * Add all of the specified transformations.
     *
     * @param transforms The transformations.
     */
    public void addAll(Map<? extends CharSequence, ? extends CharSequence> transforms) {
        if (transforms != null) {
            transforms.forEach(this::add);
        }
    }

    /**
     * Add all of the specified transformations.
     *
     * @param mapTransform The transform map containing the desired transformations.
     */
    public void addAll(MapTransform mapTransform) {
        if (mapTransform != null) {
            addAll(mapTransform.getTransforms());
        }
    }

    /**
     * Remove a transform.
     *
     * @param input The input text.
     */
    public void remove(String input) {
        map.remove(input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean test(CharSequence text) {
        return map.containsKey(Strings.trim(text));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String apply(CharSequence text) {
        String transformed = Strings.trim(text);
        if (test(transformed)) {
            transformed = map.get(transformed);
        }
        return transformed;
    }
}