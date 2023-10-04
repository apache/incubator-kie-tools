import * as React from "react";
import { useContext } from "react";
import {
  OnRequestExternalModelsAvailableToInclude,
  OnRequestExternalModelByPath,
  ExternalModelsIndex,
} from "../DmnEditor";

export interface DmnEditorExternalModelsContextType {
  onRequestExternalModelByPath?: OnRequestExternalModelByPath;
  onRequestExternalModelsAvailableToInclude?: OnRequestExternalModelsAvailableToInclude;
  externalModelsByNamespace: ExternalModelsIndex;
}

const DmnEditorExternalModelsContext = React.createContext<DmnEditorExternalModelsContextType>({} as any);

export function useExternalModels() {
  return useContext(DmnEditorExternalModelsContext);
}

export function DmnEditorExternalModelsContextProvider(
  _props: React.PropsWithChildren<DmnEditorExternalModelsContextType>
) {
  const { children, ...props } = _props;
  return <DmnEditorExternalModelsContext.Provider value={props}>{children}</DmnEditorExternalModelsContext.Provider>;
}
