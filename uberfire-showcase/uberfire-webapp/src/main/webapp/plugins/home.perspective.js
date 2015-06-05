$registerPerspective({
    id: "Home",
    is_default: true,
    panel_type: "org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter",
    view: {
        parts: [
            {
                place: "welcome",
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
                width: 450,
                panel_type: "org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter",
                parts: [
                    {
                        place: "ReadmeScreen",
                        parameters: {}
                    },
                    {
                        place: "TodoListScreen",
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
                        place: "SampleWorkbenchEditor",
                        parameters: {}
                    },
                    {
                        place: "YouTubeScreen",
                        parameters: {}
                    }
                ]
            }
        ]
    }
});
