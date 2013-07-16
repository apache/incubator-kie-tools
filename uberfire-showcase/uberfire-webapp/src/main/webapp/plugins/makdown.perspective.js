$registerPerspective({
    id: "Markdown Live Editor",
    is_serializable: false,
    panel_type: "root_static",
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
                panel_type: "simple",
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
