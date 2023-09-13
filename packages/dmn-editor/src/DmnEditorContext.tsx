import * as React from "react";
import { useContext, useMemo, useRef } from "react";
import { DmnModel } from "./store/Store";

export interface DmnEditorContextType {
  dmnModelBeforeEditingRef: React.MutableRefObject<DmnModel | undefined>;
}
const DmnEditorContext = React.createContext<DmnEditorContextType>({} as any);

export function useDmnEditor() {
  return useContext(DmnEditorContext);
}

export function DmnEditorContextProvider(props: React.PropsWithChildren<{}>) {
  const dmnModelBeforeEditingRef = useRef<DmnModel | undefined>(undefined);
  const value = useMemo(() => ({ dmnModelBeforeEditingRef }), []);
  return <DmnEditorContext.Provider value={value}>{props.children}</DmnEditorContext.Provider>;
}
