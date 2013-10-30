$registerSplashScreen({
    id: "home.splash",
    templateUrl: "home.splash.html",
    title: function () {
        return "Cool Home Splash " + Math.floor(Math.random() * 10);
    },
    display_next_time: true,
    interception_points: ["Home"]
});
