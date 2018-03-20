angular.
  module('clusters').
  controller('ClustersListController', function ClustersListController($http, $q, $controller, $scope, clustersService, datacentersService, providersService) {
      angular.extend(this, $controller('DefaultListController', {$scope: $scope}));
      var self = this;

      $q.all([
         providersService.query().$promise,
         datacentersService.query().$promise,
         clustersService.query().$promise
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
  controller('ClustersFormController', function ClustersFormController($http, $routeParams, $location, config, clustersService, datacentersService) {
    var self = this;

    self.cluster = {};
    self.cluster.meta = {};
    self.config = config;
    self.datacenters = datacentersService.list();

    if ($routeParams.id) {
      self.cluster = clustersService.get({id:$routeParams.id}, function() {
        if (self.cluster.id == undefined) {
          $location.url('/clusters/');
        }
      });
    }

    self.save = function() {
      console.log(self.cluster);
      self.cluster = clustersService.save(self.cluster, function(){
        $location.url('/cluster/' + self.cluster.id);
      });
    }

    self.remove = function() {
      console.log(self.cluster);
      self.cluster = clustersService.remove({id:self.cluster.id}, function(){
        $location.url('/cluster/');
      });
    }

    self.addMeta = function(key, value) {
      self.cluster.meta[key] = value;
    }

    self.removeMeta = function(key) {
      delete self.cluster.meta[key];
    }

    self.showAsMeta = function(key) {
        var json = self.config.cluster.meta;
        for(var i = 0; i < json.length; i++) {
          var obj = json[i];
          if (obj.key == key) {
            return false;
          }
        }
        return true;
      }

  });
