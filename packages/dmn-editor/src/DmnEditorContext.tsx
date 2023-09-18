import * as React from "react";
import { useContext, useMemo, useRef } from "react";
import { DmnModel } from "./store/Store";
import { DmnEditorProps } from "./DmnEditor";

export type DmnEditorContextProviderProps = Pick<
  DmnEditorProps,
  "includedModelsContextDescription" | "includedModelsContextName"
>;

export type DmnEditorContextType = DmnEditorContextProviderProps & {
  dmnModelBeforeEditingRef: React.MutableRefObject<DmnModel | undefined>;
};

const DmnEditorContext = React.createContext<DmnEditorContextType>({} as any);

export function useDmnEditor() {
  return useContext(DmnEditorContext);
}

export function DmnEditorContextProvider(props: React.PropsWithChildren<DmnEditorContextProviderProps>) {
  const dmnModelBeforeEditingRef = useRef<DmnModel | undefined>(undefined);
  const value = useMemo<DmnEditorContextType>(
    () => ({
      dmnModelBeforeEditingRef,
      includedModelsContextDescription: props.includedModelsContextDescription,
      includedModelsContextName: props.includedModelsContextName,
    }),
    [props.includedModelsContextDescription, props.includedModelsContextName]
  );
  return <DmnEditorContext.Provider value={value}>{props.children}</DmnEditorContext.Provider>;
}
