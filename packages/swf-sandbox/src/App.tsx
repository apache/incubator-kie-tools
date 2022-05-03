import * as React from "react";
import { HashRouter } from "react-router-dom";
import { EnvContextProvider } from "./env/EnvContextProvider";
import { EditorEnvelopeLocatorContextProvider } from "./envelopeLocator/EditorEnvelopeLocatorContext";
import { AppI18nContextProvider } from "./i18n";
import { KieSandboxExtendedServicesContextProvider } from "./kieSandboxExtendedServices/KieSandboxExtendedServicesContextProvider";
import { NavigationContextProvider } from "./navigation/NavigationContextProvider";
import { RoutesSwitch } from "./navigation/RoutesSwitch";
import { OpenShiftContextProvider } from "./openshift/OpenShiftContextProvider";
import { SettingsContextProvider } from "./settings/SettingsContext";
import { WorkspacesContextProvider } from "./workspace/WorkspacesContextProvider";

export const App = () => (
  <HashRouter>
    {nest(
      [AppI18nContextProvider, {}],
      [EditorEnvelopeLocatorContextProvider, {}],
      [EnvContextProvider, {}],
      [KieSandboxExtendedServicesContextProvider, {}],
      [SettingsContextProvider, {}],
      [WorkspacesContextProvider, {}],
      [OpenShiftContextProvider, {}],
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
