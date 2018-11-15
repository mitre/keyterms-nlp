'use strict';

controllers.controller("lidCtl", [
    '$scope', '$log', '$q', '$sce', '$filter', 'lidService',
    function ($scope, $log, $q, $sce, $filter, lidService) {

        $scope.lidService = lidService;

        $scope.profiles = [];
        $scope.products = [];
        $scope.analyzer = undefined;

        $scope.text = '';

        $scope.product = undefined;

        $scope.profile = undefined;

        $scope.busy = false;

        $(document).ready(function () {
            $.ajaxSetup({cache: false});
            lidService.profiles().then(function (data) {
                $scope.profiles = data;
            });
            lidService.products().then(function (data) {
                $scope.products = data;
            });
        });

        $scope.submitButton = function () {
            if (($scope.text) && ($scope.text.trim())) {
                $scope.busy = true;
                $scope.profile = [];
                lidService.profile($scope.text.trim(), $scope.analyzer)
                    .then(function (result) {
                        $scope.profile = result;
                        $scope.busy = false;
                    });
            }
        };

    }]);