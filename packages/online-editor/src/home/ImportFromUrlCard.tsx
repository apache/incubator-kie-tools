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
import { settings } from "cluster";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { useHistory } from "react-router";
import { useRoutes } from "../navigation/Hooks";
import { useCancelableEffect } from "../reactExt/Hooks";
import { AuthStatus, useSettings } from "../settings/SettingsContext";
import { ImportableUrl, isPotentiallyGit, UrlType, useImportableUrl } from "../workspace/hooks/ImportableUrlHooks";
import { PromiseStateStatus, usePromiseState } from "../workspace/hooks/PromiseState";
import { GitServerRef } from "../workspace/worker/api/GitServerRef";
import { useWorkspaces } from "../workspace/WorkspacesContext";
import { AdvancedCloneModal, AdvancedCloneModalRef } from "./AdvancedCloneModalContent";

export function ImportFromUrlCard() {
  const routes = useRoutes();
  const history = useHistory();
  const settings = useSettings();
  const [url, setUrl] = useState("");

  const enhancedImportableUrl = useEnhancedImportableUrl(url);
  const { importableUrl, selectedBranch, refsPromise } = enhancedImportableUrl;

  const advancedCloneModalRef = React.useRef<AdvancedCloneModalRef>(null);

  const validation = useMemo(() => {
    if (!url) {
      return {
        option: ValidatedOptions.default,
        helperText: (
          <FormHelperText isHidden={true} icon={<Spinner size={"sm"} />}>
            Loading...
          </FormHelperText>
        ),
      };
    }

    if (refsPromise.status === PromiseStateStatus.PENDING) {
      return {
        option: ValidatedOptions.default,
        helperText: (
          <FormHelperText isHidden={false} icon={<Spinner size={"sm"} />}>
            Loading...
          </FormHelperText>
        ),
      };
    }

    if (importableUrl.error) {
      return {
        option: ValidatedOptions.error,
        helperTextInvalid: importableUrl.error,
      };
    }

    return {
      option: ValidatedOptions.success,
      helperText: (
        <FormHelperText
          isHidden={false}
          icon={<CheckCircleIcon style={{ visibility: "hidden", width: 0 }} />}
          style={selectedBranch ? {} : { visibility: "hidden" }}
        >
          <>
            <CodeBranchIcon />
            &nbsp;&nbsp;{selectedBranch}
            &nbsp;&nbsp;
            {(enhancedImportableUrl.importableUrl.type === UrlType.GITHUB_DOT_COM ||
              enhancedImportableUrl.importableUrl.type === UrlType.GIST_DOT_GITHUB_DOT_COM) &&
            settings.github.authStatus === AuthStatus.SIGNED_IN ? (
              <>
                <UserIcon />
                &nbsp;&nbsp;{settings.github.user?.login}
              </>
            ) : (
              <>
                <UsersIcon />
                &nbsp;&nbsp;<i>Anonymous</i>
              </>
            )}
            <Button
              isSmall={true}
              variant={ButtonVariant.link}
              style={{ paddingTop: 0, paddingBottom: 0 }}
              onClick={() => advancedCloneModalRef.current?.open()}
            >
              Change...
            </Button>
          </>
        </FormHelperText>
      ),
    };
  }, [
    url,
    refsPromise.status,
    importableUrl.error,
    selectedBranch,
    enhancedImportableUrl.importableUrl.type,
    settings.github.authStatus,
    settings.github.user?.login,
  ]);

  const isValid = useMemo(() => {
    return validation.option === ValidatedOptions.success;
  }, [validation.option]);

  const onSubmit = useCallback(
    (e: React.FormEvent) => {
      e.preventDefault();
      e.stopPropagation();

      if (!isValid) {
        return;
      }

      history.push({
        pathname: routes.importModel.path({}),
        search: routes.importModel.queryString({ url: url, branch: selectedBranch }),
      });
    },
    [history, isValid, routes.importModel, selectedBranch, url]
  );

  const buttonLabel = useMemo(() => {
    if (isPotentiallyGit(importableUrl.type) && isValid) {
      return "Clone";
    } else {
      return "Import";
    }
  }, [importableUrl.type, isValid]);

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
            <Text component={TextVariants.p}>Import a GitHub Repository, a GitHub Gist, or any other file URL.</Text>
          </TextContent>
          <br />
          <Form onSubmit={onSubmit}>
            <FormGroup
              helperTextInvalid={validation.helperTextInvalid}
              helperText={validation.helperText}
              helperTextInvalidIcon={<ExclamationCircleIcon />}
              fieldId="url"
              validated={validation.option}
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
        </CardFooter>
      </Card>
      <AdvancedCloneModal ref={advancedCloneModalRef} enhancedImportableUrl={enhancedImportableUrl} />
    </>
  );
}

