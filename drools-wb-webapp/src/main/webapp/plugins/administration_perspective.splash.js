$registerSplashScreen({
    id: "administration_perspective.splash",
    templateUrl: "administration_perspective.splash.html",
    title: function () {
        return "Help";
    },
    display_next_time: true,
    interception_points: ["org.drools.workbench.client.perspectives.AdministrationPerspective"]
});