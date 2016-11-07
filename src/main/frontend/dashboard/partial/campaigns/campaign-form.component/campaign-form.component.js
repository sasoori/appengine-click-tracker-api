angular.module('clickTracker').directive('campaignForm', function (campaignsService, $state) {
    return {
        restrict: 'E',
        replace: true,
        scope: {
            editedCampaign: '='
        },
        templateUrl: 'partial/campaigns/campaign-form.component/campaign-form.html',
        link: function (scope, element, attrs, fn) {
            scope.campaign = {};

            var editableFields = [
                {field: 'name', defaultValue: null},
                {field: 'referral', defaultValue: null},
                {field: 'platforms', defaultValue: []}
            ];
            scope.platformTypes = [
                {
                    name: 'Android',
                    type: 'ANDROID',
                    checked: false
                },
                {
                    name: 'iPhone',
                    type: 'IPHONE',
                    checked: false
                }
            ];
            _.each(editableFields, function (field) {
                scope.campaign[field.field] = field.defaultValue;
            });

            if (!_.isEmpty(scope.editedCampaign)) {
                var tempCampaign = _.cloneDeep(scope.editedCampaign);
                _.each(editableFields, function (field) {
                    if (tempCampaign[field.field] !== undefined) {
                        scope.campaign[field.field] = tempCampaign[field.field];
                    }
                });
                _.each(scope.platformTypes, function (platform) {
                    platform.checked = _.includes(scope.campaign.platforms, platform.type)
                })
            }
            scope.addCampaign = function () {
                scope.campaign.platforms = _.map(_.filter(scope.platformTypes, 'checked'), 'type');
                saveCampaign();
            };
            function saveCampaign() {
                campaignsService.saveCampaign(_.cloneDeep(scope.campaign), scope.editedCampaign).then(function () {
                    $state.go('campaigns');
                });
            }
        }
    };
});
