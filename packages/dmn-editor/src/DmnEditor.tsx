import "@patternfly/react-core/dist/styles/base.css";
import "reactflow/dist/style.css";

import * as React from "react";
import { useCallback, useImperativeHandle, useMemo, useRef } from "react";
import { Drawer, DrawerContent, DrawerContentBody } from "@patternfly/react-core/dist/js/components/Drawer";
import { Tab, TabTitleIcon, TabTitleText, Tabs } from "@patternfly/react-core/dist/js/components/Tabs";
import { CatalogIcon } from "@patternfly/react-icons/dist/js/icons/catalog-icon";
import { FileIcon } from "@patternfly/react-icons/dist/js/icons/file-icon";
import { InfrastructureIcon } from "@patternfly/react-icons/dist/js/icons/infrastructure-icon";
import { PficonTemplateIcon } from "@patternfly/react-icons/dist/js/icons/pficon-template-icon";
import { BoxedExpression } from "./boxedExpressions/BoxedExpression";
import { DataTypes } from "./dataTypes/DataTypes";
import { Diagram } from "./diagram/Diagram";
import { DmnVersionLabel } from "./diagram/DmnVersionLabel";
import { Documentation } from "./documentation/Documentation";
import { IncludedModels } from "./includedModels/IncludedModels";
import { DiagramPropertiesPanel } from "./propertiesPanel/DiagramPropertiesPanel";
import {
  DmnEditorStoreApiContext,
  DmnEditorTab,
  State,
  StoreApiType,
  createDmnEditorStore,
  useDmnEditorStore,
  useDmnEditorStoreApi,
} from "./store/Store";
import { useEffectAfterFirstRender } from "./useEffectAfterFirstRender";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { BeePropertiesPanel } from "./propertiesPanel/BeePropertiesPanel";

import "./DmnEditor.css"; // Leave it for last, as this overrides some of the PF and RF styles.

const ON_MODEL_CHANGE_DEBOUNCE_TIME_IN_MS = 500;

export type DmnEditorRef = {
  reset: (mode: State["dmn"]["model"]) => void;
};

export type DmnEditorProps = {
  model: State["dmn"]["model"];
  onModelChange?: (model: State["dmn"]["model"]) => void;
};

