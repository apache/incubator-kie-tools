import * as React from "react";
import { useContext, useMemo, useRef } from "react";
import { DmnEditorProps } from "./DmnEditor";
import { DmnLatestModel } from "@kie-tools/dmn-marshaller";

export type DmnEditorContextProviderProps = Pick<
  DmnEditorProps,
  "externalContextDescription" | "externalContextName" | "issueTrackerHref" | "model" | "onRequestToJumpToPath"
>;

export type DmnModelBeforeEditing = DmnLatestModel;

export type DmnEditorContextType = Pick<
  DmnEditorContextProviderProps,
  "externalContextDescription" | "externalContextName" | "issueTrackerHref" | "onRequestToJumpToPath"
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
      externalContextDescription: props.externalContextDescription,
      externalContextName: props.externalContextName,
      issueTrackerHref: props.issueTrackerHref,
      onRequestToJumpToPath: props.onRequestToJumpToPath,
    }),
    [props.externalContextDescription, props.externalContextName, props.issueTrackerHref, props.onRequestToJumpToPath]
  );
  return <DmnEditorContext.Provider value={value}>{props.children}</DmnEditorContext.Provider>;
}
