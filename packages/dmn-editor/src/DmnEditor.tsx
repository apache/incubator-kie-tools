/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import "@patternfly/react-core/dist/styles/base.css";
import "reactflow/dist/style.css";

import * as React from "react";
import * as ReactDOM from "react-dom";
import * as RF from "reactflow";
import { ErrorBoundary, ErrorBoundaryPropsWithFallback } from "react-error-boundary";
import { useCallback, useEffect, useImperativeHandle, useMemo, useRef, useState } from "react";
import { original } from "immer";
import { PMML } from "@kie-tools/pmml-editor-marshaller";
import { DmnLatestModel, AllDmnMarshallers } from "@kie-tools/dmn-marshaller";
import { Normalized, normalize } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { FileIcon } from "@patternfly/react-icons/dist/js/icons/file-icon";
import { InfrastructureIcon } from "@patternfly/react-icons/dist/js/icons/infrastructure-icon";
import { PficonTemplateIcon } from "@patternfly/react-icons/dist/js/icons/pficon-template-icon";
import { Drawer, DrawerContent, DrawerContentBody } from "@patternfly/react-core/dist/js/components/Drawer";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { Tab, TabTitleIcon, TabTitleText, Tabs } from "@patternfly/react-core/dist/js/components/Tabs";
import { BoxedExpressionScreen } from "./boxedExpressions/BoxedExpressionScreen";
import { DataTypes } from "./dataTypes/DataTypes";
import { Diagram, DiagramRef } from "./diagram/Diagram";
import { DmnVersionLabel } from "./diagram/DmnVersionLabel";
import { BoxedExpressionPropertiesPanel } from "./propertiesPanel/BoxedExpressionPropertiesPanel";
import { DmnEditorContextProvider, useDmnEditor } from "./DmnEditorContext";
import { DmnEditorErrorFallback } from "./DmnEditorErrorFallback";
import {
  DmnEditorExternalModelsContextProvider,
  useExternalModels,
} from "./includedModels/DmnEditorDependenciesContext";
import { IncludedModels } from "./includedModels/IncludedModels";
import { DiagramPropertiesPanel } from "./propertiesPanel/DiagramPropertiesPanel";
import { ComputedStateCache } from "./store/ComputedStateCache";
import { Computed, DmnEditorTab, createDmnEditorStore, defaultStaticState } from "./store/Store";
import { DmnEditorStoreApiContext, StoreApiType, useDmnEditorStore, useDmnEditorStoreApi } from "./store/StoreContext";
import { DmnDiagramSvg } from "./svg/DmnDiagramSvg";
import { useEffectAfterFirstRender } from "./useEffectAfterFirstRender";
import { INITIAL_COMPUTED_CACHE } from "./store/computed/initial";
import { Commands, CommandsContextProvider, useCommands } from "./commands/CommandsContextProvider";
import { DmnEditorSettingsContextProvider } from "./settings/DmnEditorSettingsContext";
import { JavaCodeCompletionService } from "@kie-tools/import-java-classes-component/dist/components/ImportJavaClasses/services";
import "@kie-tools/dmn-marshaller/dist/kie-extensions"; // This is here because of the KIE Extension for DMN.
import "./DmnEditor.css"; // Leave it for last, as this overrides some of the PF and RF styles.

const ON_MODEL_CHANGE_DEBOUNCE_TIME_IN_MS = 500;

const SVG_PADDING = 20;

export type DmnEditorRef = {
  reset: (mode: DmnLatestModel) => void;
  getDiagramSvg: () => Promise<string | undefined>;
  openBoxedExpressionEditor: (nodeId: string) => void;
  getCommands: () => Commands;
};

/**
 * We need to keep in sync:
 *   * dmn-editor/src/DmnEditor.tsx - NodeEvaluationResults
 *   * dmn-editor-envelope/src/DmnEditorRoot.tsx -
 *   * dmn-editor-envelope/src/NewDmnEditorEnvelopeApi.tsx - newDmnEditor_showDmnEvaluationResults
 *   * dmn-editor-envelope/src/NewDmnEditorFactory.tsx - NewDmnEditorInterface#showDmnEvaluationResults
 *   * extended-services-api/src/dmnResult.ts - DmnEvaluationStatus
 *
 * For more details see: https://github.com/apache/incubator-kie-issues/issues/1823
 */
export type NodeEvaluationResults = {
  evaluationResult: EvaluationResult;
  evaluationHitsCountByRuleOrRowId: Map<string, number>;
};

