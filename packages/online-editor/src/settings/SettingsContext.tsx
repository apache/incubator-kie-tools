import * as React from "react";
import { useContext } from "react";

export interface SettingsContextType {
  github: any;
  gitlab: any;
  bitbucket: any;
  openshift: any;
  googleDrive: any;
  dropbox: any;
}

export const SettingsContext = React.createContext<SettingsContextType>({} as any);

export function SettingsContextProvider(props: any) {
  return <SettingsContext.Provider value={{} as any}>{props.children}</SettingsContext.Provider>;
}

export function useSettings() {
  return useContext(SettingsContext);
}
