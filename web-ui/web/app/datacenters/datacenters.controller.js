angular.
  module('datacenters').
  controller('DatacentersListController', function DatacentersListController($http, $q, $controller, $scope, datacentersService, providersService) {
    angular.extend(this, $controller('DefaultListController', {$scope: $scope}));
    var self = this;

    $q.all([
       providersService.query().$promise,
       datacentersService.query().$promise
    ]).then(function(data) {
       self.providers = data[0];
       self.datacenters = data[1];
       self.clusters = data[2];
    }).catch(function(data) {
      console.log("error ...");
    }).finally(function () {
      console.log("finally ...");
    });
  }).
  controller('DatacentersFormController', function DatacentersFormController($http, $routeParams, $location, config, datacentersService, providersService) {
    var self = this;

    self.datacenter = {};
    self.datacenter.meta = {};
    self.config = config;
    self.providers = providersService.query();

    if ($routeParams.id) {
      self.datacenter = datacentersService.get({id:$routeParams.id}, function() {
        if (self.datacenter.id == undefined) {
          $location.url('/datacenters/');
        }
      });
    }

    self.save = function() {
      console.log(self.datacenter);
      self.datacenter = datacentersService.save(self.datacenter, function(){
        $location.url('/datacenter/' + self.datacenter.id);
      });
    }

    self.remove = function() {
      console.log(self.datacenter.id);
      self.datacenter = datacentersService.remove({id:self.datacenter.id}, function(){
        $location.url('/datacenter');
      });

    }

    self.addMeta = function(key, value) {
      self.datacenter.meta[key] = value;
    }

    self.removeMeta = function(key) {
      delete self.datacenter.meta[key];
    }

    self.showAsMeta = function(key) {
      var json = self.config.datacenter.meta;
      for(var i = 0; i < json.length; i++) {
        var obj = json[i];
        if (obj.key == key) {
          return false;
        }
      }
      return true;
    }

  });
