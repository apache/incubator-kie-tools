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
import { Diagram, DiagramRef } from "./diagram/Diagram";
import { SwfEditorContextProvider, useSwfEditor } from "./SwfEditorContext";
import { SwfEditorErrorFallback } from "./SwfEditorErrorFallback";
import { ComputedStateCache } from "./store/ComputedStateCache";
import { Computed, createSwfEditorStore, defaultStaticState } from "./store/Store";
import { SwfEditorStoreApiContext, StoreApiType, useSwfEditorStore, useSwfEditorStoreApi } from "./store/StoreContext";
import { SwfDiagramSvg } from "./svg/SwfDiagramSvg";
import { useEffectAfterFirstRender } from "./useEffectAfterFirstRender";
import { INITIAL_COMPUTED_CACHE } from "./store/computed/initial";
import { Commands, CommandsContextProvider, useCommands } from "./commands/CommandsContextProvider";
import { SwfEditorSettingsContextProvider } from "./settings/SwfEditorSettingsContext";
import { Specification } from "@serverlessworkflow/sdk-typescript";
import "./SwfEditor.css"; // Leave it for last, as this overrides some of the PF and RF styles.
import { swfEditorDictionaries, SwfEditorI18nContext, swfEditorI18nDefaults, useSwfEditorI18n } from "./i18n";
import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";

const ON_MODEL_CHANGE_DEBOUNCE_TIME_IN_MS = 500;

const SVG_PADDING = 20;

export type SwfEditorRef = {
  reset: (model: Specification.IWorkflow) => void;
  getDiagramSvg: () => Promise<string | undefined>;
  getCommands: () => Commands;
};

export type OnSwfModelChange = (model: Specification.IWorkflow) => void;

export type SwfEditorProps = {
  /**
   * The SWF itself.
   */
  model: Specification.IWorkflow;
  /**
   * Called when a change occurs on `model`, so the controlled flow of the component can be done.
   */
  onModelChange?: OnSwfModelChange;
  /**
   * A link that will take users to an issue tracker so they can report problems they find on the SWF Editor.
   * This is shown on the ErrorBoundary fallback component, when an uncaught error happens.
   */
  issueTrackerHref?: string;
  /**
   * A flag to enable read-only mode on the SWF Editor.
   * When enabled navigation is still possible,
   * but no changes can be made and the model itself is unaltered.
   */
  isReadOnly?: boolean;
  /**
   * Notifies the caller when the SWF Editor performs a new edit after the debounce time.
   */
  onModelDebounceStateChanged?: (changed: boolean) => void;

  locale: string;
};

