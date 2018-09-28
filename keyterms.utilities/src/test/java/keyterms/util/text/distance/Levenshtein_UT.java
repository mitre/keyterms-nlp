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

package keyterms.util.text.distance;

import java.nio.CharBuffer;

import org.junit.Assert;
import org.junit.Test;

import keyterms.util.collect.Bags;

import static org.junit.Assert.assertEquals;

public class Levenshtein_UT {

    private final Levenshtein editDistance = new Levenshtein();

    @Test
    public void lenient() {
        // Checking that no errors occur
        editDistance.getDistance(null, null);
        editDistance.getDistance(null, "Hello World!");
        editDistance.getDistance("Hello World!", null);
        editDistance.getDistance(null, "cat".toCharArray());
        editDistance.getDistance(null, CharBuffer.wrap("cat".toCharArray()));
        editDistance.getDistance(null, new Character[] { 'c', null, 't' });
        editDistance.getDistance(null, Bags.arrayList('c', null, 't'));
    }

    @Test
    public void getDistance() {
        assertEquals(0, editDistance.getDistance(null, null), 0);
        assertEquals(1, editDistance.getDistance(null, "Hello World!"), 0);
        assertEquals(1, editDistance.getDistance("Hello World!", null), 0);
        assertEquals(1, editDistance.getDistance("", "cat"), 0);
        assertEquals(1, editDistance.getDistance("cat", ""), 0);
        assertEquals(0, editDistance.getDistance("cat", "cat"), 0);
        assertEquals(1.0 / 3.0, editDistance.getDistance("cat", "bat"), 0);
        assertEquals(1, editDistance.getDistance("cat", "dog"), 0);
        assertEquals(1.0 / 3.0, editDistance.getDistance("cat", "ct"), 0);
        assertEquals(1.0 / 4.0, editDistance.getDistance("cat", "cats"), 0);
        assertEquals(1, editDistance.getDistance("cat", "d"), 0);
        assertEquals(3.0 / 6.0, editDistance.getDistance("boggle", "gogles"), 0);
    }

