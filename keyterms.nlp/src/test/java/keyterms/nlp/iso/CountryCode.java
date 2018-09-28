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

package keyterms.nlp.iso;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A pool of the ISO-3166-1 country codes.
 *
 * <p> From <a href="https://www.iso.org/obp/ui/#search">iso.org</a>. </p>
 */
public enum CountryCode {

    AFG("af", 4, "Afghanistan"),
    ALA("ax", 248, "Åland Islands"),
    ALB("al", 8, "Albania"),
    DZA("dz", 12, "Algeria"),
    ASM("as", 16, "American Samoa"),
    AND("ad", 20, "Andorra"),
    AGO("ao", 24, "Angola"),
    AIA("ai", 660, "Anguilla"),
    ATA("aq", 10, "Antarctica"),
    ATG("ag", 28, "Antigua and Barbuda"),
    ARG("ar", 32, "Argentina"),
    ARM("am", 51, "Armenia"),
    ABW("aw", 533, "Aruba"),
    AUS("au", 36, "Australia"),
    AUT("at", 40, "Austria"),
    AZE("az", 31, "Azerbaijan"),
    BHS("bs", 44, "Bahamas"),
    BHR("bh", 48, "Bahrain"),
    BGD("bd", 50, "Bangladesh"),
    BRB("bb", 52, "Barbados"),
    BLR("by", 112, "Belarus"),
    BEL("be", 56, "Belgium"),
    BLZ("bz", 84, "Belize"),
    BEN("bj", 204, "Benin"),
    BMU("bm", 60, "Bermuda"),
    BTN("bt", 64, "Bhutan"),
    BOL("bo", 68, "Bolivia"),
    BES("bq", 535, "Sint Eustatius and Saba Bonaire"),
    BIH("ba", 70, "Bosnia and Herzegovina"),
    BWA("bw", 72, "Botswana"),
    BVT("bv", 74, "Bouvet Island"),
    BRA("br", 76, "Brazil"),
    IOT("io", 86, "British Indian Ocean Territory"),
    BRN("bn", 96, "Brunei Darussalam"),
    BGR("bg", 100, "Bulgaria"),
    BFA("bf", 854, "Burkina Faso"),
    BDI("bi", 108, "Burundi"),
    CPV("cv", 132, "Cabo Verde"),
    KHM("kh", 116, "Cambodia"),
    CMR("cm", 120, "Cameroon"),
    CAN("ca", 124, "Canada"),
    CYM("ky", 136, "Cayman Islands"),
    CAF("cf", 140, "Central African Republic"),
    TCD("td", 148, "Chad"),
    CHL("cl", 152, "Chile"),
    CHN("cn", 156, "China"),
    CXR("cx", 162, "Christmas Island"),
    CCK("cc", 166, "Cocos"),
    COL("co", 170, "Colombia"),
    COM("km", 174, "Comoros"),
    COD("cd", 180, "Congo"),
    COG("cg", 178, "Congo"),
    COK("ck", 184, "Cook Islands"),
    CRI("cr", 188, "Costa Rica"),
    CIV("ci", 384, "Côte d'Ivoire"),
    HRV("hr", 191, "Croatia"),
    CUB("cu", 192, "Cuba"),
    CUW("cw", 531, "Curaçao"),
    CYP("cy", 196, "Cyprus"),
    CZE("cz", 203, "Czech Republic"),
    DNK("dk", 208, "Denmark"),
    DJI("dj", 262, "Djibouti"),
    DMA("dm", 212, "Dominica"),
    DOM("do", 214, "Dominican Republic"),
    ECU("ec", 218, "Ecuador"),
    EGY("eg", 818, "Egypt"),
    SLV("sv", 222, "El Salvador"),
    GNQ("gq", 226, "Equatorial Guinea"),
    ERI("er", 232, "Eritrea"),
    EST("ee", 233, "Estonia"),
    ETH("et", 231, "Ethiopia"),
    FLK("fk", 238, "Falkland Islands  [Malvinas]"),
    FRO("fo", 234, "Faroe Islands"),
    FJI("fj", 242, "Fiji"),
    FIN("fi", 246, "Finland"),
    FRA("fr", 250, "France"),
    GUF("gf", 254, "French Guiana"),
    PYF("pf", 258, "French Polynesia"),
    ATF("tf", 260, "French Sourn Territories"),
    GAB("ga", 266, "Gabon"),
    GMB("gm", 270, "Gambia"),
    GEO("ge", 268, "Georgia"),
    DEU("de", 276, "Germany"),
    GHA("gh", 288, "Ghana"),
    GIB("gi", 292, "Gibraltar"),
    GRC("gr", 300, "Greece"),
    GRL("gl", 304, "Greenland"),
    GRD("gd", 308, "Grenada"),
    GLP("gp", 312, "Guadeloupe"),
    GUM("gu", 316, "Guam"),
    GTM("gt", 320, "Guatemala"),
    GGY("gg", 831, "Guernsey"),
    GIN("gn", 324, "Guinea"),
    GNB("gw", 624, "Guinea-Bissau"),
    GUY("gy", 328, "Guyana"),
    HTI("ht", 332, "Haiti"),
    HMD("hm", 334, "Heard Island and McDonald Islands"),
    VAT("va", 336, "Holy See"),
    HND("hn", 340, "Honduras"),
    HKG("hk", 344, "Hong Kong"),
    HUN("hu", 348, "Hungary"),
    ISL("is", 352, "Iceland"),
    IND("in", 356, "India"),
    IDN("id", 360, "Indonesia"),
    IRN("ir", 364, "Iran"),
    IRQ("iq", 368, "Iraq"),
    IRL("ie", 372, "Ireland"),
    IMN("im", 833, "Isle of Man"),
    ISR("il", 376, "Israel"),
    ITA("it", 380, "Italy"),
    JAM("jm", 388, "Jamaica"),
    JPN("jp", 392, "Japan"),
    JEY("je", 832, "Jersey"),
    JOR("jo", 400, "Jordan"),
    KAZ("kz", 398, "Kazakhstan"),
    KEN("ke", 404, "Kenya"),
    KIR("ki", 296, "Kiribati"),
    PRK("kp", 408, "Korea"),
    KOR("kr", 410, "Korea"),
    KWT("kw", 414, "Kuwait"),
    KGZ("kg", 417, "Kyrgyzstan"),
    LAO("la", 418, "Lao People's Democratic Republic"),
    LVA("lv", 428, "Latvia"),
    LBN("lb", 422, "Lebanon"),
    LSO("ls", 426, "Lesotho"),
    LBR("lr", 430, "Liberia"),
    LBY("ly", 434, "Libya"),
    LIE("li", 438, "Liechtenstein"),
    LTU("lt", 440, "Lithuania"),
    LUX("lu", 442, "Luxembourg"),
    MAC("mo", 446, "Macao"),
    MKD("mk", 807, "Macedonia"),
    MDG("mg", 450, "Madagascar"),
    MWI("mw", 454, "Malawi"),
    MYS("my", 458, "Malaysia"),
    MDV("mv", 462, "Maldives"),
    MLI("ml", 466, "Mali"),
    MLT("mt", 470, "Malta"),
    MHL("mh", 584, "Marshall Islands"),
    MTQ("mq", 474, "Martinique"),
    MRT("mr", 478, "Mauritania"),
    MUS("mu", 480, "Mauritius"),
    MYT("yt", 175, "Mayotte"),
    MEX("mx", 484, "Mexico"),
    FSM("fm", 583, "Micronesia"),
    MDA("md", 498, "Moldova"),
    MCO("mc", 492, "Monaco"),
    MNG("mn", 496, "Mongolia"),
    MNE("me", 499, "Montenegro"),
    MSR("ms", 500, "Montserrat"),
    MAR("ma", 504, "Morocco"),
    MOZ("mz", 508, "Mozambique"),
    MMR("mm", 104, "Myanmar"),
    NAM("na", 516, "Namibia"),
    NRU("nr", 520, "Nauru"),
    NPL("np", 524, "Nepal"),
    NLD("nl", 528, "Nerlands"),
    NCL("nc", 540, "New Caledonia"),
    NZL("nz", 554, "New Zealand"),
    NIC("ni", 558, "Nicaragua"),
    NER("ne", 562, "Niger"),
    NGA("ng", 566, "Nigeria"),
    NIU("nu", 570, "Niue"),
    NFK("nf", 574, "Norfolk Island"),
    MNP("mp", 580, "Norrn Mariana Islands"),
    NOR("no", 578, "Norway"),
    OMN("om", 512, "Oman"),
    PAK("pk", 586, "Pakistan"),
    PLW("pw", 585, "Palau"),
    PSE("ps", 275, "State of Palestine"),
    PAN("pa", 591, "Panama"),
    PNG("pg", 598, "Papua New Guinea"),
    PRY("py", 600, "Paraguay"),
    PER("pe", 604, "Peru"),
    PHL("ph", 608, "Philippines"),
    PCN("pn", 612, "Pitcairn"),
    POL("pl", 616, "Poland"),
    PRT("pt", 620, "Portugal"),
    PRI("pr", 630, "Puerto Rico"),
    QAT("qa", 634, "Qatar"),
    REU("re", 638, "Réunion"),
    ROU("ro", 642, "Romania"),
    RUS("ru", 643, "Russian Federation"),
    RWA("rw", 646, "Rwanda"),
    BLM("bl", 652, "Saint Barthélemy"),
    SHN("sh", 654, "Ascension and Tristan da Cunha Saint Helena"),
    KNA("kn", 659, "Saint Kitts and Nevis"),
    LCA("lc", 662, "Saint Lucia"),
    MAF("mf", 663, "Saint Martin"),
    SPM("pm", 666, "Saint Pierre and Miquelon"),
    VCT("vc", 670, "Saint Vincent and Grenadines"),
    WSM("ws", 882, "Samoa"),
    SMR("sm", 674, "San Marino"),
    STP("st", 678, "Sao Tome and Principe"),
    SAU("sa", 682, "Saudi Arabia"),
    SEN("sn", 686, "Senegal"),
    SRB("rs", 688, "Serbia"),
    SYC("sc", 690, "Seychelles"),
    SLE("sl", 694, "Sierra Leone"),
    SGP("sg", 702, "Singapore"),
    SXM("sx", 534, "Sint Maarten"),
    SVK("sk", 703, "Slovakia"),
    SVN("si", 705, "Slovenia"),
    SLB("sb", 90, "Solomon Islands"),
    SOM("so", 706, "Somalia"),
    ZAF("za", 710, "South Africa"),
    SGS("gs", 239, "South Georgia and South Sandwich Islands"),
    SSD("ss", 728, "South Sudan"),
    ESP("es", 724, "Spain"),
    LKA("lk", 144, "Sri Lanka"),
    SDN("sd", 729, "Sudan"),
    SUR("sr", 740, "Suriname"),
    SJM("sj", 744, "Svalbard and Jan Mayen"),
    SWZ("sz", 748, "Swaziland"),
    SWE("se", 752, "Sweden"),
    CHE("ch", 756, "Switzerland"),
    SYR("sy", 760, "Syrian Arab Republic"),
    TWN("tw", 158, "Taiwan"),
    TJK("tj", 762, "Tajikistan"),
    TZA("tz", 834, "United Republic of Tanzania"),
    THA("th", 764, "Thailand"),
    TLS("tl", 626, "Timor-Leste"),
    TGO("tg", 768, "Togo"),
    TKL("tk", 772, "Tokelau"),
    TON("to", 776, "Tonga"),
    TTO("tt", 780, "Trinidad and Tobago"),
    TUN("tn", 788, "Tunisia"),
    TUR("tr", 792, "Turkey"),
    TKM("tm", 795, "Turkmenistan"),
    TCA("tc", 796, "Turks and Caicos Islands"),
    TUV("tv", 798, "Tuvalu"),
    UGA("ug", 800, "Uganda"),
    UKR("ua", 804, "Ukraine"),
    ARE("ae", 784, "United Arab Emirates"),
    GBR("gb", 826, "United Kingdom of Great Britain and Norrn Ireland"),
    UMI("um", 581, "United States Minor Outlying Islands"),
    USA("us", 840, "United States of America"),
    URY("uy", 858, "Uruguay"),
    UZB("uz", 860, "Uzbekistan"),
    VUT("vu", 548, "Vanuatu"),
    VEN("ve", 862, "Venezuela"),
    VNM("vn", 704, "Viet Nam"),
    VGB("vg", 92, "Virgin Islands"),
    VIR("vi", 850, "Virgin Islands"),
    WLF("wf", 876, "Wallis and Futuna"),
    ESH("eh", 732, "Western Sahara*"),
    YEM("ye", 887, "Yemen"),
    ZMB("zm", 894, "Zambia"),
    ZWE("zw", 716, "Zimbabwe"),
    UK(null, -1, "United Kingdom"),
    SU(null, -2, "Soviet Union"),
    AC(null, -3, "Ascension Island"),
    EU(null, -4, "European Union");

