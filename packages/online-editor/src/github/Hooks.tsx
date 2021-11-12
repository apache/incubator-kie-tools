import { useMemo } from "react";
import { AuthStatus, useSettings } from "../settings/SettingsContext";

export function useGitHubAuthInfo() {
  const settings = useSettings();
  return useMemo(() => {
    if (settings.github.authStatus !== AuthStatus.SIGNED_IN) {
      return undefined;
    }

    return {
      name: settings.github.user!.name,
      email: settings.github.user!.email,
      username: settings.github.user!.login,
      password: settings.github.token!,
    };
  }, [settings.github]);
}
