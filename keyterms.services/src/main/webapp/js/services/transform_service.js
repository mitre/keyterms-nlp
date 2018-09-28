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

services.service('transformService', [
    '$log', '$http', '$q',
    function ($log, $http, $q) {

        var service = {
            transformKeys: [],
            keyTree: {},
            sources: [],
            customTree: {},
            customSources: []
        };

        service.NO_PROFILE = {code_points: 0, entries: []};

        service.sortTextKeys = function (textArray) {
            textArray.sort(function (k1, k2) {
                var diff = 0;
                var l1 = (k1.text) ? k1.text.toLowerCase() : '';
                var l2 = (k2.text) ? k2.text.toLowerCase() : '';
                if (l1 !== l2) {
                    diff = (l1 > l2) ? +1 : -1;
                }
                return diff;
            });
        };

        service.sortText = function (textArray) {
            textArray.sort(function (k1, k2) {
                var diff = 0;
                var l1 = (k1) ? k1.toLowerCase() : '';
                var l2 = (k2) ? k2.toLowerCase() : '';
                if (l1 !== l2) {
                    diff = (l1 > l2) ? +1 : -1;
                }
                return diff;
            });
        };

        service.sortData = function () {
            service.sortTextKeys(service.transformKeys);
        };

        service.populateTrees = function () {
            service.keyTree = {};
            service.transformKeys.forEach(function (key) {
                var custom = key.custom;
                var src = key.source.text;
                var tgt = key.target.text;
                var skm = key.scheme.text;
                if (!service.keyTree[src]) {
                    service.keyTree[src] = {};
                }
                if (!service.keyTree[src][tgt]) {
                    service.keyTree[src][tgt] = {};
                }
                if (!service.keyTree[src][tgt][skm]) {
                    service.keyTree[src][tgt][skm] = key;
                }
                if (custom) {
                    if (!service.customTree[src]) {
                        service.customTree[src] = {};
                    }
                    if (!service.customTree[src][tgt]) {
                        service.customTree[src][tgt] = {};
                    }
                    if (!service.customTree[src][tgt][skm]) {
                        service.customTree[src][tgt][skm] = key;
                    }
                }
            });
            service.sources = [];
            for (var src in service.keyTree) {
                if (service.keyTree.hasOwnProperty(src)) {
                    service.sources.push(src);
                }
            }
            service.sortText(service.sources);
            service.customSources = [];
            for (var customSrc in service.customTree) {
                if (service.customTree.hasOwnProperty(customSrc)) {
                    service.customSources.push(customSrc);
                }
            }
            service.sortText(service.customSources);
        };

        service.getSources = function (all) {
            return (all) ? service.sources : service.customSources;
        };

        service.getTargets = function (src, all) {
            var targets = [];
            if (all) {
                if ((src) && (service.keyTree[src])) {
                    for (var tgt in service.keyTree[src]) {
                        if (service.keyTree[src].hasOwnProperty(tgt)) {
                            targets.push(tgt);
                        }
                    }
                }
            } else {
                if ((src) && (service.customTree[src])) {
                    for (var customTgt in service.customTree[src]) {
                        if (service.customTree[src].hasOwnProperty(customTgt)) {
                            targets.push(customTgt);
                        }
                    }
                }
            }
            service.sortText(targets);
            return targets;
        };

        service.getSchemes = function (src, tgt, all) {
            var schemes = [];
            if (all) {
                if ((src) && (tgt) && (service.keyTree[src]) && (service.keyTree[src][tgt])) {
                    for (var scheme in service.keyTree[src][tgt]) {
                        if (service.keyTree[src][tgt].hasOwnProperty(scheme)) {
                            schemes.push(scheme);
                        }
                    }
                }
            } else {
                if ((src) && (tgt) && (service.customTree[src]) && (service.customTree[src][tgt])) {
                    for (var customScheme in service.customTree[src][tgt]) {
                        if (service.customTree[src][tgt].hasOwnProperty(customScheme)) {
                            schemes.push(customScheme);
                        }
                    }
                }
            }
            service.sortText(schemes);
            return schemes;
        };

        service.profile = function (text) {
            var deferred = $q.defer();
            if (text) {
                $http({
                    url: 'svc/transform/profile_text',
                    method: 'POST',
                    data: text
                }).then(
                    function (response) {
                        deferred.resolve(response.data);
                    }, function (errorResponse) {
                        $log.error('Error profiling text: ' + errorResponse);
                        deferred.resolve(service.NO_PROFILE);
                    }
                );
            } else {
                deferred.resolve(service.NO_PROFILE);
            }
            return deferred.promise;
        };

        service.getTransformKey = function (src, tgt, scheme) {
            var key = undefined;
            if ((src) && (tgt) && (service.keyTree[src]) && (service.keyTree[src][tgt])) {
                var variant = (scheme) ? scheme : '';
                key = service.keyTree[src][tgt][variant];
            }
            return key;
        };

        service.getTransformKeys = function (force) {
            var deferred = $q.defer();
            if ((!force) && (service.transformKeys.length > 0)) {
                deferred.resolve(service.transformKeys);
            } else {
                $http.get('svc/transform/transform_keys').then(
                    function (response) {
                        service.transformKeys = response.data;
                        service.sortData();
                        service.populateTrees();
                        $log.info('Found ' + service.transformKeys.length + ' transformation keys.');
                        deferred.resolve(service.transformKeys);
                    }, function (errorResponse) {
                        $log.error('Error getting text transformation keys: ' + errorResponse);
                        deferred.resolve(service.transformKeys);
                    }
                );
            }
            return deferred.promise;
        };

        service.transform = function (text, key) {
            var deferred = $q.defer();
            if ((text) && (key)) {
                $http({
                    url: 'svc/transform/transform',
                    method: 'POST',
                    params: {
                        key: key.text
                    },
                    data: text
                }).then(
                    function (response) {
                        deferred.resolve(response.data);
                    }, function (errorResponse) {
                        $log.error('Error transforming text: ' + errorResponse);
                        deferred.resolve('');
                    }
                );
            } else {
                deferred.resolve('');
            }
            return deferred.promise;
        };

        service.getTransformKeys();
        return service;
    }
]);