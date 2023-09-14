import * as React from "react";
import { useContext } from "react";
import { DependenciesByNamespace, OnRequestModelByPath, OnRequestModelsAvailableToInclude } from "../DmnEditor";

export interface DmnEditorDependenciesContextType {
  onRequestModelByPath?: OnRequestModelByPath;
  onRequestModelsAvailableToInclude?: OnRequestModelsAvailableToInclude;
  dependenciesByNamespace: DependenciesByNamespace;
}

const DmnEditorDependenciesContext = React.createContext<DmnEditorDependenciesContextType>({} as any);

export function useDmnEditorDependencies() {
  return useContext(DmnEditorDependenciesContext);
}

export function DmnEditorDependenciesContextProvider(
  _props: React.PropsWithChildren<DmnEditorDependenciesContextType>
) {
  const { children, ...props } = _props;
  return <DmnEditorDependenciesContext.Provider value={props}>{children}</DmnEditorDependenciesContext.Provider>;
}