export function useEnhancedImportableUrl(url?: string) {
  const importableUrl = useImportableUrl(url);

  const { defaultBranch, refsPromise, defaultRef } = useGitRefs(
    isPotentiallyGit(importableUrl.type) ? (importableUrl as { url: URL }).url : undefined
  );

  const potentialSelectedBranch = useMemo(() => {
    if (isPotentiallyGit(importableUrl.type)) {
      return (importableUrl as any).branch ?? defaultBranch;
    } else {
      return undefined;
    }
  }, [defaultBranch, importableUrl]);

  const selectedBranch = useMemo(() => {
    if (!potentialSelectedBranch) {
      return undefined;
    }

    const potentialSelectedBranchExists = refsPromise.data?.some(
      ({ ref }) => ref === `refs/heads/${potentialSelectedBranch}`
    );

    if (potentialSelectedBranchExists) {
      return potentialSelectedBranch;
    } else {
      return undefined;
    }
  }, [potentialSelectedBranch, refsPromise.data]);

  const enhancedImportableUrl: ImportableUrl = useMemo(() => {
    if (importableUrl.type === UrlType.INVALID || refsPromise.status === PromiseStateStatus.PENDING) {
      return importableUrl;
    }

    if (!isPotentiallyGit(importableUrl.type)) {
      return importableUrl;
    }

    if (defaultBranch) {
      if (selectedBranch) {
        return importableUrl;
      } else {
        return {
          type: UrlType.INVALID,
          url: importableUrl.url.toString(),
          error: `Selected branch '${potentialSelectedBranch}' does not exist.`,
        };
      }
    }

    return {
      type: UrlType.INVALID,
      url: importableUrl.url.toString(),
      error: `Can't determine git refs for '${importableUrl.url.toString()}'`,
    };
  }, [importableUrl, refsPromise.status, defaultBranch, selectedBranch, potentialSelectedBranch]);

  return { importableUrl: enhancedImportableUrl, defaultBranch, refsPromise, selectedBranch, defaultRef };
}

export function useGitRefs(url?: URL) {
  const workspaces = useWorkspaces();
  const settings = useSettings();
  const [refsPromise, setRefsPromise] = usePromiseState<GitServerRef[]>();

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!url) {
          setRefsPromise({ error: "Can't determine git refs without URL." });
          return;
        }
        setRefsPromise({ loading: true });
        workspaces
          .getGitServerRefs({
            url: url.toString(),
            authInfo:
              settings.github.user && settings.github.token
                ? { username: settings.github.user.login, password: settings.github.token }
                : undefined,
          })
          .then((refs) => {
            if (canceled.get()) {
              return;
            }
            setRefsPromise({ data: refs });
          })
          .catch((e) => {
            if (canceled.get()) {
              return;
            }
            console.log(e);
            setRefsPromise({ error: e });
          });
      },
      [url, workspaces, setRefsPromise]
    )
  );

  const defaultRef = useMemo(
    () => refsPromise.data && refsPromise.data.filter((f) => f.ref === "HEAD").pop()?.target,
    [refsPromise]
  );

  return { refsPromise, defaultBranch: defaultRef?.replace("refs/heads/", ""), defaultRef };
}
