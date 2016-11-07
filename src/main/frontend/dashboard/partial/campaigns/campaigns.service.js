'use strict';
angular.module('clickTracker').factory('campaignsService', function($q, $http, IP) {

    var campaignsService = {
        model: {
            campaignsList : {}
        },
        getCampaigns: function() {
            var deferred = $q.defer();
            $http({
                method: 'GET',
                url: IP + '/campaigns',
                ignoreLoadingBar: false
            }).then(function(res) {
                campaignsService.model.campaignsList = res.data;
                deferred.resolve(res && res.data ? res.data : []);
            }, function(error) {
                deferred.reject(error);
            });
            return deferred.promise;
        },
        getCampaign: function(id) {
            var deferred = $q.defer();
            $http({
                method: 'GET',
                url: IP + '/campaigns/' + id,
                ignoreLoadingBar: false
            }).then(function(res) {
                deferred.resolve(res && res.data ? res.data : []);
            }, function(error) {
                deferred.reject(error);
            });
            return deferred.promise;
        },
        saveCampaign: function(newCampaign, editedCampaign) {
            var deferred= $q.defer();
            var campaignId = '';
            var campaign = newCampaign;

            if (editedCampaign) {
                campaign = _.extend(editedCampaign, newCampaign);
                campaignId = '/' + editedCampaign.id;
            }
            $http({
                method: editedCampaign ? 'PUT' : 'POST',
                url: IP + '/campaigns' + campaignId,
                data: campaign,
                ignoreLoadingBar: false
            }).then(function (res) {
                deferred.resolve(res);
            }, function(error) {
                deferred.reject(error);
            });
            return deferred.promise;
        },
        deleteCampaigns: function(selectedCampaigns) {
            var query = '?id=' + selectedCampaigns.join('&id=');
            var deferred = $q.defer();
            $http({
                method: 'DELETE',
                url: IP + '/campaigns' + query,
                ignoreLoadingBar: false
            }).then(function(res) {
                campaignsService.model.projectList = res.data;
                deferred.resolve(res.data);
            }, function(error) {
                deferred.reject(error);
            });
            return deferred.promise;
        }
    };

    return campaignsService;
});