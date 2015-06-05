$registerPerspective({
    id: "Dashboard",
    roles: [ "director", "manager" ],
    panel_type: "org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter",
    view: {
        parts: [
            {
                place: "IPInfoGadget"
            }
        ],
        panels: [
            {
                width: 450,
                position: "west",
                panel_type: "org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter",
                parts: [
                    {
                        place: "my_angular_js"
                    }
                ]
            },
            {
                width: 380,
                position: "east",
                panel_type: "org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter",
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
                panel_type: "org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter",
                parts: [
                    {
                        place: "welcome"
                    }
                ]
            }
        ]
    }
});
