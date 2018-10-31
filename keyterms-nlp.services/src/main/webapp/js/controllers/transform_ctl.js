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

controllers.controller("transformCtl", [
    '$scope', '$log', '$q', '$sce', '$filter', 'transformService',
    function ($scope, $log, $q, $sce, $filter, transformService) {

        $scope.LINE_BREAKS = /((\r\n?)|(\n))/;

        $scope.transformService = transformService;

        $scope.all = false;
        $scope.sources = [];
        $scope.targets = [];
        $scope.schemes = [];
        $scope.selectedSource = undefined;
        $scope.selectedTarget = undefined;
        $scope.selectedScheme = undefined;
        $scope.inputText = '';
        $scope.transformation = '';

        $scope.profile = transformService.NO_PROFILE;

        $scope.busy = false;

        $scope.profileText = function () {
            var inputText = ($scope.inputText) ? $scope.inputText.trim() : '';
            inputText = (inputText.length > 0) ? inputText : undefined;
            if (inputText) {
                $scope.transformService.profile(inputText)
                    .then(function (profile) {
                        $scope.profile = (profile)
                            ? profile
                            : $scope.transformService.NO_PROFILE;
                    });
            } else {
                $scope.profile = transformService.NO_PROFILE;
            }
        };

        $scope.percent = function (profile, entry) {
            var percent = 0;
            if ((profile) && (entry)) {
                var cp = entry.code_points;
                var tcp = profile.code_points;
                if (tcp !== 0) {
                    percent = Math.round(cp * 100 / tcp);
                }
            }
            return percent + '%';
        };

        $scope.$watch('transformService.sources', function () {
            $scope.showAllChange();
        }, true);

        $scope.showAllChange = function () {
            $scope.selectedSource = undefined;
            $scope.sources = $scope.transformService.getSources(
                $scope.all);
            $scope.srcChange();
        };

        $scope.srcChange = function () {
            $scope.selectedTarget = undefined;
            $scope.targets = $scope.transformService.getTargets(
                $scope.selectedSource, $scope.all);
            $scope.tgtChange();
        };

        $scope.tgtChange = function () {
            $scope.selectedScheme = undefined;
            $scope.schemes = $scope.transformService.getSchemes(
                $scope.selectedSource, $scope.selectedTarget, $scope.all);
        };

        $scope.submitButton = function (form) {
            form.input_text.$touched = true;
            $scope.doTransformation(form);
        };

        $scope.doTransformation = function (form) {
            $scope.busy = true;
            form.input_text.$setValidity('no_text', true);
            form.input_text.$setValidity('no_source', true);
            form.input_text.$setValidity('no_target', true);
            form.input_text.$setValidity('no_scheme', true);
            $scope.transformation = '';
            $scope.checkError().then(function (errorMessage) {
                if (!errorMessage) {
                    var inputText = ($scope.inputText) ? $scope.inputText.trim() : '';
                    inputText = (inputText.length > 0) ? inputText : undefined;
                    transformService.transform(
                        inputText,
                        transformService.getTransformKey(
                            $scope.selectedSource,
                            $scope.selectedTarget,
                            $scope.selectedScheme)
                    ).then(function (results) {
                        $scope.transformation = results;
                        $scope.busy = false;
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
            if (inputText) {
                var msg = '';
                if (!$scope.selectedSource) {
                    msg = 'no_source';
                }
                if ((msg.length === 0) && (!$scope.selectedTarget)) {
                    msg = 'no_target';
                }
                if (msg.length === 0) {
                    var key = transformService.getTransformKey(
                        $scope.selectedSource,
                        $scope.selectedTarget,
                        $scope.selectedScheme);
                    if (!key) {
                        msg = 'no_scheme';
                    }
                }
                deferred.resolve(msg);
            } else {
                deferred.resolve('no_text');
            }
            return deferred.promise;
        };

        $scope.getTransformation = function () {
            var text = $scope.transformation;
            if (text) {
                var html = '';
                text.trim().split($scope.LINE_BREAKS).forEach(function (p) {
                    if (p !== undefined) {
                        var t = (p) ? p.trim() : '&nbsp;';
                        html += '<p style="margin: 0">' + t + '</p>';
                    }
                });
                text = html;
            }
            return $sce.trustAsHtml(text);
        };

    }]);