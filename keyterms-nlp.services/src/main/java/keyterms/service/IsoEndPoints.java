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
import java.util.stream.Collectors;

import javax.inject.Singleton;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import keyterms.nlp.iso.Country;
import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.nlp.text.ScriptProfile;
import keyterms.service.json.CountryDef;
import keyterms.service.json.LanguageDef;
import keyterms.service.json.ScriptDef;

/**
 * The service endpoints specific to text transformation (transliteration).
 */
@Provider
@Singleton
@Path("/iso")
public class IsoEndPoints {
    /**
     * Get the ISO standards information for the specified country.
     *
     * @param query A textual representation of the country.
     *
     * @return The ISO standards information for the specified country.
     */
    @GET
    @Path("country")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CountryDef> getCountries(@QueryParam("query") String query) {
        return Country.find(query).stream()
                .map(CountryDef::new)
                .collect(Collectors.toList());
    }

    /**
     * Get the ISO standards information for the specified language.
     *
     * @param query A textual representation of the language.
     *
     * @return The ISO standards information for the specified language.
     */
    @GET
    @Path("language")
    @Produces(MediaType.APPLICATION_JSON)
    public List<LanguageDef> getLanguages(@QueryParam("query") String query) {
        return Language.find(query).stream()
                .map(LanguageDef::new)
                .collect(Collectors.toList());
    }

    /**
     * Get the ISO standards information for the specified written script.
     *
     * @param query A textual representation of the written script.
     *
     * @return The ISO standards information for the specified written script.
     */
    @GET
    @Path("script")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ScriptDef> getScript(@QueryParam("query") String query) {
        return Script.find(query).stream()
                .map(ScriptDef::new)
                .collect(Collectors.toList());
    }

    /**
     * Get the ISO standards information for the specified written script.
     *
     * @param text The text to profile.
     *
     * @return The ISO standards information for the specified written script.
     */
    @POST
    @Path("profile_text")
    @Produces(MediaType.APPLICATION_JSON)
    public ScriptProfile profile(String text) {
        return KeyTermsService.PROFILER.profile(text);
    }

    /**
     * Get the ISO standards information for the specified written script.
     *
     * @param preferNonLatin A flag indicating whether to prefer non-latin scripts.
     * @param suppressLatin A flag indicating whether to suppress latin.
     * @param text The text to profile.
     *
     * @return The ISO standards information for the specified written script.
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
}