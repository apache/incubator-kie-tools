import { CommonI18n } from "@kie-tools/i18n-common-dictionary";
import { ReferenceDictionary } from "@kie-tools-core/i18n/dist/core";

interface BpmnEditorEnvelopeDictionary
  extends ReferenceDictionary<{
    unselect: string;
    deleteSelection: string;
    selectDeselectAll: string;
    createGroupWrappingSelection: string;
    hideFromDrd: string;
    copyNodes: string;
    cutNodes: string;
    pasteNodes: string;
    openClosePropertiesPanel: string;
    toggleHierarchyHighlights: string;
    selectionUp: string;
    selectionDown: string;
    selectionLeft: string;
    selectionRight: string;
    selectionUpBigDistance: string;
    selectionDownBigDistance: string;
    selectionLeftBigDistance: string;
    selectionRightBigDistance: string;
    focusOnSelection: string;
    resetPositionToOrigin: string;
    rightMouseButton: string;
    holdAndDragtoPan: string;
    holdAndScrollToZoomInOut: string;
    holdAndScrollToNavigateHorizontally: string;
    appendTasknode: string;
    appendGatewayNode: string;
    appendIntermediateCatchEventNode: string;
    appendIntermediateThrowEventNode: string;
    appendTextAnnotationNode: string;
    appendEndEventNode: string;
    milestone: string;
    flexibleProcesses: string;
    unableToOpenFile: string;
    errorMessage: string;
  }> {}

export interface BpmnEditorEnvelopeI18n extends BpmnEditorEnvelopeDictionary, CommonI18n {}
