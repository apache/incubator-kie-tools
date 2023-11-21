import * as React from "react";
import { Switch } from "@patternfly/react-core/dist/js/components/Switch";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Slider } from "@patternfly/react-core/dist/js/components/Slider";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";

const MIN_SNAP = 5;
const MAX_SNAP = 50;
const SNAP_STEP = 5;

export function OverlaysPanel() {
  const diagram = useDmnEditorStore((s) => s.diagram);
  const dmnEditorStoreApi = useDmnEditorStoreApi();

  return (
    <>
      <Form
        onKeyDown={(e) => e.stopPropagation()} // Prevent ReactFlow KeyboardShortcuts from triggering when editing stuff on Overlays Panel
      >
        <FormGroup label="Snapping">
          <Switch
            aria-label={"Snapping"}
            isChecked={diagram.snapGrid.isEnabled}
            onChange={(newValue) =>
              dmnEditorStoreApi.setState((state) => {
                state.diagram.snapGrid.isEnabled = newValue;
              })
            }
          />
        </FormGroup>
        <FormGroup label="Horizontal">
          <Slider
            className={"kie-dmn-editor--snap-slider"}
            isDisabled={!diagram.snapGrid.isEnabled}
            value={diagram.snapGrid.x}
            min={MIN_SNAP}
            max={MAX_SNAP}
            isInputVisible={true}
            inputValue={diagram.snapGrid.x}
            step={SNAP_STEP}
            showTicks={true}
            hasTooltipOverThumb={true}
            onChange={(newSliderValue, newInputValue) =>
              dmnEditorStoreApi.setState((state) => {
                state.diagram.snapGrid.x = Math.min(MAX_SNAP, Math.max(MIN_SNAP, newInputValue ?? newSliderValue));
              })
            }
          />
        </FormGroup>
        <FormGroup label="Vertical">
          <Slider
            className={"kie-dmn-editor--snap-slider"}
            isDisabled={!diagram.snapGrid.isEnabled}
            value={diagram.snapGrid.y}
            min={MIN_SNAP}
            max={MAX_SNAP}
            isInputVisible={true}
            inputValue={diagram.snapGrid.y}
            step={SNAP_STEP}
            showTicks={true}
            hasTooltipOverThumb={true}
            onChange={(newSliderValue, newInputValue) =>
              dmnEditorStoreApi.setState((state) => {
                state.diagram.snapGrid.y = Math.min(MAX_SNAP, Math.max(MIN_SNAP, newInputValue ?? newSliderValue));
              })
            }
          />
        </FormGroup>
      </Form>
      <br />
      <Divider inset={{ default: "insetMd" }} />
      <br />
      <Form
        onKeyDown={(e) => e.stopPropagation()} // Prevent ReactFlow KeyboardShortcuts from triggering when editing stuff on Overlays Panel
      >
        {/* <FormGroup label={"Highlight execution hits"}>
          <Switch
            aria-label={"Highlight execution hits"}
            isChecked={diagram.overlays.enableExecutionHitsHighlights}
            onChange={(newValue) =>
              dmnEditorStoreApi.setState((state) => {
                state.diagram.overlays.enableExecutionHitsHighlights = newValue;
              })
            }
          />
        </FormGroup> */}
        <FormGroup label={"Highlight selected node(s) hierarchy"}>
          <Switch
            aria-label={"Highlight selected node(s) hierarchy"}
            isChecked={diagram.overlays.enableNodeHierarchyHighlight}
            onChange={(newValue) =>
              dmnEditorStoreApi.setState((state) => {
                state.diagram.overlays.enableNodeHierarchyHighlight = newValue;
              })
            }
          />
        </FormGroup>
        {/* <FormGroup label={"Custom node styles"}>
          <Switch
            aria-label={"ustom node styles"}
            isChecked={diagram.overlays.enableCustomNodeStyles}
            onChange={(newValue) =>
              dmnEditorStoreApi.setState((state) => {
                state.diagram.overlays.enableCustomNodeStyles = newValue;
              })
            }
          />
        </FormGroup> */}
        <FormGroup label={"Show data type toolbar on nodes"}>
          <Switch
            aria-label={"Show data type toolbar on nodes"}
            isChecked={diagram.overlays.enableDataTypesToolbarOnNodes}
            onChange={(newValue) =>
              dmnEditorStoreApi.setState((state) => {
                state.diagram.overlays.enableDataTypesToolbarOnNodes = newValue;
              })
            }
          />
        </FormGroup>
        <FormGroup label={"Enable styles"}>
          <Switch
            aria-label={"Show data type toolbar on nodes"}
            isChecked={diagram.overlays.enableStyles}
            onChange={(newValue) =>
              dmnEditorStoreApi.setState((state) => {
                state.diagram.overlays.enableStyles = newValue;
              })
            }
          />
        </FormGroup>
      </Form>
    </>
  );
}
