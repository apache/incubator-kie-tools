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

import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Card, CardBody, CardFooter, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Form, FormGroup, FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers/constants";
import CheckCircleIcon from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import CodeBranchIcon from "@patternfly/react-icons/dist/js/icons/code-branch-icon";
import { CodeIcon } from "@patternfly/react-icons/dist/js/icons/code-icon";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import UserIcon from "@patternfly/react-icons/dist/js/icons/user-icon";
import UsersIcon from "@patternfly/react-icons/dist/js/icons/users-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useHistory } from "react-router";
import { AuthSource, AuthSourceKeys } from "../authSources/AuthSourceHooks";
import { useRoutes } from "../navigation/Hooks";
import { AuthStatus, useSettings } from "../settings/SettingsContext";
import { ImportableUrl, isPotentiallyGit, UrlType, useClonableUrl, useImportableUrl } from "./ImportableUrlHooks";
import { PromiseState, PromiseStateStatus } from "../workspace/hooks/PromiseState";
import { AdvancedCloneModal, AdvancedCloneModalRef } from "./AdvancedCloneModalContent";
import { AuthSourceIcon } from "../authSources/AuthSourceIcon";

export function ImportFromUrlCard() {
  const routes = useRoutes();
  const history = useHistory();
  const settings = useSettings();

  const [authSource, setAuthSource] = useState<AuthSourceKeys>(AuthSourceKeys.NONE);
  const [url, setUrl] = useState("");
  const [branch, setBranch] = useState("");

  const advancedCloneModalRef = useRef<AdvancedCloneModalRef>(null);

  const importableUrl = useImportableUrl(url);
  const clonableUrlObject = useClonableUrl(url, authSource, branch);
  const { clonableUrl, selectedBranch, gitRefsPromise } = clonableUrlObject;

  // AUTH SOURCE FROM URL (begin)
  useEffect(() => {
    const urlType = importableUrl.type;
    if (urlType === UrlType.GITHUB_DOT_COM || urlType === UrlType.GIST_DOT_GITHUB_DOT_COM) {
      if (settings.github.authStatus === AuthStatus.SIGNED_IN) {
        setAuthSource(AuthSourceKeys.GITHUB);
        return;
      }
    }

    setAuthSource(AuthSourceKeys.NONE);
  }, [importableUrl, settings.github.authStatus]);
  // AUTH SOURCE FROM URL (end)

  // BRANCH FROM URL (begin)
  useEffect(() => {
    setBranch(selectedBranch ?? "");
  }, [selectedBranch]);
  // BRANCH FROM URL (end)

  const validation = useImportableUrlValidation(authSource, url, branch, clonableUrlObject, advancedCloneModalRef);

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
        search: routes.import.queryString({ url, branch, authSource }),
      });
    },
    [authSource, branch, history, isValid, routes.import, url]
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
          <TextContent>
            <Text component={TextVariants.h2}>
              <CodeIcon />
              &nbsp;&nbsp;From URL
            </Text>
          </TextContent>
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
                ouiaId={"url"}
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
          {isPotentiallyGit(importableUrl.type) && !isValid && gitRefsPromise.status !== PromiseStateStatus.PENDING && (
            <Button
              isSmall={true}
              variant={ButtonVariant.link}
              style={{ paddingBottom: 0, verticalAlign: "text-top" }}
              onClick={() => advancedCloneModalRef.current?.open()}
            >
              Advanced...
            </Button>
          )}
        </CardFooter>
      </Card>
      <AdvancedCloneModal
        ref={advancedCloneModalRef}
        clonableUrl={clonableUrlObject}
        validation={validation}
        onSubmit={onSubmit}
        onClose={undefined}
        authSource={authSource}
        setAuthSource={setAuthSource}
        url={url}
        setUrl={setUrl}
        branch={branch}
        setBranch={setBranch}
      />
    </>
  );
}

export function useImportableUrlValidation(
  authSource: AuthSourceKeys | undefined,
  url: string | undefined,
  branch: string | undefined,
  clonableUrl: ReturnType<typeof useClonableUrl>,
  advancedCloneModalRef?: React.RefObject<AdvancedCloneModalRef>
) {
  const settings = useSettings();

  return useMemo(() => {
    if (!url) {
      return {
        option: ValidatedOptions.default,
        helperText: <FormHelperText isHidden={true} icon={<Spinner size={"sm"} />} />,
      };
    }

    if (clonableUrl.gitRefsPromise.status === PromiseStateStatus.PENDING) {
      return {
        option: ValidatedOptions.default,
        helperText: (
          <FormHelperText isHidden={false} icon={<Spinner size={"sm"} />}>
            Loading...
          </FormHelperText>
        ),
      };
    }

    if (clonableUrl.clonableUrl.error) {
      return {
        option: ValidatedOptions.error,
        helperTextInvalid: clonableUrl.clonableUrl.error,
      };
    }

    return {
      option: ValidatedOptions.success,
      helperText: (
        <FormHelperText
          isHidden={false}
          icon={<CheckCircleIcon style={{ visibility: "hidden", width: 0 }} />}
          style={branch ? {} : { visibility: "hidden" }}
        >
          <>
            <CodeBranchIcon />
            &nbsp;&nbsp;
            {branch}
            &nbsp;&nbsp;
            <AuthSourceIcon authSource={authSource} />
            &nbsp;&nbsp;
            {authSource === AuthSourceKeys.GITHUB ? settings.github.user?.login ?? "Not logged in" : "Anonymous"}
            <Button
              isSmall={true}
              variant={ButtonVariant.link}
              style={{ paddingTop: 0, paddingBottom: 0 }}
              onClick={() => advancedCloneModalRef?.current?.open()}
            >
              Change...
            </Button>
          </>
        </FormHelperText>
      ),
    };
  }, [
    url,
    clonableUrl.gitRefsPromise.status,
    clonableUrl.clonableUrl.error,
    branch,
    authSource,
    settings.github.user?.login,
    advancedCloneModalRef,
  ]);
}