    @Test
    public void noMemoryProblems() {
        String longString1 =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam euismod lorem sed nibh blandit ut " +
                        "rhoncus velit auctor. Aenean tincidunt lectus a nibh aliquet at varius massa semper. Aliquam" +
                        " erat volutpat. Quisque hendrerit velit velit, a iaculis mauris. Sed a ultrices enim. Donec " +
                        "quis mi lacus. Nullam non elit nec justo pretium consectetur. Morbi arcu ligula, " +
                        "suscipit nec vehicula vitae, malesuada eu augue. Mauris rutrum ipsum non tortor porta vitae " +
                        "hendrerit elit tincidunt. Sed libero lacus, aliquam vitae sagittis ac, " +
                        "commodo eget elit. Ut sit amet quam non mi aliquam hendrerit ac ac tellus. Maecenas mattis " +
                        "tincidunt nisl, id posuere arcu tincidunt eget. Vestibulum placerat magna ac massa tempus " +
                        "ultricies. Nullam diam velit, tempus ac tincidunt tincidunt, " +
                        "consequat eget purus. Fusce ultrices, justo at aliquam imperdiet, mi neque faucibus lacus, " +
                        "ut fermentum ante erat vitae diam. Donec in ante leo, non malesuada neque. In porttitor " +
                        "tempor tincidunt. Etiam tortor ipsum, dignissim sed mattis nec, " +
                        "bibendum nec lacus. Pellentesque habitant morbi tristique senectus et netus et malesuada " +
                        "fames ac turpis egestas. Nam eu erat vel ante rutrum sodales vitae non nulla. Phasellus at " +
                        "gravida felis. Phasellus eu ligula lacus, sit amet faucibus neque. In sed dui eros. Donec " +
                        "lacus urna, scelerisque iaculis viverra in, viverra iaculis risus. Sed eleifend, " +
                        "sapien sit amet imperdiet hendrerit, est nisl ullamcorper felis, " +
                        "in convallis urna orci non tortor. Nam eget lacus a arcu pulvinar aliquet. In hac habitasse " +
                        "platea dictumst. Maecenas at sollicitudin massa. Vestibulum ante ipsum primis in faucibus " +
                        "orci luctus et ultrices posuere cubilia Curae; Duis laoreet varius cursus. Morbi cursus, " +
                        "mi et venenatis iaculis, orci dui pulvinar lectus, eu mollis nisl nisl sit amet leo. " +
                        "Suspendisse in nisi lacus, mattis semper neque. Sed venenatis ultricies est ut sagittis. " +
                        "Vestibulum tempus blandit ultricies. Donec egestas vehicula neque, " +
                        "non adipiscing quam fermentum nec. Aliquam sed turpis nisl, " +
                        "eu tristique enim. Ut non malesuada lacus. Suspendisse varius vulputate ullamcorper. Integer" +
                        " a dolor vitae nibh pretium cursus ac auctor est. Aliquam velit turpis, " +
                        "scelerisque aliquam mollis a, blandit nec nulla. Nam vehicula, " +
                        "augue sit amet dignissim auctor, mauris est accumsan mi, sed tincidunt lorem orci et purus. " +
                        "Sed volutpat, dolor sit amet fringilla venenatis, mi sem blandit lacus, " +
                        "ut semper velit turpis a eros. Proin sed feugiat lacus. Nulla euismod odio et augue " +
                        "malesuada vel sagittis diam blandit. Fusce nisi arcu, egestas nec tincidunt at, " +
                        "egestas id quam. Donec dignissim lobortis nunc a faucibus. Phasellus elementum purus eu " +
                        "purus semper ut tristique leo viverra. Curabitur ultrices mollis eros aliquet lobortis. " +
                        "Pellentesque tincidunt bibendum quam. Morbi ac lorem a nisl tincidunt commodo. Donec ornare " +
                        "semper cursus. Sed volutpat tellus eu tortor venenatis sagittis. Nunc eu arcu pellentesque " +
                        "felis porta varius. Sed eleifend tempus magna, a auctor turpis adipiscing sed. Suspendisse " +
                        "aliquam lacinia diam, vel placerat eros tempor in. Nulla turpis odio, " +
                        "consectetur ac auctor in, ultricies sit amet mi. Duis nec risus nisi, " +
                        "ac rhoncus odio. In vitae metus vitae nisl molestie molestie. Pellentesque in ante sed nibh " +
                        "tincidunt pulvinar interdum quis magna. Curabitur aliquam venenatis magna, " +
                        "at adipiscing enim ullamcorper sed. Vivamus massa augue, malesuada a fringilla ac, " +
                        "ultrices sed ante. Vivamus tortor turpis, fermentum facilisis dignissim quis, " +
                        "convallis vel mi. iam nibh nulla, vulputate eget elementum ut, " +
                        "luctus vel mi. Donec mi turpis, pulvinar a varius non, tincidunt eu sapien. Sed a turpis " +
                        "ipsum, a scelerisque lectus. Donec at lorem nec risus viverra interdum. Morbi dui tellus, " +
                        "egestas at semper egestas, tincidunt et diam. In hac habitasse platea dictumst. Vivamus ut " +
                        "tincidunt metus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse " +
                        "eleifend ligula sed nibh accumsan ut vehicula lectus tempor. Nam venenatis arcu rhoncus " +
                        "sapien condimentum sodales. Nunc pretium lorem eu nisi feugiat molestie. Ut aliquet mollis " +
                        "sollicitudin. Vivamus non suscipit sapien. Etiam at accumsan ante. Proin porta aliquam " +
                        "euismod. In eget elit justo. Phasellus sit amet ligula at diam lobortis consectetur. ";
        String longString2 =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mullam uismod lorems sed nibh blandit ut " +
                        "rhoncus velit auctor. Aenean tincidunt lectus a nibh aliquet at varius massa semper. Aliquam" +
                        " erat volutpat. Quisque hendrerit velit velit, a iaculis mauris. Sed a ultrices enim. Donec " +
                        "quis mi lacus. Nullam non elit nec justo pretium consectetur. Morbi arcu ligula, " +
                        "suscipit nec vehicula vitae, malesuada eu augue. Mauris rutrum ipsum non tortor porta vitae " +
                        "hendrerit elit tincidunt. Sed libero lacus, aliquam vitae sagittis ac, " +
                        "commodo eget elit. Ut sit amet quam non mi aliquam hendrerit ac ac tellus. Maecenas mattis " +
                        "tincidunt nisl, id posuere arcu tincidunt eget. Vestibulum placerat magna ac massa tempus " +
                        "ultricies. Nullam diam velit, tempus ac tincidunt tincidunt, " +
                        "consequat eget purus. Fusce ultrices, justo at aliquam imperdiet, mi neque faucibus lacus, " +
                        "ut fermentum ante erat vitae diam. Donec in ante leo, non malesuada neque. In porttitor " +
                        "tempor tincidunt. Etiam tortor ipsum, dignissim sed mattis nec, " +
                        "bibendum nec lacus. Pellentesque habitant morbi tristique senectus et netus et malesuada " +
                        "fames ac turpis egestas. Nam eu erat vel ante rutrum sodales vitae non nulla. Phasellus at " +
                        "gravida felis. Phasellus eu ligula lacus, sit amet faucibus neque. In sed dui eros. Donec " +
                        "lacus urna, scelerisque iaculis viverra in, viverra iaculis risus. Sed eleifend, " +
                        "sapien sit amet imperdiet hendrerit, est nisl ullamcorper felis, " +
                        "in convallis urna orci non tortor. Nam eget lacus a arcu pulvinar aliquet. In hac habitasse " +
                        "platea dictumst. Maecenas at sollicitudin massa. Vestibulum ante ipsum primis in faucibus " +
                        "orci luctus et ultrices posuere cubilia Curae; Duis laoreet varius cursus. Morbi cursus, " +
                        "mi et venenatis iaculis, orci dui pulvinar lectus, eu mollis nisl nisl sit amet leo. " +
                        "Suspendisse in nisi lacus, mattis semper neque. Sed venenatis ultricies est ut sagittis. " +
                        "Vestibulum tempus blandit ultricies. Donec egestas vehicula neque, " +
                        "non adipiscing quam fermentum nec. Aliquam sed turpis nisl, " +
                        "eu tristique enim. Ut non malesuada lacus. Suspendisse varius vulputate ullamcorper. Integer" +
                        " a dolor vitae nibh pretium cursus ac auctor est. Aliquam velit turpis, " +
                        "scelerisque aliquam mollis a, blandit nec nulla. Nam vehicula, " +
                        "augue sit amet dignissim auctor, mauris est accumsan mi, sed tincidunt lorem orci et purus. " +
                        "Sed volutpat, dolor sit amet fringilla venenatis, mi sem blandit lacus, " +
                        "ut semper velit turpis a eros. Proin sed feugiat lacus. Nulla euismod odio et augue " +
                        "malesuada vel sagittis diam blandit. Fusce nisi arcu, egestas nec tincidunt at, " +
                        "egestas id quam. Donec dignissim lobortis nunc a faucibus. Phasellus elementum purus eu " +
                        "purus semper ut tristique leo viverra. Curabitur ultrices mollis eros aliquet lobortis. " +
                        "Pellentesque tincidunt bibendum quam. Morbi ac lorem a nisl tincidunt commodo. Donec ornare " +
                        "semper cursus. Sed volutpat tellus eu tortor venenatis sagittis. Nunc eu arcu pellentesque " +
                        "felis porta varius. Sed eleifend tempus magna, a auctor turpis adipiscing sed. Suspendisse " +
                        "aliquam lacinia diam, vel placerat eros tempor in. Nulla turpis odio, " +
                        "consectetur ac auctor in, ultricies sit amet mi. Duis nec risus nisi, " +
                        "ac rhoncus odio. In vitae metus vitae nisl molestie molestie. Pellentesque in ante sed nibh " +
                        "tincidunt pulvinar interdum quis magna. Curabitur aliquam venenatis magna, " +
                        "at adipiscing enim ullamcorper sed. Vivamus massa augue, malesuada a fringilla ac, " +
                        "ultrices sed ante. Vivamus tortor turpis, fermentum facilisis dignissim quis, " +
                        "convallis vel mi. iam nibh nulla, vulputate eget elementum ut, " +
                        "luctus vel mi. Donec mi turpis, pulvinar a varius non, tincidunt eu sapien. Sed a turpis " +
                        "ipsum, a scelerisque lectus. Donec at lorem nec risus viverra interdum. Morbi dui tellus, " +
                        "egestas at semper egestas, tincidunt et diam. In hac habitasse platea dictumst. Vivamus ut " +
                        "tincidunt metus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse " +
                        "eleifend ligula sed nibh accumsan ut vehicula lectus tempor. Nam venenatis arcu rhoncus " +
                        "sapien condimentum sodales. Nunc pretium lorem eu nisi feugiat molestie. Ut aliquet mollis " +
                        "sollicitudin. Vivamus non suscipit sapien. Etiam at accumsan ante. Proin porta aliquam " +
                        "euismod. In eget elit justo. Phasellus sit amet ligula at diam lobortis consectetur. ";
        assertEquals(0, editDistance.getDistance(longString1, longString1), 0);
        assertEquals(3.0 / 4242.0, editDistance.getDistance(longString1, longString2), 0);
        assertEquals(1, editDistance.getDistance("", longString1), 0);
    }

