$registerSplashScreen({
    id: "artifact_repository_perspective.splash",
    templateUrl: "artifact_repository_perspective.splash.html",
    title: function () {
        return "Help";
    },
    display_next_time: false,
    interception_points: ["org.guvnor.m2repo.client.perspectives.GuvnorM2RepoPerspective"]
});