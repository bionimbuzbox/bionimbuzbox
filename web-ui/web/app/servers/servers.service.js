angular.
  module('servers').
  factory('serversService', function ($resource, config) {
    return $resource(
      config.api.server,
      null,
      {
        "get":   {"method": "GET",  "url": config.api.server + "/server/:id"},
        "save":   {"method": "POST",  "url": config.api.server + "/server"},
        "query":   {"method": "GET",  "url": config.api.server + "/servers", "isArray": true},
        "list":   {"method": "GET",  "url": config.api.server + "/servers", "isArray": true},
        "remove":   {"method": "DELETE",  "url": config.api.server + "/server/:id"},
        "delete":   {"method": "DELETE",  "url": config.api.server + "/server/:id"}
      }
    );
  });
