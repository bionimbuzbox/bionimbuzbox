angular.
  module('datacenters').
  factory('datacentersService', function ($resource, config) {
    return $resource(
      config.api.datacenter,
      null,
      {
        "get":   {"method": "GET",  "url": config.api.datacenter + "/datacenter/:id"},
        "save":   {"method": "POST",  "url": config.api.datacenter + "/datacenter"},
        "query":   {"method": "GET",  "url": config.api.datacenter + "/datacenters", "isArray": true},
        "list":   {"method": "GET",  "url": config.api.datacenter + "/datacenters", "isArray": true},
        "remove":   {"method": "DELETE",  "url": config.api.datacenter + "/datacenter/:id"},
        "delete":   {"method": "DELETE",  "url": config.api.datacenter + "/datacenter/:id"}
      }
    );
  });