    @Test
    public void getDistance_CharArrays() {
        char[] sequence1 = "cat".toCharArray();
        char[] sequence2 = "dog".toCharArray();
        assertEquals(1, editDistance.getDistance(sequence1, sequence2), 0);
    }

    @Test
    public void getDistance_CharacterArrays() {
        Character[] sequence1 = new Character[] { 'c', 'a', 't' };
        Character[] sequence2 = new Character[] { 'c', null, 't' };
        assertEquals(1.0 / 3.0, editDistance.getDistance(sequence1, sequence2), 0);
    }

    // CODE BELOW IS FROM original StringMetrics_UT

    private final String original = "12345";

    // insertion
    private final String compareToOriginal1 = "112345";
    private final int ldTarget1 = 1;
    private final double ldSimTarget1 = 1 - (ldTarget1 / (double)compareToOriginal1.length());

    // deletion
    private final String compareToOriginal2 = "1245";
    private final int ldTarget2 = 1;
    private final double ldSimTarget2 = 1 - (ldTarget2 / (double)original.length());

    // transposition
    private final String compareToOriginal3 = "13245";
    private final int ldTarget3 = 2;
    private final double ldSimTarget3 = 1 - (ldTarget3 / (double)original.length());

    // empty string
    private final String compareToOriginal4 = "";
    private final int ldTarget4 = original.length();

