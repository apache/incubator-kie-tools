/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { GithubIcon } from "@patternfly/react-icons/dist/js/icons/github-icon";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup } from "@patternfly/react-core/dist/js/components/InputGroup";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { AuthStatus, useSettings, useSettingsDispatch } from "./SettingsContext";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { useOnlineI18n } from "../i18n";
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";

export const GITHUB_OAUTH_TOKEN_SIZE = 40;
export const GITHUB_TOKENS_URL = "https://github.com/settings/tokens";
export const GITHUB_TOKENS_HOW_TO_URL =
  "https://help.github.com/en/github/authenticating-to-github/creating-a-personal-access-token-for-the-command-line";

export enum GitHubSignInOption {
  PERSONAL_ACCESS_TOKEN,
  OAUTH,
}

enum GitHubTokenScope {
  GIST = "gist",
  REPO = "repo",
}

/** UNCOMMENT FOR OAUTH WEB WORKFLOW WITH GITHUB **/
/* DUPLICATED FROM JAVA CLASS */
export interface GitHubOAuthResponse {
  access_token: string;
  token_type: string;
  scope: string;
}

export function GitHubSettingsTab() {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const { i18n } = useOnlineI18n();

  const [githubSignInOption, setGitHubSignInOption] = useState(GitHubSignInOption.PERSONAL_ACCESS_TOKEN);
  const [potentialGitHubToken, setPotentialGitHubToken] = useState<string | undefined>(undefined);
  const [isGitHubTokenValid, setIsGitHubTokenValid] = useState(true);

  const githubTokenValidated = useMemo(() => {
    return isGitHubTokenValid ? "default" : "error";
  }, [isGitHubTokenValid]);

  const githubTokenHelperText = useMemo(() => {
    return isGitHubTokenValid ? undefined : "Invalid token. Check if it has the 'repo' scope.";
  }, [isGitHubTokenValid]);

  /** UNCOMMENT FOR OAUTH WEB WORKFLOW WITH GITHUB **/
  // const [allowPrivateRepositories, setAllowPrivateRepositories] = useState(true);
  // const basBackendEndpoint = `http://localhost:8080/github_oauth`;
  // const queryParams = useQueryParams();
  // const history = useHistory();
  // const githubOAuthEndpoint = `https://github.com/login/oauth/authorize`;
  // const GITHUB_APP_CLIENT_ID = `2d5a6222146b382e5fd8`;
  // useEffect(() => {
  //   const effect = async () => {
  //     const code = queryParams.get(QueryParams.GITHUB_OAUTH_CODE);
  //     if (code) {
  //       const url = new URL(window.location.href);
  //       url.searchParams.delete(QueryParams.GITHUB_OAUTH_CODE);
  //       url.searchParams.delete(QueryParams.GITHUB_OAUTH_STATE);
  //       history.replace({
  //         pathname: history.location.pathname,
  //         search: url.search,
  //       });
  //
  //       const res = await fetch(`${basBackendEndpoint}?code=${code}&client_id=${GITHUB_APP_CLIENT_ID}&redirect_uri=`);
  //       const resJson: GitHubOAuthResponse = await res.json();
  //       await settings.github.authService.authenticate(resJson.access_token);
  //     }
  //   };
  //   effect();
  // }, [history, queryParams, settings.github.authService]);
  //
  // const onSignInWithGitHub = useCallback(() => {
  //   const redirectUri = new URL(`${window.location.href}`);
  //   redirectUri.searchParams.set(QueryParams.SETTINGS, SettingsTabs.GITHUB);
  //
  //   const state = new Date().getTime();
  //   const scope = allowPrivateRepositories
  //       ? `${GitHubTokenScope.GIST},${GitHubTokenScope.REPO}`
  //       : GitHubTokenScope.GIST;
  //   const encodedRedirectUri = encodeURIComponent(decodeURIComponent(redirectUri.href));
  //   window.location.href = `${githubOAuthEndpoint}?scope=${scope}&state=${state}&client_id=${GITHUB_APP_CLIENT_ID}&allow_signup=true&redirect_uri=${encodedRedirectUri}`;
  // }, [allowPrivateRepositories]);

  const githubTokenToDisplay = useMemo(() => {
    return obfuscate(potentialGitHubToken ?? settings.github.token);
  }, [settings.github, potentialGitHubToken]);

  const onPasteGitHubToken = useCallback(
    (e) => {
      const token = e.clipboardData.getData("text/plain").slice(0, GITHUB_OAUTH_TOKEN_SIZE);
      setPotentialGitHubToken(token);
      settingsDispatch.github.authService
        .authenticate(token)
        .then(() => setIsGitHubTokenValid(true))
        .catch((e) => setIsGitHubTokenValid(false));
    },
    [settingsDispatch.github.authService]
  );

  const onSignOutFromGitHub = useCallback(() => {
    settingsDispatch.github.authService.reset();
    setPotentialGitHubToken(undefined);
  }, [settingsDispatch.github.authService]);

  return (
    <Page>
      {settings.github.authStatus === AuthStatus.TOKEN_EXPIRED && (
        <PageSection>
          <EmptyState>
            <EmptyStateIcon icon={ExclamationTriangleIcon} />
            <TextContent>
              <Text component={"h2"}>GitHub Token expired</Text>
            </TextContent>
            <br />
            <EmptyStateBody>
              <TextContent>Reset your token to sign in with GitHub again.</TextContent>
              <br />
              <Button variant={ButtonVariant.tertiary} onClick={onSignOutFromGitHub}>
                Reset
              </Button>
            </EmptyStateBody>
          </EmptyState>
        </PageSection>
      )}
      {settings.github.authStatus === AuthStatus.LOADING && (
        <PageSection>
          <EmptyState>
            <EmptyStateIcon icon={GithubIcon} />
            <TextContent>
              <Text component={"h2"}>Signing in with GitHub</Text>
            </TextContent>
            <br />
            <br />
            <Spinner />
          </EmptyState>
        </PageSection>
      )}
      {settings.github.authStatus === AuthStatus.SIGNED_IN && (
        <PageSection>
          <EmptyState>
            <EmptyStateIcon icon={CheckCircleIcon} color={"var(--pf-global--success-color--100)"} />
            <TextContent>
              <Text component={"h2"}>{"You're signed in with GitHub."}</Text>
            </TextContent>
            <EmptyStateBody>
              <TextContent>
                Gists are <b>{settings.github.scopes?.includes(GitHubTokenScope.GIST) ? "enabled" : "disabled"}.</b>
              </TextContent>
              <TextContent>
                Private repositories are{" "}
                <b>{settings.github.scopes?.includes(GitHubTokenScope.REPO) ? "enabled" : "disabled"}.</b>
              </TextContent>
              <br />
              <TextContent>
                <b>Token: </b>
                <i>{obfuscate(settings.github.token)}</i>
              </TextContent>
              <TextContent>
                <b>User: </b>
                <i>{settings.github.user?.login}</i>
              </TextContent>
              <TextContent>
                <b>Scope: </b>
                <i>{settings.github.scopes?.join(", ") || "(none)"}</i>
              </TextContent>
              <br />
              <Button variant={ButtonVariant.tertiary} onClick={onSignOutFromGitHub}>
                Sign out
              </Button>
            </EmptyStateBody>
          </EmptyState>
        </PageSection>
      )}
      {settings.github.authStatus === AuthStatus.SIGNED_OUT && (
        <>
          <PageSection>
            {/** UNCOMMENT FOR OAUTH WEB WORKFLOW WITH GITHUB **/}
            {/*{githubSignInOption == GitHubSignInOption.OAUTH && (*/}
            {/*  <>*/}
            {/*    <EmptyState>*/}
            {/*      <EmptyStateIcon icon={GithubIcon} />*/}
            {/*      <TextContent>*/}
            {/*        <Text component={"h2"}>{"You're not connected to GitHub."}</Text>*/}
            {/*      </TextContent>*/}
            {/*      <EmptyStateBody>*/}
            {/*        <TextContent>{"Signing in with GitHub enables syncing your Workspaces."}</TextContent>*/}
            {/*        <TextContent>*/}
            {/*          {"You can also sign in using a "}*/}
            {/*          <a href={"#"} onClick={() => setGitHubSignInOption(GitHubSignInOption.PERSONAL_ACCESS_TOKEN)}>*/}
            {/*            Personal Access Token*/}
            {/*          </a>*/}
            {/*          {"."}*/}
            {/*        </TextContent>*/}
            {/*        <br />*/}
            {/*        <br />*/}
            {/*        <Bullseye>*/}
            {/*          <Checkbox*/}
            {/*            id="settings-github--allow-private-repositories"*/}
            {/*            isChecked={allowPrivateRepositories}*/}
            {/*            onChange={setAllowPrivateRepositories}*/}
            {/*            label={"Allow private repositories"}*/}
            {/*          />*/}
            {/*        </Bullseye>*/}
            {/*        <br />*/}
            {/*        <Button variant={ButtonVariant.primary} onClick={onSignInWithGitHub}>*/}
            {/*          Sign in with GitHub*/}
            {/*        </Button>*/}
            {/*      </EmptyStateBody>*/}
            {/*    </EmptyState>*/}
            {/*  </>*/}
            {/*)}*/}
            {githubSignInOption == GitHubSignInOption.PERSONAL_ACCESS_TOKEN && (
              <>
                <PageSection variant={"light"} isFilled={true} style={{ height: "100%" }}>
                  {/** UNCOMMENT FOR OAUTH WEB WORKFLOW WITH GITHUB **/}
                  {/*<Button*/}
                  {/*  variant="link"*/}
                  {/*  isInline={true}*/}
                  {/*  icon={<AngleLeftIcon />}*/}
                  {/*  onClick={() => setGitHubSignInOption(GitHubSignInOption.OAUTH)}*/}
                  {/*>*/}
                  {/*  Back*/}
                  {/*</Button>*/}
                  {/*<br />*/}
                  {/*<br />*/}
                  <>
                    <p>
                      <span className="pf-u-mr-sm">{i18n.githubTokenModal.body.disclaimer}</span>
                      <a href={GITHUB_TOKENS_HOW_TO_URL} target={"_blank"}>
                        {i18n.githubTokenModal.body.learnMore}
                        <ExternalLinkAltIcon className="pf-u-mx-sm" />
                      </a>
                    </p>
                  </>
                  <br />
                  <h3>
                    <a href={GITHUB_TOKENS_URL} target={"_blank"}>
                      {i18n.githubTokenModal.footer.createNewToken}
                      <ExternalLinkAltIcon className="pf-u-mx-sm" />
                    </a>
                  </h3>
                  <br />
                  <Form>
                    <FormGroup
                      isRequired={true}
                      helperTextInvalid={githubTokenHelperText}
                      validated={githubTokenValidated}
                      label={"Token"}
                      fieldId={"github-pat"}
                      helperText={"Your token must include the 'repo' scope."}
                    >
                      <InputGroup>
                        <TextInput
                          autoComplete={"off"}
                          id="token-input"
                          name="tokenInput"
                          aria-describedby="token-text-input-helper"
                          placeholder={"Paste your GitHub token here"}
                          maxLength={GITHUB_OAUTH_TOKEN_SIZE}
                          validated={githubTokenValidated}
                          value={githubTokenToDisplay}
                          onPaste={onPasteGitHubToken}
                          autoFocus={true}
                        />
                      </InputGroup>
                    </FormGroup>
                  </Form>
                </PageSection>
              </>
            )}
          </PageSection>
        </>
      )}
    </Page>
  );
}

export function obfuscate(token?: string) {
  if (!token) {
    return undefined;
  }

  if (token.length <= 8) {
    return token;
  }

  const stars = new Array(token.length - 8).join("*");
  const pieceToObfuscate = token.substring(4, token.length - 4);
  return token.replace(pieceToObfuscate, stars);
}
