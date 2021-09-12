import { Tab, Tabs, TabTitleText } from "@patternfly/react-core/dist/js/components/Tabs";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import * as React from "react";
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { GithubIcon } from "@patternfly/react-icons/dist/js/icons/github-icon";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ActionGroup, Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { GITHUB_OAUTH_TOKEN_SIZE } from "../common/GithubService";
import { InputGroup, InputGroupText } from "@patternfly/react-core/dist/js/components/InputGroup";
import { GlobalContext } from "../common/GlobalContext";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";

export enum GitHubSignInOption {
  PERSONAL_ACCESS_TOKEN,
  OAUTH,
}

export function SettingsModal() {
  const globalContext = useContext(GlobalContext);
  const [activeTab, setActiveTab] = useState(-1);

  // github
  const [githubSignInOption, setGitHubSignInOption] = useState(GitHubSignInOption.OAUTH);
  const [potentialGitHubToken, setPotentialGitHubToken] = useState("");
  const [githubAuthenticated, setGitHubAuthenticated] = useState(false);
  const [isGitHubTokenInvalid, setIsGitHubTokenInvalid] = useState(false);
  const [isGitHubAuthenticating, setGtHubAuthenticating] = useState(false);
  const [isGitHubTokenExpired, setGitHubTokenExpired] = useState(false);

  const githubTokenToDisplay = useMemo(() => {
    return obfuscate(potentialGitHubToken || globalContext.githubService.resolveToken());
  }, [globalContext.githubService, potentialGitHubToken]);

  const onPasteGitHubToken = useCallback(
    (e) => {
      const token = e.clipboardData.getData("text/plain").slice(0, GITHUB_OAUTH_TOKEN_SIZE);
      setPotentialGitHubToken(token);
      setGtHubAuthenticating(true);
      setGitHubTokenExpired(false);
      globalContext.githubService
        .authenticate(token)
        .then(delay(1000))
        .then((isAuthenticated) => {
          setGitHubAuthenticated(isAuthenticated);
          setIsGitHubTokenInvalid(!isAuthenticated);
        })
        .finally(() => setGtHubAuthenticating(false));
    },
    [globalContext.githubService]
  );

  const onSignOutFromGitHub = useCallback(() => {
    globalContext.githubService.reset();
    setPotentialGitHubToken("");
    setGitHubAuthenticated(false);
    setIsGitHubTokenInvalid(false);
    setGitHubTokenExpired(false);
    setGitHubSignInOption(GitHubSignInOption.OAUTH);
  }, [globalContext.githubService]);

  const githubTokenValidated = useMemo(() => {
    return isGitHubAuthenticating ? "default" : isGitHubTokenInvalid ? "error" : "default";
  }, [isGitHubAuthenticating, isGitHubTokenInvalid]);

  useEffect(() => {
    globalContext.githubService.authenticate().then((isAuthenticated) => {
      setGitHubAuthenticated(isAuthenticated);
      setGitHubTokenExpired(globalContext.githubService.resolveToken() !== "" && !isAuthenticated);
      setPotentialGitHubToken(globalContext.githubService.resolveToken() || "");
    });
  }, [globalContext.githubService]);

  return (
    <Tabs activeKey={activeTab} onSelect={(e, k) => setActiveTab(k as number)} isVertical={false} isBox={false}>
      <Tab className="kogito-tooling--settings-tab" eventKey={-1} title={<TabTitleText>General</TabTitleText>}>
        <Page>
          <PageSection>General</PageSection>
        </Page>
      </Tab>
      <Tab className="kogito-tooling--settings-tab" eventKey={0} title={<TabTitleText>GitHub</TabTitleText>}>
        <Page>
          {githubAuthenticated && (
            <PageSection>
              <Bullseye>
                <EmptyState>
                  <EmptyStateIcon icon={CheckCircleIcon} color={"var(--pf-global--success-color--100)"} />
                  <TextContent>
                    <Text component={"h2"}>
                      {"You're signed in with GitHub as "}
                      <u>{globalContext.githubService.getLogin()}</u>
                      {"."}
                    </Text>
                  </TextContent>
                  <EmptyStateBody>
                    <TextContent>Syncing Workspaces with GitHub is enabled.</TextContent>
                    <br />
                    <Button variant={ButtonVariant.tertiary} onClick={onSignOutFromGitHub}>
                      Sign out
                    </Button>
                  </EmptyStateBody>
                </EmptyState>
              </Bullseye>
            </PageSection>
          )}
          {!githubAuthenticated && (
            <>
              {isGitHubTokenExpired && (
                <Alert
                  style={{ margin: "15px" }}
                  variant="danger"
                  isInline={false}
                  title="Your GitHub Personal Access Token is expired."
                />
              )}
              <PageSection>
                {githubSignInOption == GitHubSignInOption.OAUTH && (
                  <Bullseye>
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
                        <Button variant={ButtonVariant.tertiary}>Sign in with GitHub</Button>
                      </EmptyStateBody>
                    </EmptyState>
                  </Bullseye>
                )}
                {githubSignInOption == GitHubSignInOption.PERSONAL_ACCESS_TOKEN && (
                  <>
                    <PageSection variant={"light"} isFilled={true} style={{ height: "100%" }}>
                      <Form>
                        <FormGroup isRequired={true} label={"GitHub Personal Access Token"} fieldId={"github-pat"}>
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
                            {isGitHubAuthenticating && (
                              <InputGroupText style={{ border: "none" }}>
                                <Spinner isSVG={true} size="lg" />
                              </InputGroupText>
                            )}
                          </InputGroup>
                        </FormGroup>
                        <ActionGroup>
                          <Button variant="link" onClick={() => setGitHubSignInOption(GitHubSignInOption.OAUTH)}>
                            Cancel
                          </Button>
                        </ActionGroup>
                      </Form>
                    </PageSection>
                  </>
                )}
              </PageSection>
            </>
          )}
        </Page>
      </Tab>
      <Tab className="kogito-tooling--settings-tab" eventKey={1} title={<TabTitleText>GitLab</TabTitleText>}>
        <Page>
          <PageSection>
            <TextContent>GitLab</TextContent>
          </PageSection>
        </Page>
      </Tab>
      <Tab className="kogito-tooling--settings-tab" eventKey={2} title={<TabTitleText>BitBucket</TabTitleText>}>
        <Page>
          <PageSection>
            <TextContent>BitBucket</TextContent>
          </PageSection>
        </Page>
      </Tab>
      <Tab className="kogito-tooling--settings-tab" eventKey={3} title={<TabTitleText>OpenShift</TabTitleText>}>
        <Page>
          <PageSection>
            <TextContent>OpenShift</TextContent>
          </PageSection>
        </Page>
      </Tab>
      <Tab className="kogito-tooling--settings-tab" eventKey={4} title={<TabTitleText>Google Drive</TabTitleText>}>
        <Page>
          <PageSection>
            <TextContent>Google Drive</TextContent>
          </PageSection>
        </Page>
      </Tab>
      <Tab className="kogito-tooling--settings-tab" eventKey={5} title={<TabTitleText>Dropbox</TabTitleText>}>
        <Page>
          <PageSection>
            <TextContent>Dropbox</TextContent>
          </PageSection>
        </Page>
      </Tab>
    </Tabs>
  );
}

function obfuscate(token: string) {
  if (token.length <= 8) {
    return token;
  }

  const stars = new Array(token.length - 8).join("*");
  const pieceToObfuscate = token.substring(4, token.length - 4);
  return token.replace(pieceToObfuscate, stars);
}

function delay<T>(ms: number) {
  return (ret: T) =>
    new Promise<T>((res) => {
      setTimeout(() => {
        res(ret);
      }, ms);
    });
}
