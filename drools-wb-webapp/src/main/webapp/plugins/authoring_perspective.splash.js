$registerSplashScreen({
    id: "authoring_perspective.splash",
    templateUrl: "authoring_perspective.splash.html",
    title: function () {
        return "Help";
    },
    display_next_time: true,
    interception_points: ["org.drools.workbench.client.perspectives.AuthoringPerspective"]
});