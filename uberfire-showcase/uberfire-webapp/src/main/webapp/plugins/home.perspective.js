$registerPerspective({
    id: "Home",
    is_default: true,
    panel_type: "org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter",
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
                panel_type: "org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter",
                parts: [
                    {
                        place: "YouTubeVideos",
                        parameters: {}
                    }
                ]
            },
            {
                position: "east",
                panel_type: "org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter",
                parts: [
                    {
                        place: "TodoListScreen",
                        parameters: {}
                    },
                    {
                        place: "ReadmeScreen",
                        parameters: {}
                    }
                ]
            },
            {
                height: 400,
                position: "south",
                panel_type: "org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter",
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
