import * as React from "react";
import { useContext, useMemo, useRef } from "react";
import { DmnEditorProps } from "./DmnEditor";
import { DmnLatestModel } from "@kie-tools/dmn-marshaller";

export type DmnEditorContextProviderProps = Pick<
  DmnEditorProps,
  | "externalContextDescription"
  | "externalContextName"
  | "issueTrackerHref"
  | "model"
  | "onRequestToJumpToPath"
  | "onRequestToResolvePath"
>;

export type DmnModelBeforeEditing = DmnLatestModel;

export type DmnEditorContextType = Pick<
  DmnEditorContextProviderProps,
  | "externalContextDescription"
  | "externalContextName"
  | "issueTrackerHref"
  | "onRequestToJumpToPath"
  | "onRequestToResolvePath"
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
      onRequestToResolvePath: props.onRequestToResolvePath,
    }),
    [
      props.externalContextDescription,
      props.externalContextName,
      props.issueTrackerHref,
      props.onRequestToJumpToPath,
      props.onRequestToResolvePath,
    ]
  );
  return <DmnEditorContext.Provider value={value}>{props.children}</DmnEditorContext.Provider>;
}
