import "@patternfly/react-core/dist/styles/base.css";
import "reactflow/dist/style.css";

import * as React from "react";
import { useCallback, useEffect, useImperativeHandle, useRef, useState, useMemo } from "react";
import { Drawer, DrawerContent, DrawerContentBody } from "@patternfly/react-core/dist/js/components/Drawer";
import { Tab, TabTitleIcon, TabTitleText, Tabs } from "@patternfly/react-core/dist/js/components/Tabs";
import { FileIcon } from "@patternfly/react-icons/dist/js/icons/file-icon";
import { InfrastructureIcon } from "@patternfly/react-icons/dist/js/icons/infrastructure-icon";
import { PficonTemplateIcon } from "@patternfly/react-icons/dist/js/icons/pficon-template-icon";
import { BoxedExpression } from "./boxedExpressions/BoxedExpression";
import { DataTypes } from "./dataTypes/DataTypes";
import { Diagram } from "./diagram/Diagram";
import { DmnVersionLabel } from "./diagram/DmnVersionLabel";
import { IncludedModels } from "./includedModels/IncludedModels";
import { DiagramPropertiesPanel } from "./propertiesPanel/DiagramPropertiesPanel";
import {
  DmnEditorStoreApiContext,
  DmnEditorTab,
  StoreApiType,
  createDmnEditorStore,
  defaultStaticState,
  useDmnEditorStore,
  useDmnEditorStoreApi,
} from "./store/Store";
import { useEffectAfterFirstRender } from "./useEffectAfterFirstRender";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { BeePropertiesPanel } from "./propertiesPanel/BeePropertiesPanel";
import { DmnEditorDerivedStoreContextProvider, useDmnEditorDerivedStore } from "./store/DerivedStore";
import { DmnEditorContextProvider, useDmnEditor } from "./DmnEditorContext";
import { DmnEditorExternalModelsContextProvider } from "./includedModels/DmnEditorDependenciesContext";
import { ErrorBoundary, ErrorBoundaryPropsWithFallback } from "react-error-boundary";
import { DmnEditorErrorFallback } from "./DmnEditorErrorFallback";
import { DmnLatestModel, AllDmnMarshallers } from "@kie-tools/dmn-marshaller";
import { PMML } from "@kie-tools/pmml-editor-marshaller";
import { original } from "immer";

import "@kie-tools/dmn-marshaller/dist/kie-extensions"; // This is here because of the KIE Extension for DMN.
import "./DmnEditor.css"; // Leave it for last, as this overrides some of the PF and RF styles.

const ON_MODEL_CHANGE_DEBOUNCE_TIME_IN_MS = 500;

export type DmnEditorRef = {
  reset: (mode: DmnLatestModel) => void;
};

export type EvaluationResults = Record<string, any>;
export type ValidationMessages = Record<string, any>;
export type OnDmnModelChange = (model: DmnLatestModel) => void;

export type OnRequestToJumpToPath = (pathRelativeToTheOpenFile: string) => void;
export type OnRequestToResolvePath = (pathRelativeToTheOpenFile: string) => string;
export type OnRequestExternalModelsAvailableToInclude = () => Promise<string[]>;
export type OnRequestExternalModelByPath = (pathRelativeToTheOpenFile: string) => Promise<ExternalModel | null>;
export type ExternalModelsIndex = Record<string /** pathRelativeToTheOpenFile */, ExternalModel | undefined>;
export type ExternalModel = ({ type: "dmn" } & ExternalDmn) | ({ type: "pmml" } & ExternalPmml);

export type ExternalDmnsIndex = Map<string /** pathRelativeToTheOpenFile */, ExternalDmn>;
export type ExternalDmn = { model: DmnLatestModel; pathRelativeToTheOpenFile: string; svg: string };

export type ExternalPmmlsIndex = Map<string /** pathRelativeToTheOpenFile */, ExternalPmml>;
export type ExternalPmml = { model: PMML; pathRelativeToTheOpenFile: string };

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
   * To show information about execution results directly on the DMN diagram and/or Boxed Expression Editor, use this prop.
   */
  evaluationResults?: EvaluationResults;
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
   * When users want to jump to another file, this method is called, allowing the controller of this component decide what to do.
   * Links are only rendered if this is provided. Otherwise, paths will be rendered as text.
   */
  onRequestToJumpToPath?: OnRequestToJumpToPath;
  /**
   * All paths inside the DMN Editor are relative. To be able to resolve them and display them as absolute paths, this function is called.
   * If undefined, the relative paths will be displayed.
   */
  onRequestToResolvePath?: OnRequestToResolvePath;
};

