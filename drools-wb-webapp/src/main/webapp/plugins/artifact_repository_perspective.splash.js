$registerSplashScreen({
    id: "artifact_repository_perspective.splash",
    templateUrl: "artifact_repository_perspective.splash.html",
    title: function () {
        return "Help";
    },
    display_next_time: true,
    interception_points: ["org.guvnor.m2repo.client.perspectives.GuvnorM2RepoPerspective"]
});