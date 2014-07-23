$registerPerspective({
    id: "Markdown Live Editor",
    is_serializable: false,
    panel_type: "org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter",
    view: {
        parts: [
            {
                place: "MarkdownLiveViewer",
                parameters: {}
            }
        ],
        panels: [
            {
                width: 600,
                min_width: 300,
                position: "west",
                panel_type: "org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter",
                parts: [
                    {
                        place: "MarkdownLiveEditor",
                        parameters: {}
                    }
                ]
            }
        ]
    },
    on_close: function () {
    }
});
