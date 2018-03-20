(function() {
    var bioNimbuxBox = angular.module("bioNimbuxBox", [
      'ngRoute',
      'ngResource',
      'ngPatternRestrict',
      'providers',
      'datacenters',
      'clusters',
      'servers',
      'workflows'
    ]);

    fetchData().then(bootstrapApplication);

    function fetchData() {
        var initInjector = angular.injector(["ng"]);
        var $http = initInjector.get("$http");

        return $http.get("config.json").then(function(response) {
            bioNimbuxBox.constant("config", response.data);
        }, function(errorResponse) {
            alert(errorResponse);
        });
    }

    function bootstrapApplication() {
        angular.element(document).ready(function() {
            angular.bootstrap(document, ["bioNimbuxBox"]);
        });
    }
}());
