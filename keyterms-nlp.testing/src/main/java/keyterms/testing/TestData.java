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

package keyterms.testing;

import java.util.HashMap;
import java.util.Map;

/**
 * Common test data.
 */
public final class TestData {
    /**
     * Test phrases in several languages.
     */
    public static final Map<String, String> LANGUAGE_PHRASES;

    // Initialize the test phrases.
    static {
        LANGUAGE_PHRASES = new HashMap<>();
        LANGUAGE_PHRASES.put("ara-arab", "قفز الثعلب البني السريع فوق الكلب الكسول.");
        LANGUAGE_PHRASES.put("zho-hant", "快速的棕色狐狸跳過了懶狗。");
        LANGUAGE_PHRASES.put("eng-latn", "The quick brown fox jumped over the lazy dog.");
        LANGUAGE_PHRASES.put("fra-latn", "Le rapide renard brun sauta par dessus le chien paresseux.");
        LANGUAGE_PHRASES.put("deu-latn", "Der schnelle braune Fuchs sprang über den faulen Hund.");
        LANGUAGE_PHRASES.put("heb-hebr", "השועל החום המהיר קפץ מעל הכלב העצל.");
        LANGUAGE_PHRASES.put("hin-deva", "फुर्तीली भूरी लोमड़ी आलसी कुत्ते के उपर से कूद गई।");
        LANGUAGE_PHRASES.put("ita-latn", "La volpe marrone veloce saltò sul cane pigro.");
        LANGUAGE_PHRASES.put("jpn-kana", "クイックブラウンキツネは怠惰な犬の上を飛び出しました。");
        LANGUAGE_PHRASES.put("rus-cyrl", "Быстрая, коричневая лиса, перепрыгнула через ленивого пса.");
        LANGUAGE_PHRASES.put("spa-latn", "El rápido zorro marrón saltó sobre el perro perezoso.");
        LANGUAGE_PHRASES.put("tha-thai", "สุนัขจิ้งจอกสีน้ำตาลได้อย่างรวดเร็วเพิ่มขึ้นกว่าสุนัขขี้เกียจ.");
    }

    /**
     * Simple lorem ipsum text.
     */
    public static final String LOREM_IPSUM =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et " +
                    "dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi" +
                    " ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit" +
                    " esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident," +
                    " sunt in culpa qui officia deserunt mollit anim id est laborum.";

