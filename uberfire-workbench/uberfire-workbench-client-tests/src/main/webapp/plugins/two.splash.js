$registerSplashScreen({
    id: "two.splash",
    templateUrl: "two.splash.html",
    title: function () {
        return "Splash Two";
    },
    display_next_time: true,
    interception_points: ["org.uberfire.wbtest.client.splash.HasJsSplashTwo"]
});