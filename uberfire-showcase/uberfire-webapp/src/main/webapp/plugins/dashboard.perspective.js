$registerPerspective({
    id: "Dashboard",
    roles: [ "director", "manager" ],
    panel_type: "root_static",
    view: {
        parts: [
            {
                place: "Chart"
            }
        ],
        panels: [
            {
                width: 370,
                height: 340,
                position: "south",
                panel_type: "static",
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
                        panel_type: "static",
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
                            panel_type: "static",
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
                panel_type: "static",
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
                        panel_type: "static",
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
