angular.
  module('workflows').
  controller('WorkflowsListController', function WorkflowsListController($http, $filter, workflowsService) {
    var self = this;

    self.workflows = workflowsService.query();
    
    self.run = function(id) {
    	console.log("Running " + id + " workflow ...")
        workflowsService.run({id:id}, function(){
          alert("Workflow " + id + " is running ...");
        });
      }

  }).
  controller('WorkflowsFormController', function WorkflowsFormController($http, $routeParams, $location, config, workflowsService) {
    var self = this;

    self.workflow = {};

    if ($routeParams.id) {
      self.workflow = workflowsService.get({id:$routeParams.id}, function() {
        if (self.workflow.id == undefined) {
          $location.url('/workflows/');
        }
      });
    }

    self.save = function() {
      console.log(self.workflow);
      self.workflow = workflowsService.save(self.workflow, function(){
        $location.url('/workflow/' + self.workflow.id);
      });
    }

    self.remove = function() {
      console.log(self.workflow);
      self.workflow = workflowsService.remove({id:self.workflow.id}, function(){
        $location.url('/workflow/');
      });
    }
  });
