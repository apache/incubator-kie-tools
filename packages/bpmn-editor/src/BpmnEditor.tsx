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

import { AllBpmnMarshallers, BpmnLatestModel } from "@kie-tools/bpmn-marshaller";
import { DmnLatestModel } from "@kie-tools/dmn-marshaller";
import { Drawer, DrawerContent, DrawerContentBody } from "@patternfly/react-core/dist/js/components/Drawer";
import { original, WritableDraft } from "immer";
import * as React from "react";
import { useCallback, useImperativeHandle, useMemo, useRef } from "react";
import * as ReactDOM from "react-dom";
import { ErrorBoundary, ErrorBoundaryPropsWithFallback } from "react-error-boundary";
import * as RF from "reactflow";
import { BpmnEditorContextProvider, useBpmnEditor } from "./BpmnEditorContext";
import { BpmnEditorErrorFallback } from "./BpmnEditorErrorFallback";
import { BpmnDiagram } from "./diagram/BpmnDiagram";
import { BpmnVersionLabel } from "./diagram/BpmnVersionLabel";
import { BpmnEditorExternalModelsContextProvider } from "./externalModels/BpmnEditorExternalModelsContext";
import { Normalized, normalize } from "./normalization/normalize";
import { INITIAL_COMPUTED_CACHE } from "./store/initialComputedCache";
import { ComputedStateCache } from "@kie-tools/xyflow-react-kie-diagram/dist/store/ComputedStateCache";
import { XyFlowReactKieDiagramStoreApiContext } from "@kie-tools/xyflow-react-kie-diagram/dist/store/Store";
import { State, createBpmnEditorStore, getDefaultStaticState } from "./store/Store";
import {
  BpmnEditorStoreApiContext,
  StoreApiType,
  useBpmnEditorStore,
  useBpmnEditorStoreApi,
} from "./store/StoreContext";
import { BpmnDiagramSvg } from "./svg/BpmnDiagramSvg";
import { useStateAsItWasBeforeConditionBecameTrue } from "@kie-tools/xyflow-react-kie-diagram/dist/reactExt/useStateAsItWasBeforeConditionBecameTrue";
import { useEffectAfterFirstRender } from "@kie-tools/xyflow-react-kie-diagram/dist/reactExt/useEffectAfterFirstRender";
import { Commands, CommandsContextProvider, useCommands } from "./commands/CommandsContextProvider";
import { DiagramRef } from "@kie-tools/xyflow-react-kie-diagram/dist/diagram/XyFlowReactKieDiagram";
import { BpmnDiagramEdgeData, BpmnDiagramNodeData, BpmnNodeElement, BpmnNodeType } from "./diagram/BpmnDiagramDomain";
import { PropertiesPanel } from "./propertiesPanel/PropertiesPanel";
import { ns as bpmn20ns } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/meta";

// Leave custom CSS always for last.
import "@kie-tools/xyflow-react-kie-diagram/dist/patternfly-customizations.css";
import "@kie-tools/xyflow-react-kie-diagram/dist/xyflow-customizations.css";
import "./BpmnEditor.css";
import { BPMN20__tProcess, BPMN20__tTask } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import {
  BpmnEditorCustomTasksContextProvider,
  useCustomTasks,
} from "./customTasks/BpmnEditorCustomTasksContextProvider";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";
import { bpmnEditorI18nDefaults, bpmnEditorDictionaries, BpmnEditorI18nContext } from "./i18n";

const ON_MODEL_CHANGE_DEBOUNCE_TIME_IN_MS = 500;

const SVG_PADDING = 20;

/////////////////////////////////////////////////////
///// (begin) WORKAROUND FOR DATA MAPPING EXPRESSIONS
export const xsiNs = new Map<string, string>([
  ["http://www.w3.org/2001/XMLSchema-instance", ""],
  ["", "http://www.w3.org/2001/XMLSchema-instance"],
]);

export const XSI_NS = "xsi:";
export type XSI = "xsi";

bpmn20ns.set(XSI_NS, xsiNs.get("")!);
bpmn20ns.set(xsiNs.get("")!, XSI_NS);

