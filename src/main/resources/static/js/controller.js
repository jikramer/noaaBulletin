var app = angular.module('app', []);

 

/*************************************************************
 * * * * * * * * * * parse controller * * * * * * * * *
 *************************************************************/

app.controller('parsecontroller', function($scope, $http, $location) {
	var messageClassName = "hide";
	var screenMessage = "";

 
	$scope.messageClass = function() {
		return messageClassName;
	}

	$scope.message = function() {
		return screenMessage;
	}

	$scope.doparse = function(post) {
		var url = $location.absUrl() + "doParse";

		var config = {
			headers : {
				'Content-Type' : 'application/json;charset=utf-8;'
			}
		}

		$http.post(url, post, config).then(function(response) {
			screenMessage = "Sucessful!";
			messageClassName = "label label-success";

		}, function(response) {
			screenMessage = "Fail!";
			messageClassName = "label label-danger";
		});
	}

	$scope.clearparseform = function() {
		
		$scope.form = {};
		screenMessage = "";	
	    $scope.post.filename = '';
	    $scope.post.station = '';
  	    
		$scope.parseForm.$setPristine();
	    $scope.parseForm.$setUntouched();   
	}
	
});

/***********************************************************************
 * * * * * * * * * * build controller * * * * * * * * *
 ***********************************************************************/

app.controller('buildcontroller', function($scope, $http, $location) {
	var messageClassName = "hide";
	var screenMessage = "";
	var doSample = false;

	$scope.items = [];
	$scope.newitem = '';
	$scope.add = function() {
		if ($scope.items.length < 4) {
			$scope.items.push($scope.newitem);
		}
	}
	$scope.del = function(i) {
		$scope.items[i] = '';
		$scope.items.splice(i, 1);
 	}

	$scope.zoneitems = [];
	$scope.zonenewitem = '';
	$scope.zoneadd = function() {
		if ($scope.zoneitems.length < 4) {
			$scope.zoneitems.push($scope.zonenewitem);
		}
	}
	$scope.zonedel = function(i) {
		$scope.zoneitems[i] = '';
	    $scope.zoneitems.splice(i, 1);
 	}
	
	
	$scope.messageClass = function() {
		return messageClassName;
	}

	$scope.message = function() {
		return screenMessage;
	}

	$scope.showSample = function() {
		var value = "";

		if (!doSample) {
			value = "hide";
		} else
			value = "show";
 
		return value;
	};

	$scope.getsampledata = function(post) {
		var url = $location.absUrl() + "getsampleweather";

		var config = {
			headers : {
				'Content-Type' : 'application/json;charset=utf-8;'
			}
		}

		$scope.label = function() {
			return "Sample Result Set:";
		}

		$http.post(url, post, config).then(function(response) {
			doSample = (doSample) ? false : true;
			$scope.response = response.data

		}, function(response) {
			$scope.getResultMessage = "Fail!";
		});
	}

	$scope.buildoutputfile = function(post) {
		var url = $location.absUrl() + "buildoutputfile";

		var config = {
			headers : {
				'Content-Type' : 'application/json;charset=utf-8;'
			}
		}
		
		
		$scope.filteredLabel = function() {
			return "Results:";
		}

		$http.post(url, post, config).then(function(response) {
			doSample = false;

			screenMessage = "Sucessful!";
			messageClassName = "label label-success";

		}, function(response) {
			screenMessage = "Fail!";
			messageClassName = "label label-danger";
		});
	}

	$scope.clearall = function(post) {
		
		$scope.buildForm = {};
		screenMessage = "";	
	
	    post.station = '';
	    post.zones = '';
	    post.additionalzones = [];
	    post.keywords = [];
	    
	    post.keyword = '';
	    post.fileNameOut = '';
	  
		$scope.response = '';
		doSample = false;
 		$scope.value = 'hide';
	
 		$scope.items = [];
 		$scope.zoneitems = [];
 		
 	//		$scope.buildForm.$setPristine();
        $scope.model = '';
 		
 	//	$scope.buildForm.$setPristine();
	//    $scope.buildForm.$setUntouched();   
	  
 		
 		//document.getElementById("buildForm").reset();
	}

});