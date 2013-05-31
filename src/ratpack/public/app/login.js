angular.module('angular-auth-demo').controller({
  LoginController: function ($scope, $http, authService) {
    $scope.submit = function() {
	var data = "username=" + $scope.username + "&" +
            "password=" + $scope.password;
      $http.post('auth/login', data).success(function() {
        authService.loginConfirmed();
      });
    }
  }
  
});