///// (end) WORKAROUND FOR DATA MAPPING EXPRESSIONS
/////////////////////////////////////////////////////

export type BpmnEditorRef = {
  reset: (mode: BpmnLatestModel) => void;
  getDiagramSvg: () => Promise<string | undefined>;
  getCommands: () => Commands;
};

export type OnBpmnModelChange = (model: Normalized<BpmnLatestModel>) => void;

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
export type ExternalModel = { type: "dmn" } & ExternalDmn;

export type ExternalDmnsIndex = Map<string /** normalizedPosixPathRelativeToTheOpenFile */, ExternalDmn>;

export type ExternalDmn = {
  model: DmnLatestModel;
  normalizedPosixPathRelativeToTheOpenFile: string;
  svg: string;
};

export type CustomTask = {
  /**
   * A unique identifier for this Custom Task.
   */
  id: string;
  /**
   * The name of the Group this Custom Task will be placed in the Palette.
   */
  displayGroup: string;
  /**
   * The name of this Custom Task.
   */
  displayName: string;
  /**
   * A longer text describing this Custom Task in more detail.
   */
  displayDescription: string;
  /**
   * The icon of this Custom Task. Please use an <svg> element.
   */
  iconSvgElement: React.ReactElement;
  /**
   * Names of Data Mapping inputs that you don't want displayed in the "Data Mapping" modal.
   */
  dataInputReservedNames: string[];
  /**
   * Names of Data Mapping outputs that you don't want displayed in the "Data Mapping" modal.
   */
  dataOutputReservedNames: string[];
  /**
   * A React Component that will be rendered when Properties Panel is open for nodes matching this Custom Task.
   */
  propertiesPanelComponent: React.ComponentType<{
    task: ElementFilter<Unpacked<NonNullable<Normalized<BPMN20__tProcess["flowElement"]>>>, "task"> & {
      __$$element: "task";
    };
  }>;
  /**
   *
   * @param task A Task element
   * @returns A boolean value representing whether or not the `task` parameter is an instance of this Custom Task.
   */
  matches: (task: ElementFilter<Unpacked<NonNullable<Normalized<BPMN20__tProcess["flowElement"]>>>, "task">) => boolean;
  /**
   *
   * @returns The Task element representing this Custom Task. Pair this function with `onAdded` if you need more control over what gets added to the BPMN model.
   */
  produce: () => ElementFilter<Unpacked<NonNullable<Normalized<BPMN20__tProcess["flowElement"]>>>, "task">;
  /**
   * Called when an instance of this Custom Task is dragged into the BPMN model or when any Task morphs to it.
   * @param state A WritableDraft allowing you to modify the BPMN Editor's state in-place when this Custom Task is added to the BPMN model.
   */
  onAdded?: (
    state: WritableDraft<State>,
    task: ElementFilter<Unpacked<NonNullable<Normalized<BPMN20__tProcess["flowElement"]>>>, "task">
  ) => void;
};

