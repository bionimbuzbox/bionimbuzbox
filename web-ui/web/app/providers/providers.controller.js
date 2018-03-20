angular.
  module('providers').
  controller('ProvidersListController', function ProvidersListController($http, $filter, providersService) {
    var self = this;

    self.providers = providersService.query();

  }).
  controller('ProvidersFormController', function ProvidersFormController($http, $routeParams, $location, config, providersService) {
    var self = this;

    self.provider = {};
    self.provider.meta = {};
    self.config = config;

    if ($routeParams.id) {
      self.provider = providersService.get({id:$routeParams.id}, function() {
        if (self.provider.id == undefined) {
          $location.url('/providers/');
        }
      });
    }

    self.save = function() {
      console.log(self.provider);
      self.provider = providersService.save(self.provider, function(){
        $location.url('/provider/' + self.provider.id);
      });
    }

    self.remove = function() {
      console.log(self.provider);
      self.provider = providersService.remove({id:self.provider.id}, function(){
        $location.url('/provider/');
      });
    }

    self.addMeta = function(key, value) {
      self.provider.meta[key] = value;
    }

    self.removeMeta = function(key) {
      delete self.provider.meta[key];
    }

    self.showAsMeta = function(key) {
      if (self.provider.type != undefined) {
        var json = self.config.providerTypes[self.provider.type].meta;
        for(var i = 0; i < json.length; i++) {
          var obj = json[i];
          if (obj.key == key) {
            return false;
          }
        }
      }
      var json = self.config.provider.meta;
      for(var i = 0; i < json.length; i++) {
        var obj = json[i];
        if (obj.key == key) {
          return false;
        }
      }
      return true;
    }

  });

/*
  angular.
    module('providers').
    controller('ProvidersFormController', function ProvidersFormController($http, $routeParams, providersConfig) {
      var self = this;

      self.id = $routeParams.id;
      self.type = $routeParams.type;
      self.provider = {};
      self.provider.type = self.type.toUpperCase();
      self.messages = [];

      providersConfig.getData().then(function(response) {
        self.config = response.data[self.type];
      });

      if (self.id) {
        $http.get('api/v1/provider/' + self.id).then(function(response) {
          self.provider = response.data;
        });
      }

      self.load = function() {}

      self.save = function() {
        $http.post('api/v1/provider/', self.provider).then(function(response) {
          self.provider = response.data;
          self.messages.push("OK");
        });
      }

    });
*/
