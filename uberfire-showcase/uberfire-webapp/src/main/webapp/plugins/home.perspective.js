$registerPerspective({
    id: "Home",
    is_default: true,
    panel_type: "org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter",
    view: {
        parts: [
            {
                place: "welcome",
                min_height: 100,
                parameters: {}
            }
        ],
        panels: [
            {
                width: 250,
                min_width: 200,
                position: "west",
                panel_type: "org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter",
                parts: [
                    {
                        place: "YouTubeVideos",
                        parameters: {}
                    }
                ]
            },
            {
                width: 300,
                min_width: 200,
                position: "east",
                panel_type: "org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter",
                parts: [
                    {
                        place: "TodoListScreen",
                        parameters: {}
                    }
                ]
            },
            {
                height: 400,
                position: "south",
                panel_type: "org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter",
                parts: [
                    {
                        place: "YouTubeScreen",
                        parameters: {}
                    }
                ]
            }
        ]
    }
});
