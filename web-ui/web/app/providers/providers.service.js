angular.
  module('providers').
  factory('providersService', function ($resource, config) {
    return $resource(
      config.api.provider,
      null,
      {
        "get":   {"method": "GET",  "url": config.api.provider + "/provider/:id"},
        "save":   {"method": "POST",  "url": config.api.provider + "/provider"},
        "query":   {"method": "GET",  "url": config.api.provider + "/providers", "isArray": true},
        "list":   {"method": "GET",  "url": config.api.provider + "/providers", "isArray": true},
        "remove":   {"method": "DELETE",  "url": config.api.provider + "/provider/:id"},
        "delete":   {"method": "DELETE",  "url": config.api.provider + "/provider/:id"}
      }
    );
  });
