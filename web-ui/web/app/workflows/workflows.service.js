angular.
  module('workflows').
  factory('workflowsService', function ($resource, config) {
    return $resource(
      config.api.workflow,
      null,
      {
          "get":   {"method": "GET",  "url": config.api.workflow + "/workflow/:id"},
          "save":   {"method": "POST",  "url": config.api.workflow + "/workflow"},
          "query":   {"method": "GET",  "url": config.api.workflow + "/workflows", "isArray": true},
          "list":   {"method": "GET",  "url": config.api.workflow + "/workflows", "isArray": true},
          "remove":   {"method": "DELETE",  "url": config.api.workflow + "/workflow/:id"},
          "delete":   {"method": "DELETE",  "url": config.api.workflow + "/workflow/:id"},
          "run":   {"method": "POST",  "url": config.api.workflow + "/workflow/:id/run", "params": {id:'@id'}}
      }
    );
  });
