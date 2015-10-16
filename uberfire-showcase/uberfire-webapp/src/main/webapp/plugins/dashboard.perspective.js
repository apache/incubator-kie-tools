$registerPerspective({
    id: "Dashboard",
    roles: [ "director", "manager" ],
    panel_type: "org.uberfire.client.workbench.panels.impl.ClosableSimpleWorkbenchPanelPresenter",
    view: {
        parts: [
            {
                place: "GitHubCommitStats"
            }
        ],
        panels: [
            {
                width: 450,
                position: "west",
                panel_type: "org.uberfire.client.workbench.panels.impl.ClosableSimpleWorkbenchPanelPresenter",
                parts: [
                    {
                        place: "GitHubCommitDaysStats"
                    }
                ]
            },
            {
                width: 380,
                position: "east",
                panel_type: "org.uberfire.client.workbench.panels.impl.ClosableSimpleWorkbenchPanelPresenter",
                parts: [
                    {
                        place: "TwitterGadget"
                    }
                ]
            },
            {
                width: 570,
                height: 340,
                position: "south",
                panel_type: "org.uberfire.client.workbench.panels.impl.ClosableSimpleWorkbenchPanelPresenter",
                parts: [
                    {
                        place: "GitHubFrequencyStats"
                    }
                ]
            }
        ]
    }
});
