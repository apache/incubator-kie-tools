/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import React, { useCallback, useEffect, useState } from "react";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Button, ButtonType, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { useAuthSessions, useAuthSessionsDispatch } from "../AuthSessionsContext";
import { AuthSessionsService } from "../AuthSessionsService";
import { useEnv } from "../../env/hooks/EnvContext";
import { useRoutes } from "../../navigation/Hooks";
import { AUTH_SESSION_OIDC_DEFAULT_SCOPES, AuthSession } from "../AuthSessionApi";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Form, FormGroup, ActionGroup, FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { ExpandableSection } from "@patternfly/react-core/dist/js/components/ExpandableSection";
import { HelperText, HelperTextItem } from "@patternfly/react-core";

type Props = {
  onAddAuthSession: (authSession: AuthSession) => void;
};

export const NewAuthSessionModal: React.FC<Props> = ({ onAddAuthSession }) => {
  const [runtimeUrl, setRuntimeUrl] = useState<string>();
  const [alias, setAlias] = useState<string>();
  const [forceLoginPrompt, setForceLoginPrompt] = useState(false);
  const routes = useRoutes();
  const { env } = useEnv();

  const [audience, setAudience] = useState<string>();
  const [scope, setScope] = useState<string>(AUTH_SESSION_OIDC_DEFAULT_SCOPES);
  const [clientId, setClientId] = useState<string>(env.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_OIDC_CLIENT_CLIENT_ID);

  const { isNewAuthSessionModalOpen } = useAuthSessions();
  const { setIsNewAuthSessionModalOpen, add } = useAuthSessionsDispatch();
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setLoading] = useState(false);
  const [isAdvancedOIDCSettingsExpanded, setIsAdvancedOIDCSettingsExpanded] = useState(false);

  const onCancel = useCallback(() => {
    setIsNewAuthSessionModalOpen(false);
    setRuntimeUrl("");
    setAlias("");
    setClientId(env.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_OIDC_CLIENT_CLIENT_ID);
    setScope(AUTH_SESSION_OIDC_DEFAULT_SCOPES);
    setAudience("");
  }, [env.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_OIDC_CLIENT_CLIENT_ID, setIsNewAuthSessionModalOpen]);

  const onConnect = useCallback<React.FormEventHandler>(
    (e) => {
      e.stopPropagation();
      e.preventDefault();

      async function c() {
        try {
          setError(null);

          if (!runtimeUrl || !alias) {
            setError("Both Alias and URL are required.");
            return;
          }

          try {
            new URL(runtimeUrl);
          } catch (e) {
            setError("Invalid URL.");
            return;
          }

          const checkResults = await AuthSessionsService.checkIfAuthenticationRequired(runtimeUrl);

          if (checkResults.isAuthenticationRequired) {
            await AuthSessionsService.authenticate({
              runtimeUrl,
              authServerUrl: checkResults.authServerUrl,
              clientId,
              name: alias,
              forceLoginPrompt,
              audience,
              scope,
              loginSuccessRoute: routes.login.url({ base: window.location.origin, pathParams: {} }),
            });
          } else {
            const authSession = await AuthSessionsService.buildAuthSession({
              runtimeUrl,
              name: alias,
              isAuthenticationRequired: checkResults.isAuthenticationRequired,
            });

            await add(authSession);

            AuthSessionsService.cleanTemporaryAuthSessionData();

            onAddAuthSession(authSession);
            setIsNewAuthSessionModalOpen(false);
          }
        } catch (e) {
          console.error(e);
          setError(`Could not communicate with runtime running at '${runtimeUrl}'`);
        } finally {
          setLoading(false);
        }
      }

      setLoading(true);
      c();
    },
    [
      runtimeUrl,
      alias,
      clientId,
      forceLoginPrompt,
      audience,
      scope,
      routes.login,
      add,
      onAddAuthSession,
      setIsNewAuthSessionModalOpen,
    ]
  );

  return (
    <Modal
      isOpen={isNewAuthSessionModalOpen}
      onClose={onCancel}
      title={"Connect to a runtime"}
      variant={ModalVariant.large}
    >
      <Form onSubmit={onConnect}>
        <FormGroup label="Alias" isRequired={true}>
          <TextInput
            id="alias"
            aria-label="Alias"
            autoFocus={true}
            onChange={(_event, val) => setAlias(val)}
            placeholder="Enter an alias..."
            tabIndex={0}
          />
        </FormGroup>
        <FormGroup label="URL" isRequired={true}>
          {error === "error" ? (
            <FormHelperText>
              <HelperText>
                <HelperTextItem variant="error">{error}</HelperTextItem>
              </HelperText>
            </FormHelperText>
          ) : (
            <FormHelperText>
              <HelperText>
                <HelperTextItem variant="default"></HelperTextItem>
              </HelperText>
            </FormHelperText>
          )}
          <TextInput
            id="url"
            aria-label="URL"
            tabIndex={0}
            onChange={(_event, val) => setRuntimeUrl(val)}
            placeholder="Enter a URL..."
          />
        </FormGroup>
        <FormGroup isRequired={false}>
          <FormHelperText>
            <HelperText>
              <HelperTextItem variant="default">
                Check this box if you are connecting to a secured runtime and intend to use a different user than the
                one currently logged in to your Identity Provider.
              </HelperTextItem>
            </HelperText>
          </FormHelperText>
          <Checkbox
            id="force-login-prompt"
            aria-label="Force Login Prompt"
            isChecked={forceLoginPrompt}
            onChange={(_event, checked) => setForceLoginPrompt(checked)}
            label={
              <span className="pf-v5-c-form__label pf-v5-c-form__label-text">
                Force login prompt <i>(for secured runtimes only)</i>
              </span>
            }
            tabIndex={0}
          />
        </FormGroup>

        <ExpandableSection
          toggleText={"Advanced OpenID Connect settings"}
          onToggle={() => setIsAdvancedOIDCSettingsExpanded((currentValue) => !currentValue)}
          isExpanded={isAdvancedOIDCSettingsExpanded}
        >
          <FormGroup label="Client ID" isRequired={true}>
            {error === "error" ? (
              <FormHelperText>
                <HelperText>
                  <HelperTextItem variant="error">{error}</HelperTextItem>
                </HelperText>
              </FormHelperText>
            ) : (
              <FormHelperText>
                <HelperText>
                  <HelperTextItem variant="default"></HelperTextItem>
                </HelperText>
              </FormHelperText>
            )}
            <TextInput
              id="clientId"
              aria-label="Client ID"
              value={clientId}
              tabIndex={isAdvancedOIDCSettingsExpanded ? 0 : undefined}
              onChange={(_event, val) => setClientId(val)}
            />
          </FormGroup>
          <FormGroup label="Scope" isRequired={true}>
            {error === "error" ? (
              <FormHelperText>
                <HelperText>
                  <HelperTextItem variant="error">{error}</HelperTextItem>
                </HelperText>
              </FormHelperText>
            ) : (
              <FormHelperText>
                <HelperText>
                  <HelperTextItem variant="default"></HelperTextItem>
                </HelperText>
              </FormHelperText>
            )}
            <TextInput
              id="scope"
              aria-label="Scope"
              value={scope}
              tabIndex={isAdvancedOIDCSettingsExpanded ? 0 : undefined}
              onChange={(_event, val) => setScope(val)}
            />
          </FormGroup>
          <FormGroup label="Audience" isRequired={false}>
            {error === "error" ? (
              <FormHelperText>
                <HelperText>
                  <HelperTextItem variant="error">{error}</HelperTextItem>
                </HelperText>
              </FormHelperText>
            ) : (
              <FormHelperText>
                <HelperText>
                  <HelperTextItem variant="default"></HelperTextItem>
                </HelperText>
              </FormHelperText>
            )}
            <TextInput
              id="audience"
              aria-label="Audience"
              value={audience}
              tabIndex={isAdvancedOIDCSettingsExpanded ? 0 : undefined}
              onChange={(_event, val) => setAudience(val)}
              placeholder="The Audience (aud) or Identifier of the application."
            />
          </FormGroup>
        </ExpandableSection>

        <ActionGroup>
          <Button
            type={ButtonType.submit}
            variant={ButtonVariant.primary}
            isLoading={isLoading}
            isDisabled={isLoading || !runtimeUrl || !alias}
          >
            Connect
          </Button>
          <Button variant={ButtonVariant.link} onClick={onCancel}>
            Cancel
          </Button>
        </ActionGroup>
      </Form>
    </Modal>
  );
};
