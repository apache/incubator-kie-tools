$registerPerspective({
    id: "Dashboard",
    roles: [ "director", "manager" ],
    panel_type: "org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter",
    view: {
        parts: [
            {
                place: "IPInfoGadget"
            }
        ],
        panels: [
            {
                width: 370,
                height: 340,
                position: "south",
                panel_type: "org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter",
                parts: [
                    {
                        place: "StockQuotesGadget"
                    }
                ],
                panels: [
                    {
                        width: 570,
                        height: 340,
                        position: "east",
                        panel_type: "org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter",
                        parts: [
                            {
                                place: "WeatherGadget"
                            }
                        ],
                        panels: [
                        {
                            width: 520,
                            height: 340,
                            position: "east",
                            panel_type: "org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter",
                            parts: [
                                {
                                    place: "SportsNewsGadget"
                                }
                            ]
                        }
                    ]
                    }
                ]
            },
            {
                width: 700,
                min_width: 330,
                position: "east",
                panel_type: "org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter",
                parts: [
                    {
                        place: "TodoListScreen"
                    }
                ],
                panels: [
                    {
                        width: 380,
                        height: 330,
                        position: "east",
                        panel_type: "org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter",
                        parts: [
                            {
                                place: "IPInfoGadget"
                            }
                        ]
                    }
                ]
            }
        ]
    }
});