export type BpmnEditorProps = {
  /**
   * The BPMN itself.
   */
  model: BpmnLatestModel;
  /**
   * The original version of `model` before upgrading to `latest`.
   */
  originalVersion?: AllBpmnMarshallers["version"];
  /**
   * Definitions of custom BPMN20_tTask elements.
   */
  customTasks?: CustomTask[];
  /**
   * The ReactElement to render on the button which opens the Custom Tasks panel.
   */
  customTasksPaletteIcon?: React.ReactNode;
  /**
   * Called when a change occurs on `model`, so the controlled flow of the component can be done.
   */
  onModelChange?: OnBpmnModelChange;
  /**
   * Called when the contents of a specific available model is necessary. Used by the "Business Rule Task" node for DMN model auto-detection.
   */
  onRequestExternalModelByPath?: OnRequestExternalModelByPath;
  /**
   * Called when the list of paths of available models to be included is needed. Used by the "Business Rule Task" node for DMN model auto-detection.
   */
  onRequestExternalModelsAvailableToInclude?: OnRequestExternalModelsAvailableToInclude;
  /**
   * When the DMN represented by `model` ("This DMN") contains `import`ed models, this prop needs to map their contents by namespace.
   * The DMN model won't be correctly rendered if an included model is not found on this object.
   */
  externalModelsByNamespace?: ExternalModelsIndex;
  /**
   * The name of context in which this instance of BPMN Editor is running. For example, if this BPMN Editor instance
   * is displaying a model from a project called "My project", you could use `externalContextName={"My project"}`
   */
  externalContextName?: string;
  /**
   * Describe the context in which this instance of BPMN Editor is running. For example, if this BPMN Editor instance
   * is displaying a model from a project called "My project", you could use
   * `externalContextDescription={'All models (DMN, etc) of "My project" are available.'}`
   */
  externalContextDescription?: string;
  /**
   * A link that will take users to an issue tracker so they can report problems they find on the BPMN Editor.
   * This is shown on the ErrorBoundary fallback component, when an uncaught error happens.
   */
  issueTrackerHref?: string;
  /**
   * When users want to jump to another file, this method is called, allowing the controller of this component decide what to do.
   * Links are only rendered if this is provided. Otherwise, paths will be rendered as text.
   */
  onRequestToJumpToPath?: OnRequestToJumpToPath;
  /**
   * All paths inside the BPMN Editor are relative. To be able to resolve them and display them as absolute paths, this function is called.
   * If undefined, the relative paths will be displayed.
   */
  onRequestToResolvePath?: OnRequestToResolvePath;
  /**
   * Notifies the caller when the BPMN Editor performs a new edit after the debounce time.
   */
  onModelDebounceStateChanged?: (changed: boolean) => void;
  locale: string;
};

export const BpmnEditorInternal = ({
  model,
  originalVersion,
  onModelChange,
  onModelDebounceStateChanged,
  forwardRef,
  locale,
}: BpmnEditorProps & { forwardRef?: React.Ref<BpmnEditorRef> }) => {
  const isPropertiesPanelOpen = useBpmnEditorStore((s) => s.propertiesPanel.isOpen);
  const bpmn = useBpmnEditorStore((s) => s.bpmn);
  const isDiagramEditingInProgress = useBpmnEditorStore((s) => s.computed(s).isDiagramEditingInProgress());
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();
  const { commandsRef } = useCommands();
  const { customTasks } = useCustomTasks();

  const { bpmnModelBeforeEditingRef, bpmnEditorRootElementRef } = useBpmnEditor();

  // Refs
  const diagramRef = useRef<DiagramRef<BpmnNodeType, BpmnDiagramNodeData, BpmnDiagramEdgeData>>(null);
  const diagramContainerRef = useRef<HTMLDivElement>(null);

  // Allow imperativelly controlling the Editor.
  useImperativeHandle(
    forwardRef,
    () => ({
      reset: (model) => {
        const state = bpmnEditorStoreApi.getState();
        return state.dispatch(state).reset(normalize(model));
      },
      getDiagramSvg: async () => {
        const nodes = diagramRef.current?.getReactFlowInstance()?.getNodes() as  // This casting is required because XYFlow doesn't correctly type the "getNodes()" function with the node types.
          | undefined
          | RF.Node<BpmnDiagramNodeData<BpmnNodeElement>, BpmnNodeType>[];

        const edges = diagramRef.current?.getReactFlowInstance()?.getEdges();
        if (!nodes || !edges) {
          return undefined;
        }

        const bounds = RF.getNodesBounds(nodes);
        const state = bpmnEditorStoreApi.getState();

        const svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
        svg.setAttribute("width", bounds.width + SVG_PADDING * 2 + "");
        svg.setAttribute(
          "height",
          // It's not possible to calculate the text height which is outside of the node for the dataObject node shape
          bounds.height + SVG_PADDING * 5 + ""
        );

        // We're still on React 17.
        // eslint-disable-next-line react/no-deprecated
        ReactDOM.render(
          // Indepdent of where the nodes are located, they'll always be rendered at the top-left corner of the SVG
          <g transform={`translate(${-bounds.x + SVG_PADDING} ${-bounds.y + SVG_PADDING})`}>
            <BpmnDiagramSvg
              nodes={nodes}
              edges={edges}
              customTasks={customTasks}
              snapGrid={state.xyFlowReactKieDiagram.snapGrid}
            />
          </g>,
          svg
        );

        return new XMLSerializer().serializeToString(svg);
      },
      getCommands: () => commandsRef.current,
    }),
    [bpmnEditorStoreApi, commandsRef, customTasks]
  );

  // Make sure the BPMN Editor reacts to props changing.
  useEffectAfterFirstRender(() => {
    bpmnEditorStoreApi.setState((state) => {
      // Avoid unecessary state updates
      if (model === original(state.bpmn.model)) {
        return;
      }

      state.bpmn.model = normalize(model);

      bpmnModelBeforeEditingRef.current = state.bpmn.model;
    });
  }, [bpmnEditorStoreApi, model]);

  useStateAsItWasBeforeConditionBecameTrue(
    bpmn.model,
    isDiagramEditingInProgress,
    useCallback((prev) => (bpmnModelBeforeEditingRef.current = prev), [bpmnModelBeforeEditingRef])
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
      if (model === bpmn.model) {
        return;
      }

      onModelDebounceStateChanged?.(true);
      console.debug("BPMN EDITOR: Model changed!");
      onModelChange?.(bpmn.model);
    }, ON_MODEL_CHANGE_DEBOUNCE_TIME_IN_MS);

    return () => {
      clearTimeout(timeout);
    };
  }, [isDiagramEditingInProgress, onModelChange, bpmn.model]);

  const propertiesPanel = useMemo(() => <PropertiesPanel />, []);

  return (
    <div ref={bpmnEditorRootElementRef} className={"kie-bpmn-editor--root"}>
      <Drawer isExpanded={isPropertiesPanelOpen} isInline={true} position={"right"}>
        <DrawerContent panelContent={propertiesPanel}>
          <DrawerContentBody>
            <div
              className={"kie-bpmn-editor--diagram-container"}
              ref={diagramContainerRef}
              data-testid={"kie-bpmn-editor--diagram-container"}
            >
              {originalVersion && <BpmnVersionLabel version={originalVersion} />}
              <BpmnDiagram diagramRef={diagramRef} container={diagramContainerRef} locale={locale} />
            </div>
          </DrawerContentBody>
        </DrawerContent>
      </Drawer>
    </div>
  );
};

