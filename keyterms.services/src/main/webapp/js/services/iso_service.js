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

services.service('isoService', [
    '$log', '$http', '$q',
    function ($log, $http, $q) {

        var service = {
            searchResults: []
        };

        service.sortResults = function (type) {
            service.searchResults.sort(function (r1, r2) {
                var diff = 0;
                if (type === 'language') {
                    if (r1.name !== r2.name) {
                        diff = (r1.name > r2.name) ? +1 : -1;
                    }
                    if (diff === 0) {
                        diff = (r1.code > r2.code) ? +1 : -1;
                    }
                } else {
                    if (r1.number !== r2.number) {
                        diff = (r1.number > r2.number) ? +1 : -1;
                    }
                }
                return diff;
            });
            service.searchResults.forEach(function (result) {
                if (result.aliases) {
                    result.aliases.sort(function (a1, a2) {
                        var diff = 0;
                        if (a1 !== a2) {
                            diff = (a1 > a2) ? +1 : -1;
                        }
                        return diff;
                    });
                }
            });
        };

        service.lookup = function (type, query) {
            var deferred = $q.defer();
            service.searchResults.length = 0;
            $http({
                url: 'svc/iso/' + type,
                method: 'GET',
                params: {
                    query: query
                }
            }).then(
                function (response) {
                    service.searchResults = response.data;
                    service.searchResults.forEach(function (r) {
                        r.resultType = type;
                    });
                    service.sortResults(type);
                    deferred.resolve(service.searchResults);
                }, function (errorResponse) {
                    $log.error('Error searching country definitions: ' + errorResponse);
                    deferred.resolve(service.searchResults);
                }
            );
            return deferred.promise;
        };

        return service;
    }
]);