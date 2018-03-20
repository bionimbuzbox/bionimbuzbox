angular.
  module('servers').
  controller('ServersListController', function ServersListController($http, $q, $controller, $scope, serversService, clustersService, datacentersService, providersService) {
    angular.extend(this, $controller('DefaultListController', {$scope: $scope}));
    var self = this;

    $q.all([
       providersService.query().$promise,
       datacentersService.query().$promise,
       clustersService.query().$promise,
       serversService.query().$promise
    ]).then(function(data) {
       self.providers = data[0];
       self.datacenters = data[1];
       self.clusters = data[2];
       self.servers = data[3];
    }).catch(function(data) {
      console.log("error ...");
    }).finally(function () {
      console.log("finally ...");
    });
  }).
  controller('ServersFormController', function ServersFormController($http, $routeParams, $location, config, serversService, clustersService) {
    var self = this;

    self.server = {};
    self.server.meta = {};
    self.config = config;
    self.clusters = clustersService.query();

    if ($routeParams.id) {
      self.server = serversService.get({id:$routeParams.id}, function() {
        if (self.server.id == undefined) {
          $location.url('/servers/');
        }
      });
    }

    self.save = function() {
      console.log(self.server);
      self.server = serversService.save(self.server, function(){
        $location.url('/server/' + self.server.id);
      });
    }

    self.remove = function() {
      console.log(self.server);
      self.server = serversService.remove({id:self.server.id}, function(){
        $location.url('/server/');
      });
    }

    self.addMeta = function(key, value) {
      self.server.meta[key] = value;
    }

    self.removeMeta = function(key) {
      delete self.server.meta[key];
    }

    self.showAsMeta = function(key) {
      var json = self.config.server.meta;
      for(var i = 0; i < json.length; i++) {
        var obj = json[i];
        if (obj.key == key) {
          return false;
        }
      }
      return true;
    }

  });
