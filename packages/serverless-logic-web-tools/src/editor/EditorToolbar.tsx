/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import { EmbeddedEditorRef, useDirtyState } from "@kie-tools-core/editor/dist/embedded";
import type { RestEndpointMethodTypes as OctokitRestEndpointMethodTypes } from "@octokit/plugin-rest-endpoint-methods/dist-types/generated/parameters-and-response-types";
import { Alert, AlertActionCloseButton, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import {
  Dropdown,
  DropdownGroup,
  DropdownItem,
  DropdownPosition,
  DropdownToggle,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import { PageHeaderToolsItem, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import {
  Toolbar,
  ToolbarContent,
  ToolbarItem,
  ToolbarItemProps,
} from "@patternfly/react-core/dist/js/components/Toolbar";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { AngleLeftIcon } from "@patternfly/react-icons/dist/js/icons/angle-left-icon";
import { ArrowCircleUpIcon } from "@patternfly/react-icons/dist/js/icons/arrow-circle-up-icon";
import { CaretDownIcon } from "@patternfly/react-icons/dist/js/icons/caret-down-icon";
import { OutlinedHddIcon } from "@patternfly/react-icons/dist/js/icons/outlined-hdd-icon";
import { DesktopIcon } from "@patternfly/react-icons/dist/js/icons/desktop-icon";
import { DownloadIcon } from "@patternfly/react-icons/dist/js/icons/download-icon";
import { EllipsisVIcon } from "@patternfly/react-icons/dist/js/icons/ellipsis-v-icon";
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import { FolderIcon } from "@patternfly/react-icons/dist/js/icons/folder-icon";
import { GithubIcon } from "@patternfly/react-icons/dist/js/icons/github-icon";
import { ImageIcon } from "@patternfly/react-icons/dist/js/icons/image-icon";
import { PlusIcon } from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { SaveIcon } from "@patternfly/react-icons/dist/js/icons/save-icon";
import { SyncAltIcon } from "@patternfly/react-icons/dist/js/icons/sync-alt-icon";
import { OutlinedClockIcon } from "@patternfly/react-icons/dist/js/icons/outlined-clock-icon";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons/trash-icon";
import { Location } from "history";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useHistory } from "react-router";
import { isOfKind } from "@kie-tools-core/workspaces-git-fs/dist/constants/ExtensionHelper";
import { useAppI18n } from "../i18n";
import {
  useNavigationBlockersBypass,
  useNavigationStatus,
  useNavigationStatusToggle,
  useRoutes,
} from "../navigation/Hooks";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { useGitHubAuthInfo } from "../settings/github/Hooks";
import { AuthStatus, GithubScopes, useSettings, useSettingsDispatch } from "../settings/SettingsContext";
import { FileLabel } from "../workspace/components/FileLabel";
import { WorkspaceLabel } from "../workspace/components/WorkspaceLabel";
import { PromiseStateWrapper } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useWorkspacePromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceHooks";
import {
  GIST_DEFAULT_BRANCH,
  GIST_ORIGIN_REMOTE_NAME,
  GIT_ORIGIN_REMOTE_NAME,
} from "@kie-tools-core/workspaces-git-fs/dist/constants/GitConstants";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { CreateGitHubRepositoryModal } from "./CreateGitHubRepositoryModal";
import { FileSwitcher } from "./FileSwitcher";
import { ExtendedServicesButtons } from "./ExtendedServices/ExtendedServicesButtons";
import { ExtendedServicesDropdownGroup } from "./ExtendedServices/ExtendedServicesDropdownGroup";
import { NewFileDropdownMenu } from "./NewFileDropdownMenu";
import { ConfirmDeployModal } from "./Deploy/ConfirmDeployModal";
import { useSharedValue } from "@kie-tools-core/envelope-bus/dist/hooks";
import { WorkspaceStatusIndicator } from "../workspace/components/WorkspaceStatusIndicator";
import { WorkspaceKind } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceOrigin";
import { useEditorEnvelopeLocator } from "../envelopeLocator/EditorEnvelopeLocatorContext";
import { UrlType, useImportableUrl } from "../workspace/hooks/ImportableUrlHooks";
import { useEnv } from "../env/EnvContext";
import { useGlobalAlert, useGlobalAlertsDispatchContext } from "../alerts/GlobalAlertsContext";
import { Link } from "react-router-dom";
import { routes } from "../navigation/Routes";

export interface Props {
  editor: EmbeddedEditorRef | undefined;
  workspaceFile: WorkspaceFile;
}

const showWhenSmall: ToolbarItemProps["visibility"] = {
  default: "visible",
  "2xl": "hidden",
  xl: "hidden",
  lg: "visible",
  md: "visible",
};

const hideWhenSmall: ToolbarItemProps["visibility"] = {
  default: "hidden",
  "2xl": "visible",
  xl: "visible",
  lg: "hidden",
  md: "hidden",
};

const hideWhenTiny: ToolbarItemProps["visibility"] = {
  default: "hidden",
  "2xl": "visible",
  xl: "visible",
  lg: "visible",
  md: "hidden",
};

export function EditorToolbar(props: Props) {
  const { env } = useEnv();
  const routes = useRoutes();
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const history = useHistory();
  const workspaces = useWorkspaces();
  const [isShareDropdownOpen, setShareDropdownOpen] = useState(false);
  const [isSyncGitHubGistDropdownOpen, setSyncGitHubGistDropdownOpen] = useState(false);
  const [isSyncGitRepositoryDropdownOpen, setSyncGitRepositoryDropdownOpen] = useState(false);
  const [isLargeKebabOpen, setLargeKebabOpen] = useState(false);
  const [isSmallKebabOpen, setSmallKebabOpen] = useState(false);
  const { i18n } = useAppI18n();
  const isEdited = useDirtyState(props.editor);
  const downloadRef = useRef<HTMLAnchorElement>(null);
  const downloadAllRef = useRef<HTMLAnchorElement>(null);
  const downloadPreviewRef = useRef<HTMLAnchorElement>(null);
  const copyContentTextArea = useRef<HTMLTextAreaElement>(null);
  const [isNewFileDropdownMenuOpen, setNewFileDropdownMenuOpen] = useState(false);
  const workspacePromise = useWorkspacePromise(props.workspaceFile.workspaceId);
  const [isGitHubGistLoading, setGitHubGistLoading] = useState(false);
  const editorEnvelopeLocator = useEditorEnvelopeLocator();
  const alertsDispatch = useGlobalAlertsDispatchContext();
  const [gitHubGist, setGitHubGist] =
    useState<OctokitRestEndpointMethodTypes["gists"]["get"]["response"]["data"] | undefined>(undefined);
  const workspaceImportableUrl = useImportableUrl({
    isFileSupported: (path: string) => editorEnvelopeLocator.hasMappingFor(path),
    urlString: workspacePromise.data?.descriptor.origin.url?.toString(),
  });

  const githubAuthInfo = useGitHubAuthInfo();
  const canPushToGitRepository = useMemo(() => !!githubAuthInfo, [githubAuthInfo]);
  const navigationBlockersBypass = useNavigationBlockersBypass();

  const [flushes] = useSharedValue(
    workspaces.workspacesSharedWorker.workspacesWorkerBus.clientApi.shared.kieSandboxWorkspacesStorage_flushes
  );

  const isSaved = useMemo(() => {
    return !isEdited && flushes && !flushes.some((f) => f.includes(props.workspaceFile.workspaceId));
  }, [isEdited, flushes, props.workspaceFile.workspaceId]);

  // Prevent from closing without flushing before.
  useEffect(() => {
    if (isSaved) {
      return;
    }

    window.onbeforeunload = () => "Some changes are not written to disk yet.";
    return () => {
      window.onbeforeunload = null;
    };
  }, [isSaved]);

  const canBeDeployed = useMemo(
    () => isOfKind("sw", props.workspaceFile.relativePath) || isOfKind("dash", props.workspaceFile.relativePath),
    [props.workspaceFile.relativePath]
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (gitHubGist || workspaceImportableUrl.type !== UrlType.GIST) {
          return;
        }

        const { gistId } = workspaceImportableUrl;

        if (!gistId) {
          return;
        }

        settingsDispatch.github.octokit.gists.get({ gist_id: gistId }).then(({ data: gist }) => {
          if (canceled.get()) {
            return;
          }

          if (gist) {
            setGitHubGist(gist);
          }
        });
      },
      [gitHubGist, workspaceImportableUrl, settingsDispatch.github.octokit.gists]
    )
  );

  const successfullyCreateGistAlert = useGlobalAlert(
    useCallback(
      ({ close }) => {
        if (workspacePromise.data?.descriptor.origin.kind !== WorkspaceKind.GITHUB_GIST) {
          return <></>;
        }

        const gistUrl = workspacePromise.data?.descriptor.origin.url.toString();
        return (
          <Alert
            variant="success"
            title={i18n.editorPage.alerts.createGist}
            actionClose={<AlertActionCloseButton onClose={close} />}
            actionLinks={<AlertActionLink onClick={() => window.open(gistUrl, "_blank")}>{gistUrl}</AlertActionLink>}
          />
        );
      },
      [i18n, workspacePromise]
    ),
    { durationInSeconds: 4 }
  );

  const loadingGistAlert = useGlobalAlert(
    useCallback(
      ({ close }) => {
        if (workspacePromise.data?.descriptor.origin.kind !== WorkspaceKind.GITHUB_GIST) {
          return <></>;
        }

        const gistUrl = workspacePromise.data?.descriptor.origin.url.toString();
        return (
          <Alert
            variant="info"
            title={
              <>
                <Spinner size={"sm"} />
                &nbsp;&nbsp; Updating gist...
              </>
            }
            actionClose={<AlertActionCloseButton onClose={close} />}
            actionLinks={<AlertActionLink onClick={() => window.open(gistUrl, "_blank")}>{gistUrl}</AlertActionLink>}
          />
        );
      },
      [workspacePromise]
    )
  );

  useEffect(() => {
    if (isGitHubGistLoading) {
      loadingGistAlert.show();
    } else {
      loadingGistAlert.close();
    }
  }, [isGitHubGistLoading, loadingGistAlert]);

  const successfullyUpdateGistAlert = useGlobalAlert(
    useCallback(
      ({ close }) => {
        if (workspacePromise.data?.descriptor.origin.kind !== WorkspaceKind.GITHUB_GIST) {
          return <></>;
        }

        const gistUrl = workspacePromise.data?.descriptor.origin.url.toString();
        return (
          <Alert
            variant="success"
            title={i18n.editorPage.alerts.updateGist}
            actionClose={<AlertActionCloseButton onClose={close} />}
            actionLinks={<AlertActionLink onClick={() => window.open(gistUrl, "_blank")}>{gistUrl}</AlertActionLink>}
          />
        );
      },
      [i18n, workspacePromise]
    ),
    { durationInSeconds: 4 }
  );

  const errorAlert = useGlobalAlert(
    useCallback(
      ({ close }) => (
        <Alert
          variant="danger"
          title={i18n.editorPage.alerts.error}
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      ),
      [i18n]
    )
  );

  const shouldIncludeDownloadSvgDropdownItem = useMemo(() => {
    return isOfKind("sw", props.workspaceFile.relativePath);
  }, [props.workspaceFile]);

  const onDownload = useCallback(() => {
    props.editor?.getStateControl().setSavedCommand();
    alertsDispatch.closeAll();
    props.workspaceFile.getFileContents().then((content) => {
      if (downloadRef.current) {
        const fileBlob = new Blob([content], { type: "text/plain" });
        downloadRef.current.href = URL.createObjectURL(fileBlob);
        downloadRef.current.click();
      }
    });
  }, [props.editor, props.workspaceFile, alertsDispatch]);

  const downloadWorkspaceZip = useCallback(async () => {
    if (!props.editor) {
      return;
    }

    const zipBlob = await workspaces.prepareZip({ workspaceId: props.workspaceFile.workspaceId });
    if (downloadAllRef.current) {
      downloadAllRef.current.href = URL.createObjectURL(zipBlob);
      downloadAllRef.current.click();
    }
    if (workspacePromise.data?.descriptor.origin.kind === WorkspaceKind.LOCAL) {
      await workspaces.createSavePoint({ workspaceId: props.workspaceFile.workspaceId, gitConfig: githubAuthInfo });
    }
  }, [props.editor, props.workspaceFile, workspaces, workspacePromise.data, githubAuthInfo]);

  const downloadSvg = useCallback(() => {
    props.editor?.getPreview().then((previewSvg) => {
      if (downloadPreviewRef.current && previewSvg) {
        const fileBlob = new Blob([previewSvg], { type: "image/svg+xml" });
        downloadPreviewRef.current.href = URL.createObjectURL(fileBlob);
        downloadPreviewRef.current.click();
      }
    });
  }, [props.editor]);

  const forceUpdateGitHubGist = useCallback(async () => {
    try {
      if (!githubAuthInfo) {
        return;
      }

      setGitHubGistLoading(true);

      await workspaces.push({
        workspaceId: props.workspaceFile.workspaceId,
        remote: GIST_ORIGIN_REMOTE_NAME,
        ref: GIST_DEFAULT_BRANCH,
        remoteRef: `refs/heads/${GIST_DEFAULT_BRANCH}`,
        force: true,
        authInfo: githubAuthInfo,
      });

      await workspaces.pull({
        workspaceId: props.workspaceFile.workspaceId,
        authInfo: githubAuthInfo,
      });
    } catch (e) {
      errorAlert.show();
    } finally {
      setGitHubGistLoading(false);
      setSyncGitHubGistDropdownOpen(false);
    }

    successfullyUpdateGistAlert.show();
  }, [workspaces, props.workspaceFile.workspaceId, githubAuthInfo, successfullyUpdateGistAlert, errorAlert]);

  const errorPushingGist = useGlobalAlert(
    useCallback(
      ({ close }) => (
        <Alert
          variant="danger"
          title={i18n.editorPage.alerts.errorPushingGist}
          actionLinks={[
            <AlertActionLink
              key="force"
              onClick={() => {
                close();
                forceUpdateGitHubGist();
              }}
            >
              Force push
            </AlertActionLink>,
            <AlertActionLink key="dismiss" onClick={close}>
              Dismiss
            </AlertActionLink>,
          ]}
          actionClose={<AlertActionCloseButton onClose={close} />}
        >
          <b>{i18n.editorPage.alerts.forcePushWarning}</b>
        </Alert>
      ),
      [i18n, forceUpdateGitHubGist]
    )
  );

  const updateGitHubGist = useCallback(async () => {
    try {
      if (!githubAuthInfo) {
        return;
      }

      setGitHubGistLoading(true);

      await workspaces.createSavePoint({
        workspaceId: props.workspaceFile.workspaceId,
        gitConfig: githubAuthInfo,
      });

      await workspaces.push({
        workspaceId: props.workspaceFile.workspaceId,
        remote: GIST_ORIGIN_REMOTE_NAME,
        ref: GIST_DEFAULT_BRANCH,
        remoteRef: `refs/heads/${GIST_DEFAULT_BRANCH}`,
        force: false,
        authInfo: githubAuthInfo,
      });

      await workspaces.pull({
        workspaceId: props.workspaceFile.workspaceId,
        authInfo: githubAuthInfo,
      });
    } catch (e) {
      errorPushingGist.show();
      throw e;
    } finally {
      setGitHubGistLoading(false);
      setSyncGitHubGistDropdownOpen(false);
    }

    successfullyUpdateGistAlert.show();
  }, [successfullyUpdateGistAlert, githubAuthInfo, workspaces, props.workspaceFile.workspaceId, errorPushingGist]);

  const createGitHubGist = useCallback(async () => {
    try {
      if (!githubAuthInfo) {
        return;
      }
      setGitHubGistLoading(true);
      const gist = await settingsDispatch.github.octokit.gists.create({
        description: workspacePromise.data?.descriptor.name ?? "",
        public: true,

        // This file is used just for creating the Gist. The `push -f` overwrites it.
        files: {
          "README.md": {
            content: `
This Gist was created from Serverless Logic Web Tools.

This file is temporary and you should not be seeing it.
If you are, it means that creating this Gist failed and it can safely be deleted.
`,
          },
        },
      });

      if (!gist.data.git_push_url) {
        throw new Error("Gist creation failed.");
      }

      await workspaces.initGistOnWorkspace({
        workspaceId: props.workspaceFile.workspaceId,
        remoteUrl: new URL(gist.data.git_push_url),
        branch: GIST_DEFAULT_BRANCH,
      });

      await workspaces.addRemote({
        workspaceId: props.workspaceFile.workspaceId,
        url: gist.data.git_push_url,
        name: GIST_ORIGIN_REMOTE_NAME,
        force: true,
      });

      await workspaces.branch({
        workspaceId: props.workspaceFile.workspaceId,
        checkout: true,
        name: GIST_DEFAULT_BRANCH,
      });

      await workspaces.createSavePoint({
        workspaceId: props.workspaceFile.workspaceId,
        gitConfig: githubAuthInfo,
      });

      await workspaces.push({
        workspaceId: props.workspaceFile.workspaceId,
        remote: GIST_ORIGIN_REMOTE_NAME,
        ref: GIST_DEFAULT_BRANCH,
        remoteRef: `refs/heads/${GIST_DEFAULT_BRANCH}`,
        force: true,
        authInfo: githubAuthInfo,
      });

      await workspaces.pull({
        workspaceId: props.workspaceFile.workspaceId,
        authInfo: githubAuthInfo,
      });

      successfullyCreateGistAlert.show();

      return;
    } catch (err) {
      errorAlert.show();
      throw err;
    } finally {
      setGitHubGistLoading(false);
    }
  }, [
    settingsDispatch.github.octokit,
    workspacePromise,
    workspaces,
    props.workspaceFile.workspaceId,
    githubAuthInfo,
    successfullyCreateGistAlert,
    errorAlert,
  ]);

  const forkGitHubGist = useCallback(async () => {
    try {
      if (!githubAuthInfo || !gitHubGist?.id) {
        return;
      }
      setGitHubGistLoading(true);

      // Fork Gist
      const gist = await settingsDispatch.github.octokit.gists.fork({
        gist_id: gitHubGist.id,
      });

      const remoteName = gist.data.id;

      // Adds forked gist remote to current one
      await workspaces.addRemote({
        workspaceId: props.workspaceFile.workspaceId,
        url: gist.data.git_push_url,
        name: remoteName,
        force: true,
      });

      // Commit
      await workspaces.createSavePoint({
        workspaceId: props.workspaceFile.workspaceId,
        gitConfig: githubAuthInfo,
      });

      // Push to forked gist remote
      await workspaces.push({
        workspaceId: props.workspaceFile.workspaceId,
        remote: remoteName,
        ref: GIST_DEFAULT_BRANCH,
        remoteRef: `refs/heads/${GIST_DEFAULT_BRANCH}`,
        force: true,
        authInfo: githubAuthInfo,
      });

      // Redirect to import workspace
      navigationBlockersBypass.execute(() => {
        history.push({
          pathname: routes.importModel.path({}),
          search: routes.importModel.queryString({ url: gist.data.html_url }),
        });
      });
    } catch (err) {
      errorAlert.show();
      throw err;
    } finally {
      setGitHubGistLoading(false);
    }
  }, [
    githubAuthInfo,
    gitHubGist,
    settingsDispatch.github.octokit.gists,
    workspaces,
    props.workspaceFile.workspaceId,
    navigationBlockersBypass,
    history,
    routes.importModel,
    errorAlert,
  ]);

  const workspaceHasNestedDirectories = useMemo(
    () => workspacePromise.data?.files.filter((f) => f.relativePath !== f.name).length !== 0,
    [workspacePromise]
  );

  const isGitHubGistOwner = useMemo(() => {
    return githubAuthInfo?.username && gitHubGist?.owner?.login === githubAuthInfo.username;
  }, [githubAuthInfo, gitHubGist]);

  const canCreateGitRepository = useMemo(
    () =>
      settings.github.authStatus === AuthStatus.SIGNED_IN &&
      settings.github.scopes?.includes(GithubScopes.REPO) &&
      workspacePromise.data?.descriptor.origin.kind === WorkspaceKind.LOCAL,
    [workspacePromise, settings.github.authStatus, settings.github.scopes]
  );

  const canCreateGitHubGist = useMemo(
    () =>
      settings.github.authStatus === AuthStatus.SIGNED_IN &&
      settings.github.scopes?.includes(GithubScopes.GIST) &&
      workspacePromise.data?.descriptor.origin.kind === WorkspaceKind.LOCAL &&
      !workspaceHasNestedDirectories,
    [workspacePromise, settings.github.authStatus, settings.github.scopes, workspaceHasNestedDirectories]
  );

  const canUpdateGitHubGist = useMemo(
    () =>
      settings.github.authStatus === AuthStatus.SIGNED_IN &&
      settings.github.scopes?.includes(GithubScopes.GIST) &&
      !!isGitHubGistOwner &&
      workspacePromise.data?.descriptor.origin.kind === WorkspaceKind.GITHUB_GIST &&
      !workspaceHasNestedDirectories,
    [
      workspacePromise,
      settings.github.authStatus,
      settings.github.scopes,
      workspaceHasNestedDirectories,
      isGitHubGistOwner,
    ]
  );

  const canForkGitHubGist = useMemo(
    () =>
      settings.github.authStatus === AuthStatus.SIGNED_IN &&
      settings.github.scopes?.includes(GithubScopes.GIST) &&
      !isGitHubGistOwner &&
      workspacePromise.data?.descriptor.origin.kind === WorkspaceKind.GITHUB_GIST &&
      !workspaceHasNestedDirectories,
    [
      workspacePromise,
      settings.github.authStatus,
      settings.github.scopes,
      workspaceHasNestedDirectories,
      isGitHubGistOwner,
    ]
  );

  const [isCreateGitHubRepositoryModalOpen, setCreateGitHubRepositoryModalOpen] = useState(false);

  const shareDropdownItems = useMemo(
    () => [
      <DropdownGroup key={"download-group"} label="Download">
        <DropdownItem
          onClick={onDownload}
          key={"download-file-item"}
          description={`${props.workspaceFile.name} will be downloaded`}
          icon={<DownloadIcon />}
          ouiaId="download-file-dropdown-button"
        >
          Current file
        </DropdownItem>
        {shouldIncludeDownloadSvgDropdownItem && (
          <DropdownItem
            key={`dropdown-download-svg`}
            data-testid="dropdown-download-svg"
            component="button"
            onClick={downloadSvg}
            description={`Image of ${props.workspaceFile.name} will be downloaded in SVG format`}
            icon={<ImageIcon />}
          >
            {"Current file's SVG"}
          </DropdownItem>
        )}
        <DropdownItem
          onClick={downloadWorkspaceZip}
          key={"download-zip-item"}
          description={`A zip file including all files will be downloaded`}
          icon={<FolderIcon />}
        >
          All files
        </DropdownItem>
      </DropdownGroup>,
      ...(workspacePromise.data?.descriptor.origin.kind === WorkspaceKind.LOCAL ||
      workspacePromise.data?.descriptor.origin.kind === WorkspaceKind.GITHUB_GIST
        ? [
            <DropdownGroup key={"github-group"} label={i18n.names.github}>
              <Tooltip
                data-testid={"create-github-repository-tooltip"}
                key={`dropdown-create-github-repository`}
                content={<div>{`You can't create a repository because you're not authenticated with GitHub.`}</div>}
                trigger={!canCreateGitRepository ? "mouseenter click" : ""}
                position="left"
              >
                <DropdownItem
                  icon={<GithubIcon />}
                  data-testid={"create-github-repository-button"}
                  component="button"
                  onClick={() => setCreateGitHubRepositoryModalOpen(true)}
                  isDisabled={!canCreateGitRepository}
                >
                  Create Repository...
                </DropdownItem>
              </Tooltip>
              {/* TODO: Uncomment when this works again
              <Tooltip
                data-testid={"create-github-gist-tooltip"}
                key={`dropdown-create-github-gist`}
                content={<div>{i18n.editorToolbar.cantCreateGistTooltip}</div>}
                trigger={!canCreateGitHubGist ? "mouseenter click" : ""}
                position="left"
              >
                <DropdownItem
                  icon={<GithubIcon />}
                  data-testid={"create-github-gist-button"}
                  component="button"
                  onClick={createGitHubGist}
                  isDisabled={!canCreateGitHubGist}
                >
                  {i18n.editorToolbar.createGist}
                </DropdownItem>
              </Tooltip> */}
              {!canPushToGitRepository && (
                <>
                  <Divider />
                  <DropdownItem>
                    <Link to={routes.settings.github.path({})}>
                      <Button isInline={true} variant={ButtonVariant.link}>
                        Configure GitHub token...
                      </Button>
                    </Link>
                  </DropdownItem>
                </>
              )}
            </DropdownGroup>,
          ]
        : []),
    ],
    [
      canPushToGitRepository,
      onDownload,
      workspacePromise,
      props.workspaceFile,
      shouldIncludeDownloadSvgDropdownItem,
      downloadSvg,
      downloadWorkspaceZip,
      i18n,
      canCreateGitRepository,
      routes,
    ]
  );

  useEffect(() => {
    if (!workspacePromise.data) {
      return;
    }
    if (downloadRef.current) {
      downloadRef.current.download = `${props.workspaceFile.name}`;
    }
    if (downloadAllRef.current) {
      downloadAllRef.current.download = `${workspacePromise.data.descriptor.name}.zip`;
    }
    if (downloadPreviewRef.current) {
      downloadPreviewRef.current.download = `${props.workspaceFile.name}.svg`;
    }
  }, [props.workspaceFile, workspacePromise.data]);

  const deleteWorkspaceFile = useCallback(async () => {
    if (!workspacePromise.data) {
      return;
    }

    if (workspacePromise.data.files.length === 1) {
      await workspaces.deleteWorkspace({ workspaceId: props.workspaceFile.workspaceId });
      history.push({ pathname: routes.home.path({}) });
      return;
    }

    const nextFile = workspacePromise.data.files
      .filter((f) => {
        return f.relativePath !== props.workspaceFile.relativePath;
      })
      .pop();

    await workspaces.deleteFile({
      file: props.workspaceFile,
    });

    if (!nextFile) {
      history.push({ pathname: routes.home.path({}) });
      return;
    }

    history.push({
      pathname: routes.workspaceWithFilePath.path({
        workspaceId: nextFile.workspaceId,
        fileRelativePath: nextFile.relativePathWithoutExtension,
        extension: nextFile.extension,
      }),
    });
  }, [routes, history, workspacePromise.data, props.workspaceFile, workspaces]);

  const workspaceNameRef = useRef<HTMLInputElement>(null);

  const resetWorkspaceName = useCallback(() => {
    if (workspaceNameRef.current && workspacePromise.data) {
      workspaceNameRef.current.value = workspacePromise.data.descriptor.name;
    }
  }, [workspacePromise.data]);

  useEffect(resetWorkspaceName, [resetWorkspaceName]);

  const onRenameWorkspace = useCallback(
    async (newName: string | undefined) => {
      if (!newName) {
        resetWorkspaceName();
        return;
      }

      if (!workspacePromise.data || newName === workspacePromise.data.descriptor.name) {
        return;
      }

      await workspaces.renameWorkspace({
        workspaceId: workspacePromise.data.descriptor.workspaceId,
        newName: newName.trim(),
      });
    },
    [workspacePromise.data, workspaces, resetWorkspaceName]
  );

  const onWorkspaceNameKeyDown = useCallback(
    (e: React.KeyboardEvent<HTMLInputElement>) => {
      e.stopPropagation();
      if (e.keyCode === 13 /* Enter */) {
        e.currentTarget.blur();
      } else if (e.keyCode === 27 /* ESC */) {
        resetWorkspaceName();
        e.currentTarget.blur();
      }
    },
    [resetWorkspaceName]
  );

  const deleteFileDropdownItem = useMemo(() => {
    return (
      <DropdownItem key={"delete-dropdown-item"} onClick={deleteWorkspaceFile} ouiaId={"delete-file-button"}>
        <Flex flexWrap={{ default: "nowrap" }}>
          <FlexItem>
            <TrashIcon />
            &nbsp;&nbsp;Delete <b>{`"${props.workspaceFile.nameWithoutExtension}"`}</b>
          </FlexItem>
          <FlexItem>
            <b>
              <FileLabel extension={props.workspaceFile.extension} />
            </b>
          </FlexItem>
        </Flex>
      </DropdownItem>
    );
  }, [deleteWorkspaceFile, props.workspaceFile]);

  const pushingAlert = useGlobalAlert(
    useCallback(
      ({ close }) => {
        if (workspacePromise.data?.descriptor.origin.kind !== WorkspaceKind.GIT) {
          return <></>;
        }

        return (
          <Alert
            variant="info"
            title={
              <>
                <Spinner size={"sm"} />
                &nbsp;&nbsp; {`Pushing to '${workspacePromise.data?.descriptor.origin.url}'...`}
              </>
            }
          />
        );
      },
      [workspacePromise]
    )
  );

  const comittingAlert = useGlobalAlert(
    useCallback(({ close }) => {
      return (
        <Alert
          variant="info"
          title={
            <>
              <Spinner size={"sm"} />
              &nbsp;&nbsp; {`Creating commit...`}
            </>
          }
        />
      );
    }, [])
  );

  const commitSuccessAlert = useGlobalAlert(
    useCallback(({ close }) => {
      return <Alert variant="success" title={`Commit created.`} ouiaId={"commit-created-alert"} />;
    }, []),
    { durationInSeconds: 2 }
  );

  const createSavePointDropdownItem = useMemo(() => {
    return (
      <DropdownItem
        key={"commit-dropdown-item"}
        icon={<SaveIcon />}
        onClick={async () => {
          comittingAlert.show();
          await workspaces.createSavePoint({
            workspaceId: props.workspaceFile.workspaceId,
            gitConfig: githubAuthInfo,
          });
          comittingAlert.close();
          commitSuccessAlert.show();
        }}
        description={"Create a save point"}
        ouiaId={"commit-button"}
      >
        Commit
      </DropdownItem>
    );
  }, [workspaces, props.workspaceFile, githubAuthInfo, comittingAlert, commitSuccessAlert]);

  const pushSuccessAlert = useGlobalAlert(
    useCallback(
      ({ close }) => {
        if (workspacePromise.data?.descriptor.origin.kind !== WorkspaceKind.GIT) {
          return <></>;
        }

        return <Alert variant="success" title={`Pushed to '${workspacePromise.data?.descriptor.origin.url}'`} />;
      },
      [workspacePromise]
    ),
    { durationInSeconds: 4 }
  );

  const pushErrorAlert = useGlobalAlert(
    useCallback(
      ({ close }) => {
        if (workspacePromise.data?.descriptor.origin.kind !== WorkspaceKind.GIT) {
          return <></>;
        }

        return (
          <Alert
            variant="danger"
            title={`Error pushing to '${workspacePromise.data?.descriptor.origin.url}'`}
            actionClose={<AlertActionCloseButton onClose={close} />}
          />
        );
      },
      [workspacePromise]
    )
  );

  const pullingAlert = useGlobalAlert(
    useCallback(
      ({ close }) => {
        if (workspacePromise.data?.descriptor.origin.kind !== WorkspaceKind.GIT) {
          return <></>;
        }

        return (
          <Alert
            variant="info"
            title={
              <>
                <Spinner size={"sm"} />
                &nbsp;&nbsp; {`Pulling from '${workspacePromise.data?.descriptor.origin.url}'...`}
              </>
            }
          />
        );
      },
      [workspacePromise]
    )
  );

  const pullSuccessAlert = useGlobalAlert(
    useCallback(
      ({ close }) => {
        if (workspacePromise.data?.descriptor.origin.kind !== WorkspaceKind.GIT) {
          return <></>;
        }

        return <Alert variant="success" title={`Pulled from '${workspacePromise.data?.descriptor.origin.url}'`} />;
      },
      [workspacePromise]
    ),
    { durationInSeconds: 4 }
  );

  const pushNewBranch = useCallback(
    async (newBranchName: string) => {
      if (!githubAuthInfo || !workspacePromise.data) {
        return;
      }

      try {
        pushingAlert.show();

        await workspaces.createSavePoint({
          workspaceId: props.workspaceFile.workspaceId,
          gitConfig: githubAuthInfo,
        });

        await workspaces.branch({
          workspaceId: props.workspaceFile.workspaceId,
          checkout: false,
          name: newBranchName,
        });

        await workspaces.push({
          workspaceId: props.workspaceFile.workspaceId,
          remote: GIT_ORIGIN_REMOTE_NAME,
          remoteRef: `refs/heads/${newBranchName}`,
          ref: newBranchName,
          force: false,
          authInfo: githubAuthInfo,
        });

        history.push({
          pathname: routes.importModel.path({}),
          search: routes.importModel.queryString({
            url: `${workspacePromise.data.descriptor.origin.url}`,
            branch: newBranchName,
          }),
        });
      } finally {
        pushingAlert.close();
      }
    },
    [githubAuthInfo, routes, history, props.workspaceFile.workspaceId, workspacePromise, workspaces, pushingAlert]
  );

  const pullErrorAlert = useGlobalAlert<{ newBranchName: string }>(
    useCallback(
      ({ close }, { newBranchName }) => {
        if (workspacePromise.data?.descriptor.origin.kind !== WorkspaceKind.GIT) {
          return <></>;
        }

        return (
          <Alert
            variant="danger"
            title={`Error pulling from '${workspacePromise.data?.descriptor.origin.url}'`}
            actionClose={<AlertActionCloseButton onClose={close} />}
            actionLinks={
              <>
                {canPushToGitRepository && (
                  <AlertActionLink onClick={() => pushNewBranch(newBranchName)}>
                    {`Switch to '${newBranchName}'`}
                  </AlertActionLink>
                )}

                {!canPushToGitRepository && (
                  <AlertActionLink onClick={() => history.push(routes.settings.github.path({}))}>
                    {`Configure GitHub token...`}
                  </AlertActionLink>
                )}
              </>
            }
          >
            This usually happens when your branch has conflicts with the upstream branch.
            <br />
            <br />
            {canPushToGitRepository && `You can still save your work to a new branch.`}
            {!canPushToGitRepository &&
              `To be able to save your work on a new branch, please authenticate with GitHub.`}
          </Alert>
        );
      },
      [canPushToGitRepository, pushNewBranch, workspacePromise, history, routes]
    )
  );

  const pullFromGitRepository = useCallback(
    async (args: { showAlerts: boolean }) => {
      pullingAlert.close();
      pullErrorAlert.close();
      pullSuccessAlert.close();

      if (args.showAlerts) {
        pullingAlert.show();
      }
      await workspaces.createSavePoint({
        workspaceId: props.workspaceFile.workspaceId,
        gitConfig: githubAuthInfo,
      });

      try {
        await workspaces.pull({
          workspaceId: props.workspaceFile.workspaceId,
          authInfo: githubAuthInfo,
        });

        if (args.showAlerts) {
          pullSuccessAlert.show();
        }
      } catch (e) {
        console.error(e);
        if (args.showAlerts) {
          const randomString = (Math.random() + 1).toString(36).substring(7);
          const newBranchName = `${workspacePromise.data?.descriptor.origin.branch}-${randomString}`;
          pullErrorAlert.show({ newBranchName });
        }
      } finally {
        if (args.showAlerts) {
          pullingAlert.close();
        }
      }
    },
    [
      pullingAlert,
      pullErrorAlert,
      pullSuccessAlert,
      workspaces,
      props.workspaceFile.workspaceId,
      githubAuthInfo,
      workspacePromise,
    ]
  );

  const pushToGitRepository = useCallback(async () => {
    pushingAlert.close();
    pushErrorAlert.close();
    pushSuccessAlert.close();

    if (!githubAuthInfo) {
      return;
    }

    pushingAlert.show();
    try {
      const workspaceId = props.workspaceFile.workspaceId;
      await workspaces.createSavePoint({
        workspaceId: workspaceId,
        gitConfig: githubAuthInfo,
      });

      const workspace = await workspaces.getWorkspace({ workspaceId });
      await workspaces.push({
        workspaceId: props.workspaceFile.workspaceId,
        ref: workspace.origin.branch,
        remote: GIST_ORIGIN_REMOTE_NAME,
        remoteRef: `refs/heads/${workspace.origin.branch}`,
        force: false,
        authInfo: githubAuthInfo,
      });
      await pullFromGitRepository({ showAlerts: false });
      pushSuccessAlert.show();
    } catch (e) {
      console.error(e);
      pushErrorAlert.show();
    } finally {
      pushingAlert.close();
    }
  }, [
    pullFromGitRepository,
    githubAuthInfo,
    props.workspaceFile,
    pushErrorAlert,
    pushSuccessAlert,
    pushingAlert,
    workspaces,
  ]);

  const isGistWorkspace = useMemo(
    () => workspacePromise.data?.descriptor.origin.kind === WorkspaceKind.GITHUB_GIST,
    [workspacePromise.data?.descriptor.origin.kind]
  );
  const navigationStatus = useNavigationStatus();
  const navigationStatusToggle = useNavigationStatusToggle();
  const confirmNavigationAlert = useGlobalAlert<{ lastBlockedLocation: Location }>(
    useCallback(
      (_, { lastBlockedLocation }) => (
        <Alert
          data-testid="unsaved-alert"
          variant="warning"
          title={
            workspacePromise.data?.descriptor.origin.kind === WorkspaceKind.LOCAL
              ? i18n.editorPage.alerts.unsaved.titleLocal
              : i18n.editorPage.alerts.unsaved.titleGit
          }
          actionClose={
            <AlertActionCloseButton data-testid="unsaved-alert-close-button" onClose={navigationStatusToggle.unblock} />
          }
          actionLinks={
            <>
              <Divider inset={{ default: "insetMd" }} />
              <br />
              {(workspacePromise.data?.descriptor.origin.kind === WorkspaceKind.LOCAL && (
                <AlertActionLink
                  data-testid="unsaved-alert-save-button"
                  onClick={() => {
                    navigationStatusToggle.unblock();
                    return downloadWorkspaceZip();
                  }}
                  style={{ fontWeight: "bold" }}
                >
                  {`${i18n.terms.download} '${workspacePromise.data?.descriptor.name}'`}
                </AlertActionLink>
              )) || (
                <PushToGitHubAlertActionLinks
                  canPush={isGistWorkspace ? canUpdateGitHubGist : canPushToGitRepository}
                  kind={workspacePromise.data?.descriptor.origin.kind}
                  remoteRef={`${GIT_ORIGIN_REMOTE_NAME}/${workspacePromise.data?.descriptor.origin.branch}`}
                  onPush={() => {
                    navigationStatusToggle.unblock();
                    return isGistWorkspace ? updateGitHubGist() : pushToGitRepository();
                  }}
                />
              )}
              <br />
              <br />
              <AlertActionLink
                data-testid="unsaved-alert-close-without-save-button"
                onClick={() =>
                  navigationBlockersBypass.execute(() => {
                    history.push(lastBlockedLocation);
                  })
                }
              >
                {i18n.editorPage.alerts.unsaved.proceedAnyway}
              </AlertActionLink>
              <br />
              <br />
            </>
          }
        >
          <br />
          <p>{i18n.editorPage.alerts.unsaved.message}</p>
        </Alert>
      ),
      [
        workspacePromise.data?.descriptor,
        i18n,
        navigationStatusToggle,
        isGistWorkspace,
        canUpdateGitHubGist,
        canPushToGitRepository,
        downloadWorkspaceZip,
        updateGitHubGist,
        pushToGitRepository,
        navigationBlockersBypass,
        history,
      ]
    )
  );

  useEffect(() => {
    if (navigationStatus.lastBlockedLocation) {
      confirmNavigationAlert.show({ lastBlockedLocation: navigationStatus.lastBlockedLocation });
    } else {
      confirmNavigationAlert.close();
    }
  }, [confirmNavigationAlert, navigationStatus]);

  const [isVsCodeDropdownOpen, setVsCodeDropdownOpen] = useState(false);

  const createRepositorySuccessAlert = useGlobalAlert<{ url: string }>(
    useCallback(({ close }, { url }) => {
      return (
        <Alert
          variant="success"
          title={`GitHub repository created.`}
          actionClose={<AlertActionCloseButton onClose={close} />}
          actionLinks={<AlertActionLink onClick={() => window.open(url, "_blank")}>{url}</AlertActionLink>}
        />
      );
    }, [])
  );

  const canSeeWorkspaceToolbar = useMemo(
    () =>
      workspacePromise.data?.descriptor.origin.kind !== WorkspaceKind.LOCAL || workspacePromise.data?.files.length > 1,
    [workspacePromise.data?.descriptor.origin.kind, workspacePromise.data?.files.length]
  );

  return (
    <PromiseStateWrapper
      promise={workspacePromise}
      resolved={(workspace) => (
        <>
          <PageSection type={"nav"} variant={"light"} padding={{ default: "noPadding" }}>
            {workspace && canSeeWorkspaceToolbar && (
              <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
                <FlexItem>
                  <Button
                    className={"kie-tools--masthead-hoverable"}
                    variant={ButtonVariant.plain}
                    onClick={() => history.push({ pathname: routes.workspaceWithFiles.path(props.workspaceFile) })}
                  >
                    <AngleLeftIcon />
                  </Button>
                  &nbsp;&nbsp;
                  <WorkspaceLabel descriptor={workspace.descriptor} />
                  &nbsp;&nbsp;
                  <div data-testid={"toolbar-title-workspace"} className={"kogito--editor__toolbar-name-container"}>
                    <Title
                      aria-label={"EmbeddedEditorFile name"}
                      headingLevel={"h3"}
                      size={"md"}
                      style={{ fontStyle: "italic" }}
                    >
                      {workspace.descriptor.name}
                    </Title>
                    <TextInput
                      ref={workspaceNameRef}
                      type={"text"}
                      aria-label={"Edit workspace name"}
                      onKeyDown={onWorkspaceNameKeyDown}
                      className={"kogito--editor__toolbar-subtitle"}
                      onBlur={(e) => onRenameWorkspace(e.target.value)}
                      style={{ fontStyle: "italic" }}
                      ouiaId={"directory-name-input"}
                    />
                  </div>
                  <WorkspaceStatusIndicator workspace={workspace} />
                </FlexItem>
                {/*<Divider inset={{ default: "insetMd" }} isVertical={true} />*/}
                {workspace.descriptor.origin.kind === WorkspaceKind.GIT &&
                  workspaceImportableUrl.type === UrlType.GITHUB && (
                    <FlexItem>
                      <Toolbar style={{ padding: 0 }}>
                        <ToolbarItem style={{ marginRight: 0 }}>
                          <Dropdown
                            isPlain={true}
                            onSelect={() => setVsCodeDropdownOpen(false)}
                            isOpen={isVsCodeDropdownOpen}
                            toggle={
                              <DropdownToggle toggleIndicator={null} onToggle={setVsCodeDropdownOpen}>
                                <img
                                  style={{ width: "14px" }}
                                  alt="vscode-logo-blue"
                                  src={routes.static.images.vscodeLogoBlue.path({})}
                                />
                                &nbsp; &nbsp;
                                {`Open "${workspace.descriptor.name}"`}
                                &nbsp; &nbsp;
                                <CaretDownIcon />
                              </DropdownToggle>
                            }
                            dropdownItems={[
                              <DropdownGroup key={"open-in-vscode"}>
                                {navigationStatus.shouldBlockNavigationTo({ pathname: "__external" }) && (
                                  <>
                                    <Alert
                                      isInline={true}
                                      variant={"warning"}
                                      title={"You have new changes to push"}
                                      actionLinks={
                                        <PushToGitHubAlertActionLinks
                                          canPush={canPushToGitRepository}
                                          remoteRef={`${GIT_ORIGIN_REMOTE_NAME}/${workspacePromise.data?.descriptor.origin.branch}`}
                                          onPush={pushToGitRepository}
                                        />
                                      }
                                    >
                                      {`Opening '${workspace.descriptor.name}' on vscode.dev won't show your latest changes.`}
                                    </Alert>
                                    <Divider />
                                  </>
                                )}
                                <DropdownItem
                                  href={`https://vscode.dev/github${
                                    new URL(workspace.descriptor.origin.url).pathname.endsWith(".git")
                                      ? new URL(workspace.descriptor.origin.url).pathname.replace(".git", "")
                                      : new URL(workspace.descriptor.origin.url).pathname
                                  }/tree/${workspace.descriptor.origin.branch}`}
                                  target={"_blank"}
                                  icon={<ExternalLinkAltIcon />}
                                  description={`The '${workspace.descriptor.origin.branch}' branch will be opened.`}
                                >
                                  vscode.dev
                                </DropdownItem>
                                <Divider />
                                <DropdownItem
                                  href={`vscode://vscode.git/clone?url=${workspace.descriptor.origin.url.toString()}`}
                                  target={"_blank"}
                                  icon={<ExternalLinkAltIcon />}
                                  description={"The default branch will be opened."}
                                >
                                  VS Code Desktop
                                </DropdownItem>
                              </DropdownGroup>,
                            ]}
                          />
                        </ToolbarItem>
                      </Toolbar>
                    </FlexItem>
                  )}
              </Flex>
            )}
          </PageSection>
          <PageSection type={"nav"} variant={"light"} style={{ paddingTop: 0, paddingBottom: "16px" }}>
            <Flex
              justifyContent={{ default: "justifyContentSpaceBetween" }}
              alignItems={{ default: "alignItemsCenter" }}
              flexWrap={{ default: "nowrap" }}
            >
              <FlexItem>
                <PageHeaderToolsItem visibility={{ default: "visible" }}>
                  <Flex flexWrap={{ default: "nowrap" }} alignItems={{ default: "alignItemsCenter" }}>
                    <FlexItem>
                      <FileSwitcher workspace={workspace} workspaceFile={props.workspaceFile} />
                    </FlexItem>
                    <FlexItem>
                      {(isEdited && (
                        <Tooltip content={"Saving in memory..."} position={"bottom"}>
                          <TextContent
                            style={{ color: "gray", ...(!props.workspaceFile ? { visibility: "hidden" } : {}) }}
                          >
                            <Text
                              aria-label={"Saving in memory..."}
                              data-testid="is-saving-in-memory-indicator"
                              component={TextVariants.small}
                            >
                              <span>
                                <OutlinedClockIcon size={"sm"} />
                              </span>
                            </Text>
                          </TextContent>
                        </Tooltip>
                      )) || (
                        <Tooltip content={"File is in memory."} position={"bottom"}>
                          <TextContent
                            style={{ color: "gray", ...(!props.workspaceFile ? { visibility: "hidden" } : {}) }}
                          >
                            <Text
                              aria-label={"File is in memory."}
                              data-testid="is-saved-in-memory-indicator"
                              component={TextVariants.small}
                            >
                              <span>
                                <DesktopIcon size={"sm"} />
                              </span>
                            </Text>
                          </TextContent>
                        </Tooltip>
                      )}
                    </FlexItem>
                    <FlexItem>
                      {(!isSaved && (
                        <Tooltip content={"Writing file..."} position={"bottom"}>
                          <TextContent
                            style={{ color: "gray", ...(!props.workspaceFile ? { visibility: "hidden" } : {}) }}
                          >
                            <Text
                              aria-label={"Writing file..."}
                              data-testid="is-writing-indicator"
                              component={TextVariants.small}
                            >
                              <span>
                                <OutlinedClockIcon size={"sm"} />
                              </span>
                            </Text>
                          </TextContent>
                        </Tooltip>
                      )) || (
                        <Tooltip content={"File is written on disk."} position={"bottom"}>
                          <TextContent
                            style={{ color: "gray", ...(!props.workspaceFile ? { visibility: "hidden" } : {}) }}
                          >
                            <Text
                              aria-label={"File is written on disk."}
                              data-testid="is-written-indicator"
                              component={TextVariants.small}
                            >
                              <span>
                                <OutlinedHddIcon size={"sm"} />
                              </span>
                            </Text>
                          </TextContent>
                        </Tooltip>
                      )}
                    </FlexItem>
                  </Flex>
                </PageHeaderToolsItem>
              </FlexItem>
              <FlexItem>
                <Toolbar>
                  <ToolbarContent style={{ paddingRight: 0 }}>
                    <ToolbarItem>
                      <Dropdown
                        position={"right"}
                        isOpen={isNewFileDropdownMenuOpen}
                        toggle={
                          <DropdownToggle
                            onToggle={setNewFileDropdownMenuOpen}
                            toggleVariant="primary"
                            toggleIndicator={CaretDownIcon}
                          >
                            <PlusIcon />
                            &nbsp;&nbsp;New file
                          </DropdownToggle>
                        }
                      >
                        <NewFileDropdownMenu
                          workspaceId={props.workspaceFile.workspaceId}
                          destinationDirPath={props.workspaceFile.relativeDirPath}
                          onAddFile={async (file) => {
                            setNewFileDropdownMenuOpen(false);
                            if (!file) {
                              return;
                            }

                            history.push({
                              pathname: routes.workspaceWithFilePath.path({
                                workspaceId: file.workspaceId,
                                fileRelativePath: file.relativePathWithoutExtension,
                                extension: file.extension,
                              }),
                            });
                          }}
                        />
                      </Dropdown>
                    </ToolbarItem>
                    {canBeDeployed && (
                      <ToolbarItem visibility={hideWhenSmall}>
                        <ExtendedServicesButtons workspace={workspace} workspaceFile={props.workspaceFile} />
                      </ToolbarItem>
                    )}
                    {workspace.descriptor.origin.kind === WorkspaceKind.GITHUB_GIST && (
                      <ToolbarItem>
                        <Dropdown
                          onSelect={() => setSyncGitHubGistDropdownOpen(false)}
                          isOpen={isSyncGitHubGistDropdownOpen}
                          position={DropdownPosition.right}
                          toggle={
                            <DropdownToggle
                              id={"sync-dropdown"}
                              data-testid={"sync-dropdown"}
                              onToggle={(isOpen) => setSyncGitHubGistDropdownOpen(isOpen)}
                            >
                              Sync
                            </DropdownToggle>
                          }
                          dropdownItems={[
                            <DropdownGroup key={"sync-gist-dropdown-group"}>
                              {canForkGitHubGist && (
                                <>
                                  <li role="menuitem">
                                    <Alert
                                      isInline={true}
                                      variant={"info"}
                                      title={
                                        <span style={{ whiteSpace: "nowrap" }}>
                                          {"Can't update Gists you don't own"}
                                        </span>
                                      }
                                      actionLinks={
                                        <AlertActionLink onClick={forkGitHubGist} style={{ fontWeight: "bold" }}>
                                          {`Fork Gist`}
                                        </AlertActionLink>
                                      }
                                    >
                                      {`You can create a fork of '${workspace.descriptor.name}' to save your updates.`}
                                    </Alert>
                                  </li>
                                  <Divider />
                                </>
                              )}
                              <Tooltip
                                data-testid={"gist-it-tooltip"}
                                content={<div>{i18n.editorToolbar.cantUpdateGistTooltip}</div>}
                                trigger={!canUpdateGitHubGist ? "mouseenter click" : ""}
                                position="left"
                              >
                                <>
                                  <DropdownItem
                                    icon={<GithubIcon />}
                                    onClick={updateGitHubGist}
                                    isDisabled={!canUpdateGitHubGist}
                                  >
                                    Update Gist
                                  </DropdownItem>
                                  {!canPushToGitRepository && (
                                    <>
                                      <Divider />
                                      <DropdownItem onClick={() => history.push(routes.settings.github.path({}))}>
                                        <Button isInline={true} variant={ButtonVariant.link}>
                                          Configure GitHub token...
                                        </Button>
                                      </DropdownItem>
                                    </>
                                  )}
                                </>
                              </Tooltip>
                            </DropdownGroup>,
                          ]}
                        />
                      </ToolbarItem>
                    )}
                    {workspace.descriptor.origin.kind === WorkspaceKind.GIT && (
                      <ToolbarItem>
                        <Dropdown
                          onSelect={() => setSyncGitRepositoryDropdownOpen(false)}
                          isOpen={isSyncGitRepositoryDropdownOpen}
                          position={DropdownPosition.right}
                          toggle={
                            <DropdownToggle
                              id={"sync-dropdown"}
                              data-testid={"sync-dropdown"}
                              onToggle={(isOpen) => setSyncGitRepositoryDropdownOpen(isOpen)}
                            >
                              Sync
                            </DropdownToggle>
                          }
                          dropdownItems={[
                            <DropdownGroup key={"sync-gist-dropdown-group"}>
                              <DropdownItem
                                icon={<SyncAltIcon />}
                                onClick={() => pullFromGitRepository({ showAlerts: true })}
                                description={`Get new changes made upstream at '${GIT_ORIGIN_REMOTE_NAME}/${workspace.descriptor.origin.branch}'.`}
                              >
                                Pull
                              </DropdownItem>
                              <Tooltip
                                data-testid={"gist-it-tooltip"}
                                content={
                                  <div>{`You need to be signed in with GitHub to push to this repository.`}</div>
                                }
                                trigger={!canPushToGitRepository ? "mouseenter click" : ""}
                                position="left"
                              >
                                <>
                                  <DropdownItem
                                    icon={<ArrowCircleUpIcon />}
                                    onClick={pushToGitRepository}
                                    isDisabled={!canPushToGitRepository}
                                    description={`Send your changes upstream to '${GIT_ORIGIN_REMOTE_NAME}/${workspace.descriptor.origin.branch}'.`}
                                  >
                                    Push
                                  </DropdownItem>
                                  {!canPushToGitRepository && (
                                    <>
                                      <Divider />
                                      <DropdownItem onClick={() => history.push(routes.settings.github.path({}))}>
                                        <Button isInline={true} variant={ButtonVariant.link}>
                                          Configure GitHub token...
                                        </Button>
                                      </DropdownItem>
                                    </>
                                  )}
                                </>
                              </Tooltip>
                            </DropdownGroup>,
                          ]}
                        />
                      </ToolbarItem>
                    )}
                    <ToolbarItem visibility={hideWhenSmall}>
                      <Dropdown
                        onSelect={() => setShareDropdownOpen(false)}
                        isOpen={isShareDropdownOpen}
                        dropdownItems={shareDropdownItems}
                        position={DropdownPosition.right}
                        toggle={
                          <DropdownToggle
                            id={"share-dropdown"}
                            data-testid={"share-dropdown"}
                            onToggle={(isOpen) => setShareDropdownOpen(isOpen)}
                          >
                            {i18n.editorToolbar.share}
                          </DropdownToggle>
                        }
                      />
                    </ToolbarItem>
                    <ToolbarItem visibility={hideWhenSmall} style={{ marginRight: 0 }}>
                      <KebabDropdown
                        id={"kebab-lg"}
                        state={[isLargeKebabOpen, setLargeKebabOpen]}
                        items={[deleteFileDropdownItem, <Divider key={"divider-0"} />, createSavePointDropdownItem]}
                      />
                    </ToolbarItem>
                    <ToolbarItem visibility={showWhenSmall} style={{ marginRight: 0 }}>
                      <KebabDropdown
                        id={"kebab-sm"}
                        state={[isSmallKebabOpen, setSmallKebabOpen]}
                        items={[
                          deleteFileDropdownItem,
                          <Divider key={"divider-0"} />,
                          createSavePointDropdownItem,
                          <Divider key={"divider-1"} />,
                          ...shareDropdownItems,
                          ...(!canBeDeployed
                            ? []
                            : [
                                <Divider key={"divider-2"} />,
                                <ExtendedServicesDropdownGroup
                                  workspace={workspace}
                                  workspaceFile={props.workspaceFile}
                                  key="extended-services-group"
                                />,
                              ]),
                        ]}
                      />
                    </ToolbarItem>
                  </ToolbarContent>
                </Toolbar>
              </FlexItem>
            </Flex>
          </PageSection>
          <CreateGitHubRepositoryModal
            workspace={workspace}
            isOpen={isCreateGitHubRepositoryModalOpen}
            onClose={() => setCreateGitHubRepositoryModalOpen(false)}
            onSuccess={({ url }) => {
              createRepositorySuccessAlert.show({ url });
            }}
            currentFile={props.workspaceFile}
          />
          <ConfirmDeployModal workspace={workspace} workspaceFile={props.workspaceFile} />
          <textarea ref={copyContentTextArea} style={{ height: 0, position: "absolute", zIndex: -1 }} />
          <a ref={downloadRef} />
          <a ref={downloadAllRef} />
          <a ref={downloadPreviewRef} />
        </>
      )}
    />
  );
}

