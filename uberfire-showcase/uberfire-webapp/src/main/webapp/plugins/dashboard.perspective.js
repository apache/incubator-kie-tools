$registerPerspective({
    id: "Dashboard",
    roles: [ "director", "manager" ],
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
