$registerSplashScreen({
    id: "home.splash",
    templateUrl: "home.splash.html",
    body_height: 325,
    title: function () {
        return "Cool Home Splash " + Math.floor(Math.random() * 10);
    },
    on_close: function () {
        alert("this is a close Splash alert!");
    },
    display_next_time: true,
    interception_points: ["Home"]
});
