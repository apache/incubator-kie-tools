$registerSplashScreen({
    id: "one.splash",
    templateUrl: "one.splash.html",
    title: function () {
        return "Splash One";
    },
    display_next_time: true,
    interception_points: ["org.uberfire.wbtest.client.splash.HasJsSplashOne"]
});