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

'use strict';

controllers.controller("emojiCtl", [
    '$scope', '$log', '$q', '$sce', '$filter', 'emojiService',
    function ($scope, $log, $q, $sce, $filter, emojiService) {

        $scope.emojiService = emojiService;

        $scope.inputText = 'I am very 🙂 that you are not :( in 🇫🇯.\nEnjoy the <>< :D!';

        $scope.tokens = [];
        $scope.token = undefined;

        $scope.busy = false;

        $scope.submitButton = function (form) {
            form.input_text.$touched = true;
            $scope.doTokenize(form);
        };

        $scope.doTokenize = function (form) {
            $scope.busy = true;
            form.input_text.$setValidity('no_text', true);
            $scope.transformation = '';
            $scope.checkError().then(function (errorMessage) {
                if (!errorMessage) {
                    $scope.tokens = [];
                    $scope.token = undefined;
                    var inputText = ($scope.inputText) ? $scope.inputText.trim() : '';
                    inputText = (inputText.length > 0) ? inputText : undefined;
                    emojiService.tokenize(inputText)
                        .then(function (results) {
                            $scope.tokens = results;
                            $scope.busy = false;
                            $log.info('Got ' + $scope.tokens.length + ' tokens.');
                        });
                } else {
                    $log.warn('Operation is not valid: ' + errorMessage);
                    form.input_text.$setValidity(errorMessage, false);
                    $scope.busy = false;
                }
            });
        };

        $scope.checkError = function () {
            var deferred = $q.defer();
            var inputText = ($scope.inputText) ? $scope.inputText.trim() : '';
            inputText = (inputText.length > 0) ? inputText : undefined;
            if (!inputText) {
                deferred.resolve('no_text');
            } else {
                deferred.resolve('');
            }
            return deferred.promise;
        };

        $scope.selectToken = function (token) {
            $scope.token = token;
        };

        $scope.replaceToken = function (type, text) {
            if (($scope.token) && (text)) {
                $scope.emojiService.define(text).then(function (data) {
                    if (data) {
                        var token = $scope.token;
                        token.type = type;
                        token.text = data.text;
                        token.data = data;
                        var newTokens = [];
                        for (var t = 0; t < $scope.tokens.length; t++) {
                            if (t === token.index) {
                                newTokens.push(token);
                            } else {
                                newTokens.push($scope.tokens[t]);
                            }
                        }
                        $scope.tokens = newTokens;
                        $scope.token = token;
                    }
                });
            }
        };

    }]);