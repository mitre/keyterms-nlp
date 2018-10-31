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

controllers.controller("isoCtl", [
    '$scope', '$log', '$q', '$sce', '$filter', 'isoService',
    function ($scope, $log, $q, $sce, $filter, isoService) {

        $scope.isoService = isoService;

        $scope.searchType = 'country';
        $scope.searchText = '';
        $scope.resultsMessage = '';

        $scope.busy = false;

        $scope.typeChange = function (form) {
            $scope.resultsMessage = '';
            $scope.isoService.searchResults.length = 0;
            if (($scope.searchText) && ($scope.searchText.trim().length > 0)) {
                $scope.doSearch(form);
            }
        };

        $scope.getStandard = function () {
            if ($scope.searchType === 'country') {
                return 'ISO-3166-1';
            }
            if ($scope.searchType === 'language') {
                return 'ISO-639-3';
            }
            if ($scope.searchType === 'script') {
                return 'ISO-15924';
            }
            return 'Unknown';
        };

        $scope.checkError = function () {
            var deferred = $q.defer();
            var searchText = ($scope.searchText) ? $scope.searchText.trim() : '';
            searchText = (searchText.length > 0) ? searchText : undefined;
            if (searchText) {
                deferred.resolve('');
            } else {
                deferred.resolve('empty_search');
            }
            return deferred.promise;
        };

        $scope.submitButton = function (form) {
            form.search_field.$touched = true;
            $scope.doSearch(form);
        };

        $scope.doSearch = function (form) {
            $scope.busy = true;
            form.search_field.$setValidity('empty_search', true);
            $scope.resultsMessage = '';
            $scope.checkError().then(function (errorMessage) {
                if (!errorMessage) {
                    var type = $scope.searchType;
                    var query = $scope.searchText.trim();
                    $scope.isoService.lookup(type, query).then(function (results) {
                        var numResults = ((results) && (results.length)) ? results.length : 0;
                        $scope.resultsMessage = numResults + ' ' + type + '(s) for "' + query + '"';
                        $scope.busy = false;
                    }).then(function () {
                        $scope.busy = false;
                    });
                } else {
                    form.search_field.$setValidity(errorMessage, false);
                    $scope.busy = false;
                }
            });
        };

    }]);