export class EditorPage {
    public diagramLoadingScreenLocator(): string {
        return '#loading-screen'
    }

    public diagramIframeLocator(): string {
        return '//iframe[@id = \'kogito-iframe\']'
    }

    public diagramNameHeaderLocator(name: string = 'unsaved file'): string {
        return `//h3[text() = \'${name}\']`
    }
}