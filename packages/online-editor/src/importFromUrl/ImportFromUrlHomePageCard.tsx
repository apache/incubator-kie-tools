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

import { FlexItem } from "@patternfly/react-core";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Card, CardBody, CardFooter, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers/constants";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { CodeIcon } from "@patternfly/react-icons/dist/js/icons/code-icon";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useHistory } from "react-router";
import { AccountsDispatchActionKind, useAccountsDispatch } from "../accounts/AccountsDispatchContext";
import { useAuthProviders } from "../accounts/authProviders/AuthProvidersContext";
import { AUTH_SESSION_NONE } from "../accounts/authSessions/AuthSessionApi";
import { useAuthSession, useAuthSessions } from "../accounts/authSessions/AuthSessionsContext";
import { getCompatibleAuthSessionWithUrlDomain } from "../accounts/authSessions/CompatibleAuthSessions";
import { useRoutes } from "../navigation/Hooks";
import { AdvancedImportModal, AdvancedImportModalRef } from "./AdvancedImportModalContent";
import { isPotentiallyGit, useClonableUrl, useImportableUrl, useImportableUrlValidation } from "./ImportableUrlHooks";

export function ImportFromUrlCard() {
  const routes = useRoutes();
  const history = useHistory();
  const accountsDispatch = useAccountsDispatch();

  const [authSessionId, setAuthSessionId] = useState<string | undefined>(AUTH_SESSION_NONE.id);
  const [url, setUrl] = useState("");
  const [gitRefName, setGitRef] = useState("");

  const advancedImportModalRef = useRef<AdvancedImportModalRef>(null);

  const { authInfo, authSession } = useAuthSession(authSessionId);

  const importableUrl = useImportableUrl(url);
  const clonableUrl = useClonableUrl(url, authInfo, gitRefName);

  // Select authSession based on the importableUrl domain (begin)
  const authProviders = useAuthProviders();
  const { authSessions, authSessionStatus } = useAuthSessions();

  useEffect(() => {
    const urlDomain = importableUrl.url?.hostname;
    const { compatible } = getCompatibleAuthSessionWithUrlDomain({
      authProviders,
      authSessions,
      authSessionStatus,
      urlDomain,
    });
    setAuthSessionId(compatible[0]!.id);
  }, [authProviders, authSessionStatus, authSessions, importableUrl]);
  // Select authSession based on the importableUrl domain (end)

  useEffect(() => {
    setGitRef(clonableUrl.selectedGitRefName ?? "");
  }, [clonableUrl.selectedGitRefName]);

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

      history.push({
        pathname: routes.import.path({}),
        search: routes.import.queryString({ url, branch: gitRefName, authSessionId }),
      });
    },
    [authSessionId, gitRefName, history, isValid, routes.import, url]
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
                isSmall={true}
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
            <Text component={TextVariants.p}>Import a Git repository, a GitHub Gist, or any other file URL.</Text>
          </TextContent>
          <br />
          <Form onSubmit={onSubmit}>
            <FormGroup
              helperTextInvalid={validation.helperTextInvalid}
              helperText={validation.helperText}
              helperTextInvalidIcon={<ExclamationCircleIcon />}
              validated={validation.option}
              fieldId="url"
            >
              <TextInput
                id={"url"}
                ouiaId={"import-from-url-input"}
                validated={validation.option}
                isRequired={true}
                placeholder={"URL"}
                value={url}
                onChange={setUrl}
              />
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
      />
    </>
  );
}
