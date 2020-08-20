
import { DMN_DIAGRAM_EXPLORER_SCREEN } from "./DesktopConstants"
import { EditorPageSelector } from "./EditorPageSelector";

export class DmnEditorPageSelector extends EditorPageSelector {
    public diagramExplorerLocator = (): string => {
        return DMN_DIAGRAM_EXPLORER_SCREEN;
    }

    public diagramExplorerTitle = (): string => {
        return super.h3WithTextEqual("Explore diagram");
    }
} 