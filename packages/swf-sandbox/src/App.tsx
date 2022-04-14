import * as React from "react";
import { HashRouter } from "react-router-dom";
import { EditorEnvelopeLocatorContextProvider } from "./envelopeLocator/EditorEnvelopeLocatorContext";
import { AppI18nContextProvider } from "./i18n";
import { NavigationContextProvider } from "./navigation/NavigationContextProvider";
import { RoutesSwitch } from "./navigation/RoutesSwitch";
import { OpenShiftProvider } from "./openshift/OpenShiftProvider";
import { SettingsContextProvider } from "./settings/SettingsContext";
import { WorkspacesContextProvider } from "./workspace/WorkspacesContextProvider";

export const App = () => (
  <HashRouter>
    {nest(
      [AppI18nContextProvider, {}],
      [EditorEnvelopeLocatorContextProvider, {}],
      [SettingsContextProvider, {}],
      [WorkspacesContextProvider, {}],
      [OpenShiftProvider, {}],
      [NavigationContextProvider, {}],
      [RoutesSwitch, {}]
    )}
  </HashRouter>
);

function nest(...components: Array<[(...args: any[]) => any, object]>) {
  return components.reduceRight((acc, [Component, props]) => {
    return <Component {...props}>{acc}</Component>;
  }, <></>);
}