export const SwfEditorInternal = ({
  model,
  onModelChange,
  onModelDebounceStateChanged,
  forwardRef,
}: SwfEditorProps & { forwardRef?: React.Ref<SwfEditorRef> }) => {
  const { i18n } = useSwfEditorI18n();
  const swf = useSwfEditorStore((s) => s.swf);
  const isDiagramEditingInProgress = useSwfEditorStore((s) => s.computed(s).isDiagramEditingInProgress());
  const swfEditorStoreApi = useSwfEditorStoreApi();
  const { commandsRef } = useCommands();

  const { swfModelBeforeEditingRef, swfEditorRootElementRef } = useSwfEditor();

  // Refs
  const diagramRef = useRef<DiagramRef>(null);
  const diagramContainerRef = useRef<HTMLDivElement>(null);

  // Allow imperativelly controlling the Editor.
  useImperativeHandle(
    forwardRef,
    () => ({
      reset: (model) => {
        const state = swfEditorStoreApi.getState();
        return state.dispatch(state).swf.reset(model);
      },
      getDiagramSvg: async () => {
        const nodes = diagramRef.current?.getReactFlowInstance()?.getNodes();
        const edges = diagramRef.current?.getReactFlowInstance()?.getEdges();
        if (!nodes || !edges) {
          return undefined;
        }

        const bounds = RF.getNodesBounds(nodes);
        const state = swfEditorStoreApi.getState();

        const svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
        svg.setAttribute("width", bounds.width + SVG_PADDING * 2 + "");
        svg.setAttribute("height", bounds.height + SVG_PADDING * 2 + "");

        // We're still on React 17.
        // eslint-disable-next-line react/no-deprecated
        ReactDOM.render(
          // Indepdent of where the nodes are located, they'll always be rendered at the top-left corner of the SVG
          <g transform={`translate(${-bounds.x + SVG_PADDING} ${-bounds.y + SVG_PADDING})`}>
            <SwfDiagramSvg nodes={nodes} edges={edges} snapGrid={state.diagram.snapGrid} thisSwf={state.swf} />
          </g>,
          svg
        );

        return new XMLSerializer().serializeToString(svg);
      },
      getCommands: () => commandsRef.current,
    }),
    [swfEditorStoreApi, commandsRef]
  );

  // Make sure the SWF Editor reacts to props changing.
  useEffectAfterFirstRender(() => {
    swfEditorStoreApi.setState((state) => {
      // Avoid unecessary state updates
      if (model === original(state.swf.model)) {
        return;
      }

      state.diagram.autoLayout.canAutoGenerate = true;
      state.swf.model = model;

      swfModelBeforeEditingRef.current = state.swf.model;
    });
  }, [swfEditorStoreApi, model]);

  useStateAsItWasBeforeConditionBecameTrue(
    swf.model,
    isDiagramEditingInProgress,
    useCallback((prev) => (swfModelBeforeEditingRef.current = prev), [swfModelBeforeEditingRef])
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
      if (model === swf.model) {
        return;
      }

      onModelDebounceStateChanged?.(true);
      console.debug("SWF EDITOR: Model changed!");
      onModelChange?.(swf.model);
    }, ON_MODEL_CHANGE_DEBOUNCE_TIME_IN_MS);

    return () => {
      clearTimeout(timeout);
    };
  }, [isDiagramEditingInProgress, onModelChange, swf.model]);

  return (
    <div ref={swfEditorRootElementRef} className={"kie-swf-editor--root"}>
      <>
        {
          <div
            className={"kie-tools--swf-editor--diagram-container"}
            ref={diagramContainerRef}
            data-testid={"kie-tools--swf-editor--diagram-container"}
          >
            <Diagram ref={diagramRef} container={diagramContainerRef} />
          </div>
        }
      </>
    </div>
  );
};

export const SwfEditor = React.forwardRef((props: SwfEditorProps, ref: React.Ref<SwfEditorRef>) => {
  const store = useMemo(
    () => createSwfEditorStore(props.model, new ComputedStateCache<Computed>(INITIAL_COMPUTED_CACHE)),
    // Purposefully empty. This memoizes the initial value of the store
    // eslint-disable-next-line react-hooks/exhaustive-deps
    []
  );
  const storeRef = React.useRef<StoreApiType>(store);

  const resetState: ErrorBoundaryPropsWithFallback["onReset"] = useCallback(({ args }) => {
    storeRef.current?.setState((state) => {
      state.diagram = defaultStaticState().diagram;
      state.swf.model = args[0];
    });
  }, []);

  return (
    <I18nDictionariesProvider
      defaults={swfEditorI18nDefaults}
      dictionaries={swfEditorDictionaries}
      initialLocale={props.locale}
      ctx={SwfEditorI18nContext}
    >
      <SwfEditorContextProvider {...props}>
        <ErrorBoundary FallbackComponent={SwfEditorErrorFallback} onReset={resetState}>
          <SwfEditorSettingsContextProvider {...props}>
            <SwfEditorStoreApiContext.Provider value={storeRef.current}>
              <CommandsContextProvider>
                <SwfEditorInternal forwardRef={ref} {...props} />
              </CommandsContextProvider>
            </SwfEditorStoreApiContext.Provider>
          </SwfEditorSettingsContextProvider>
        </ErrorBoundary>
      </SwfEditorContextProvider>
    </I18nDictionariesProvider>
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
