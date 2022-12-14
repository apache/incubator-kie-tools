/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { PromiseStateStatus, usePromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { GitAuthProvider } from "../../authProviders/AuthProvidersApi";
import { GitAuthSession } from "../../authSessions/AuthSessionApi";
import { useAuthSessions, useAuthSessionsDispatch } from "../../authSessions/AuthSessionsContext";
import { useOnlineI18n } from "../../i18n";
import { AccountsDispatchActionKind, AccountsSection, useAccounts, useAccountsDispatch } from "../AccountsContext";
import { v4 as uuid } from "uuid";
import { AuthOptionsType, BitbucketClient } from "../../bitbucket/Hooks";
import {
  Alert,
  AlertVariant,
  Button,
  ButtonVariant,
  Form,
  FormGroup,
  Spinner,
  Text,
  TextContent,
  TextInput,
  TextVariants,
  ValidatedOptions,
} from "@patternfly/react-core";
import { ExclamationCircleIcon, ExternalLinkAltIcon, InfoAltIcon } from "@patternfly/react-icons";
import { AuthSessionDescriptionList } from "../../authSessions/AuthSessionsList";

export const BITBUCKET_OAUTH_TOKEN_SIZE = 40;

export const BITBUCKET_TOKENS_HOW_TO_URL = "https://support.atlassian.com/bitbucket-cloud/docs/create-an-app-password/";

export function ConnectToBitbucketSection(props: { authProvider: GitAuthProvider }) {
  const { i18n } = useOnlineI18n();
  const accounts = useAccounts();
  const accountsDispatch = useAccountsDispatch();
  const { authSessions } = useAuthSessions();
  const authSessionsDispatch = useAuthSessionsDispatch();

  const [bitbucketUsername, setBitbucketUsername] = useState("");
  const [bitbucketToken, setBitbucketToken] = useState("");
  const [success, setSuccess] = useState(false);
  const [newAuthSession, setNewAuthSession] = usePromiseState<GitAuthSession>();

  useCancelableEffect(
    React.useCallback(
      ({ canceled }) => {
        if (!bitbucketToken) {
          return;
        }

        if (success) {
          return;
        }

        setSuccess(false);
        setNewAuthSession({ loading: true });

        if (
          [...authSessions.values()]
            .filter(({ type }) => type === "git")
            .some(({ token }: GitAuthSession) => token === bitbucketToken)
        ) {
          setNewAuthSession({ error: "You're already logged in with this Token." });
          return;
        }
        delay(600)
          .then(() => fetchAuthenticatedBitbucketUser(bitbucketUsername, bitbucketToken, props.authProvider.domain))
          .then((response) => {
            if (canceled.get()) {
              return;
            }

            const scopes = response.headers.get("x-oauth-scopes")?.split(", ") ?? [];
            if (!scopes.includes("repository") || !scopes.includes("snippet") || !scopes.includes("account")) {
              setNewAuthSession({
                error: "Make sure your Token includes the 'account', 'repository' and 'snippet' scopes.",
              });
            }

            response.json().then((jsonResponse: any) => {
              const newAuthSession: GitAuthSession = {
                id: uuid(),
                token: bitbucketToken,
                type: "git",
                login: bitbucketUsername,
                name: jsonResponse?.display_name ?? undefined,
                uuid: jsonResponse.uuid,
                authProviderId: props.authProvider.id,
                createdAtDateISO: new Date().toISOString(),
              };

              // batch updates
              setTimeout(() => {
                authSessionsDispatch.add(newAuthSession);
                setNewAuthSession({ data: newAuthSession });
                setSuccess(true);
              }, 0);
            });
          })
          .catch((e) => {
            if (canceled.get()) {
              return;
            }

            setNewAuthSession({ error: `${e}` });
          });
      },
      [
        authSessions,
        authSessionsDispatch,
        bitbucketUsername,
        bitbucketToken,
        props.authProvider.domain,
        props.authProvider.id,
        setNewAuthSession,
        success,
      ]
    )
  );

  const validation = React.useMemo(() => {
    if (!bitbucketToken) {
      return {
        validated: ValidatedOptions.default,
        helperTextIcon: <Spinner diameter={"1em"} style={{ display: "none" }} />,
        helperText: "Your token must include the 'account', 'repository' and 'snippet' scopes.",
      };
    }

    const { status } = newAuthSession;
    switch (status) {
      case PromiseStateStatus.PENDING:
        return {
          validated: ValidatedOptions.default,
          helperTextIcon: <Spinner diameter={"1em"} />,
          helperText: "Loading...",
        };
      case PromiseStateStatus.REJECTED:
        return {
          validated: ValidatedOptions.error,
          helperTextInvalid: newAuthSession.error.join(". "),
          helperTextInvalidIcon: <ExclamationCircleIcon />,
        };
      case PromiseStateStatus.RESOLVED:
        return { validated: ValidatedOptions.success };
      default:
        assertUnreachable(status);
    }
  }, [bitbucketToken, newAuthSession]);

  const successPrimaryAction = useMemo(() => {
    if (accounts.section !== AccountsSection.CONNECT_TO_BITBUCKET || !newAuthSession.data) {
      return;
    }

    if (!accounts.onNewAuthSession) {
      return {
        action: () => accountsDispatch({ kind: AccountsDispatchActionKind.GO_HOME }),
        label: "See connected accounts",
      };
    }

    return {
      action: () => accounts.onNewAuthSession?.(newAuthSession.data),
      label: "Continue",
    };
  }, [accounts, accountsDispatch, newAuthSession.data]);

  return (
    <>
      {validation.validated === ValidatedOptions.success && (
        <>
          <Alert isPlain={true} isInline={true} variant={AlertVariant.success} title={`Successfully connected`}></Alert>
          <br />
          <br />
          <AuthSessionDescriptionList authSession={newAuthSession.data!} />
          <br />
          <br />
          <br />
          <Button variant={ButtonVariant.primary} onClick={successPrimaryAction?.action}>
            {successPrimaryAction?.label}
          </Button>
        </>
      )}
      {validation.validated !== ValidatedOptions.success && (
        <>
          <Form>
            <FormGroup
              isRequired={true}
              helperTextIcon={validation.helperTextIcon}
              helperTextInvalidIcon={validation.helperTextInvalidIcon}
              helperTextInvalid={validation.helperTextInvalid}
              helperText={validation.helperText}
              validated={validation.validated}
              label={"Bitbucket username"}
              fieldId={"bitbucket-username"}
            >
              <TextInput
                value={bitbucketUsername}
                autoComplete={"off"}
                id="bitbucket-username-input"
                name="usernameInput"
                aria-describedby="username-text-input-helper"
                placeholder={"Paste your Bitbucket username here"}
                maxLength={BITBUCKET_OAUTH_TOKEN_SIZE}
                onChange={(v) => setBitbucketUsername(v.trim())}
              />
            </FormGroup>
            <FormGroup
              isRequired={true}
              helperTextIcon={validation.helperTextIcon}
              helperTextInvalidIcon={validation.helperTextInvalidIcon}
              helperTextInvalid={validation.helperTextInvalid}
              helperText={validation.helperText}
              validated={validation.validated}
              label={"Personal Access Token (classic)"}
              fieldId={"bitbucket-pat"}
            >
              <TextInput
                value={obfuscate(bitbucketToken)}
                autoComplete={"off"}
                id="bitbucket-personal-access-token-input"
                name="tokenInput"
                aria-describedby="token-text-input-helper"
                placeholder={"Paste your Bitbucket token here"}
                maxLength={BITBUCKET_OAUTH_TOKEN_SIZE}
                validated={validation.validated}
                onPaste={(e) =>
                  setBitbucketToken(e.clipboardData.getData("text/plain").slice(0, BITBUCKET_OAUTH_TOKEN_SIZE))
                }
                autoFocus={false}
              />
            </FormGroup>
          </Form>
          <br />
          <h3>
            <a href={generateNewTokenUrl(props.authProvider.domain)} target={"_blank"} rel={"noopener"}>
              {i18n.bitbucketTokenModal.footer.createNewToken}
              &nbsp;
              <ExternalLinkAltIcon className="pf-u-mx-sm" />
            </a>
          </h3>
          <br />
          <br />
          <TextContent>
            <Text component={TextVariants.blockquote}>
              <InfoAltIcon />
              &nbsp;
              <span className="pf-u-mr-sm">{i18n.bitbucketTokenModal.body.disclaimer}&nbsp;</span>
              <a href={BITBUCKET_TOKENS_HOW_TO_URL} target={"_blank"} rel={"noopener"}>
                {i18n.bitbucketTokenModal.body.learnMore}
                &nbsp;
                <ExternalLinkAltIcon className="pf-u-mx-sm" />
              </a>
            </Text>
          </TextContent>
        </>
      )}
    </>
  );
}

export function obfuscate(token: string) {
  if (token.length <= 8) {
    return token;
  }

  const stars = new Array(token.length - 8).join("*");
  const pieceToObfuscate = token.substring(4, token.length - 4);
  return token.replace(pieceToObfuscate, stars);
}

export const generateNewTokenUrl = (domain: string) => {
  return `https://${domain}/account/settings/app-passwords/`;
};

function delay(ms: number) {
  return new Promise((res) => setTimeout(res, ms));
}

export function assertUnreachable(_x: never): never {
  throw new Error("Didn't expect to get here");
}

export function fetchAuthenticatedBitbucketUser(bitbucketUsername: string, bitbucketToken: string, domain: string) {
  const bitbucket = new BitbucketClient({
    domain,
    auth: {
      type: AuthOptionsType.BASIC,
      username: bitbucketUsername,
      password: bitbucketToken,
    },
  });

  return bitbucket.getAuthedUser();
}
