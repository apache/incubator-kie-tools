$registerSplashScreen({
    id: 'home.splash',
    templateUrl: "home.splash.html",
    title: function () {
        return 'What\'s New';
    },
    enabled: false,
    display_next_time: true,
    interception_points: ['HomePerspective']
});