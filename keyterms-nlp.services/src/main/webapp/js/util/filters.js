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

"use strict";

angular.module("keyTerms.filters", [])

    .filter("squinch", [function () {
        return function (text, maxLen) {
            var squinched = text;
            if ((text) && (maxLen) && (text.length > maxLen)) {
                var half = Math.floor(Math.ceil(maxLen / 2) - 2.5);
                squinched = text.substr(0, half) + ' ... ' + text.substr(text.length - half);
            }
            return squinched;
        }
    }])

    .filter("labels", [function () {
        return function (emote) {
            var labels = '';
            if ((emote) && (emote.labels) && (emote.labels.length)) {
                emote.labels.forEach(function (label) {
                    if (labels.length) {
                        labels += ', ';
                    }
                    labels += label;
                });
            }
            return labels;
        }
    }])

    .filter("tags", [function () {
        return function (emote) {
            var tags = '';
            if ((emote) && (emote.tags) && (emote.tags.length)) {
                emote.tags.forEach(function (tag) {
                    if (tags.length) {
                        tags += ', ';
                    }
                    tags += tag;
                });
            }
            return tags;
        }
    }])

    .filter('pretty_version', [function () {
        return function (version) {
            var pretty = 'NONE';
            if (version) {
                pretty = version.major_number + '.' + version.minor_number + '.' + version.patch_number;
                while (pretty.lastIndexOf('.0.0') === pretty.length - 2) {
                    pretty = pretty.substr(0, pretty.length - 2);
                }
            }
            return pretty;
        }
    }])

    .filter("name", [function () {
        return function (named) {
            return (named) ? named.name : '';
        }
    }])

    .filter("name_list", [function () {
        return function (names) {
            var namesList = '';
            if ((names) && (names.length)) {
                names.forEach(function (name) {
                    if (namesList.length) {
                        namesList += ' | ';
                    }
                    namesList += name;
                });
            }
            return namesList;
        }
    }])

    .filter("pretty_score", [function () {
        return function (score) {
            return (score !== undefined) ? (score * 100).toFixed(2) : '';
        }
    }])

;