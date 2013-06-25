// Here's my data model
var ViewModel = function(first, last) {
    this.firstName = ko.observable(first);
    this.lastName = ko.observable(last);
 
    this.fullName = ko.computed(function() {
        // Knockout tracks dependencies automatically. It knows that fullName depends on firstName and lastName, because these get called when evaluating fullName.
        return this.firstName() + " " + this.lastName();
    }, this);
};
 
$registerPlugin({
    id: "my ko js",
    type: "ko",
    templateUrl: "ko.sample.html",
    title: function () {
        return "ko " + Math.floor(Math.random() * 10);
    },
    on_reveal: function () {
        ko.applyBindings(new ViewModel("Planet", "Earth"));
    }
});
