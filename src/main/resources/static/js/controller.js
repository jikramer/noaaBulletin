


var app = angular.module('app', []);

app.controller('postcontroller', function($scope, $http, $location) {
	var messageClassName = "hide";
	var screenMessage = "";
	
	$scope.messageClass = function(){
    	return messageClassName;
    }
	
	$scope.xmessage = function(){
    	return screenMessage;
    }	
    
	$scope.submitForm = function(post){
        var url = $location.absUrl() + "postfilename";
		
        var config = {
                headers : {
                    'Content-Type': 'application/json;charset=utf-8;'
                }
        }
   
        $http.post(url, post, config).then(function (response) {
        	screenMessage = "Sucessful!";
        	messageClassName = "label label-success";
        
        }, function (response) {
        	screenMessage = "Fail!";
        	messageClassName = "label label-danger";
        });

    }
});

app.controller('getcontroller', function($scope, $http, $location) {
	var messageClassName = "hide";
	var screenMessage = "";

	$scope.messageClass = function(){
    	return messageClassName;
    }
	
	$scope.xmessage = function(){
    	return screenMessage;
    }	
	
	$scope.getfunction = function(){
        var url = $location.absUrl() + "getsampleweather";
		
		var config = {
                headers : {
                    'Content-Type': 'application/json;charset=utf-8;'
                }
        }

      	$scope.label = function(){
         	return "Sample Result Set:";
         }	
         
        $http.get(url, config).then(function (response) {
        	$scope.response = response.data
 
        }, function (response) {
            $scope.getResultMessage = "Fail!";
        });
    }
	
	$scope.submitForm = function(post){
        var url = $location.absUrl() + "buildoutputfile";
         
		var config = {
                headers : {
                    'Content-Type': 'application/json;charset=utf-8;'
                }
        }

      	$scope.filteredLabel = function(){
         	return "Results:";
         }	
         
        $http.post(url, post, config).then(function (response) {
        	screenMessage = "Sucessful!";
        	messageClassName = "label label-success";
        
        }, function (response) {
        	screenMessage = "Fail!";
        	messageClassName = "label label-danger";
        });

    }

});