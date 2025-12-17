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

import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Card, CardBody, CardFooter, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers/constants";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { CodeIcon } from "@patternfly/react-icons/dist/js/icons/code-icon";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { AccountsDispatchActionKind, useAccountsDispatch } from "../accounts/AccountsContext";
import { useAuthProviders } from "../authProviders/AuthProvidersContext";
import { AUTH_SESSION_NONE, AuthSession } from "../authSessions/AuthSessionApi";
import { useAuthSession, useAuthSessions } from "../authSessions/AuthSessionsContext";
import { getCompatibleAuthSessionWithUrlDomain } from "../authSessions/CompatibleAuthSessions";
import { useRoutes } from "../navigation/Hooks";
import { AdvancedImportModal, AdvancedImportModalRef } from "./AdvancedImportModalContent";
import { isPotentiallyGit, useClonableUrl, useImportableUrl, useImportableUrlValidation } from "./ImportableUrlHooks";
import { AuthProviderGroup } from "../authProviders/AuthProvidersApi";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";

export function ImportFromUrlCard() {
  const routes = useRoutes();
  const navigate = useNavigate();
  const accountsDispatch = useAccountsDispatch();

  const [authSessionId, setAuthSessionId] = useState<string | undefined>(AUTH_SESSION_NONE.id);
  const [url, setUrl] = useState("");
  const [insecurelyDisableTlsCertificateValidation, setInsecurelyDisableTlsCertificateValidation] = useState(false);
  const [disableEncoding, setDisableEncoding] = useState(false);
  const [gitRefName, setGitRef] = useState("");

  const advancedImportModalRef = useRef<AdvancedImportModalRef>(null);

  const { authInfo, authSession } = useAuthSession(authSessionId);

  const importableUrl = useImportableUrl(url);

  const clonableUrl = useClonableUrl(
    url,
    authInfo,
    gitRefName,
    insecurelyDisableTlsCertificateValidation,
    disableEncoding
  );

  // Select authSession based on the importableUrl domain (begin)
  const authProviders = useAuthProviders();
  const { authSessions, authSessionStatus } = useAuthSessions();

  const updateInsecurelyDisableTlsCertificateValidationAndDisableEncoding = useCallback(
    (newAuthSession: AuthSession) => {
      if (newAuthSession?.type === "git") {
        const localAuthProvider = authProviders.find((provider) => provider.id === newAuthSession.authProviderId);
        if (localAuthProvider?.group === AuthProviderGroup.GIT) {
          setInsecurelyDisableTlsCertificateValidation(
            localAuthProvider.insecurelyDisableTlsCertificateValidation ?? false
          );
          setDisableEncoding(localAuthProvider.disableEncoding ?? false);
        }
      }
    },
    [authProviders]
  );

  useEffect(() => {
    const urlDomain = importableUrl.url?.host;
    const { compatible } = getCompatibleAuthSessionWithUrlDomain({
      authProviders,
      authSessions,
      authSessionStatus,
      urlDomain,
    });
    setAuthSessionId(compatible[0]!.id);
    updateInsecurelyDisableTlsCertificateValidationAndDisableEncoding(compatible[0]);
  }, [
    authProviders,
    authSessionStatus,
    authSessions,
    importableUrl,
    updateInsecurelyDisableTlsCertificateValidationAndDisableEncoding,
  ]);
  // Select authSession based on the importableUrl domain (end)

  useEffect(() => {
    setGitRef(clonableUrl.selectedGitRefName ?? "");
  }, [clonableUrl.selectedGitRefName]);

  useEffect(() => {
    authSession && updateInsecurelyDisableTlsCertificateValidationAndDisableEncoding(authSession);
  }, [authSession, updateInsecurelyDisableTlsCertificateValidationAndDisableEncoding]);

  const validation = useImportableUrlValidation(authSession, url, gitRefName, clonableUrl, advancedImportModalRef);

  const isValid = useMemo(() => {
    return validation.option === ValidatedOptions.success;
  }, [validation.option]);

  const onSubmit = useCallback(
    (e: React.SyntheticEvent) => {
      e.preventDefault();
      e.stopPropagation();

      if (!isValid) {
        return;
      }

      navigate({
        pathname: routes.import.path({}),
        search: routes.import.queryString({
          url,
          branch: gitRefName,
          authSessionId,
          insecurelyDisableTlsCertificateValidation: insecurelyDisableTlsCertificateValidation.toString(),
          disableEncoding: disableEncoding.toString(),
        }),
      });
    },
    [
      authSessionId,
      gitRefName,
      navigate,
      isValid,
      routes.import,
      url,
      insecurelyDisableTlsCertificateValidation,
      disableEncoding,
    ]
  );

  const buttonLabel = useMemo(() => {
    if (isPotentiallyGit(importableUrl.type)) {
      return "Clone";
    } else {
      return "Import";
    }
  }, [importableUrl]);

  return (
    <>
      <Card isFullHeight={true} isLarge={true} isPlain={true} isSelected={url.length > 0}>
        <CardTitle>
          <Flex justifyContent={{ default: "justifyContentSpaceBetween" }} flexWrap={{ default: "nowrap" }}>
            <FlexItem>
              <TextContent>
                <Text
                  component={TextVariants.h2}
                  style={{
                    whiteSpace: "nowrap",
                    overflow: "hidden",
                    textOverflow: "ellipsis",
                  }}
                >
                  <CodeIcon />
                  &nbsp;&nbsp;From URL
                </Text>
              </TextContent>
            </FlexItem>
            <FlexItem style={{ minWidth: 0 }}>
              <Button
                size="sm"
                variant={ButtonVariant.link}
                style={{ paddingBottom: 0, fontWeight: "lighter" }}
                onClick={() => advancedImportModalRef.current?.open()}
              >
                More options...
              </Button>
            </FlexItem>
          </Flex>
        </CardTitle>
        <CardBody>
          <TextContent>
            <Text component={TextVariants.p}>
              Import a Git repository, a GitHub Gist, Bitbucket Snippet, GitLab Snippet or any other file URL.
            </Text>
          </TextContent>
          <br />
          <Form onSubmit={onSubmit}>
            <FormGroup fieldId="url">
              <TextInput
                id={"url"}
                ouiaId={"import-from-url-input"}
                validated={validation.option}
                isRequired={true}
                placeholder={"URL"}
                value={url}
                onChange={(_event, val) => setUrl(val)}
              />
              <HelperText>
                {validation.option === "error" ? (
                  <HelperTextItem variant="error" icon={<ExclamationCircleIcon />}>
                    {validation.helperTextInvalid}
                  </HelperTextItem>
                ) : (
                  validation.helperText
                )}
              </HelperText>
            </FormGroup>
          </Form>
        </CardBody>
        <CardFooter>
          <Button
            isDisabled={!isValid}
            variant={url.length > 0 ? ButtonVariant.primary : ButtonVariant.secondary}
            onClick={onSubmit}
            type={"submit"}
            ouiaId="import-from-url-button"
          >
            {buttonLabel}
          </Button>
        </CardFooter>
      </Card>
      <AdvancedImportModal
        ref={advancedImportModalRef}
        clonableUrl={clonableUrl}
        validation={validation}
        onSubmit={onSubmit}
        onClose={undefined}
        authSessionId={authSessionId}
        setAuthSessionId={(newAuthSessionId) => {
          setAuthSessionId(newAuthSessionId);
          accountsDispatch({ kind: AccountsDispatchActionKind.CLOSE });
        }}
        url={url}
        setUrl={setUrl}
        gitRefName={gitRefName}
        setGitRefName={setGitRef}
        insecurelyDisableTlsCertificateValidation={insecurelyDisableTlsCertificateValidation}
        setInsecurelyDisableTlsCertificateValidation={setInsecurelyDisableTlsCertificateValidation}
      />
    </>
  );
}
