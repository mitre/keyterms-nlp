'use strict';

controllers.controller("analyzerCtl", [
    '$scope', '$log', '$q', '$sce', '$filter', 'analyzerService',
    function ($scope, $log, $q, $sce, $filter, analyzerService) {

        $scope.analyzerService = analyzerService;

        $scope.profiles = [];
        $scope.products = [];
        $scope.analyzer = undefined;

        $scope.file = undefined;

        $scope.product = undefined;

        $scope.profile = undefined;

        $scope.preview = undefined;

        $scope.busy = false;

        $(document).ready(function () {
            $.ajaxSetup({cache: false});
            analyzerService.profiles().then(function (data) {
                $scope.profiles = data;
            });
            analyzerService.products().then(function (data) {
                $scope.products = data;
            });
        });

        $scope.fileEvent = function (input) {
            $scope.file = input.files[0];
            $scope.profile = undefined;
            $scope.preview = undefined;
            $scope.$apply();
        };

        $scope.submitButton = function () {
            if ($scope.file) {
                $scope.busy = true;
                $scope.profile = [];
                analyzerService.profile($scope.file, $scope.analyzer)
                    .then(function (result) {
                        $scope.profile = result;
                        analyzerService.preview($scope.file, $scope.analyzer)
                            .then(function (result) {
                                $scope.preview = (result) ? result : 'No preview available.';
                                $scope.busy = false;
                            });
                    });
            }
        };

    }]);