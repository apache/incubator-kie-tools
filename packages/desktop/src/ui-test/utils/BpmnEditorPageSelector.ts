
import { BPMN_DIAGRAM_EXPLORER_SCREEN } from "./DesktopConstants"
import { EditorPageSelector } from "./EditorPageSelector";

export class BpmnEditorPageSelector extends EditorPageSelector {

    public diagramExplorerLocator = (): string => {
        return BPMN_DIAGRAM_EXPLORER_SCREEN;
    }

    public diagramExplorerTitle = (): string => {
        return super.h3WithTextEqual("Explore Diagram");
    }
} 