import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { GithubIcon } from "@patternfly/react-icons/dist/js/icons/github-icon";
import { AngleLeftIcon } from "@patternfly/react-icons/dist/js/icons/angle-left-icon";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup } from "@patternfly/react-core/dist/js/components/InputGroup";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { GITHUB_OAUTH_TOKEN_SIZE } from "./GithubService";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { SettingsTabs } from "./SettingsModalBody";
import { QueryParams, useQueryParams } from "../queryParams/QueryParamsContext";
import { AuthStatus } from "../common/GlobalContext";
import { useHistory } from "react-router";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { useSettings } from "./SettingsContext";

/* DUPLICATED FROM JAVA CLASS */
export interface GitHubOAuthResponse {
  access_token: string;
  token_type: string;
  scope: string;
}
export enum GitHubSignInOption {
  PERSONAL_ACCESS_TOKEN,
  OAUTH,
}

const basBackendEndpoint = `http://localhost:8080/github_oauth`;
const githubOAuthEndpoint = `https://github.com/login/oauth/authorize`;
const GITHUB_APP_CLIENT_ID = `2d5a6222146b382e5fd8`;

export function GitHubSettingsTab() {
  const settings = useSettings();
  const queryParams = useQueryParams();
  const history = useHistory();

  const [githubSignInOption, setGitHubSignInOption] = useState(GitHubSignInOption.OAUTH);
  const [potentialGitHubToken, setPotentialGitHubToken] = useState<string | undefined>(undefined);
  const [isGitHubTokenValid, setIsGitHubTokenValid] = useState(true);
  const [allowPrivateRepositories, setAllowPrivateRepositories] = useState(true);

  const githubTokenValidated = useMemo(() => {
    return isGitHubTokenValid ? "default" : "error";
  }, [isGitHubTokenValid]);

  const githubTokenHelperText = useMemo(() => {
    return isGitHubTokenValid ? undefined : "Invalid token.";
  }, [isGitHubTokenValid]);

  useEffect(() => {
    const effect = async () => {
      const code = queryParams.get(QueryParams.GITHUB_OAUTH_CODE);
      if (code) {
        const url = new URL(window.location.href);
        url.searchParams.delete(QueryParams.GITHUB_OAUTH_CODE);
        url.searchParams.delete(QueryParams.GITHUB_OAUTH_STATE);
        history.replace({
          pathname: history.location.pathname,
          search: url.search,
        });

        const res = await fetch(`${basBackendEndpoint}?code=${code}&client_id=${GITHUB_APP_CLIENT_ID}&redirect_uri=`);
        const resJson: GitHubOAuthResponse = await res.json();
        await settings.github.authService.authenticate(resJson.access_token);
      }
    };
    effect();
  }, [history, queryParams, settings.github.authService]);

  const githubTokenToDisplay = useMemo(() => {
    return obfuscate(potentialGitHubToken ?? settings.github.token);
  }, [settings.github, potentialGitHubToken]);

  const onPasteGitHubToken = useCallback(
    (e) => {
      const token = e.clipboardData.getData("text/plain").slice(0, GITHUB_OAUTH_TOKEN_SIZE);
      setPotentialGitHubToken(token);
      settings.github.authService
        .authenticate(token)
        .then(() => setIsGitHubTokenValid(true))
        .catch((e) => setIsGitHubTokenValid(false));
    },
    [settings.github.authService]
  );

  const onSignOutFromGitHub = useCallback(() => {
    settings.github.authService.reset();
    setPotentialGitHubToken(undefined);
  }, [settings.github.authService]);

  const onSignInWithGitHub = useCallback(() => {
    const redirectUri = new URL(`${window.location.href}`);
    redirectUri.searchParams.set(QueryParams.SETTINGS, SettingsTabs.GITHUB);

    const state = new Date().getTime();
    const scope = allowPrivateRepositories ? `gist,repo` : `gist`;
    const encodedRedirectUri = encodeURIComponent(decodeURIComponent(redirectUri.href));
    window.location.href = `${githubOAuthEndpoint}?scope=${scope}&state=${state}&client_id=${GITHUB_APP_CLIENT_ID}&allow_signup=true&redirect_uri=${encodedRedirectUri}`;
  }, [allowPrivateRepositories]);

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
              <TextContent>Syncing Workspaces with GitHub is enabled.</TextContent>
              <br />
              <TextContent>
                <b>User: </b>
                <i>{settings.github.user}</i>
              </TextContent>
              <TextContent>
                <b>Scope: </b>
                <i>{settings.github.scopes?.join(", ")}</i>
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
            {githubSignInOption == GitHubSignInOption.OAUTH && (
              <>
                <EmptyState>
                  <EmptyStateIcon icon={GithubIcon} />
                  <TextContent>
                    <Text component={"h2"}>{"You're not connected to GitHub."}</Text>
                  </TextContent>
                  <EmptyStateBody>
                    <TextContent>{"Signing in with GitHub enables syncing your Workspaces."}</TextContent>
                    <TextContent>
                      {"You can also sign in using a "}
                      <a href={"#"} onClick={() => setGitHubSignInOption(GitHubSignInOption.PERSONAL_ACCESS_TOKEN)}>
                        Personal Access Token
                      </a>
                      {"."}
                    </TextContent>
                    <br />
                    <br />
                    <Bullseye>
                      <Checkbox
                        id="settings-github--allow-private-repositories"
                        isChecked={allowPrivateRepositories}
                        onChange={setAllowPrivateRepositories}
                        label={"Allow private repositories"}
                      />
                    </Bullseye>
                    <br />
                    <Button variant={ButtonVariant.primary} onClick={onSignInWithGitHub}>
                      Sign in with GitHub
                    </Button>
                  </EmptyStateBody>
                </EmptyState>
              </>
            )}
            {githubSignInOption == GitHubSignInOption.PERSONAL_ACCESS_TOKEN && (
              <>
                <PageSection variant={"light"} isFilled={true} style={{ height: "100%" }}>
                  <Button
                    variant="link"
                    isInline={true}
                    icon={<AngleLeftIcon />}
                    onClick={() => setGitHubSignInOption(GitHubSignInOption.OAUTH)}
                  >
                    Back
                  </Button>
                  <br />
                  <br />
                  <Form>
                    <FormGroup
                      isRequired={true}
                      helperTextInvalid={githubTokenHelperText}
                      validated={githubTokenValidated}
                      label={"Token"}
                      fieldId={"github-pat"}
                    >
                      <InputGroup>
                        <TextInput
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
