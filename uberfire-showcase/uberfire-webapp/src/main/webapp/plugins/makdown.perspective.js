$registerPerspective({
    id: "JS Markdown Editor",
    view: {
        parts: [
            {
                place: "MarkdownLiveViewer",
                parameters: {}
            }
        ],
        panels: [
            {
                width: 200,
                min_width: 100,
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
