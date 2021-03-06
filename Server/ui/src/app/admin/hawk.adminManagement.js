'use strict';

angular
    .module('hawk.adminManagement', ['ui.router', 'datatables'])
    .config(['$stateProvider', function ($stateProvider, $state) {

        $stateProvider
            .state('index.admin', {
                url: "admin",
                abstract: true,
                templateUrl: "app/admin/views/index.html",
                data: {
                    pageTitle: 'Admin'
                },
                controller: "AdminController",
                controllerAs: "admin"
            })

        .state('index.admin.groups', {
            url: "/groups",
            data: {
                pageTitle: 'Group Management'
            },
            templateUrl: "app/admin/views/partials/_groups.html"
        })

        .state('index.admin.servers', {
            url: "/servers",
            data: {
                pageTitle: 'Server Management'
            },
            templateUrl: "app/admin/views/partials/_servers.html"

        })


        .state('index.admin.userGroups', {
            url: "/userGroups",
            data: {
                pageTitle: 'User Group Management'
            },
            templateUrl: "app/admin/views/partials/_userGroups.html"
        })

            .state('index.admin.users', {
                url: "/users",
                data: {
                    pageTitle: 'User Management'
                },
                templateUrl: "app/admin/views/partials/_users.html"
            })

        .state('index.admin.materials', {
            url: "/materials",
            data: {
                pageTitle: 'Material Repositories'
            },
            templateUrl: "app/admin/views/partials/_materials.html"
        })
                }]);