export function PushToGitHubAlertActionLinks(props: {
  onPush: () => void;
  canPush?: boolean;
  kind?: WorkspaceKind;
  remoteRef?: string;
}) {
  const history = useHistory();

  if (props.kind === WorkspaceKind.GIT && !props.remoteRef) {
    throw new Error("Should specify remoteRef for GIT workspaces");
  }

  return (
    <>
      {!props.canPush && (
        <AlertActionLink onClick={() => history.push(routes.settings.github.path({}))}>
          {`Configure GitHub token...`}
        </AlertActionLink>
      )}
      {props.canPush && (
        <AlertActionLink onClick={props.onPush} style={{ fontWeight: "bold" }}>
          {props.kind === WorkspaceKind.GIT ? `Push to '${props.remoteRef}'` : `Update Gist`}
        </AlertActionLink>
      )}
    </>
  );
}

export function KebabDropdown(props: {
  id: string;
  items: React.ReactNode[];
  state: [boolean, React.Dispatch<React.SetStateAction<boolean>>];
}) {
  return (
    <Dropdown
      className={"kie-tools--masthead-hoverable"}
      isOpen={props.state[0]}
      isPlain={true}
      position={DropdownPosition.right}
      onSelect={() => props.state[1](false)}
      toggle={
        <DropdownToggle
          id={props.id}
          toggleIndicator={null}
          onToggle={(isOpen) => props.state[1](isOpen)}
          ouiaId={props.id}
        >
          <EllipsisVIcon />
        </DropdownToggle>
      }
      dropdownItems={props.items}
    />
  );
}
