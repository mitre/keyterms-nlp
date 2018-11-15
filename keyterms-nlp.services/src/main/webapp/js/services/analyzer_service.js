"use strict";

services.service('analyzerService', [
    '$log', '$http', '$q',
    function ($log, $http, $q) {

        var service = {
            defaultAnalyzer: 'Default Analyzer'
        };

        service.profiles = function () {
            var deferred = $q.defer();
            $http({
                url: 'svc/analyzer/profiles',
                method: 'get',
                params: {}
            }).then(
                function (response) {
                    deferred.resolve(response.data);
                }, function (errorResponse) {
                    $log.error('Error getting analyzer profiles: ' + errorResponse);
                    deferred.resolve([]);
                }
            );
            return deferred.promise;
        };

        service.products = function () {
            var deferred = $q.defer();
            $http({
                url: 'svc/analyzer/products',
                method: 'get',
                params: {}
            }).then(
                function (response) {
                    deferred.resolve(response.data);
                }, function (errorResponse) {
                    $log.error('Error getting analyzer products: ' + errorResponse);
                    deferred.resolve([]);
                }
            );
            return deferred.promise;
        };

        service.getDefaultAnalyzer = function () {
            $http({
                url: 'svc/analyzer/default_analyzer',
                method: 'get',
                params: {}
            }).then(
                function (response) {
                    service.defaultAnalyzer = response.data;
                }, function (errorResponse) {
                    $log.error('Error getting default analyzer: ' + errorResponse);
                }
            );
        };

        service.profile = function (file, analyzer) {
            var deferred = $q.defer();
            if (analyzer === '*****') {
                $http({
                    url: 'svc/analyzer/full_profile',
                    method: 'post',
                    params: {},
                    data: file
                }).then(
                    function (response) {
                        deferred.resolve(response.data);
                    }, function (errorResponse) {
                        $log.error('Error profiling data: ' + errorResponse);
                        deferred.resolve(undefined);
                    }
                );
            } else {
                $http({
                    url: 'svc/analyzer/profile',
                    method: 'post',
                    params: {
                        analyzer: analyzer
                    },
                    data: file
                }).then(
                    function (response) {
                        var result = [];
                        var key = (analyzer) ? analyzer : service.defaultAnalyzer;
                        response.data.forEach(function (record) {
                            result.push({
                                id: key,
                                value: record
                            });
                        });
                        deferred.resolve(result);
                    }, function (errorResponse) {
                        $log.error('Error profiling data: ' + errorResponse);
                        deferred.resolve(undefined);
                    }
                );
            }
            return deferred.promise;
        };

        service.preview = function (file, analyzer) {
            var deferred = $q.defer();
            $http({
                url: 'svc/analyzer/preview',
                method: 'post',
                params: {
                    analyzer: ('*****' !== analyzer) ? analyzer : undefined
                },
                data: file
            }).then(
                function (response) {
                    deferred.resolve(response.data);
                }, function (errorResponse) {
                    $log.error('Error previewing data: ' + errorResponse);
                    deferred.resolve(undefined);
                }
            );
            return deferred.promise;
        };

        service.getDefaultAnalyzer();

        return service;
    }
]);