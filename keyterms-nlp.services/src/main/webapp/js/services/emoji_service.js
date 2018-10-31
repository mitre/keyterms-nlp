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

services.service('emojiService', [
    '$log', '$http', '$q',
    function ($log, $http, $q) {

        var service = {};

        service.define = function (text) {
            var deferred = $q.defer();
            if (text) {
                $http({
                    url: 'svc/emote/define',
                    method: 'get',
                    params: {
                        text: text
                    }
                }).then(
                    function (response) {
                        deferred.resolve(response.data);
                    }, function (errorResponse) {
                        $log.error('Error getting emote definition: ' + errorResponse);
                        deferred.resolve(undefined);
                    }
                );
            } else {
                deferred.resolve(undefined);
            }
            return deferred.promise;
        };

        service.tokenize = function (text) {
            var deferred = $q.defer();
            if (text) {
                $http({
                    url: 'svc/emote/tokenize',
                    method: 'POST',
                    params: {},
                    data: text
                }).then(
                    function (response) {
                        var tokens = response.data;
                        for (var t = 0; t < tokens.length; t++) {
                            tokens[t].index = t;
                        }
                        deferred.resolve(tokens);
                    }, function (errorResponse) {
                        $log.error('Error tokenizing text: ' + errorResponse);
                        deferred.resolve('');
                    }
                );
            } else {
                deferred.resolve([]);
            }
            return deferred.promise;
        };

        return service;
    }
]);