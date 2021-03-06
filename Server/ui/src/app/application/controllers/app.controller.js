'use strict';

angular
    .module('hawk')
    /* Setup App Main Controller */
    .controller('AppController', ['$scope', '$rootScope', 'CONSTANTS', 'loginService', 'viewModel', 'accountService', 'profileService', 'authDataService', 'pipeConfig', '$auth', "$location", "$http",
        function($scope, $rootScope, CONSTANTS, loginService, viewModel, accountService, profileService, authDataService, pipeConfig, $auth, $location, $http) {
            $scope.$on('$viewContentLoaded', function() {
                //App.initComponents(); // init core components
                Layout.init(); //  Init entire layout(header, footer, sidebar, etc) on page load if the partials included in server side instead of loading with ng-include directive
            });

            $scope.userEmail = localStorage['email'];

            // console.log(localStorage);
            $scope.latestCommit = "";

            $scope.pageSidebarClosed = true;

            $scope.openSidebar = function() {
                //getLatestCommit();
                //$scope.pageSidebarClosed = !$scope.pageSidebarClosed;
            };

            $scope.userLoginForm = {};
            $scope.forgetForm = {};

            $scope.registerUser = function() {
                accountService.registerUser($scope.userLoginForm)
                    .then(function(res) {
                        console.log('User registered');
                    }, function(err) {
                        console.log(err);
                    })
            };

            //Login user
            $scope.username = '';
            $scope.password = '';

            $scope.loginUser = function() {
                loginService.login($scope.username, $scope.password)
                    .then(function(res) {
                        console.log(res);
                        $rootScope.me.UserName = $scope.username;
                        $rootScope.updatedUser.Email = $scope.username;
                    }, function(err) {
                        console.log(err);
                    });
            }

            $scope.logoutUser = function() {
                loginService.logout();

            };

            $scope.me = {};
            $scope.me.UserName = localStorage.username;
            //console.log($scope.me.UserName);

            $scope.updatedUser = {};
            $scope.updatedUser.Email = localStorage.email;
            //        function getMe () {
            //          profileService.getUserInfo()
            //            .then(function (res) {
            //              $scope.me = res;
            //              console.log(res);
            //              $scope.updatedUser = res;
            //              console.log(res);
            //            }, function (err) {
            //              console.log(err);
            //            })
            //        }
            //        getMe();



            $scope.updateUser = function(userName) {
                var user = {
                    "Email": userName
                }

                profileService.updateUserInfo(user)
                    .then(function(res) {
                        $scope.updatedUser.Email = userName;
                        $scope.me.UserName = userName;

                        localStorage.username = userName;
                        localStorage.email = userName;

                        // window.localStorage['username'] = userName;
                        console.log(res);
                    }, function(err) {
                        console.log(err);
                    })
            };

            $scope.showLoginForm = true;
            $scope.showRegisterForm = false;
            $scope.showForgotPasswordForm = false;

            $scope.showRegistration = function() {
                $scope.showLoginForm = false;
                $scope.showRegisterForm = true;
                $scope.showForgotPasswordForm = false;

            }

            $scope.showLogin = function() {
                $scope.showLoginForm = true;
                $scope.showRegisterForm = false;
                $scope.showForgotPasswordForm = false;
            }

            $scope.cancelRegister = function(){
              $scope.register_email = '';
              $scope.register_password = '';
              $scope.confirm_password = '';
              $scope.registerForm.$setPristine();
              $scope.registerForm.$setUntouched();
            }

            $scope.forgotPassword = {};
            $scope.cancelForgotPassword = function(){
              $scope.forgetForm.$setPristine();
              $scope.forgetForm.$setUntouched();
              $scope.forgotPassword.email = '';
            }

            $scope.showForgotPassword = function() {
                $scope.showLoginForm = false;
                $scope.showRegisterForm = false;
                $scope.showForgotPasswordForm = true;
            }

            $scope.authenticate = function(provider) {
                $auth.authenticate(provider)
                    .then(function(response) {
                        $auth.setToken(response.data.id);
                        $location.path("/pipelines");
                    })
                    .catch(function(response) {
                        console.log(response);
                    });
            }

            $('#log-form').keydown(function(e) {
                var key = e.which;
                if (key == 13) {
                    // As ASCII code for ENTER key is "13"
                    $scope.login(); // Submit form code
                }
            });

            $scope.login = function() {
              $scope.mismatchCredentials = {};
                var user = {
                    email: $scope.email,
                    password: $scope.password
                }

                $auth.login(user)
                    .then(function(response) {
                        console.log(response.data)
                        $auth.setToken(response.data);
                        $location.path("/pipelines");
                        $rootScope.startWebsocket('ws://' + CONSTANTS.HOST + '/ws/v1');
                    })
                    .catch(function(response) {
                      $scope.mismatchCredentials.errorMessage = response.data.message;
                        console.log(response)
                            // Handle errors here, such as displaying a notification
                            // for invalid email and/or password.
                    });
            };

            $scope.register = function(user) {

                var user = {
                    email: $scope.register_email,
                    password: $scope.register_password,
                    confirmPassword: $scope.confirm_password
                }

                // Simple GET request example:
                $http({
                    method: 'POST',
                    url: CONSTANTS.SERVER_URL + '/auth/register',
                    data: user
                }).then(function successCallback(response) {
                    $scope.showLogin();
                    console.log("success response: " + response);
                }, function errorCallback(response) {
                    console.log("error response: " + response);
                });

                $scope.register_email = undefined;
                $scope.register_password = undefined;
                $scope.confirm_password = undefined;
                $scope.registerForm.$setPristine();
                $scope.registerForm.$setUntouched();
            }

        }
    ]);
