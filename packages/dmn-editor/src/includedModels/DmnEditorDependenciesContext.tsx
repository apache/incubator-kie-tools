import * as React from "react";
import { useContext } from "react";
import { OtherDmnsByNamespace, onRequestOtherDmnByPath, OnRequestModelsAvailableToInclude } from "../DmnEditor";

export interface DmnEditorOtherDmnsContextType {
  onRequestOtherDmnByPath?: onRequestOtherDmnByPath;
  onRequestModelsAvailableToInclude?: OnRequestModelsAvailableToInclude;
  otherDmnsByNamespace: OtherDmnsByNamespace;
}

const DmnEditorOtherDmnsContext = React.createContext<DmnEditorOtherDmnsContextType>({} as any);

export function useOtherDmns() {
  return useContext(DmnEditorOtherDmnsContext);
}

export function DmnEditorOtherDmnsContextProvider(_props: React.PropsWithChildren<DmnEditorOtherDmnsContextType>) {
  const { children, ...props } = _props;
  return <DmnEditorOtherDmnsContext.Provider value={props}>{children}</DmnEditorOtherDmnsContext.Provider>;
}
