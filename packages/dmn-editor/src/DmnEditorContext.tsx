import * as React from "react";
import { useContext, useMemo, useRef } from "react";
import { DmnModel } from "./store/Store";
import { DmnEditorProps } from "./DmnEditor";

export type DmnEditorContextProviderProps = Pick<
  DmnEditorProps,
  "includedModelsContextDescription" | "includedModelsContextName" | "issueTrackerHref" | "model"
>;

export type DmnModelBeforeEditing = DmnModel;

export type DmnEditorContextType = Pick<
  DmnEditorContextProviderProps,
  "includedModelsContextDescription" | "includedModelsContextName" | "issueTrackerHref"
> & {
  dmnModelBeforeEditingRef: React.MutableRefObject<DmnModelBeforeEditing>;
};

const DmnEditorContext = React.createContext<DmnEditorContextType>({} as any);

export function useDmnEditor() {
  return useContext(DmnEditorContext);
}

export function DmnEditorContextProvider(props: React.PropsWithChildren<DmnEditorContextProviderProps>) {
  const dmnModelBeforeEditingRef = useRef<DmnModelBeforeEditing>(props.model);

  const value = useMemo<DmnEditorContextType>(
    () => ({
      dmnModelBeforeEditingRef,
      includedModelsContextDescription: props.includedModelsContextDescription,
      includedModelsContextName: props.includedModelsContextName,
      issueTrackerHref: props.issueTrackerHref,
    }),
    [props.includedModelsContextDescription, props.includedModelsContextName, props.issueTrackerHref]
  );
  return <DmnEditorContext.Provider value={value}>{props.children}</DmnEditorContext.Provider>;
}
