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

package keyterms.nlp.model;

import java.io.Serializable;

public class Transliteration
        implements Serializable {

    static final long serialVersionUID = 214269;

    public static final String LABEL_IS_SRC_SCRIPT = "isSrcScript";
    public static final String LABEL_ORDER = "Order";
    public static final String LABEL_SCRIPT = "Script";
    public static final String LABEL_TRANSFORM_TYPE = "TransformType";
    public static final String LABEL_TEXT = "Text";
    public static final String LABEL_TEXT_INDEX = "TextIndex";
    public static final String LABEL_NOTE = "Note";

    private int Order;
    private Boolean isSrcScript;
    private String Script;
    private String TextIndex;
    private String Text;
    private String TransformType;

    public Transliteration(boolean isSrcScript, int order, String scriptCode, String transformType, String text) {
        this(isSrcScript, order, scriptCode, transformType, text, "");
    }

    public Transliteration(boolean isSrcScript, int order, String scriptCode, String transformType, String text,
            String textIndex) {
        this.isSrcScript = isSrcScript;
        Order = order;
        Script = scriptCode;
        TransformType = transformType;
        Text = text;
        TextIndex = textIndex;
    }

    public Boolean getIsSrcScript() {
        return isSrcScript;
    }

    public void setIsSrcScript(Boolean isSrcScript) {
        this.isSrcScript = isSrcScript;
    }

    public int getOrder() {
        return Order;
    }

    public void setOrder(int order) {
        Order = order;
    }

    public String getScript() {
        return Script;
    }

    public void setScript(String script) {
        Script = script;
    }

    public String getTransformType() {
        return TransformType;
    }

    public void setTransformType(String transformType) {
        TransformType = transformType;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getTextIndex() {
        return TextIndex;
    }

    public void setTextIndex(String textIndex) {
        TextIndex = textIndex;
    }

    public String toString() {
        return "{" +
                "\"" +
                LABEL_IS_SRC_SCRIPT +
                "\":" +
                isSrcScript +
                "," +
                "\"" +
                LABEL_ORDER +
                "\":" +
                Order +
                "," +
                "\"" +
                LABEL_SCRIPT +
                "\":\"" +
                Script +
                "\"" +
                "," +
                "\"" +
                LABEL_TRANSFORM_TYPE +
                "\":\"" +
                TransformType +
                "\"" +
                "," +
                "\"" +
                LABEL_TEXT +
                "\":\"" +
                Text +
                "\"" +
                "," +
                "\"" +
                LABEL_TEXT_INDEX +
                "\":\"" +
                TextIndex +
                "\"" +
                "}";
    }
}