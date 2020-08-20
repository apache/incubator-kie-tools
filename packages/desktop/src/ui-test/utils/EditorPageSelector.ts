import { DIAGRAM_PROPERTIES_SCREEN } from "./DesktopConstants";

/**
 * Common selectors for Editor Page.
 * Contains selectors that are identical across different editors.
 */
export class EditorPageSelector {

    protected diagramExplorerTitle(): string {
        return ""
    }

    public diagramLoadingScreen(): string {
        return '#loading-screen'
    }

    public diagramPropertiesLocator(): string {
        return DIAGRAM_PROPERTIES_SCREEN;
    }

    public diagramIframeLocator(): string {
        return this.iframeWithId(this.diagramIframeId());
    }

    public diagramPropertiesTitle(): string {
        return this.h3WithTextEqual("Properties");
    }
    
    public diagramNameHeaderLocator(name: string = 'unsaved file'): string {
        return this.h3WithTextEqual(name);
    }

    public diagramIframeId(): string {
        return "kogito-iframe";
    }

    public h3WithTextEqual(text: string): string {
        return `//h3[text() = \'${text}\']`;
    }

    public closeEditorButton(): string {
        return '//button[@aria-label=\'Go to homepage\']';
    }

    private iframeWithId(id: string) {
        return `//iframe[@id = \'${id}\']`;
    }
} 