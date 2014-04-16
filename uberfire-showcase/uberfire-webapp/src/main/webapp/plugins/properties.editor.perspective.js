$registerPerspective({
    id: "PropertyEditor",
    panel_type: "root_list",
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
                width: 500,
                min_width: 500,
                position: "west",
                panel_type: "multi_list",
                parts: [
                    {
                        place: "PropertyEditorClientScreen",
                        parameters: {}
                    }
                ]
            },
            {
                width: 500,
                min_width: 500,
                position: "east",
                panel_type: "multi_list",
                parts: [
                    {
                        place: "PropertyEditorScreen",
                        parameters: {}
                    }
                ]
            }
        ]
    }
});