export type EvaluationResult = "succeeded" | "failed" | "skipped";
export type EvaluationResultsByNodeId = Map<string, NodeEvaluationResults>;
export type ValidationMessages = Record<string, any>;
export type OnDmnModelChange = (model: Normalized<DmnLatestModel>) => void;

export type OnRequestToJumpToPath = (normalizedPosixPathRelativeToTheOpenFile: string) => void;
export type OnRequestToResolvePath = (normalizedPosixPathRelativeToTheOpenFile: string) => string;

/** @returns a list of paths relative to the open file. */
export type OnRequestExternalModelsAvailableToInclude = () => Promise<string[]>;

export type OnRequestExternalModelByPath = (
  normalizedPosixPathRelativeToTheOpenFile: string
) => Promise<ExternalModel | null>;
export type ExternalModelsIndex = Record<
  string /** normalizedPosixPathRelativeToTheOpenFile */,
  ExternalModel | undefined
>;
export type ExternalModel = ({ type: "dmn" } & ExternalDmn) | ({ type: "pmml" } & ExternalPmml);

export type ExternalDmnsIndex = Map<string /** normalizedPosixPathRelativeToTheOpenFile */, ExternalDmn>;
export type ExternalDmn = {
  model: Normalized<DmnLatestModel>;
  normalizedPosixPathRelativeToTheOpenFile: string;
  svg: string;
};

export type ExternalPmmlsIndex = Map<string /** normalizedPosixPathRelativeToTheOpenFile */, ExternalPmml>;
export type ExternalPmml = { model: PMML; normalizedPosixPathRelativeToTheOpenFile: string };

export type DmnEditorProps = {
  /**
   * The DMN itself.
   */
  model: DmnLatestModel;
  /**
   * The original version of `model` before upgrading to `latest`.
   */
  originalVersion?: AllDmnMarshallers["version"];
  /**
   * Called when a change occurs on `model`, so the controlled flow of the component can be done.
   */
  onModelChange?: OnDmnModelChange;
  /**
   * Called when the contents of a specific available model is necessary. Used by the "Included models" tab.
   */
  onRequestExternalModelByPath?: OnRequestExternalModelByPath;
  /**
   * Called when the list of paths of available models to be included is needed. Used by the "Included models" tab.
   */
  onRequestExternalModelsAvailableToInclude?: OnRequestExternalModelsAvailableToInclude;
  /**
   * When the DMN represented by `model` ("This DMN") contains `import`ed models, this prop needs to map their contents by namespace.
   * The DMN model won't be correctly rendered if an included model is not found on this object.
   */
  externalModelsByNamespace?: ExternalModelsIndex;
  /**
   * To show information about evaluation results directly on the DMN diagram and/or Boxed Expression Editor, use this prop.
   */
  evaluationResultsByNodeId?: EvaluationResultsByNodeId;
  /**
   * To show information about validation messages directly on the DMN diagram and/or Boxed Expression Editor, use this prop.
   */
  validationMessages?: ValidationMessages;
  /**
   * The name of context in which this instance of DMN Editor is running. For example, if this DMN Editor instance
   * is displaying a model from a project called "My project", you could use `externalContextName={"My project"}`
   */
  externalContextName?: string;
  /**
   * Describe the context in which this instance of DMN Editor is running. For example, if this DMN Editor instance
   * is displaying a model from a project called "My project", you could use
   * `externalContextDescription={'All models (DMN and PMML) of "My project" are available.'}`
   */
  externalContextDescription?: string;
  /**
   * A link that will take users to an issue tracker so they can report problems they find on the DMN Editor.
   * This is shown on the ErrorBoundary fallback component, when an uncaught error happens.
   */
  issueTrackerHref?: string;
  /**
   * A flag to enable 'Evaluation Highlights' on supported channels (only ONLINE for now)
   */
  isEvaluationHighlightsSupported?: boolean;
  /**
   * A flag to enable read-only mode on the DMN Editor.
   * When enabled navigation is still possible (e.g. entering the Boxed Expression Editor, Data Types and Included Models),
   * but no changes can be made and the model itself is unaltered.
   */
  isReadOnly?: boolean;
  /**
   * Boolean flag to check whether the "Import DataTypes From JavaClasses" feature is available.
   */
  isImportDataTypesFromJavaClassesSupported?: boolean;
  /**
   * This object defines all the API methods which ImportJavaClasses component can use to dialog with the Code Completion Extension
   */
  javaCodeCompletionService?: JavaCodeCompletionService;
  /**
   * When users want to jump to another file, this method is called, allowing the controller of this component decide what to do.
   * Links are only rendered if this is provided. Otherwise, paths will be rendered as text.
   */
  onRequestToJumpToPath?: OnRequestToJumpToPath;
  /**
   * All paths inside the DMN Editor are relative. To be able to resolve them and display them as absolute paths, this function is called.
   * If undefined, the relative paths will be displayed.
   */
  onRequestToResolvePath?: OnRequestToResolvePath;
  /**
   * Notifies the caller when the DMN Editor performs a new edit after the debounce time.
   */
  onModelDebounceStateChanged?: (changed: boolean) => void;

  onOpenedBoxedExpressionEditorNodeChange?: (newOpenedNodeId: string | undefined) => void;
};