export const BpmnEditor = React.forwardRef((props: BpmnEditorProps, ref: React.Ref<BpmnEditorRef>) => {
  const store = useMemo(
    () =>
      createBpmnEditorStore(props.model, new ComputedStateCache<ReturnType<State["computed"]>>(INITIAL_COMPUTED_CACHE)),
    // Purposefully empty. This memoizes the initial value of the store
    // eslint-disable-next-line react-hooks/exhaustive-deps
    []
  );
  const storeRef = React.useRef<StoreApiType>(store);

  const resetState: ErrorBoundaryPropsWithFallback["onReset"] = useCallback(({ args }) => {
    storeRef.current?.setState((state) => {
      state.diagram = getDefaultStaticState().diagram;
      state.bpmn.model = args[0];
    });
  }, []);

  return (
    <I18nDictionariesProvider
      defaults={bpmnEditorI18nDefaults}
      dictionaries={bpmnEditorDictionaries}
      initialLocale={props.locale}
      ctx={BpmnEditorI18nContext}
    >
      <BpmnEditorContextProvider {...props}>
        <ErrorBoundary FallbackComponent={BpmnEditorErrorFallback} onReset={resetState}>
          <BpmnEditorCustomTasksContextProvider {...props}>
            <BpmnEditorExternalModelsContextProvider {...props}>
              <BpmnEditorStoreApiContext.Provider value={storeRef.current}>
                <XyFlowReactKieDiagramStoreApiContext.Provider value={storeRef.current}>
                  <CommandsContextProvider>
                    <BpmnEditorInternal forwardRef={ref} {...props} />
                  </CommandsContextProvider>
                </XyFlowReactKieDiagramStoreApiContext.Provider>
              </BpmnEditorStoreApiContext.Provider>
            </BpmnEditorExternalModelsContextProvider>
          </BpmnEditorCustomTasksContextProvider>
        </ErrorBoundary>
      </BpmnEditorContextProvider>
    </I18nDictionariesProvider>
  );
});
