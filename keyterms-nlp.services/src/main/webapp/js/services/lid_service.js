"use strict";

services.service('lidService', [
    '$log', '$http', '$q',
    function ($log, $http, $q) {

        var service = {
            defaultAnalyzer: 'Default Analyzer'
        };

        service.profiles = function () {
            var deferred = $q.defer();
            $http({
                url: 'svc/lid/profiles',
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
                url: 'svc/lid/products',
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
                url: 'svc/lid/default_analyzer',
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

        service.profile = function (text, analyzer) {
            var deferred = $q.defer();
            if (analyzer === '*****') {
                $http({
                    url: 'svc/lid/full_profile',
                    method: 'post',
                    params: {},
                    data: text
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
                    url: 'svc/lid/profile',
                    method: 'post',
                    params: {
                        analyzer: analyzer
                    },
                    data: text
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

        service.getDefaultAnalyzer();

        return service;
    }
]);