    /**
     * Relatively large lorem ipsum text.
     */
    public static final String LOREM_IPSUM_LARGE =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam congue, eros in ultrices faucibus, felis" +
                    " ex consectetur eros, sit amet molestie velit turpis ut nibh. Proin pulvinar ut purus sed" +
                    " suscipit. Etiam dictum lobortis consectetur. Ut luctus iaculis nulla, sed condimentum mi" +
                    " maximus sit amet. Ut velit erat, aliquet vel diam sit amet, molestie pulvinar nunc." +
                    " Vivamus quis auctor augue. Maecenas ipsum nisl, fermentum non odio et, venenatis ullamcorper" +
                    " odio. Donec varius, velit ac auctor aliquet, ipsum sapien ultricies quam, id ullamcorper" +
                    " lectus urna malesuada ante. In pellentesque sapien at mi suscipit eleifend. Donec mattis elit" +
                    " nec massa posuere, sed commodo urna venenatis. Integer cursus feugiat velit, sit amet molestie" +
                    " lacus hendrerit at." +
                    "\n\nCras blandit velit quis ex suscipit placerat. Aliquam mollis dui sit" +
                    " amet erat convallis, et dictum elit hendrerit. Vestibulum auctor imperdiet diam, et faucibus" +
                    " justo interdum sed. Morbi sodales dui eget ultrices viverra. Ut nec enim libero. Nam vel" +
                    " ultrices ante, et pharetra orci. Donec sit amet ligula id lacus euismod tincidunt. Phasellus" +
                    " pharetra in mauris nec tristique. Vestibulum neque urna, pretium id varius sit amet, accumsan" +
                    " id urna. Pellentesque nec tortor tortor. Etiam volutpat ultrices enim vitae mollis." +
                    " Ut sed molestie risus. Pellentesque rhoncus justo ac nisi condimentum lacinia." +
                    "\n\nPellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis" +
                    " egestas. Donec vehicula mollis urna ac venenatis. Donec et neque mauris. Phasellus dapibus" +
                    " gravida magna eu eleifend. Etiam at laoreet sem, sit amet pellentesque lacus. Etiam tortor" +
                    " erat, lacinia non aliquam et, ultrices sit amet nunc. Vestibulum ac egestas turpis, nec" +
                    " dignissim sapien. Sed tempus augue ligula, eu interdum ante aliquet ac. Interdum et malesuada" +
                    " fames ac ante ipsum primis in faucibus. Donec cursus maximus eros, nec tincidunt felis" +
                    " pharetra et. Aenean vel magna semper sapien egestas facilisis. Maecenas suscipit nunc mauris," +
                    " eu imperdiet nisl pretium dapibus. Mauris pharetra lacus rutrum, suscipit augue sed," +
                    " congue mauris. Nullam sit amet eros quis libero ultricies consequat. Morbi eu ornare nunc." +
                    " Interdum et malesuada fames ac ante ipsum primis in faucibus." +
                    "\n\nSuspendisse porta bibendum mauris vel maximus. Phasellus cursus in ligula a laoreet. Donec" +
                    " sed tincidunt est, a vulputate risus. Sed feugiat non elit ac dictum. Donec porttitor id" +
                    " neque posuere porta. Interdum et malesuada fames ac ante ipsum primis in faucibus. Maecenas" +
                    " quis diam justo. Sed feugiat nisi id arcu luctus egestas. Maecenas placerat augue quam, sit" +
                    " amet sodales libero vehicula a. Aliquam pretium orci vel eros volutpat, vitae luctus nisi" +
                    " porttitor. Donec vehicula euismod sapien, et tempus ligula mollis nec. Morbi viverra purus a" +
                    " tortor euismod congue. Donec scelerisque mollis maximus." +
                    "\n\nPhasellus ut risus scelerisque ex commodo commodo. Etiam nisl ex, iaculis eget scelerisque" +
                    " non, aliquet id magna. Pellentesque vitae lectus bibendum, ornare massa quis, scelerisque" +
                    " erat. Mauris ex tellus, semper in est tincidunt, mollis semper tortor. Cras pretium metus mi." +
                    " Donec quis pharetra enim. Sed at risus venenatis, convallis tortor quis, facilisis mauris." +
                    " Vivamus sodales urna non neque lobortis, mattis ullamcorper tortor imperdiet. Ut nisl diam," +
                    " efficitur egestas pulvinar non, molestie eget orci. In eu varius lectus. Cras in neque" +
                    " viverra, vehicula mi a, sodales velit." +
                    "\n\nClass aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos." +
                    " Etiam varius lacus eget urna tempor, vitae rhoncus magna scelerisque. Quisque bibendum massa" +
                    " vel quam vehicula, sed lacinia augue lobortis. Sed imperdiet, nunc id dictum tempus, turpis" +
                    " nulla tristique sapien, id rutrum quam libero nec lacus. Praesent porttitor porttitor nisi," +
                    " id lobortis eros eleifend id. Integer condimentum purus sed diam tempus, tincidunt blandit" +
                    " enim dictum. Sed rutrum urna eget velit placerat, id hendrerit quam rutrum. Aliquam semper" +
                    " ligula et nisl tristique vulputate. In convallis ornare odio sit amet lobortis. Vivamus urna" +
                    " dolor, ultricies a sem nec, aliquet volutpat tortor. Aliquam quis semper velit. Ut a" +
                    " pellentesque ante." +
                    "\n\nUt id sem erat. Mauris hendrerit tincidunt ipsum, sed ornare odio auctor quis. Nullam dui" +
                    " sem, aliquet non justo eu, feugiat consequat massa. Donec sit amet condimentum augue, ut" +
                    " tempus purus. Nam aliquet tincidunt egestas. Etiam lobortis nisl enim, a lacinia quam" +
                    " pharetra eu. Praesent a aliquam purus, ut tempus lorem. Proin dapibus massa arcu, et lobortis" +
                    " leo porttitor a. Pellentesque habitant morbi tristique senectus et netus et malesuada fames" +
                    " ac turpis egestas. Nullam venenatis ut risus eu luctus. Nam hendrerit tortor mi, eu rhoncus" +
                    " arcu egestas quis. Cras varius, metus ut mattis accumsan, augue libero dapibus odio, sed" +
                    " consectetur purus velit vitae tellus. Sed non ante bibendum, gravida libero non, sodales nisl." +
                    " Maecenas sit amet rhoncus tellus." +
                    "\n\nNullam ex magna, maximus et tempor ac, laoreet nec mauris. Mauris mollis eget leo in" +
                    " pretium. Nam imperdiet lacus quis erat viverra, quis consectetur orci fringilla. Vivamus" +
                    " pulvinar ante id laoreet ultrices. Curabitur quis ultricies massa. Mauris sed blandit nisi." +
                    " Aliquam semper odio dui, a ornare ex hendrerit non. Sed tristique at nibh vel ullamcorper." +
                    " Etiam quis gravida augue. Suspendisse semper vitae diam at cursus. Aliquam finibus mi nisl," +
                    " non mollis eros rutrum at." +
                    "\n\nDonec ut semper quam. Pellentesque dapibus metus ullamcorper, vestibulum orci at, tincidunt" +
                    " felis. Maecenas convallis facilisis mauris, eget tempor neque dignissim in. Morbi feugiat" +
                    " turpis eu consectetur imperdiet. Proin at metus massa. Vestibulum arcu turpis, condimentum" +
                    " at fringilla id, vehicula sit amet sapien. Cras porta lacus a nibh egestas, vel venenatis" +
                    " nibh bibendum. Suspendisse potenti. Nam interdum odio sed lacus faucibus aliquet. Fusce" +
                    " suscipit metus sed erat molestie eleifend. In hac habitasse platea dictumst. Vestibulum ante" +
                    " ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Vestibulum lorem" +
                    " felis, mattis tempus dolor quis, malesuada sagittis massa." +
                    "\n\nDuis eget turpis lacus. Ut auctor vel mauris vitae molestie. Integer aliquam suscipit elit" +
                    " id pharetra. In a metus elit. Nulla rhoncus, ex sed aliquet porta, nisi odio tincidunt elit," +
                    " ut dictum erat mauris ac velit. Nunc rhoncus, diam vel venenatis ultricies, massa nibh auctor" +
                    " magna, luctus ornare risus diam eu felis. Pellentesque vitae purus quam. Nullam neque justo," +
                    " aliquet sit amet aliquet ac, fermentum at turpis. Nullam lacus leo, suscipit at placerat in," +
                    " vehicula nec mauris. Donec cursus a augue eleifend placerat. Proin id sapien condimentum," +
                    " suscipit urna vel, pretium mi. Aliquam erat volutpat. Aliquam eu elementum risus, eu rhoncus" +
                    " lacus. Fusce tempus lacinia dictum.\n";

    /**
     * Constructor.
     */
    private TestData() {
        super();
    }
}