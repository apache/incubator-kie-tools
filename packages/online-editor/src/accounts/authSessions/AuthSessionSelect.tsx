import * as React from "react";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { AuthProviderIcon } from "../authProviders/AuthProviderIcon";
import { AUTH_SESSION_NONE, useAuthSession, useAuthSessions } from "./AuthSessionsContext";
import { IconSize } from "@patternfly/react-icons/dist/js/createIcon";
import { useAuthProviders } from "../authProviders/AuthProvidersContext";
import { useMemo, useState } from "react";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { ButtonVariant } from "@patternfly/react-core";
import PlusIcon from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";

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
      return "Expired authentication";
    } else {
      return props.authSessionId;
    }
  }, [authSession, props.authSessionId]);

  const validated = useMemo(() => {
    // Provided authSessionId doesn't exist anymore.
    if (props.authSessionId && !authSession) {
      return ValidatedOptions.warning;
    } else {
      return ValidatedOptions.default;
    }
  }, [authSession, props.authSessionId]);

  return (
    <Select
      validated={validated}
      variant={SelectVariant.single}
      selections={selectedAuthSessionId}
      isOpen={isAuthSessionSelectorOpen}
      onToggle={setAuthSessionSelectorOpen}
      isPlain={validated === ValidatedOptions.default ? props.isPlain : false}
      onSelect={(e, value) => {
        props.setAuthSessionId(value as string);
        setAuthSessionSelectorOpen(false);
      }}
      menuAppendTo={document.body}
      maxHeight={"400px"}
      style={{ minWidth: "400px" }}
      footer={
        <>
          <Button variant={ButtonVariant.link} isInline={true} icon={<PlusIcon />}>
            Connect to an account...
          </Button>
        </>
      }
    >
      {[
        <SelectOption key={AUTH_SESSION_NONE.id} value={AUTH_SESSION_NONE.id} description={<i>{}</i>}>
          <AuthProviderIcon authProvider={undefined} size={IconSize.sm} />
          &nbsp;&nbsp;
          {AUTH_SESSION_NONE.login}
        </SelectOption>,
        <Divider key={"divider-none-others"} />,
        ...[...authSessions.entries()].flatMap(([authSessionId, authSession], index) => {
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
            <React.Fragment key={`divider-${authSessionId}`}>
              {index < authSessions.size - 1 && <Divider />}
            </React.Fragment>,
          ];
        }),
      ]}
    </Select>
  );
}
