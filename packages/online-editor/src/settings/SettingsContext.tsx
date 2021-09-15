import * as React from "react";
import { useContext, useEffect, useState } from "react";
import { getCookie, setCookie } from "../common/utils";

export interface SettingsContextType {
  general: {
    guidedTourEnabled: {
      get: boolean;
      set: React.Dispatch<React.SetStateAction<boolean>>;
    };
  };
}

export const SettingsContext = React.createContext<SettingsContextType>({} as any);

const GUIDED_TOUR_ENABLED_COOKIE_NAME = "KOGITO-TOOLING-COOKIE__is-guided-tour-enabled";

export function SettingsContextProvider(props: any) {
  const [isGuidedTourEnabled, setGuidedTourEnabled] = useState(
    getBooleanCookieInitialValue(GUIDED_TOUR_ENABLED_COOKIE_NAME, true)
  );

  useEffect(() => {
    setCookie(GUIDED_TOUR_ENABLED_COOKIE_NAME, `${isGuidedTourEnabled}`);
  }, [isGuidedTourEnabled]);

  return (
    <SettingsContext.Provider
      value={{ general: { guidedTourEnabled: { get: isGuidedTourEnabled, set: setGuidedTourEnabled } } }}
    >
      {props.children}
    </SettingsContext.Provider>
  );
}

export function useSettings() {
  return useContext(SettingsContext);
}

function getBooleanCookieInitialValue<T>(name: string, defaultValue: boolean) {
  return !getCookie(name) ? defaultValue : getCookie(name) === "true";
}