export const DmnEditorInternal = ({
  model,
  onModelChange,
  forwardRef,
}: DmnEditorProps & { forwardRef?: React.Ref<DmnEditorRef> }) => {
  const { boxedExpressionEditor, dmn, navigation, dispatch, diagram } = useDmnEditorStore((s) => s);

  const dmnEditorStoreApi = useDmnEditorStoreApi();
  // Allow imperativelly controlling the Editor.
  useImperativeHandle(
    forwardRef,
    () => ({
      reset: (model) => dispatch.dmn.reset(model),
    }),
    [dispatch.dmn]
  );

  // Make sure the DMN Editor reacts to props changing.
  useEffectAfterFirstRender(() => {
    dmnEditorStoreApi.setState((state) => {
      state.dmn.model = model;
    });
  }, [dmnEditorStoreApi, dispatch.dmn, model]);

  const isDiagramMidEditing = useMemo(
    () => diagram.draggingNodes.length > 0 || diagram.resizingNodes.length > 0 || diagram.draggingWaypoints.length > 0,
    [diagram.draggingNodes.length, diagram.draggingWaypoints.length, diagram.resizingNodes.length]
  );

  // Only notify changes when dragging/resizing operations are not happening.
  useEffectAfterFirstRender(() => {
    if (isDiagramMidEditing) {
      return;
    }

    const timeout = setTimeout(() => {
      console.log("Model changed!");
      onModelChange?.(dmn.model);
    }, ON_MODEL_CHANGE_DEBOUNCE_TIME_IN_MS);

    return () => {
      clearTimeout(timeout);
    };
  }, [isDiagramMidEditing, onModelChange, dmn.model]);

  const onTabChanged = useCallback(
    (e, tab) => {
      return dispatch.navigation.setTab(tab);
    },
    [dispatch.navigation]
  );

  const diagramContainerRef = useRef<HTMLDivElement>(null);
  const beeContainerRef = useRef<HTMLDivElement>(null);

  return (
    <>
      <Tabs
        isFilled={true}
        activeKey={navigation.tab}
        onSelect={onTabChanged}
        role={"region"}
        className={"kie-dmn-editor--tabs"}
      >
        <Tab
          eventKey={DmnEditorTab.EDITOR}
          title={
            <>
              <TabTitleIcon>
                <PficonTemplateIcon />
              </TabTitleIcon>
              <TabTitleText>Editor</TabTitleText>
            </>
          }
        >
          {navigation.tab === DmnEditorTab.EDITOR && (
            <>
              {!boxedExpressionEditor.openExpressionId && (
                <Drawer isExpanded={diagram.propertiesPanel.isOpen} isInline={true} position={"right"}>
                  <DrawerContent panelContent={<DiagramPropertiesPanel />}>
                    <DrawerContentBody>
                      <div className={"kie-dmn-editor--diagram-container"} ref={diagramContainerRef}>
                        <DmnVersionLabel version={"1.4"} /> {/** FiXME: Tiago --> This version is wrong. */}
                        <Diagram container={diagramContainerRef} />
                      </div>
                    </DrawerContentBody>
                  </DrawerContent>
                </Drawer>
              )}
              {boxedExpressionEditor.openExpressionId && (
                <Drawer isExpanded={boxedExpressionEditor.propertiesPanel.isOpen} isInline={true} position={"right"}>
                  <DrawerContent panelContent={<BeePropertiesPanel />}>
                    <DrawerContentBody>
                      <div className={"kie-dmn-editor--bee-container"} ref={beeContainerRef}>
                        <BoxedExpression container={beeContainerRef} />
                      </div>
                    </DrawerContentBody>
                  </DrawerContent>
                </Drawer>
              )}
            </>
          )}
        </Tab>

        <Tab
          eventKey={DmnEditorTab.DATA_TYPES}
          title={
            <>
              <TabTitleIcon>
                <InfrastructureIcon />
              </TabTitleIcon>
              <TabTitleText>
                Data types&nbsp;&nbsp;
                <Label style={{ padding: "0 12px" }}>{dmn.model.definitions.itemDefinition?.length ?? 0}</Label>
              </TabTitleText>
            </>
          }
        >
          {navigation.tab === DmnEditorTab.DATA_TYPES && <DataTypes />}
        </Tab>

        <Tab
          eventKey={DmnEditorTab.INCLUDED_MODELS}
          title={
            <>
              <TabTitleIcon>
                <FileIcon />
              </TabTitleIcon>
              <TabTitleText>
                Included models&nbsp;&nbsp;
                <Label style={{ padding: "0 12px" }}>{dmn.model.definitions.import?.length ?? 0}</Label>
              </TabTitleText>
            </>
          }
        >
          {navigation.tab === DmnEditorTab.INCLUDED_MODELS && <IncludedModels />}
        </Tab>

        <Tab
          eventKey={DmnEditorTab.DOCUMENTATION}
          title={
            <>
              <TabTitleIcon>
                <CatalogIcon />
              </TabTitleIcon>
              <TabTitleText>Documentation</TabTitleText>
            </>
          }
        >
          {navigation.tab === DmnEditorTab.DOCUMENTATION && <Documentation />}
        </Tab>
      </Tabs>
    </>
  );
};

export const DmnEditor = React.forwardRef((props: DmnEditorProps, ref: React.Ref<DmnEditorRef>) => {
  const storeRef = React.useRef<StoreApiType>(createDmnEditorStore(props.model));

  return (
    <DmnEditorStoreApiContext.Provider value={storeRef.current}>
      <DmnEditorInternal forwardRef={ref} {...props} />
    </DmnEditorStoreApiContext.Provider>
  );
});
