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

import java.util.Map;
import java.util.function.Predicate;

/**
 * A text transformation which
 */
public class MapTransliterator
        extends Transliterator
        implements Predicate<CharSequence> {
    /**
     * The map transformation.
     */
    protected final MapTransform map = new MapTransform();

    /**
     * Constructor.
     *
     * @param key The textual representation of the transliterator key.
     */
    public MapTransliterator(CharSequence key) {
        super(true, key);
    }

    /**
     * Get the mapTransformation map.
     *
     * @return The transformation map.
     */
    public Map<String, String> getTransforms() {
        return map.getTransforms();
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
        map.add(input, output);
    }

    /**
     * Add all of the specified transformations.
     *
     * @param transforms The transformations.
     */
    public void addAll(Map<? extends CharSequence, ? extends CharSequence> transforms) {
        map.addAll(transforms);
    }

    /**
     * Add all of the specified transformations.
     *
     * @param mapTransform The transform map containing the desired transformations.
     */
    public void addAll(MapTransform mapTransform) {
        map.addAll(mapTransform);
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
    public boolean test(CharSequence charSequence) {
        return map.test(charSequence);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String apply(CharSequence text) {
        return map.apply(text);
    }
}