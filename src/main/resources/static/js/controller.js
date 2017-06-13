var app = angular.module('app', []);
app.controller('postcontroller', function($scope, $http, $location) {
    $scope.submitForm = function(){
        var url = $location.absUrl() + "postfilename";
		
        alert(url);
        var config = {
                headers : {
                    'Content-Type': 'application/json;charset=utf-8;'
                }
        }
        var data = {
            filename: $scope.filename,
            productType: $scope.productType,
        };

        $http.post(url, JSON.stringify(data), config).then(function (response) {
        	$scope.postResultMessage = "Sucessful!";
        
        }, function (response) {
            $scope.postResultMessage = "Fail!";
        });

        $scope.filename = "";
        $scope.productType = "";
    }
});

app.controller('getcontroller', function($scope, $http, $location) {
    $scope.getfunction = function(){
        var url = $location.absUrl() + "getallweather";
		
		 alert(url);
         var config = {
                headers : {
                    'Content-Type': 'application/json;charset=utf-8;'
                }
        }

        $http.get(url, config).then(function (response) {
	        $scope.response = response.data
 
        }, function (response) {
            $scope.getResultMessage = "Fail!";
        });
    }
});