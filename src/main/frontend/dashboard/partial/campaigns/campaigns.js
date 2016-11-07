'use strict';
angular.module('clickTracker').controller('CampaignsCtrl',function($scope, campaignsService, campaigns, $state, $filter, SweetAlert){
    $scope.campaigns = campaigns;
    $scope.selectAll = false;
    $scope.onOpenCampaign = function(campaign) {
        $state.go('campaigns.campaign', {id: campaign.id, name: $filter('slugify')(campaign.name)});
    };
    $scope.onSelectAll = function() {
        _.each($scope.campaigns, function (key) {
            _.assign(key , {'selected': $scope.selectAll});
        });
    };
    $scope.onRemoveClick = function() {
        var selectedItems = _.map(_.filter($scope.campaigns, 'selected'), 'id');
        if (_.isEmpty(selectedItems)) { return; }
        SweetAlert.swal({
            title: "Are you sure?",
            text: "Your will not be able to restore selected campaigns",
            type: "warning",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "Yes, delete it!",
            closeOnConfirm: true
        }, function(confirm){
            if (confirm) {
                campaignsService.deleteCampaigns(selectedItems);
                $state.reload();
            }
        });
    }
});