export const DmnEditorInternal = ({
  model,
  originalVersion,
  onModelChange,
  onOpenedBoxedExpressionEditorNodeChange,
  onModelDebounceStateChanged,
  forwardRef,
}: DmnEditorProps & { forwardRef?: React.Ref<DmnEditorRef> }) => {
  const boxedExpressionEditorActiveDrgElementId = useDmnEditorStore((s) => s.boxedExpressionEditor.activeDrgElementId);
  const dmnEditorActiveTab = useDmnEditorStore((s) => s.navigation.tab);
  const isBeePropertiesPanelOpen = useDmnEditorStore((s) => s.boxedExpressionEditor.propertiesPanel.isOpen);
  const isDiagramPropertiesPanelOpen = useDmnEditorStore((s) => s.diagram.propertiesPanel.isOpen);
  const navigationTab = useDmnEditorStore((s) => s.navigation.tab);
  const dmn = useDmnEditorStore((s) => s.dmn);
  const isDiagramEditingInProgress = useDmnEditorStore((s) => s.computed(s).isDiagramEditingInProgress());
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { commandsRef } = useCommands();

  const { dmnModelBeforeEditingRef, dmnEditorRootElementRef } = useDmnEditor();
  const { externalModelsByNamespace } = useExternalModels();

  // Code to keep FormDmnOutputs.tsx selected card highlight in proper state
  useEffect(() => {
    onOpenedBoxedExpressionEditorNodeChange?.(
      dmnEditorActiveTab === DmnEditorTab.EDITOR ? boxedExpressionEditorActiveDrgElementId : undefined
    );
  }, [boxedExpressionEditorActiveDrgElementId, dmnEditorActiveTab, onOpenedBoxedExpressionEditorNodeChange]);

  // Refs
  const diagramRef = useRef<DiagramRef>(null);
  const diagramContainerRef = useRef<HTMLDivElement>(null);
  const beeContainerRef = useRef<HTMLDivElement | null>(null);
  const drawerContentRef = useRef<HTMLDivElement | null>(null);

  // Allow imperativelly controlling the Editor.
  useImperativeHandle(
    forwardRef,
    () => ({
      reset: (model) => {
        const state = dmnEditorStoreApi.getState();
        return state.dispatch(state).dmn.reset(normalize(model));
      },
      openBoxedExpressionEditor: (nodeId: string) => {
        dmnEditorStoreApi.setState((state) => {
          state.navigation.tab = DmnEditorTab.EDITOR;
          state.dispatch(state).boxedExpressionEditor.open(nodeId);
        });
      },
      getDiagramSvg: async () => {
        const nodes = diagramRef.current?.getReactFlowInstance()?.getNodes();
        const edges = diagramRef.current?.getReactFlowInstance()?.getEdges();
        if (!nodes || !edges) {
          return undefined;
        }

        const bounds = RF.getNodesBounds(nodes);
        const state = dmnEditorStoreApi.getState();

        const svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
        svg.setAttribute("width", bounds.width + SVG_PADDING * 2 + "");
        svg.setAttribute(
          "height",
          // It's not possible to calculate the text height which is outside of the node
          // for the alternative input data shape
          bounds.height + (state.computed(state).isAlternativeInputDataShape() ? SVG_PADDING * 5 : SVG_PADDING * 2) + ""
        );

        // We're still on React 17.
        // eslint-disable-next-line react/no-deprecated
        ReactDOM.render(
          // Indepdent of where the nodes are located, they'll always be rendered at the top-left corner of the SVG
          <g transform={`translate(${-bounds.x + SVG_PADDING} ${-bounds.y + SVG_PADDING})`}>
            <DmnDiagramSvg
              nodes={nodes}
              edges={edges}
              snapGrid={state.diagram.snapGrid}
              importsByNamespace={state.computed(state).importsByNamespace()}
              thisDmn={state.dmn}
              isAlternativeInputDataShape={state.computed(state).isAlternativeInputDataShape()}
              allDataTypesById={state.computed(state).getDataTypes(externalModelsByNamespace).allDataTypesById}
              allTopLevelItemDefinitionUniqueNames={
                state.computed(state).getDataTypes(externalModelsByNamespace).allTopLevelItemDefinitionUniqueNames
              }
            />
          </g>,
          svg
        );

        return new XMLSerializer().serializeToString(svg);
      },
      getCommands: () => commandsRef.current,
    }),
    [dmnEditorStoreApi, externalModelsByNamespace, commandsRef]
  );

  // Make sure the DMN Editor reacts to props changing.
  useEffectAfterFirstRender(() => {
    dmnEditorStoreApi.setState((state) => {
      // Avoid unecessary state updates
      if (model === original(state.dmn.model)) {
        return;
      }

      state.diagram.autoLayout.canAutoGenerateDrd =
        model.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"] === undefined &&
        model.definitions.drgElement !== undefined;
      state.dmn.model = normalize(model);

      dmnModelBeforeEditingRef.current = state.dmn.model;
    });
  }, [dmnEditorStoreApi, model]);

  useStateAsItWasBeforeConditionBecameTrue(
    dmn.model,
    isDiagramEditingInProgress,
    useCallback((prev) => (dmnModelBeforeEditingRef.current = prev), [dmnModelBeforeEditingRef])
  );

  // Only notify changes when dragging/resizing operations are not happening.
  useEffectAfterFirstRender(() => {
    if (isDiagramEditingInProgress) {
      return;
    }
    onModelDebounceStateChanged?.(false);

    const timeout = setTimeout(() => {
      // Ignore changes made outside... If the controller of the component
      // changed its props, it knows it already, we don't need to call "onModelChange" again.
      if (model === dmn.model) {
        return;
      }

      onModelDebounceStateChanged?.(true);
      console.debug("DMN EDITOR: Model changed!");
      onModelChange?.(dmn.model);
    }, ON_MODEL_CHANGE_DEBOUNCE_TIME_IN_MS);

    return () => {
      clearTimeout(timeout);
    };
  }, [isDiagramEditingInProgress, onModelChange, dmn.model]);

  const onTabChanged = useCallback(
    (e, tab) => {
      dmnEditorStoreApi.setState((state) => {
        state.navigation.tab = tab;
        if (tab === DmnEditorTab.DATA_TYPES) {
          // Keep the last selected if any. Default to first on list.
          state.dataTypesEditor.activeItemDefinitionId =
            state.dataTypesEditor.activeItemDefinitionId ?? state.dmn.model.definitions.itemDefinition?.[0]?.["@_id"];
        }
      });
    },
    [dmnEditorStoreApi]
  );

  const tabTitle = useMemo(() => {
    return {
      editor: (
        <>
          <TabTitleIcon>
            <PficonTemplateIcon />
          </TabTitleIcon>
          <TabTitleText>Editor</TabTitleText>
        </>
      ),
      dataTypes: (
        <>
          <TabTitleIcon>
            <InfrastructureIcon />
          </TabTitleIcon>
          <TabTitleText>
            Data types&nbsp;&nbsp;
            <Label style={{ padding: "0 12px" }}>{dmn.model.definitions.itemDefinition?.length ?? 0}</Label>
          </TabTitleText>
        </>
      ),
      includedModels: (
        <>
          <TabTitleIcon>
            <FileIcon />
          </TabTitleIcon>
          <TabTitleText>
            Included models&nbsp;&nbsp;
            <Label style={{ padding: "0 12px" }}>{dmn.model.definitions.import?.length ?? 0}</Label>
          </TabTitleText>
        </>
      ),
    };
  }, [dmn.model.definitions.import?.length, dmn.model.definitions.itemDefinition?.length]);

  const diagramPropertiesPanel = useMemo(() => <DiagramPropertiesPanel />, []);
  const beePropertiesPanel = useMemo(() => <BoxedExpressionPropertiesPanel />, []);

  useEffect(() => {
    // This is the actual scrollableParentRef for BEE.
    drawerContentRef.current =
      (beeContainerRef?.current?.parentElement?.parentElement as HTMLDivElement | undefined) ?? null;
  }, []);

  return (
    <div ref={dmnEditorRootElementRef} className={"kie-dmn-editor--root"}>
      <Tabs
        isFilled={true}
        activeKey={navigationTab}
        onSelect={onTabChanged}
        role={"region"}
        className={"kie-dmn-editor--tabs"}
      >
        <Tab eventKey={DmnEditorTab.EDITOR} title={tabTitle.editor}>
          {navigationTab === DmnEditorTab.EDITOR && (
            <>
              {!boxedExpressionEditorActiveDrgElementId && (
                <Drawer isExpanded={isDiagramPropertiesPanelOpen} isInline={true} position={"right"}>
                  <DrawerContent panelContent={diagramPropertiesPanel}>
                    <DrawerContentBody>
                      <div
                        className={"kie-tools--dmn-editor--diagram-container"}
                        ref={diagramContainerRef}
                        data-testid={"kie-tools--dmn-editor--diagram-container"}
                      >
                        {originalVersion && <DmnVersionLabel version={originalVersion} />}
                        <Diagram ref={diagramRef} container={diagramContainerRef} />
                      </div>
                    </DrawerContentBody>
                  </DrawerContent>
                </Drawer>
              )}
              {boxedExpressionEditorActiveDrgElementId && (
                <Drawer isExpanded={isBeePropertiesPanelOpen} isInline={true} position={"right"}>
                  <DrawerContent panelContent={beePropertiesPanel}>
                    <DrawerContentBody>
                      <div className={"kie-dmn-editor--bee-container"} ref={beeContainerRef}>
                        <BoxedExpressionScreen container={drawerContentRef} />
                      </div>
                    </DrawerContentBody>
                  </DrawerContent>
                </Drawer>
              )}
            </>
          )}
        </Tab>

        <Tab eventKey={DmnEditorTab.DATA_TYPES} title={tabTitle.dataTypes}>
          <div data-testid={"kie-tools--dmn-editor--data-types-container"}>
            {navigationTab === DmnEditorTab.DATA_TYPES && <DataTypes />}
          </div>
        </Tab>

        <Tab eventKey={DmnEditorTab.INCLUDED_MODELS} title={tabTitle.includedModels}>
          <div data-testid={"kie-tools--dmn-editor--included-models-container"}>
            {navigationTab === DmnEditorTab.INCLUDED_MODELS && <IncludedModels />}
          </div>
        </Tab>
      </Tabs>
    </div>
  );
};

