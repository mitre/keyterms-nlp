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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Singleton;
import javax.servlet.annotation.WebListener;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import keyterms.analyzer.Analyzer;
import keyterms.analyzer.AnalyzerId;
import keyterms.analyzer.CoreAnalyzers;
import keyterms.analyzer.profiles.WekaProfile;
import keyterms.analyzer.profiles.WekaProfiles;
import keyterms.analyzer.text.TextInfo;
import keyterms.analyzer.text.VotingAnalyzer;
import keyterms.nlp.iso.Language;
//import keyterms.nlp.model.DisplayForm;
//import keyterms.nlp.model.IndexForm;
import keyterms.nlp.model.IndexForm;
import keyterms.nlp.model.Transliteration;
//import keyterms.nlp.model.WordForm;
import keyterms.nlp.text.ScriptProfiler;
import keyterms.nlp.transliterate.Transliterators;
import keyterms.rest.service.RestService;
import keyterms.util.collect.Keyed;
import keyterms.util.config.Setting;
import keyterms.util.config.SettingFactory;
import keyterms.util.io.Encoding;
import keyterms.util.text.Strings;
import keyterms.util.text.parser.Parsers;

/**
 * KeyTerms service endpoints.
 */
@Path("/")
@Singleton
@WebListener
public class KeyTermsService
        extends RestService {
    /**
     * The legacy operations.
     */
    private enum Operation {
        INDEX_FORM,
  //      WORD_FORMS,
        ALTERNATES
    }

    /**
     * The maximum number of results per analyzer to return when profiling data.
     */
    static final int MAX_RESULTS = 3;

    /**
     * The number of bytes of data to use in the preview function.
     */
    static final int PREVIEW_BYTES = 5_120;

    /**
     * The setting used to identify the per-language KeyTerms provided schema.
     */
    static final Setting<String> SCHEMA_MAP = new SettingFactory<>(
            "schema.map", String.class)
            .withParser((text) -> {
                Language language = null;
                Keyed<String, String> k = Parsers.parseKeyed(text);
                if (k != null) {
                    language = Language.byCode(k.getKey());
                    if (language == null) {
                        throw new IllegalArgumentException("Invalid language code: " + k.getKey());
                    }
                }
                if ((language == null) || (Strings.isBlank(k.getValue()))) {
                    throw new IllegalArgumentException("Invalid schema map entry: " + text);
                }
                return language.getCode().toUpperCase() + "=" + k.getValue();
            })
            .withMultipleValues()
            .build();

    /**
     * The default profile for text analysis.
     */
    private static final Setting<String> CLD2_HOME = new SettingFactory<>(
            "cld2.home", String.class)
            .withDefault("/var/lib/cld2")
            .build();

    /**
     * The default profile for text analysis.
     */
    private static final Setting<String> DEFAULT_PROFILE = new SettingFactory<>(
            "profile.default", String.class)
            .withDefault("udhr_test")
            .build();

    /**
     * The legacy JSON writer.
     */
    private static final Gson LEGACY_JSON = new GsonBuilder()
            .setLenient()
            .create();
    /**
     * The script profiler.
     */
    static final ScriptProfiler PROFILER = new ScriptProfiler();

    /**
     * The analyzer id or profile name for the default analyzer.
     */
    static String defaultAnalyzerKey;

    /**
     * The weka profiles instance.
     */
    static Analyzer defaultAnalyzer;

    /**
     * Get the specified analyzer.
     *
     * <p> The default analyzer will be returned if the specified analyzer name is blank. </p>
     *
     * @param analyzerName The analyzer to use.
     *
     * @return The specified analyzer.
     */
    static Analyzer getAnalyzer(String analyzerName) {
        Analyzer analyzer = defaultAnalyzer;
        if (Strings.hasText(analyzerName)) {
            String lookup = Strings.trim(analyzerName);
            analyzer = WekaProfiles.getInstance().get(lookup);
            if (analyzer == null) {
                analyzer = CoreAnalyzers.getInstance().get(AnalyzerId.valueOf(lookup));
            }
        }
        if (analyzer == null) {
            throw new IllegalArgumentException("Invalid analyzer: " + analyzerName);
        }
        return analyzer;
    }

    /**
     * Constructor.
     */
    public KeyTermsService() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void startup() {
        System.setProperty("cld2.home", CLD2_HOME.getValue());
        CoreAnalyzers.getInstance();
        WekaProfiles.getInstance(getWebRoot());
        setDefaultAnalyzer(DEFAULT_PROFILE.getValue());
        Transliterators.loadIcuBuiltIns();
        Transliterators.loadCustomRules();
        Transliterators.loadCustomTransliterators();
    }

    /**
     * Setup the default text analyzer.
     */
    private void setDefaultAnalyzer(String preferredProfile) {
        defaultAnalyzerKey = preferredProfile;
        defaultAnalyzer = WekaProfiles.getInstance().get(preferredProfile);
        if (defaultAnalyzer == null) {
            defaultAnalyzerKey = WekaProfiles.getInstance().profiles().stream()
                    .map(WekaProfile::getName)
                    .findFirst()
                    .orElse(null);
            defaultAnalyzer = WekaProfiles.getInstance().get(defaultAnalyzerKey);
            if (defaultAnalyzer != null) {
                getLogger().warn("Using random profile as default analyzer: {}", defaultAnalyzerKey);
            }
        }
        if (defaultAnalyzer == null) {
            AnalyzerId analyzerId = CoreAnalyzers.getInstance().get(null, (analyzer) ->
                    analyzer.produces(TextInfo.ENCODING) &&
                            analyzer.produces(TextInfo.LANGUAGE) &&
                            analyzer.produces(TextInfo.SCRIPT)
            ).entrySet().stream()
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);
            defaultAnalyzer = CoreAnalyzers.getInstance().get(analyzerId);
            if (defaultAnalyzer != null) {
                assert (analyzerId != null);
                defaultAnalyzerKey = analyzerId.toString();
                getLogger().warn("Using product analyzer as default analyzer: {}", analyzerId);
            }
        }
        if (defaultAnalyzer == null) {
            defaultAnalyzerKey = "VOTE";
            defaultAnalyzer = new VotingAnalyzer();
            getLogger().warn("Using voting analyzer as default analyzer.");
        }
        if (defaultAnalyzer == null) {
            getLogger().error("No preferred profile is available for text analysis.");
            throw new NullPointerException("No preferred profile is available for text analysis.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void shutdown() {
        // Intentional NoOp
    }

    /**
     * Process text as specified adhering to the legacy API for this service.
     *
     * <p>The output response may contain either HTML or JSON output which is, in the legacy service,
     * based on the content type of the request. </p>
     *
     * @param text The text to process.
     * @param languageCode The code for the language of the specified text (required for most operations).
     * @param doIndexForm A flag indicating whether to produce an index form of the specified text.
  //   * @param doWordForms A flag indicating whether to produce word forms for the specified text.
     * @param request The original HTTP request.
     *
     * @return The results as specified in the format specified by the request.
     */
    @GET
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doGet(
            @QueryParam("srctxt") String text,
            @QueryParam("lang") String languageCode,
            @QueryParam("idx") @DefaultValue("false") boolean doIndexForm,
//            @QueryParam("wform") @DefaultValue("false") boolean doWordForms,
            @Context ContainerRequestContext request) {
        Object result;
        Operation operation = Operation.ALTERNATES;
//        if (doWordForms) {
//            operation = Operation.WORD_FORMS;
//        }
        if (doIndexForm) {
            operation = Operation.INDEX_FORM;
        }
        switch (operation) {
            case INDEX_FORM:
                result = getIndexForm(text, languageCode);
                break;
//            case WORD_FORMS:
//                result = getWordForms(text, languageCode);
//                break;
            default:
                result = getAvailableTransforms(text, languageCode);
        }
        boolean htmlOutput = ((request.getMediaType() != null) &&
                (request.getMediaType().toString().toLowerCase().contains("html")));
        //@todo Below is the more correct way to determine the output format?
        // The above way was the initial implementation.
        // boolean writeHtml = request.getAcceptableMediaTypes().contains(MediaType.TEXT_HTML_TYPE);
        StringBuilder asText = new StringBuilder();
        if (htmlOutput) {
            asText.append("<html>");
            asText.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
            asText.append(LEGACY_JSON.toJson(result));
            asText.append("</html>");
        } else {
            asText.append(LEGACY_JSON.toJson(result));
        }
        return Response.ok()
                .encoding(Encoding.UTF8.name())
                .type((htmlOutput) ? MediaType.TEXT_HTML_TYPE : MediaType.APPLICATION_JSON_TYPE)
                .entity(asText.toString())
                .build();
    }

    /**
     * Process text as specified adhering to the legacy API for this service.
     *
     * <p>The output response may contain either HTML or JSON output which is, in the legacy service,
     * based on the content type of the request. </p>
     *
     * @param text The text to process.
     * @param languageCode The code for the language of the specified text (required for most operations).
     * @param doIndexForm A flag indicating whether to produce an index form of the specified text.
    // * @param doWordForms A flag indicating whether to produce word forms for the specified text.
     * @param request The original HTTP request.
     *
     * @return The results as specified in the format specified by the request.
     */
    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response doPost(
            @QueryParam("srctxt") String text,
            @QueryParam("lang") String languageCode,
            @QueryParam("idx") @DefaultValue("false") boolean doIndexForm,
//            @QueryParam("wform") @DefaultValue("false") boolean doWordForms,
            @Context ContainerRequestContext request) {
//        return doGet(text, languageCode, doIndexForm, doWordForms, request);
        return doGet(text, languageCode, doIndexForm, request);
    }

    /**
     * Get the display normalized form of the specified text.
     *
     * @param text The text.
     * @param languageCode The language of the text.
     *
     * @return The display normalized form of the specified text.
     */
//    @GET
//    @Path("display_form")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public DisplayForm getDisplayForm(
//            @QueryParam("srctxt") String text,
//            @QueryParam("lang") String languageCode) {
//        DisplayForm displayForm = null;
//        if (text != null) {
//            TextHandler handler = new TextHandler();
//            handler.setLanguage(languageCode);
//            displayForm = handler.getDisplayText(text);
//        }
//        return displayForm;
//    }

    /**
     * Get the index normalized form of the specified text.
     *
     * @param text The text.
     * @param languageCode The language of the text.
     *
     * @return The index normalized form of the specified text.
     */
    @GET
    @Path("index_form")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IndexForm getIndexForm(
            @QueryParam("srctxt") String text,
            @QueryParam("lang") String languageCode) {
        IndexForm indexForm = null;
        if (text != null) {
            TextHandler handler = new TextHandler();
            handler.setLanguage(languageCode);
            indexForm = handler.getIndexForm(text);
        }
        return indexForm;
    }

//    /**
//     * Get the word normalized form of the specified text.
//     *
//     * @param text The text.
//     * @param languageCode The language of the text.
//     *
//     * @return The word normalized form of the specified text.
//     */
//    @GET
//    @Path("word_forms")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public List<WordForm> getWordForms(
//            @QueryParam("srctxt") String text,
//            @QueryParam("lang") String languageCode) {
//        List<WordForm> wordForms = null;
//        if (text != null) {
//            TextHandler handler = new TextHandler();
//            handler.setLanguage(languageCode);
//            wordForms = handler.getWordForms(text);
//        }
//        return wordForms;
//    }

    /**
     * Get the KeyTerms provided schema for the specified language code.
     *
     * @param languageCode The language code.
     *
     * @return The KeyTerms provided schema for the specified language code.
     */
    @GET
    @Path("schema")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Set<String> getSchema(
            @QueryParam("lang") String languageCode) {
        //@todo correct the settings file and remove testing values.
        return SCHEMA_MAP.getValues().stream()
                .map(Parsers::parseKeyed)
                .filter((k) -> ((languageCode != null) && (languageCode.equalsIgnoreCase(k.getKey()))))
                .map(Keyed::getValue)
                .collect(Collectors.toSet());
    }

    /**
     * Get the available transforms of the specified text.
     *
     * @param text The text.
     * @param languageCode The language code.
     *
     * @return The available transforms of the specified text.
     */
    @GET
    @Path("transforms")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Transliteration> getAvailableTransforms(
            @QueryParam("srctxt") String text,
            @QueryParam("lang") String languageCode) {
        List<Transliteration> transliterations = null;
        if (text != null) {
            TextHandler handler = new TextHandler();
            handler.setLanguage(languageCode);
            transliterations = handler.getAvailableTransforms(text);
        }
        return transliterations;
    }
}