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

package keyterms.service;

import java.io.IOException;
import java.util.Set;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import keyterms.nlp.iso.Script;
import keyterms.nlp.text.ScriptProfile;
import keyterms.nlp.transliterate.TransformKey;
import keyterms.nlp.transliterate.Transliterator;
import keyterms.nlp.transliterate.Transliterators;

/**
 * The service endpoints specific to text transformation (transliteration).
 */
@Provider
@Singleton
@Path("/transform")
public class TransformEndPoints {
    /**
     * Get a profile of the scripts in the specified text.
     *
     * @param text The text to profile.
     *
     * @return The profile of script contents in the specified text.
     */
    @POST
    @Path("profile_text")
    @Produces(MediaType.APPLICATION_JSON)
    public ScriptProfile profile(String text) {
        return KeyTermsService.PROFILER.profile(text);
    }

    /**
     * Get the best script for the given text.
     *
     * @param preferNonLatin A flag indicating whether to prefer non-latin scripts.
     * @param suppressLatin A flag indicating whether to suppress latin.
     * @param text The text to profile.
     *
     * @return The best script for the given text.
     */
    @POST
    @Path("get_text_script")
    @Produces(MediaType.APPLICATION_JSON)
    public Script getBestScript(
            @QueryParam("prefer_non_latin") @DefaultValue("true") boolean preferNonLatin,
            @QueryParam("suppress_latin") @DefaultValue("false") boolean suppressLatin,
            String text) {
        return KeyTermsService.PROFILER.profile(text).getScript(preferNonLatin, suppressLatin);
    }

    /**
     * Get the keys which describe the available text transforms that can be applied given a description of the
     * specified source and target texts.
     *
     * @param source A description of the source text.
     * @param target A description of the target text.
     * @param scheme A description of the transform scheme.
     *
     * @return The available transformation keys that may be applicable to the specified source and target.
     */
    @GET
    @Path("transform_keys")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Set<TransformKey> getTransformKeys(
            @QueryParam("source") String source,
            @QueryParam("target") String target,
            @QueryParam("scheme") String scheme) {
        return Transliterators.getTransformKeys(source, target, scheme);
    }

    /**
     * Transform text as specified.
     *
     * @param key The transformation key.
     * @param text The text to transform.
     *
     * @return The transformed text.
     *
     * @throws IOException for input/output errors
     */
    @POST
    @Path("transform")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String transform(
            @QueryParam("key") String key,
            String text)
            throws IOException {
        Transliterator transliterator = Transliterators.get(new TransformKey(key));
        if (transliterator == null) {
            throw new IOException("Unknown transformation key: " + key);
        }
        return transliterator.apply(text);
    }
}