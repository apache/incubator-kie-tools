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
import { InputGroup } from "@patternfly/react-core/dist/js/components/InputGroup";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import ExternalLinkAltIcon from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import InfoAltIcon from "@patternfly/react-icons/dist/js/icons/info-alt-icon";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { useOnlineI18n } from "../i18n";
import { v4 as uuid } from "uuid";
import { useAuthSessionsDispatch } from "./authSessions/AuthSessionsContext";
import { AccountsModalDispatchAction, AccountsModalDispatchActionKind } from "./AccountsIcon";
import { GitAuthProvider } from "./authProviders/AuthProvidersContext";

export const GITHUB_OAUTH_TOKEN_SIZE = 40;

export const GITHUB_TOKENS_HOW_TO_URL =
  "https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token";

export function ConnectToGitHubSection(props: {
  authProvider: GitAuthProvider;
  dispatch: React.Dispatch<AccountsModalDispatchAction>;
}) {
  const { i18n } = useOnlineI18n();
  const authSessionsDispatch = useAuthSessionsDispatch();

  const onPasteGitHubToken = useCallback(
    async (e: React.ClipboardEvent, githubInstanceDomain: string) => {
      const token = e.clipboardData.getData("text/plain").slice(0, GITHUB_OAUTH_TOKEN_SIZE);

      // ????
      (document.getElementById("github-personal-access-token-input") as HTMLInputElement).setAttribute(
        "value",
        obfuscate(token)
      );

      const octokit = new Octokit({
        auth: token,
        baseUrl: githubInstanceApiUrl(githubInstanceDomain),
      });

      const response = await octokit.users.getAuthenticated();

      const scopes = response.headers["x-oauth-scopes"]?.split(", ") ?? [];
      if (!scopes.includes("repo") || !scopes.includes("gist")) {
        throw new Error("GitHub Personal Access Token (classic) must include the 'repo' and 'gist' scopes.");
      }

      authSessionsDispatch.add({
        id: uuid(),
        token,
        login: response.data.login,
        name: response.data.name ?? undefined,
        email: response.data.email ?? undefined,
      });
    },
    [authSessionsDispatch]
  );

  const isGitHubTokenValid = true;

  const githubTokenValidated = useMemo(() => {
    return isGitHubTokenValid ? "default" : "error";
  }, [isGitHubTokenValid]);

  const githubTokenHelperText = useMemo(() => {
    return isGitHubTokenValid ? undefined : "Invalid token. Check if it has the 'repo' scope.";
  }, [isGitHubTokenValid]);

  return (
    <>
      <Form>
        <FormGroup
          isRequired={true}
          helperTextInvalid={githubTokenHelperText}
          validated={githubTokenValidated}
          label={"Personal Access Token (classic)"}
          fieldId={"github-pat"}
          helperText={"Your token must include the 'repo' and 'gist' scopes."}
        >
          <InputGroup>
            <TextInput
              autoComplete={"off"}
              id="github-personal-access-token-input"
              name="tokenInput"
              aria-describedby="token-text-input-helper"
              placeholder={"Paste your GitHub token here"}
              maxLength={GITHUB_OAUTH_TOKEN_SIZE}
              validated={githubTokenValidated}
              onPaste={(e) => onPasteGitHubToken(e, props.authProvider.domain)}
              autoFocus={true}
            />
          </InputGroup>
        </FormGroup>
      </Form>
      <br />
      <h3>
        <a href={generateNewTokenUrl(props.authProvider.domain)} target={"_blank"}>
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
          <a href={GITHUB_TOKENS_HOW_TO_URL} target={"_blank"}>
            {i18n.githubTokenModal.body.learnMore}
            &nbsp;
            <ExternalLinkAltIcon className="pf-u-mx-sm" />
          </a>
        </Text>
      </TextContent>
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

export const githubInstanceApiUrl = (domain: string) => {
  return `https://${domain}/api/v3`;
};
