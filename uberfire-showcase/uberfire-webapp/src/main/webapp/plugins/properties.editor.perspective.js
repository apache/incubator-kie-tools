$registerPerspective({
    id: "PropertyEditor",
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
                width: 500,
                min_width: 500,
                position: "west",
                panel_type: "org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter",
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
                panel_type: "org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter",
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