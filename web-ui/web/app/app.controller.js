angular.
  module('bioNimbuxBox').
  controller('DefaultListController', function DefaultListController($http, $filter) {
    var self = this;

    self.filterByID = function(id, list){
      console.log("id: " + id + " list: " + list);
      var itens = $filter('filter')(list, {id : id});
      return itens[0];
     }
  });
