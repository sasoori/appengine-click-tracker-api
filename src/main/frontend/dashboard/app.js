angular.module('clickTracker', [
    'ui.bootstrap',
    'ui.router',
    'ngAnimate',
    'angular-loading-bar',
    'ngTagsInput',
    'oitozero.ngSweetAlert'
]);

angular.module('clickTracker').config(function ($stateProvider, $urlRouterProvider) {

    $stateProvider.state('campaigns', {
        url: '/campaigns',
        views: {
            '@' : {
                templateUrl: 'partial/campaigns/campaigns.html',
                controller: 'CampaignsCtrl',
                resolve: {
                    campaigns: function(campaignsService) {
                        return campaignsService.getCampaigns();
                    }
                }
            }
        }
    });
    $stateProvider.state('campaigns.campaign', {
        url: '/campaign/:id/:name',
        views: {
            '@': {
                templateUrl: 'partial/campaigns/campaign/campaign.html',
                controller: 'CampaignCtrl',
                resolve: {
                    campaign: function($stateParams, campaignsService) {
                        return _.find(campaignsService.model.campaignsList, ['id', parseInt($stateParams.id)]);
                    }
                }
            }
        }
    });
    $stateProvider.state('campaigns.add-campaign', {
        url: '/add',
        views: {
            '@': {
                templateUrl: 'partial/campaigns/add-campaign/add-campaign.html',
                controller: 'AddCampaignCtrl'
            }
        }
    });
    /* Add New States Above */
    $urlRouterProvider.otherwise('/campaigns');

});

angular.module('clickTracker').run(function ($rootScope) {

    $rootScope.safeApply = function (fn) {
        var phase = $rootScope.$$phase;
        if (phase === '$apply' || phase === '$digest') {
            if (fn && (typeof(fn) === 'function')) {
                fn();
            }
        } else {
            this.$apply(fn);
        }
    };

});