    // long string
    private final String compareToOriginal5 = "1234512345123451234512345123451234512345123451234512345123451234512345" +
            "123451234512345123451234512345123451234512345123451234512345123451234512345123451234512345123451234512345" +
            "123451234512345123451234512345123451234512345123451234512345123451234512345";
    private final int ldTarget5 = 245;
    private final double ldSimTarget5 = 1 - (ldTarget5 / (double)compareToOriginal5.length());

    @Test
    public void levenshteinDistanceTest() {
        Assert.assertEquals(ldTarget1, editDistance.getRawDistance(original, compareToOriginal1), 0.0);
        Assert.assertEquals(ldTarget1, editDistance.getRawDistance(compareToOriginal1, original), 0.0);
        Assert.assertEquals(ldTarget2, editDistance.getRawDistance(original, compareToOriginal2), 0.0);
        Assert.assertEquals(ldTarget2, editDistance.getRawDistance(compareToOriginal2, original), 0.0);
        Assert.assertEquals(ldTarget3, editDistance.getRawDistance(original, compareToOriginal3), 0.0);
        Assert.assertEquals(ldTarget3, editDistance.getRawDistance(compareToOriginal3, original), 0.0);
        Assert.assertEquals(ldTarget4, editDistance.getRawDistance(original, compareToOriginal4), 0.0);
        Assert.assertEquals(ldTarget4, editDistance.getRawDistance(compareToOriginal4, original), 0.0);
        Assert.assertEquals(ldTarget5, editDistance.getRawDistance(original, compareToOriginal5), 0.0);
        Assert.assertEquals(ldTarget5, editDistance.getRawDistance(compareToOriginal5, original), 0.0);

        /* edge case: two empty strings */
        Assert.assertEquals(0.0, editDistance.getRawDistance("", ""), 0);
    }

    @Test
    public void levenshteinSimilarityTest() {
        Assert.assertEquals(ldSimTarget1, editDistance.getSimilarity(original, compareToOriginal1), 0.0);
        Assert.assertEquals(ldSimTarget2, editDistance.getSimilarity(original, compareToOriginal2), 0.0);
        Assert.assertEquals(ldSimTarget3, editDistance.getSimilarity(original, compareToOriginal3), 0.0);
        double ldSimTarget4 = 0.0;
        Assert.assertEquals(ldSimTarget4, editDistance.getSimilarity(original, compareToOriginal4), 0.0);
        Assert.assertEquals(ldSimTarget5, editDistance.getSimilarity(original, compareToOriginal5), 0.0);

        /* edge case: two empty strings */
        Assert.assertEquals(1.0, editDistance.getSimilarity("", ""), 0.0);
    }
}