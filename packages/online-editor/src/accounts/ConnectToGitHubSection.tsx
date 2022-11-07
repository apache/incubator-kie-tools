/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { Octokit } from "@octokit/rest";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import ExternalLinkAltIcon from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import InfoAltIcon from "@patternfly/react-icons/dist/js/icons/info-alt-icon";
import * as React from "react";
import { Alert, AlertVariant } from "@patternfly/react-core/dist/js/components/Alert";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers";
import ExclamationCircleIcon from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { useCallback, useMemo, useState } from "react";
import { v4 as uuid } from "uuid";
import { getGithubInstanceApiUrl } from "../github/Hooks";
import { useOnlineI18n } from "../i18n";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import {
  AccountsDispatchActionKind,
  AccountsSection,
  useAccounts,
  useAccountsDispatch,
} from "./AccountsDispatchContext";
import { GitAuthProvider } from "./authProviders/AuthProvidersContext";
import { useAuthSessions, useAuthSessionsDispatch } from "./authSessions/AuthSessionsContext";
import { AuthSessionDescriptionList } from "./authSessions/AuthSessionsList";
import { GitAuthSession } from "./authSessions/AuthSessionApi";
import { PromiseStateStatus, usePromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";

export const GITHUB_OAUTH_TOKEN_SIZE = 40;

export const GITHUB_TOKENS_HOW_TO_URL =
  "https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token";

export function ConnectToGitHubSection(props: { authProvider: GitAuthProvider }) {
  const { i18n } = useOnlineI18n();
  const accounts = useAccounts();
  const accountsDispatch = useAccountsDispatch();
  const { authSessions } = useAuthSessions();
  const authSessionsDispatch = useAuthSessionsDispatch();

  const [githubToken, setGitHubToken] = useState("");
  const [success, setSuccess] = useState(false);
  const [newAuthSession, setNewAuthSession] = usePromiseState<GitAuthSession>();

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!githubToken) {
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
            .some(({ token }: GitAuthSession) => token === githubToken)
        ) {
          setNewAuthSession({ error: "You're already logged in with this Token." });
          return;
        }

        delay(600)
          .then(() => fetchAuthenticatedGitHubUser(githubToken, getGithubInstanceApiUrl(props.authProvider.domain)))
          .then((response) => {
            if (canceled.get()) {
              return;
            }

            const scopes = response.headers["x-oauth-scopes"]?.split(", ") ?? [];
            if (!scopes.includes("repo") || !scopes.includes("gist")) {
              setNewAuthSession({
                error: "Make sure your Token includes the 'repo' and 'gist' scopes.",
              });
            }

            const newAuthSession: GitAuthSession = {
              id: uuid(),
              token: githubToken,
              type: "git",
              login: response.data.login,
              name: response.data.name ?? undefined,
              email: response.data.email ?? undefined,
              authProviderId: props.authProvider.id,
              createdAtDateISO: new Date().toISOString(),
            };

            // batch updates
            setTimeout(() => {
              authSessionsDispatch.add(newAuthSession);
              setNewAuthSession({ data: newAuthSession });
              setSuccess(true);
            }, 0);
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
        githubToken,
        props.authProvider.domain,
        props.authProvider.id,
        setNewAuthSession,
        success,
      ]
    )
  );

  const validation = useMemo(() => {
    if (!githubToken) {
      return {
        validated: ValidatedOptions.default,
        helperTextIcon: <Spinner diameter={"1em"} style={{ display: "none" }} />,
        helperText: "Your token must include the 'repo' and 'gist' scopes.",
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
  }, [githubToken, newAuthSession]);

  const successPrimaryAction = useMemo(() => {
    if (accounts.section !== AccountsSection.CONNECT_TO_NEW_GITHUB_ACC || !newAuthSession.data) {
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
              label={"Personal Access Token (classic)"}
              fieldId={"github-pat"}
            >
              <TextInput
                value={obfuscate(githubToken)}
                autoComplete={"off"}
                id="github-personal-access-token-input"
                name="tokenInput"
                aria-describedby="token-text-input-helper"
                placeholder={"Paste your GitHub token here"}
                maxLength={GITHUB_OAUTH_TOKEN_SIZE}
                validated={validation.validated}
                onPaste={(e) => setGitHubToken(e.clipboardData.getData("text/plain").slice(0, GITHUB_OAUTH_TOKEN_SIZE))}
                autoFocus={true}
              />
            </FormGroup>
          </Form>
          <br />
          <h3>
            <a href={generateNewTokenUrl(props.authProvider.domain)} target={"_blank"} rel={"noopener"}>
              {i18n.githubTokenModal.footer.createNewToken}
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
              <span className="pf-u-mr-sm">{i18n.githubTokenModal.body.disclaimer}&nbsp;</span>
              <a href={GITHUB_TOKENS_HOW_TO_URL} target={"_blank"} rel={"noopener"}>
                {i18n.githubTokenModal.body.learnMore}
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
  return `https://${domain}/settings/tokens`;
};

function delay(ms: number) {
  return new Promise((res) => setTimeout(res, ms));
}

export function assertUnreachable(_x: never): never {
  throw new Error("Didn't expect to get here");
}

export function fetchAuthenticatedGitHubUser(githubToken: string, githubInstanceApiUrl: string | undefined) {
  const octokit = new Octokit({
    auth: githubToken,
    baseUrl: githubInstanceApiUrl,
  });

  return octokit.users.getAuthenticated();
}
