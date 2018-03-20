angular.
  module('clusters').
  factory('clustersService', function ($resource, config) {
    return $resource(
      config.api.cluster,
      null,
      {
        "get":   {"method": "GET",  "url": config.api.cluster + "/cluster/:id"},
        "save":   {"method": "POST",  "url": config.api.cluster + "/cluster"},
        "query":   {"method": "GET",  "url": config.api.cluster + "/clusters", "isArray": true},
        "list":   {"method": "GET",  "url": config.api.cluster + "/clusters", "isArray": true},
        "remove":   {"method": "DELETE",  "url": config.api.cluster + "/cluster/:id"},
        "delete":   {"method": "DELETE",  "url": config.api.cluster + "/cluster/:id"}
      }
    );
  });