export const DmnEditor = React.forwardRef((props: DmnEditorProps, ref: React.Ref<DmnEditorRef>) => {
  const store = useMemo(
    () => createDmnEditorStore(props.model, new ComputedStateCache<Computed>(INITIAL_COMPUTED_CACHE)),
    // Purposefully empty. This memoizes the initial value of the store
    // eslint-disable-next-line react-hooks/exhaustive-deps
    []
  );
  const storeRef = React.useRef<StoreApiType>(store);

  const resetState: ErrorBoundaryPropsWithFallback["onReset"] = useCallback(({ args }) => {
    storeRef.current?.setState((state) => {
      state.diagram = defaultStaticState().diagram;
      state.dmn.model = args[0];
    });
  }, []);

  return (
    <DmnEditorContextProvider {...props}>
      <ErrorBoundary FallbackComponent={DmnEditorErrorFallback} onReset={resetState}>
        <DmnEditorSettingsContextProvider {...props}>
          <DmnEditorExternalModelsContextProvider {...props}>
            <DmnEditorStoreApiContext.Provider value={storeRef.current}>
              <CommandsContextProvider>
                <DmnEditorInternal forwardRef={ref} {...props} />
              </CommandsContextProvider>
            </DmnEditorStoreApiContext.Provider>
          </DmnEditorExternalModelsContextProvider>
        </DmnEditorSettingsContextProvider>
      </ErrorBoundary>
    </DmnEditorContextProvider>
  );
});

export function usePrevious<T>(value: T) {
  const [current, setCurrent] = useState<T>(value);
  const [previous, setPrevious] = useState<T>(value);

  if (value !== current) {
    setPrevious(current);
    setCurrent(value);
  }

  return previous;
}

/**
 *
 * @param state The state to save when condition is true
 * @param condition Boolean that, when becomes true, sets the ref with the previous value of the first parameter -- `state`.
 * @param ref The ref that stores the value
 * @returns The ref that was given as the 3rd parameter.
 */
export function useStateAsItWasBeforeConditionBecameTrue<T>(state: T, condition: boolean, set: (prev: T) => void) {
  const previous = usePrevious(state);

  useEffect(() => {
    if (condition) {
      console.debug("HOOK: `useStateBeforeCondition` --> ASSIGN");
      set(previous);
    }
    // !!!! EXCEPTIONAL CASE: Ignore "previous" changes on purpose, as we only want to save the last state before `condition` became true.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [condition, set]);
}
