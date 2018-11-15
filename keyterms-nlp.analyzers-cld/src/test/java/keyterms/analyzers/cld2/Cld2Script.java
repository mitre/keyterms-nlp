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

package keyterms.analyzers.cld2;

import keyterms.util.text.Strings;

enum Cld2Script {

    ULScript_Common(0, " Zyyy"),
    ULScript_Latin(1, " Latn"),
    ULScript_Greek(2, " Grek"),
    ULScript_Cyrillic(3, " Cyrl"),
    ULScript_Armenian(4, " Armn"),
    ULScript_Hebrew(5, " Hebr"),
    ULScript_Arabic(6, " Arab"),
    ULScript_Syriac(7, " Syrc"),
    ULScript_Thaana(8, " Thaa"),
    ULScript_Devanagari(9, " Deva"),
    ULScript_Bengali(10, " Beng"),
    ULScript_Gurmukhi(11, " Guru"),
    ULScript_Gujarati(12, " Gujr"),
    ULScript_Oriya(13, " Orya"),
    ULScript_Tamil(14, " Taml"),
    ULScript_Telugu(15, " Telu"),
    ULScript_Kannada(16, " Knda"),
    ULScript_Malayalam(17, " Mlym"),
    ULScript_Sinhala(18, " Sinh"),
    ULScript_Thai(19, " Thai"),
    ULScript_Lao(20, " Laoo"),
    ULScript_Tibetan(21, " Tibt"),
    ULScript_Myanmar(22, " Mymr"),
    ULScript_Georgian(23, " Geor"),
    ULScript_Hani(24, " Hani"),
    ULScript_Ethiopic(25, " Ethi"),
    ULScript_Cherokee(26, " Cher"),
    ULScript_Canadian_Aboriginal(27, " Cans"),
    ULScript_Ogham(28, " Ogam"),
    ULScript_Runic(29, " Runr"),
    ULScript_Khmer(30, " Khmr"),
    ULScript_Mongolian(31, " Mong"),
    ULScript_32(32, ""),
    ULScript_33(33, ""),
    ULScript_Bopomofo(34, " Bopo"),
    ULScript_35(35, ""),
    ULScript_Yi(36, " Yiii"),
    ULScript_Old_Italic(37, " Ital"),
    ULScript_Gothic(38, " Goth"),
    ULScript_Deseret(39, " Dsrt"),
    ULScript_Inherited(40, " Zinh"),
    ULScript_Tagalog(41, " Tglg"),
    ULScript_Hanunoo(42, " Hano"),
    ULScript_Buhid(43, " Buhd"),
    ULScript_Tagbanwa(44, " Tagb"),
    ULScript_Limbu(45, " Limb"),
    ULScript_Tai_Le(46, " Tale"),
    ULScript_Linear_B(47, " Linb"),
    ULScript_Ugaritic(48, " Ugar"),
    ULScript_Shavian(49, " Shaw"),
    ULScript_Osmanya(50, " Osma"),
    ULScript_Cypriot(51, " Cprt"),
    ULScript_Braille(52, " Brai"),
    ULScript_Buginese(53, " Bugi"),
    ULScript_Coptic(54, " Copt"),
    ULScript_New_Tai_Lue(55, " Talu"),
    ULScript_Glagolitic(56, " Glag"),
    ULScript_Tifinagh(57, " Tfng"),
    ULScript_Syloti_Nagri(58, " Sylo"),
    ULScript_Old_Persian(59, " Xpeo"),
    ULScript_Kharoshthi(60, " Khar"),
    ULScript_Balinese(61, " Bali"),
    ULScript_Cuneiform(62, " Xsux"),
    ULScript_Phoenician(63, " Phnx"),
    ULScript_Phags_Pa(64, " Phag"),
    ULScript_Nko(65, " Nkoo"),
    ULScript_Sundanese(66, " Sund"),
    ULScript_Lepcha(67, " Lepc"),
    ULScript_Ol_Chiki(68, " Olck"),
    ULScript_Vai(69, " Vaii"),
    ULScript_Saurashtra(70, " Saur"),
    ULScript_Kayah_Li(71, " Kali"),
    ULScript_Rejang(72, " Rjng"),
    ULScript_Lycian(73, " Lyci"),
    ULScript_Carian(74, " Cari"),
    ULScript_Lydian(75, " Lydi"),
    ULScript_Cham(76, " Cham"),
    ULScript_Tai_Tham(77, " Lana"),
    ULScript_Tai_Viet(78, " Tavt"),
    ULScript_Avestan(79, " Avst"),
    ULScript_Egyptian_Hieroglyphs(80, " Egyp"),
    ULScript_Samaritan(81, " Samr"),
    ULScript_Lisu(82, " Lisu"),
    ULScript_Bamum(83, " Bamu"),
    ULScript_Javanese(84, " Java"),
    ULScript_Meetei_Mayek(85, " Mtei"),
    ULScript_Imperial_Aramaic(86, " Armi"),
    ULScript_Old_South_Arabian(87, " Sarb"),
    ULScript_Inscriptional_Parthian(88, " Prti"),
    ULScript_Inscriptional_Pahlavi(89, " Phli"),
    ULScript_Old_Turkic(90, " Orkh"),
    ULScript_Kaithi(91, " Kthi"),
    ULScript_Batak(92, " Batk"),
    ULScript_Brahmi(93, " Brah"),
    ULScript_Mandaic(94, " Mand"),
    ULScript_Chakma(95, " Cakm"),
    ULScript_Meroitic_Cursive(96, " Merc"),
    ULScript_Meroitic_Hieroglyphs(97, " Mero"),
    ULScript_Miao(98, " Plrd"),
    ULScript_Sharada(99, " Shrd"),
    ULScript_Sora_Sompeng(100, " Sora"),
    ULScript_Takri(101, " Takr");

    private final int number;

    private final String code;

    Cld2Script(int number, String code) {
        this.number = number;
        this.code = Strings.trim(code);
    }

    public int getNumber() {
        return number;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return name() + "[" + number + "," + code + "]";
    }
}