import * as React from "react";
import { Select, SelectOption, SelectPosition, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { AuthProviderIcon } from "../authProviders/AuthProviderIcon";
import { AuthSessionStatus, AUTH_SESSION_NONE, useAuthSession, useAuthSessions } from "./AuthSessionsContext";
import { IconSize } from "@patternfly/react-icons/dist/js/createIcon";
import { useAuthProviders } from "../authProviders/AuthProvidersContext";
import { useMemo, useState } from "react";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { ButtonVariant } from "@patternfly/react-core";
import PlusIcon from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { AccountsDispatchActionKind, useAccountsDispatch } from "../AccountsDispatchContext";
import ExclamationCircleIcon from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";

export function AuthSessionSelect(props: {
  authSessionId: string | undefined;
  setAuthSessionId: React.Dispatch<React.SetStateAction<string | undefined>>;
  isPlain: boolean;
  position?: SelectPosition;
}) {
  const [isAuthSessionSelectorOpen, setAuthSessionSelectorOpen] = useState(false);

  const { authSessions, authSessionStatus } = useAuthSessions();
  const authProviders = useAuthProviders();

  const { authSession } = useAuthSession(props.authSessionId);
  const accountsDispatch = useAccountsDispatch();

  const selectedAuthSessionId = useMemo(() => {
    // Provided authSessionId doesn't exist anymore.
    if (props.authSessionId && !authSession) {
      return "Authentication expired";
    } else {
      return props.authSessionId;
    }
  }, [authSession, props.authSessionId]);

  const validated = useMemo(() => {
    if (props.authSessionId && !authSession) {
      return ValidatedOptions.warning;
    } else {
      return ValidatedOptions.default;
    }
  }, [authSession, props.authSessionId]);

  return (
    <Select
      position={props.position}
      validated={validated}
      variant={SelectVariant.single}
      selections={selectedAuthSessionId}
      isOpen={isAuthSessionSelectorOpen}
      onToggle={setAuthSessionSelectorOpen}
      isPlain={validated === ValidatedOptions.default ? props.isPlain : false}
      onSelect={(e, value) => {
        e.stopPropagation();
        props.setAuthSessionId(value as string);
        setAuthSessionSelectorOpen(false);
      }}
      className={props.isPlain ? "kie-tools--masthead-hoverable" : ""}
      menuAppendTo={"parent"}
      maxHeight={"400px"}
      style={{ minWidth: "400px" }}
      footer={
        <>
          <Button
            variant={ButtonVariant.link}
            isInline={true}
            icon={<PlusIcon />}
            onClick={() =>
              accountsDispatch({
                kind: AccountsDispatchActionKind.SELECT_AUTH_PROVDER,
                onNewAuthSession: (newAuthSession) => props.setAuthSessionId(newAuthSession.id),
              })
            }
          >
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
        <React.Fragment key={"divider-none"}>
          <>{authSessions.size > 0 && <Divider />}</>
        </React.Fragment>,
        ...[...authSessions.entries()].flatMap(([authSessionId, authSession], index) => {
          if (authSession.type === "none") {
            return [];
          }
          const authProvider = authProviders.find((a) => a.id === authSession.authProviderId);
          return [
            <SelectOption key={authSessionId} value={authSessionId} description={<i>{authProvider?.name}</i>}>
              <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
                <FlexItem>
                  <AuthProviderIcon authProvider={authProvider} size={IconSize.sm} />
                  &nbsp;&nbsp;
                  {authSession.login}
                </FlexItem>
                {authSessionStatus.get(authSession.id) === AuthSessionStatus.INVALID && (
                  <FlexItem style={{ zIndex: 99999 }}>
                    <Tooltip
                      position={"bottom"}
                      content={"Could not authenticate using this session. Its Token was probably revoked, or expired."}
                    >
                      <>
                        {/* Color copied from PF4 */}
                        <ExclamationCircleIcon color={"#c9190b"} /> {/* Color copied from PF4 */}
                      </>
                    </Tooltip>
                  </FlexItem>
                )}
              </Flex>
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
