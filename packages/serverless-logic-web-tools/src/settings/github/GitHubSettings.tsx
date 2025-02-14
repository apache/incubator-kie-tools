/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { QuickStartContext, QuickStartContextValues } from "@patternfly/quickstarts";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateHeader,
  EmptyStateFooter,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup, InputGroupItem } from "@patternfly/react-core/dist/js/components/InputGroup";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { AddCircleOIcon } from "@patternfly/react-icons/dist/js/icons/add-circle-o-icon";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import { GithubIcon } from "@patternfly/react-icons/dist/js/icons/github-icon";
import React from "react";
import { useCallback, useContext, useMemo, useRef, useState } from "react";
import { makeCookieName } from "../../cookies";
import { QuickStartIds } from "../../quickstarts-data";
import { AuthStatus, useSettings, useSettingsDispatch } from "../../settings/SettingsContext";
import { SettingsPageContainer } from "../SettingsPageContainer";
import { SettingsPageProps } from "../types";
import { FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";

const PAGE_TITLE = "GitHub";
export const GITHUB_OAUTH_TOKEN_SIZE = 40;
export const GITHUB_TOKENS_URL = "https://github.com/settings/tokens";
export const GITHUB_TOKENS_HOW_TO_URL =
  "https://help.github.com/en/github/authenticating-to-github/creating-a-personal-access-token-for-the-command-line";
export const GITHUB_AUTH_TOKEN_COOKIE_NAME = makeCookieName("github", "oauth-token");

export enum GitHubSignInOption {
  PERSONAL_ACCESS_TOKEN,
  OAUTH,
}

enum GitHubTokenScope {
  GIST = "gist",
  REPO = "repo",
}

export function GitHubSettings(props: SettingsPageProps) {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const qsContext = useContext<QuickStartContextValues>(QuickStartContext);

  const [potentialGitHubToken, setPotentialGitHubToken] = useState<string | undefined>(undefined);
  const [isGitHubTokenValid, setIsGitHubTokenValid] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const tokenInput = useRef<HTMLInputElement>(null);

  const githubTokenValidated = useMemo(() => {
    return isGitHubTokenValid ? "default" : "error";
  }, [isGitHubTokenValid]);

  const githubTokenHelperText = useMemo(() => {
    return isGitHubTokenValid ? undefined : "Invalid token. Check if it has the 'repo' scope.";
  }, [isGitHubTokenValid]);

  const githubTokenToDisplay = useMemo(() => {
    return obfuscate(potentialGitHubToken ?? settings.github.token) ?? "";
  }, [settings.github, potentialGitHubToken]);

  const handleModalToggle = useCallback(() => {
    setPotentialGitHubToken(undefined);
    setIsGitHubTokenValid(true);
    setIsModalOpen((prevIsModalOpen) => !prevIsModalOpen);
  }, []);

  const onPasteGitHubToken = useCallback(
    (e) => {
      const token = e.clipboardData.getData("text/plain").slice(0, GITHUB_OAUTH_TOKEN_SIZE);
      setPotentialGitHubToken(token);
      settingsDispatch.github.authService
        .authenticate(token)
        .then(() => handleModalToggle())
        .catch(() => setIsGitHubTokenValid(false));
    },
    [settingsDispatch.github.authService, handleModalToggle]
  );

  const onSignOutFromGitHub = useCallback(() => {
    settingsDispatch.github.authService.reset();
    setPotentialGitHubToken(undefined);
  }, [settingsDispatch.github.authService]);

  return (
    <SettingsPageContainer
      pageTitle={PAGE_TITLE}
      subtitle={
        <>
          Data you provide here is necessary for creating repositories containing models you design, and syncing changes
          with GitHub.
          <br />
          All information is locally stored in your browser and never shared with anyone.
        </>
      }
    >
      <PageSection isFilled>
        <PageSection variant={"light"}>
          {settings.github.authStatus === AuthStatus.TOKEN_EXPIRED && (
            <EmptyState>
              <EmptyStateHeader icon={<EmptyStateIcon icon={ExclamationTriangleIcon} />} />
              <TextContent>
                <Text component={"h2"}>GitHub Token expired</Text>
              </TextContent>
              <EmptyStateBody>
                <TextContent>Reset your token to sign in with GitHub again.</TextContent>
              </EmptyStateBody>
              <EmptyStateFooter>
                <br />
                <Button variant={ButtonVariant.secondary} onClick={onSignOutFromGitHub}>
                  Reset
                </Button>
              </EmptyStateFooter>
            </EmptyState>
          )}
          {settings.github.authStatus === AuthStatus.LOADING && (
            <EmptyState>
              <EmptyStateHeader icon={<EmptyStateIcon icon={GithubIcon} />} />
              <EmptyStateFooter>
                <TextContent>
                  <Text component={"h2"}>Signing in with GitHub</Text>
                </TextContent>
                <br />
                <br />
                <Spinner />
              </EmptyStateFooter>
            </EmptyState>
          )}
          {settings.github.authStatus === AuthStatus.SIGNED_IN && (
            <EmptyState>
              <EmptyStateHeader
                icon={<EmptyStateIcon icon={CheckCircleIcon} color={"var(--pf-v5-global--success-color--100)"} />}
              />
              <TextContent>
                <Text component={"h2"} ouiaId={"signed-in-text"}>
                  {"You're signed in with GitHub."}
                </Text>
              </TextContent>
              <EmptyStateBody>
                Gists are <b>{settings.github.scopes?.includes(GitHubTokenScope.GIST) ? "enabled" : "disabled"}.</b>
                <br />
                Private repositories are{" "}
                <b>{settings.github.scopes?.includes(GitHubTokenScope.REPO) ? "enabled" : "disabled"}.</b>
                <br />
                <b>Token: </b>
                <i>{obfuscate(settings.github.token)}</i>
                <br />
                <b>User: </b>
                <i>{settings.github.user?.login}</i>
                <br />
                <b>Scope: </b>
                <i>{settings.github.scopes?.join(", ") || "(none)"}</i>
              </EmptyStateBody>
              <EmptyStateFooter>
                <br />
                <Button variant={ButtonVariant.secondary} onClick={onSignOutFromGitHub}>
                  Sign out
                </Button>
              </EmptyStateFooter>
            </EmptyState>
          )}
          {settings.github.authStatus === AuthStatus.SIGNED_OUT && (
            <EmptyState>
              <EmptyStateHeader icon={<EmptyStateIcon icon={AddCircleOIcon} />} />
              <TextContent>
                <Text component={"h2"}>{"No access token"}</Text>
              </TextContent>
              <EmptyStateBody>
                You currently have no tokens to display. Access tokens allow you for creating repositories containing
                models you design, and syncing changes with GitHub.
              </EmptyStateBody>
              <EmptyStateFooter>
                <Button variant={ButtonVariant.primary} onClick={handleModalToggle} ouiaId={"add-access-token-button"}>
                  Add access token
                </Button>
              </EmptyStateFooter>
            </EmptyState>
          )}
        </PageSection>
      </PageSection>

      {props.pageContainerRef.current && (
        <Modal
          title="Create new token"
          isOpen={isModalOpen && settings.github.authStatus !== AuthStatus.LOADING}
          onClose={handleModalToggle}
          variant={ModalVariant.large}
          appendTo={props.pageContainerRef.current}
        >
          <Form onSubmit={(e) => e.preventDefault()}>
            <h3>
              <a href={GITHUB_TOKENS_URL} target={"_blank"} rel="noopener noreferrer">
                Create a new token&nbsp;&nbsp;
                <ExternalLinkAltIcon />
              </a>
            </h3>
            <FormGroup isRequired={true} label={"Token"} fieldId={"github-pat"}>
              <InputGroup>
                <InputGroupItem isFill>
                  <TextInput
                    ref={tokenInput}
                    autoComplete={"off"}
                    id="token-input"
                    name="tokenInput"
                    aria-describedby="token-text-input-helper"
                    placeholder={"Paste your GitHub token here"}
                    maxLength={GITHUB_OAUTH_TOKEN_SIZE}
                    validated={githubTokenValidated}
                    value={githubTokenToDisplay}
                    onPaste={onPasteGitHubToken}
                    tabIndex={1}
                    ouiaId={"token-input"}
                  />
                </InputGroupItem>
              </InputGroup>
              {githubTokenValidated === "error" ? (
                <FormHelperText>
                  <HelperText>
                    <HelperTextItem variant="error">{githubTokenHelperText}</HelperTextItem>
                  </HelperText>
                </FormHelperText>
              ) : (
                <FormHelperText>
                  <HelperText>
                    <HelperTextItem variant="default">
                      Your token must include the &apos; repo &apos; scope.
                    </HelperTextItem>
                  </HelperText>
                </FormHelperText>
              )}
            </FormGroup>
            <TextContent>
              <Text>
                <Button
                  isInline={true}
                  key="quickstart"
                  variant="link"
                  onClick={() => {
                    qsContext.setActiveQuickStartID?.(QuickStartIds.GitHubTokenQuickStart);
                    setTimeout(() => qsContext.setQuickStartTaskNumber?.(QuickStartIds.GitHubTokenQuickStart, 0), 0);
                  }}
                >
                  Need help getting started? Follow our quickstart guide.
                </Button>
              </Text>
            </TextContent>
          </Form>
        </Modal>
      )}
    </SettingsPageContainer>
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