    public static CountryCode getCodeForString(String codeString) {
        if (codeString == null) {
            return null;
        }
        codeString = codeString.trim();
        for (CountryCode curCode : values()) {
            if (curCode.name().equalsIgnoreCase(codeString)) {
                return curCode;
            }
            if (curCode.iso3.equalsIgnoreCase(codeString)) {
                return curCode;
            }
            if (curCode.iso2.equalsIgnoreCase(codeString)) {
                return curCode;
            }
            if (curCode.englishName.equalsIgnoreCase(codeString)) {
                return curCode;
            }
            for (String curForm : curCode.altforms) {
                if (codeString.equalsIgnoreCase(curForm)) {
                    return curCode;
                }
            }
        }
        return null;
    }

    private final String englishName;
    private final String iso2;
    private final String iso3;
    private final Set<String> altforms;

    CountryCode(String iso2code, int number, String engName, String... otherVariants) {
        this.englishName = engName;
        if (name().length() == 2) {
            this.iso2 = name().toLowerCase();
            this.iso3 = null;
        } else {
            this.iso2 = iso2code;
            this.iso3 = name().toLowerCase();
        }
        altforms = new HashSet<>();
        if (otherVariants != null) {
            Collections.addAll(altforms, otherVariants);
        }
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getIso2() {
        return iso2;
    }

    public String getIso3() {
        return iso3;
    }

    public Set<String> getAltForms() {
        return altforms;
    }
}