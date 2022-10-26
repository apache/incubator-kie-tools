import * as React from "react";
import UserIcon from "@patternfly/react-icons/dist/js/icons/user-icon";
import { useCallback, useMemo, useReducer, useState } from "react";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";
import { Card, CardBody, CardHeaderMain, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import GithubIcon from "@patternfly/react-icons/dist/js/icons/github-icon";
import BitbucketIcon from "@patternfly/react-icons/dist/js/icons/bitbucket-icon";
import GitlabIcon from "@patternfly/react-icons/dist/js/icons/gitlab-icon";
import OpenshiftIcon from "@patternfly/react-icons/dist/js/icons/openshift-icon";
import QuestionIcon from "@patternfly/react-icons/dist/js/icons/question-icon";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { CardHeader, Form, FormGroup, InputGroup, TextInput } from "@patternfly/react-core";
import { v4 as uuid } from "uuid";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { useOnlineI18n } from "../i18n";
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import { InfoAltIcon } from "@patternfly/react-icons/dist/js/icons/info-alt-icon";
import { Octokit } from "@octokit/rest";
import { IconSize } from "@patternfly/react-icons/dist/js/createIcon";
import AngleLeftIcon from "@patternfly/react-icons/dist/js/icons/angle-left-icon";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";

export interface AuthSession {
  id: string;
  token: string;
  login: string;
  email: string;
  name: string;
}

export type AuthProvider =
  | {
      id: string;
      type: "openshift";
      name: string;
      domain: undefined;
      iconPath?: string;
      enabled: true;
    }
  | {
      id: string;
      type: "github" | "bitbucket" | "gitlab";
      name: string;
      domain: string;
      iconPath?: string;
      enabled: boolean;
    };

const AUTH_PROVIDERS: AuthProvider[] = [
  {
    id: "github_dot_com", // Primary Key
    domain: "github.com",
    type: "github",
    name: "GitHub",
    enabled: true,
    iconPath: "", // (Optional). Each type has a default icon path that is always part of the webapp.
  },
  {
    id: "gitlab_dot_com",
    domain: "gitlab.com",
    type: "gitlab",
    name: "GitLab",
    enabled: false,
    iconPath: "", // (Optional). Each type has a default icon path that is always part of the webapp.
  },
  {
    id: "bitbucket_dot_com",
    domain: "bitbucket.com",
    type: "bitbucket",
    name: "Bitbucket",
    enabled: false,
    iconPath: "", // (Optional). Each type has a default icon path that is always part of the webapp.
  },
  {
    id: "github_at_ibm",
    domain: "github.ibm.com",
    type: "github",
    name: "GitHub @ IBM",
    enabled: true,
    iconPath: "static/assets/ibm-github-icon.png", // Always relative path
  },
  {
    id: "bitbucket_at_my_customer",
    domain: "bitbucket.my-customer.com",
    type: "bitbucket",
    name: "Bitbucket @ My customer",
    enabled: false,
    iconPath: "static/assets/bitbucket-my-customer.png", // Always relative path
  },
  {
    id: "github_at_my_partner",
    domain: "my-partner.ibm.com",
    type: "github",
    name: "GitHub @ My partner",
    enabled: false,
    iconPath: "static/assets/my-partner-github-icon.png", // Always relative path
  },
  {
    id: "openshift",
    type: "openshift",
    name: "OpenShift cluster",
    domain: undefined,
    enabled: true,
  },
];

export const GITHUB_OAUTH_TOKEN_SIZE = 40;
export const GITHUB_TOKENS_HOW_TO_URL =
  "https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token";

export function AccountsIcon() {
  const { i18n } = useOnlineI18n();

  const [isAccountsModalOpen, setAccountsModalOpen] = useState(false);

  const [authSessions, setAuthSessions] = useState<Map<string, AuthSession>>(new Map());
  const [selectedAuthProvider, setSelectedAuthProvider] = useState<AuthProvider>();

  const authProviders = useMemo<AuthProvider[]>(() => AUTH_PROVIDERS, []);

  const onPasteGitHubToken = useCallback(async (e: React.ClipboardEvent, githubInstanceDomain: string) => {
    const token = e.clipboardData.getData("text/plain").slice(0, GITHUB_OAUTH_TOKEN_SIZE);
    (document.getElementById("github-personal-access-token-input") as HTMLInputElement).setAttribute(
      "value",
      obfuscate(token)
    );

    const octokit = new Octokit({
      auth: token,
      baseUrl: githubApiUrl(githubInstanceDomain),
    });

    const response = await octokit.users.getAuthenticated();

    const scopes = response.headers["x-oauth-scopes"]?.split(", ") ?? [];
    if (!scopes.includes("repo") || !scopes.includes("gist")) {
      throw new Error("GitHub Personal Access Token (classic) must include the 'repo' and 'gist' scopes.");
    }

    setAuthSessions((prev) => {
      const id = uuid();
      return prev.set(id, {
        id,
        token,
        login: response.data.login,
        name: response.data.name ?? "",
        email: response.data.email ?? "",
      });
    });
  }, []);

  const isGitHubTokenValid = true;

  const githubTokenValidated = useMemo(() => {
    return isGitHubTokenValid ? "default" : "error";
  }, [isGitHubTokenValid]);

  const githubTokenHelperText = useMemo(() => {
    return isGitHubTokenValid ? undefined : "Invalid token. Check if it has the 'repo' scope.";
  }, [isGitHubTokenValid]);

  return (
    <>
      <Button
        variant={ButtonVariant.plain}
        onClick={() => setAccountsModalOpen((prev) => !prev)}
        aria-label="Accounts"
        className={"kie-tools--masthead-hoverable-dark"}
      >
        <UserIcon />
      </Button>
      <Modal
        variant={ModalVariant.medium}
        isOpen={isAccountsModalOpen}
        onClose={() => setAccountsModalOpen(false)}
        header={
          <div>
            <TextContent>
              <Text component={TextVariants.h1}>
                {!selectedAuthProvider && <>Connect to a new account</>}
                {selectedAuthProvider && (
                  <>
                    {`Connect with`}
                    &nbsp;
                    {selectedAuthProvider.name}
                    &nbsp;
                    <AuthProviderIcon authProvider={selectedAuthProvider} size={"sm"} />
                  </>
                )}
              </Text>
            </TextContent>
            <>
              <Button
                key={"back"}
                onClick={() => setSelectedAuthProvider(undefined)}
                variant={ButtonVariant.link}
                style={{ paddingLeft: 0 }}
                icon={<AngleLeftIcon />}
              >
                {`Back`}
              </Button>
              <br />
            </>
            <br />
            <Divider inset={{ default: "inset3xl" }} />
          </div>
        }
      >
        <>
          {selectedAuthProvider && (
            <>
              {selectedAuthProvider.type === "github" && (
                <Page>
                  <PageSection variant={"light"}>
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
                            onPaste={(e) => onPasteGitHubToken(e, selectedAuthProvider.domain)}
                            autoFocus={true}
                          />
                        </InputGroup>
                      </FormGroup>
                    </Form>
                    <br />
                    <h3>
                      <a href={generateNewGitHubPatUrl(selectedAuthProvider.domain)} target={"_blank"}>
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
                  </PageSection>
                </Page>
              )}
            </>
          )}
          {!selectedAuthProvider && (
            <>
              {authSessions.size <= 0 && (
                <>
                  <Gallery hasGutter={true}>
                    {authProviders
                      .sort((a, b) => (a.name > b.name ? -1 : 1))
                      .sort((a) => (a.enabled ? -1 : 1))
                      .map((authProvider) => (
                        <Card
                          key={authProvider.id}
                          isSelectable={authProvider.enabled}
                          isRounded={true}
                          onClick={() => {
                            if (authProvider.enabled) {
                              return setSelectedAuthProvider(authProvider);
                            }
                          }}
                          style={{ opacity: authProvider.enabled ? 1 : 0.5 }}
                        >
                          <CardHeader>
                            <CardHeaderMain>
                              <CardTitle>
                                {authProvider.name}
                                {!authProvider.enabled && (
                                  <TextContent>
                                    <Text component={TextVariants.small}>
                                      <i>Available soon!</i>
                                    </Text>
                                  </TextContent>
                                )}
                              </CardTitle>
                              <TextContent>
                                <Text component={TextVariants.small}>
                                  <i>{authProvider.domain ?? <>&nbsp;</>}</i>
                                </Text>
                              </TextContent>
                            </CardHeaderMain>
                          </CardHeader>
                          <br />
                          <CardBody>
                            <AuthProviderIcon authProvider={authProvider} size={"lg"} />
                          </CardBody>
                        </Card>
                      ))}
                  </Gallery>
                </>
              )}
              {authSessions.size > 0 && (
                <>
                  {[...authSessions.values()].map((authSession) => {
                    return <>{JSON.stringify(authSession)}</>;
                  })}
                </>
              )}
            </>
          )}
        </>
      </Modal>
    </>
  );
}

export function AuthProviderIcon(props: { authProvider: AuthProvider; size: IconSize | keyof typeof IconSize }) {
  if (props.authProvider.iconPath) {
    return <QuestionIcon size={props.size} />;
    // return <img width={"120px"} height={"120px"} src={props.authProvider.iconPath} />;
  }

  if (props.authProvider.type === "github") {
    return <GithubIcon size={props.size} />;
  }

  if (props.authProvider.type === "bitbucket") {
    return <BitbucketIcon size={props.size} />;
  }

  if (props.authProvider.type === "gitlab") {
    return <GitlabIcon size={props.size} />;
  }

  if (props.authProvider.type === "openshift") {
    return <OpenshiftIcon size={props.size} />;
  }

  return <QuestionIcon size={props.size} />;
}

export function obfuscate(token: string) {
  if (token.length <= 8) {
    return token;
  }

  const stars = new Array(token.length - 8).join("*");
  const pieceToObfuscate = token.substring(4, token.length - 4);
  return token.replace(pieceToObfuscate, stars);
}

export const generateNewGitHubPatUrl = (domain: string) => {
  return `https://${domain}/settings/tokens`;
};

export const githubApiUrl = (domain: string) => {
  return `https://${domain}/api/v3`;
};
