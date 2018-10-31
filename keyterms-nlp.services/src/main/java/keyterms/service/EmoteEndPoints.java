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

import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import keyterms.nlp.emoji.Emoji;
import keyterms.nlp.emoji.Emote;
import keyterms.nlp.emoji.EmoteTokenizer;
import keyterms.nlp.emoji.Emoticon;

/**
 * The service endpoints specific to emoji identification and normalization.
 */
@Provider
@Singleton
@Path("/emote")
public class EmoteEndPoints {
    /**
     * The emojicon normalizer.
     */
    private static final EmoteTokenizer EMOJICON_TOKENIZER = new EmoteTokenizer();

    /**
     * Get the emoticon or emoji with the specified text.
     *
     * @param text The text representation of the emote.
     *
     * @return The specified emoticon or emoji.
     */
    @GET
    @Path("define")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Emote define(@QueryParam("text") String text) {
        Emote emote = Emoji.getEmoji(text);
        if (emote == null) {
            emote = Emoticon.getEmoticon(text);
        }
        return emote;
    }

    /**
     * Tokenize the specified text extracting emotes from non-emote text.
     *
     * @param text The text.
     *
     * @return The tokens in the text.
     */
    @POST
    @Path("tokenize")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<EmoteTokenizer.Token> normalize(String text) {
        return EMOJICON_TOKENIZER.tokenize(text);
    }
}