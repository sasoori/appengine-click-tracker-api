/*global moment */
angular.module('clickTracker').filter('dateMomentUtc', function() {
    return function(input) {
        return moment.utc(input).format("Do MMM YYYY");
    };
});
angular.module('clickTracker').filter('slugify', function() {
    return function(input) {
        return input
            .toLowerCase()
            .replace(/[^\w ]+/g,'')
            .replace(/ +/g,'-')
            ;
    };
});
