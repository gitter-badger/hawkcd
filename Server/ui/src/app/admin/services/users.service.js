'use strict';

angular
    .module('hawk.adminManagement')
    .factory('adminService', ['$http', '$q', 'CONSTANTS', 'websocketSenderService', 'jsonHandlerService', function($http, $q, CONSTANTS, websocketSenderService, jsonHandlerService) {
        var adminService = this;

        adminService.getAllUserGroups = function() {
            var methodName = "getAll";
            var className = "UserGroupService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"\", \"object\": \"\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        adminService.getAllUserGroupDTOs = function() {
            var methodName = "getAllUserGroups";
            var className = "UserGroupService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"\", \"object\": \"\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        adminService.updateUserGroupDTO = function(userGroup) {
            var methodName = "updateUserGroupDto";
            var className = "UserGroupService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"net.hawkengine.model.dto.UserGroupDto\", \"object\": " + JSON.stringify(userGroup) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        adminService.deleteUserGroup = function(id) {
            var methodName = "delete";
            var className = "UserGroupService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + id + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        adminService.getAllUsers = function() {
            var methodName = "getAll";
            var className = "UserService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"\", \"object\": \"\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        adminService.addUser = function(user) {
            var methodName = "addUserWithoutProvider";
            var className = "UserService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"net.hawkengine.model.User\", \"object\": " + JSON.stringify(user) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        adminService.addUserGroup = function(userGroup) {
            var methodName = "addUserGroupDto";
            var className = "UserGroupService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"net.hawkengine.model.dto.UserGroupDto\", \"object\": " + JSON.stringify(userGroup) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        adminService.updateUser = function(user) {
            var methodName = "update";
            var className = "UserService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"net.hawkengine.model.User\", \"object\": " + JSON.stringify(user) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        adminService.updateUserPassword = function(user, newUserPassword,oldPassword) {
            var methodName = "changeUserPassword";
            var className = "UserService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"net.hawkengine.model.dto.UserDto\", \"object\": " + JSON.stringify(user) + "}, " +
                "{\"packageName\": \"java.lang.String\", \"object\": \"" + newUserPassword + "\"}, " +
                "{\"packageName\": \"java.lang.String\", \"object\": \"" + oldPassword + "\"}"
            ];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        adminService.deleteUser = function(id) {
            var methodName = "delete";
            var className = "UserService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + id + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        adminService.assignUser = function(user, group) {
            var methodName = "assignUserToGroup";
            var className = "UserGroupService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"net.hawkengine.model.User\", \"object\": " + JSON.stringify(user) + "}, " +
                "{\"packageName\": \"net.hawkengine.model.dto.UserGroupDto\", \"object\": " + JSON.stringify(group) + "}"
            ];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        adminService.unassignUser = function(user, group) {
            var methodName = "unassignUserFromGroup";
            var className = "UserGroupService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"net.hawkengine.model.User\", \"object\": " + JSON.stringify(user) + "}, " +
                "{\"packageName\": \"net.hawkengine.model.dto.UserGroupDto\", \"object\": " + JSON.stringify(group) + "}"
            ];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        // var usersEndPoint = CONSTANTS.BASE_URL + CONSTANTS.ACCOUNT + CONSTANTS.USERS + '/';
        // var registerEndPoint = CONSTANTS.BASE_URL + CONSTANTS.ACCOUNT + '/Register';
        //
        // var token = window.localStorage['accessToken'];
        //
        // adminService.registerUser = function (user, token) {
        //     var defer = $q.defer();
        //
        //     $http.post(registerEndPoint, user, {
        //             headers: {
        //                 'Authorization': 'bearer ' + token
        //             }
        //         })
        //         .success(function (res) {
        //             defer.resolve(res);
        //         })
        //         .error(function (err, status) {
        //             defer.reject(err);
        //         });
        //
        //     return defer.promise;
        // };
        //
        // adminService.getAllUsers = function(token) {
        //     var defer = $q.defer();
        //
        //     $http.get(usersEndPoint, {
        //             headers: {
        //                 'Authorization': 'bearer ' + token
        //             }
        //         })
        //         .success(function(res) {
        //             defer.resolve(res);
        //         })
        //         .error(function(err, status) {
        //             defer.reject(err);
        //         });
        //
        //     return defer.promise;
        // };
        // adminService.getUser = function(id, token) {
        //     var defer = $q.defer();
        //
        //     $http.get(usersEndPoint + id, {
        //             headers: {
        //                 'Authorization': 'bearer ' + token
        //             }
        //         })
        //         .success(function(res) {
        //             defer.resolve(res);
        //         })
        //         .error(function(err, status) {
        //             defer.reject(err);
        //         });
        //
        //     return defer.promise;
        // };
        // adminService.deleteUser = function(id, token) {
        //     var defer = $q.defer();
        //
        //     $http.delete(usersEndPoint + id, {
        //             headers: {
        //                 'Authorization': 'bearer ' + token
        //             }
        //         })
        //         .success(function(res) {
        //             defer.resolve(res);
        //         })
        //         .error(function(err, status) {
        //             defer.reject(err);
        //         });
        //
        //     return defer.promise;
        // };

        return adminService;
    }]);