export const DmnEditorInternal = ({
  model,
  originalVersion,
  onModelChange,
  forwardRef,
}: DmnEditorProps & { forwardRef?: React.Ref<DmnEditorRef> }) => {
  const { boxedExpressionEditor, dmn, navigation, dispatch, diagram } = useDmnEditorStore((s) => s);

  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { isDiagramEditingInProgress } = useDmnEditorDerivedStore();
  const { dmnModelBeforeEditingRef, dmnEditorRootElementRef } = useDmnEditor();

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
      // Avoid unecessary state updates
      if (model === original(state.dmn.model)) {
        return;
      }
      state.dmn.model = model;
      dmnModelBeforeEditingRef.current = model;
    });
  }, [dmnEditorStoreApi, dispatch.dmn, model]);

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

    const timeout = setTimeout(() => {
      // Ignore changes made outside... If the controller of the component
      // changed its props, it knows it already, we don't need to call "onModelChange" again.
      if (model === dmn.model) {
        return;
      }

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

  const diagramContainerRef = useRef<HTMLDivElement>(null);
  const beeContainerRef = useRef<HTMLDivElement>(null);

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
  const beePropertiesPanel = useMemo(() => <BeePropertiesPanel />, []);

  return (
    <div ref={dmnEditorRootElementRef} className={"kie-dmn-editor--root"}>
      <Tabs
        isFilled={true}
        activeKey={navigation.tab}
        onSelect={onTabChanged}
        role={"region"}
        className={"kie-dmn-editor--tabs"}
      >
        <Tab eventKey={DmnEditorTab.EDITOR} title={tabTitle.editor}>
          {navigation.tab === DmnEditorTab.EDITOR && (
            <>
              {!boxedExpressionEditor.activeDrgElementId && (
                <Drawer isExpanded={diagram.propertiesPanel.isOpen} isInline={true} position={"right"}>
                  <DrawerContent panelContent={diagramPropertiesPanel}>
                    <DrawerContentBody>
                      <div className={"kie-dmn-editor--diagram-container"} ref={diagramContainerRef}>
                        {originalVersion && <DmnVersionLabel version={originalVersion} />}
                        <Diagram container={diagramContainerRef} />
                      </div>
                    </DrawerContentBody>
                  </DrawerContent>
                </Drawer>
              )}
              {boxedExpressionEditor.activeDrgElementId && (
                <Drawer isExpanded={boxedExpressionEditor.propertiesPanel.isOpen} isInline={true} position={"right"}>
                  <DrawerContent panelContent={beePropertiesPanel}>
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

        <Tab eventKey={DmnEditorTab.DATA_TYPES} title={tabTitle.dataTypes}>
          {navigation.tab === DmnEditorTab.DATA_TYPES && <DataTypes />}
        </Tab>

        <Tab eventKey={DmnEditorTab.INCLUDED_MODELS} title={tabTitle.includedModels}>
          {navigation.tab === DmnEditorTab.INCLUDED_MODELS && <IncludedModels />}
        </Tab>
      </Tabs>
    </div>
  );
};

export const DmnEditor = React.forwardRef((props: DmnEditorProps, ref: React.Ref<DmnEditorRef>) => {
  const storeRef = React.useRef<StoreApiType>(createDmnEditorStore(props.model));

  const resetState: ErrorBoundaryPropsWithFallback["onReset"] = useCallback(({ args }) => {
    storeRef.current?.setState((state) => {
      state.diagram = defaultStaticState().diagram;
      state.dmn.model = args[0];
    });
  }, []);

  return (
    <DmnEditorContextProvider {...props}>
      <ErrorBoundary FallbackComponent={DmnEditorErrorFallback} onReset={resetState}>
        <DmnEditorExternalModelsContextProvider {...props}>
          <DmnEditorStoreApiContext.Provider value={storeRef.current}>
            <DmnEditorDerivedStoreContextProvider>
              <DmnEditorInternal forwardRef={ref} {...props} />
            </DmnEditorDerivedStoreContextProvider>
          </DmnEditorStoreApiContext.Provider>
        </DmnEditorExternalModelsContextProvider>
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
