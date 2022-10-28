import * as React from "react";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { AuthProviderIcon } from "../authProviders/AuthProviderIcon";
import { AUTH_SESSION_NONE, useAuthSession, useAuthSessions } from "./AuthSessionsContext";
import { IconSize } from "@patternfly/react-icons/dist/js/createIcon";
import { useAuthProviders } from "../authProviders/AuthProvidersContext";
import { useMemo, useState } from "react";

export function AuthSessionSelect(props: {
  authSessionId: string | undefined;
  setAuthSessionId: React.Dispatch<React.SetStateAction<string | undefined>>;
  isPlain: boolean;
}) {
  const [isAuthSessionSelectorOpen, setAuthSessionSelectorOpen] = useState(false);

  const { authSessions } = useAuthSessions();
  const authProviders = useAuthProviders();

  const { authSession } = useAuthSession(props.authSessionId);

  const selectedAuthSessionId = useMemo(() => {
    // Provided authSessionId doesn't exist anymore.
    if (props.authSessionId && !authSession) {
      return "⚠️  Expired";
    } else {
      return props.authSessionId;
    }
  }, [authSession, props.authSessionId]);

  return (
    <Select
      variant={SelectVariant.single}
      selections={selectedAuthSessionId}
      isOpen={isAuthSessionSelectorOpen}
      onToggle={setAuthSessionSelectorOpen}
      isPlain={props.isPlain}
      onSelect={(e, value) => {
        props.setAuthSessionId(value as string);
        setAuthSessionSelectorOpen(false);
      }}
      menuAppendTo={document.body}
      maxHeight={"400px"}
      style={{ minWidth: "400px" }}
    >
      {[
        <SelectOption key={AUTH_SESSION_NONE.id} value={AUTH_SESSION_NONE.id} description={<i>{}</i>}>
          <AuthProviderIcon authProvider={undefined} size={IconSize.sm} />
          &nbsp;&nbsp;
          {"None"}
        </SelectOption>,
        ...[...authSessions.entries()].flatMap(([authSessionId, authSession]) => {
          if (authSession.type === "none") {
            return [];
          }
          const authProvider = authProviders.find((a) => a.id === authSession.authProviderId);
          return [
            <SelectOption key={authSessionId} value={authSessionId} description={<i>{authProvider?.name}</i>}>
              <AuthProviderIcon authProvider={authProvider} size={IconSize.sm} />
              &nbsp;&nbsp;
              {authSession.login}
            </SelectOption>,
          ];
        }),
      ]}
    </Select>
  );
}
