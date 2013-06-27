$registerPerspective({
    id: "Markdown Live Editor",
    is_serializable: false,
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
