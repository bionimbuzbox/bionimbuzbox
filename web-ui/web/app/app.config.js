angular.
  module('bioNimbuxBox').
  config(['$locationProvider', '$routeProvider',
    function config($locationProvider, $routeProvider) {
      $locationProvider.hashPrefix('!');

      $routeProvider.
        when('/federation', {
          template: '<federation></federation>'
        }).
        when('/datacenters', {
          template: '<datacenters-list></datacenters-list>'
        }).
        when('/datacenter/:id?', {
          template: '<datacenters-form></datacenters-form>'
        }).
        when('/clusters', {
          template: '<clusters-list></clusters-list>'
        }).
        when('/cluster/:id?', {
          template: '<clusters-form></clusters-form>'
        }).
        when('/servers', {
          template: '<servers-list></servers-list>'
        }).
        when('/server/:id?', {
          template: '<servers-form></servers-form>'
        }).
        when('/workflows', {
          template: '<workflows-list></workflows-list>'
        }).
        when('/workflow/:id?', {
          template: '<workflows-form></workflows-form>'
        }).
        when('/providers', {
          template: '<providers-list></providers-list>'
        }).
        /*
        when('/provider/:type/:id?', {
          template: '<providers-form></providers-form>'
        }).
        */
        when('/provider/:id?', {
          template: '<providers-form></providers-form>'
        }).
        otherwise('/');
    }
  ]);
