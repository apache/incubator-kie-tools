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

import {
  Dropdown,
  DropdownGroup,
  DropdownItem,
  DropdownPosition,
  DropdownToggle,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import {
  Toolbar,
  ToolbarContent,
  ToolbarGroup,
  ToolbarItem,
  ToolbarItemProps,
} from "@patternfly/react-core/dist/js/components/Toolbar";
import { EllipsisVIcon } from "@patternfly/react-icons/dist/js/icons/ellipsis-v-icon";
import { SaveIcon } from "@patternfly/react-icons/dist/js/icons/save-icon";
import { AngleLeftIcon } from "@patternfly/react-icons/dist/js/icons/angle-left-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useOnlineI18n } from "../i18n";
import { KieSandboxExtendedServicesButtons } from "./KieSandboxExtendedServices/KieSandboxExtendedServicesButtons";
import {
  useNavigationBlockersBypass,
  useNavigationStatus,
  useNavigationStatusToggle,
  useRoutes,
} from "../navigation/Hooks";
import { EmbeddedEditorRef, useDirtyState } from "@kie-tools-core/editor/dist/embedded";
import { useHistory } from "react-router";
import { EmbedModal } from "./EmbedModal";
import { Alerts, AlertsController, useAlert } from "../alerts/Alerts";
import { Alert, AlertActionCloseButton, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { OutlinedClockIcon } from "@patternfly/react-icons/dist/js/icons/outlined-clock-icon";
import { FolderIcon } from "@patternfly/react-icons/dist/js/icons/folder-icon";
import { ImageIcon } from "@patternfly/react-icons/dist/js/icons/image-icon";
import { DownloadIcon } from "@patternfly/react-icons/dist/js/icons/download-icon";
import { PlusIcon } from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { GithubIcon } from "@patternfly/react-icons/dist/js/icons/github-icon";
import { ArrowCircleUpIcon } from "@patternfly/react-icons/dist/js/icons/arrow-circle-up-icon";
import { ColumnsIcon } from "@patternfly/react-icons/dist/js/icons/columns-icon";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { NewFileDropdownMenu } from "./NewFileDropdownMenu";
import { PageHeaderToolsItem, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { FileLabel } from "../filesList/FileLabel";
import { useWorkspacePromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceHooks";
import { OutlinedHddIcon } from "@patternfly/react-icons/dist/js/icons/outlined-hdd-icon";
import { DesktopIcon } from "@patternfly/react-icons/dist/js/icons/desktop-icon";
import { FileSwitcher } from "./FileSwitcher";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { KieSandboxExtendedServicesDropdownGroup } from "./KieSandboxExtendedServices/KieSandboxExtendedServicesDropdownGroup";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons/trash-icon";
import { CaretDownIcon } from "@patternfly/react-icons/dist/js/icons/caret-down-icon";
import {
  GIST_ORIGIN_REMOTE_NAME,
  GIT_ORIGIN_REMOTE_NAME,
} from "@kie-tools-core/workspaces-git-fs/dist/constants/GitConstants";
import { WorkspaceKind } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceOrigin";
import { PromiseStateWrapper } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { WorkspaceLabel } from "../workspace/components/WorkspaceLabel";
import { EditorPageDockDrawerRef } from "./EditorPageDockDrawer";
import { SyncAltIcon } from "@patternfly/react-icons/dist/js/icons/sync-alt-icon";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { UrlType, useImportableUrl } from "../importFromUrl/ImportableUrlHooks";
import { Location } from "history";
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import { CreateGitHubRepositoryModal } from "./CreateGitHubRepositoryModal";
import { useEditorEnvelopeLocator } from "../envelopeLocator/hooks/EditorEnvelopeLocatorContext";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import type { RestEndpointMethodTypes as OctokitRestEndpointMethodTypes } from "@octokit/plugin-rest-endpoint-methods/dist-types/generated/parameters-and-response-types";
import { useSharedValue } from "@kie-tools-core/envelope-bus/dist/hooks";
import { WorkspaceStatusIndicator } from "../workspace/components/WorkspaceStatusIndicator";
import { ResponsiveDropdown } from "../ResponsiveDropdown/ResponsiveDropdown";
import { ResponsiveDropdownToggle } from "../ResponsiveDropdown/ResponsiveDropdownToggle";
import { useAuthSession } from "../accounts/authSessions/AuthSessionsContext";
import { AuthSessionSelect, AuthSessionSelectFilter } from "../accounts/authSessions/AuthSessionSelect";
import { useAuthProvider } from "../accounts/authProviders/AuthProvidersContext";
import { useOctokit } from "../github/Hooks";
import { AccountsDispatchActionKind, useAccountsDispatch } from "../accounts/AccountsDispatchContext";
import { SelectPosition } from "@patternfly/react-core/dist/js/components/Select";
import {
  authSessionsSelectFilterCompatibleWithGistUrlDomain,
  authSessionsSelectFilterCompatibleWithGitUrlDomain,
  noOpAuthSessionSelectFilter,
} from "../accounts/authSessions/CompatibleAuthSessions";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";

export interface Props {
  alerts: AlertsController | undefined;
  alertsRef: (controller: AlertsController) => void;
  editor: EmbeddedEditorRef | undefined;
  workspaceFile: WorkspaceFile;
  editorPageDock: EditorPageDockDrawerRef | undefined;
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
  const routes = useRoutes();
  const editorEnvelopeLocator = useEditorEnvelopeLocator();
  const history = useHistory();
  const workspaces = useWorkspaces();
  const accountsDispatch = useAccountsDispatch();
  const [isShareDropdownOpen, setShareDropdownOpen] = useState(false);
  const [isSyncGitHubGistDropdownOpen, setSyncGitHubGistDropdownOpen] = useState(false);
  const [isSyncGitRepositoryDropdownOpen, setSyncGitRepositoryDropdownOpen] = useState(false);
  const [isLargeKebabOpen, setLargeKebabOpen] = useState(false);
  const [isSmallKebabOpen, setSmallKebabOpen] = useState(false);
  const [isEmbedModalOpen, setEmbedModalOpen] = useState(false);
  const { i18n } = useOnlineI18n();
  const isEdited = useDirtyState(props.editor);
  const downloadRef = useRef<HTMLAnchorElement>(null);
  const downloadAllRef = useRef<HTMLAnchorElement>(null);
  const downloadPreviewRef = useRef<HTMLAnchorElement>(null);
  const copyContentTextArea = useRef<HTMLTextAreaElement>(null);
  const [isNewFileDropdownMenuOpen, setNewFileDropdownMenuOpen] = useState(false);
  const workspacePromise = useWorkspacePromise(props.workspaceFile.workspaceId);
  const [isGitHubGistLoading, setGitHubGistLoading] = useState(false);
  const [gitHubGist, setGitHubGist] =
    useState<OctokitRestEndpointMethodTypes["gists"]["get"]["response"]["data"] | undefined>(undefined);
  const workspaceImportableUrl = useImportableUrl(workspacePromise.data?.descriptor.origin.url?.toString());

  const { authSession, authInfo, gitConfig } = useAuthSession(workspacePromise.data?.descriptor.gitAuthSessionId);
  const authProvider = useAuthProvider(authSession);

  const octokit = useOctokit(authSession);

  const canPushToGitRepository = useMemo(
    () => authSession?.type === "git" && !!authProvider,
    [authProvider, authSession?.type]
  );
  const navigationBlockersBypass = useNavigationBlockersBypass();

  const [flushes] = useSharedValue(
    workspaces.workspacesSharedWorker.workspacesWorkerBus.clientApi.shared.kieSandboxWorkspacesStorage_flushes
  );

  const authSessionSelectFilter = useMemo(() => {
    if (!workspacePromise.data) {
      return noOpAuthSessionSelectFilter();
    }

    if (workspacePromise.data.descriptor.origin.kind === WorkspaceKind.LOCAL) {
      return noOpAuthSessionSelectFilter();
    }

    if (workspacePromise.data.descriptor.origin.kind === WorkspaceKind.GIT) {
      return authSessionsSelectFilterCompatibleWithGitUrlDomain(
        new URL(workspacePromise.data.descriptor.origin.url).hostname
      );
    }

    if (workspacePromise.data.descriptor.origin.kind === WorkspaceKind.GITHUB_GIST) {
      return authSessionsSelectFilterCompatibleWithGistUrlDomain(
        new URL(workspacePromise.data.descriptor.origin.url).hostname,
        gitHubGist?.owner?.login
      );
    }

    return noOpAuthSessionSelectFilter();
  }, [gitHubGist, workspacePromise.data]);

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

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (gitHubGist || workspaceImportableUrl.type !== UrlType.GIST_DOT_GITHUB_DOT_COM) {
          return;
        }

        const { gistId } = workspaceImportableUrl;

        if (!gistId) {
          return;
        }

        octokit.gists.get({ gist_id: gistId }).then(({ data: gist }) => {
          if (canceled.get()) {
            return;
          }

          if (gist) {
            setGitHubGist(gist);
          }
        });
      },
      [gitHubGist, workspaceImportableUrl, octokit.gists]
    )
  );

  const successfullyCreateGistAlert = useAlert(
    props.alerts,
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

  const loadingGistAlert = useAlert(
    props.alerts,
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

  const successfullyUpdateGistAlert = useAlert(
    props.alerts,
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

  const errorAlert = useAlert(
    props.alerts,
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
    return props.workspaceFile.extension.toLowerCase() !== "pmml";
  }, [props.workspaceFile]);

  const shouldIncludeEmbedDropdownItem = useMemo(() => {
    return props.workspaceFile.extension.toLowerCase() !== "pmml";
  }, [props.workspaceFile]);

  const onDownload = useCallback(() => {
    props.editor?.getStateControl().setSavedCommand();
    props.alerts?.closeAll();
    props.workspaceFile.getFileContents().then((content) => {
      if (downloadRef.current) {
        const fileBlob = new Blob([content], { type: "text/plain" });
        downloadRef.current.href = URL.createObjectURL(fileBlob);
        downloadRef.current.click();
      }
    });
  }, [props.editor, props.workspaceFile, props.alerts]);

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
      await workspaces.createSavePoint({ workspaceId: props.workspaceFile.workspaceId, gitConfig });
    }
  }, [
    props.editor,
    props.workspaceFile.workspaceId,
    workspaces,
    workspacePromise.data?.descriptor.origin.kind,
    gitConfig,
  ]);

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
      if (!authInfo || !workspacePromise.data) {
        return;
      }

      setGitHubGistLoading(true);

      await workspaces.push({
        workspaceId: props.workspaceFile.workspaceId,
        remote: GIST_ORIGIN_REMOTE_NAME,
        ref: workspacePromise.data.descriptor.origin.branch,
        remoteRef: `refs/heads/${workspacePromise.data.descriptor.origin.branch}`,
        force: true,
        authInfo,
      });

      await workspaces.pull({
        workspaceId: props.workspaceFile.workspaceId,
        authInfo,
      });
    } catch (e) {
      errorAlert.show();
    } finally {
      setGitHubGistLoading(false);
      setSyncGitHubGistDropdownOpen(false);
    }

    successfullyUpdateGistAlert.show();
  }, [
    successfullyUpdateGistAlert,
    authInfo,
    workspacePromise.data,
    workspaces,
    props.workspaceFile.workspaceId,
    errorAlert,
  ]);

  const errorPushingGist = useAlert(
    props.alerts,
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
              Push forcefully
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
      if (!authInfo || !workspacePromise.data) {
        return;
      }

      setGitHubGistLoading(true);

      await workspaces.createSavePoint({
        workspaceId: props.workspaceFile.workspaceId,
        gitConfig,
      });

      await workspaces.push({
        workspaceId: props.workspaceFile.workspaceId,
        remote: GIST_ORIGIN_REMOTE_NAME,
        ref: workspacePromise.data.descriptor.origin.branch,
        remoteRef: `refs/heads/${workspacePromise.data.descriptor.origin.branch}`,
        force: false,
        authInfo,
      });

      await workspaces.pull({
        workspaceId: props.workspaceFile.workspaceId,
        authInfo,
      });
    } catch (e) {
      errorPushingGist.show();
      throw e;
    } finally {
      setGitHubGistLoading(false);
      setSyncGitHubGistDropdownOpen(false);
    }

    successfullyUpdateGistAlert.show();
  }, [
    successfullyUpdateGistAlert,
    authInfo,
    workspacePromise.data,
    workspaces,
    props.workspaceFile.workspaceId,
    gitConfig,
    errorPushingGist,
  ]);

  const createGitHubGist = useCallback(async () => {
    try {
      if (!authInfo) {
        return;
      }
      setGitHubGistLoading(true);
      const gist = await octokit.gists.create({
        description: workspacePromise.data?.descriptor.name ?? "",
        public: true,

        // This file is used just for creating the Gist. The `push -f` overwrites it.
        files: {
          "README.md": {
            content: `
This Gist was created from KIE Sandbox.

This file is temporary and you should not be seeing it.
If you are, it means that creating this Gist failed and it can safely be deleted.
`,
          },
        },
      });

      if (!gist.data.git_push_url) {
        throw new Error("Gist creation failed.");
      }

      const gistDefaultBranch = (
        await workspaces.getGitServerRefs({
          url: new URL(gist.data.git_push_url).toString(),
          authInfo,
        })
      )
        .find((serverRef) => serverRef.ref === "HEAD")!
        .target!.replace("refs/heads/", "");

      await workspaces.initGistOnWorkspace({
        workspaceId: props.workspaceFile.workspaceId,
        remoteUrl: new URL(gist.data.git_push_url),
        branch: gistDefaultBranch,
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
        name: gistDefaultBranch,
      });

      await workspaces.createSavePoint({
        workspaceId: props.workspaceFile.workspaceId,
        gitConfig,
      });

      await workspaces.push({
        workspaceId: props.workspaceFile.workspaceId,
        remote: GIST_ORIGIN_REMOTE_NAME,
        ref: gistDefaultBranch,
        remoteRef: `refs/heads/${gistDefaultBranch}`,
        force: true,
        authInfo,
      });

      await workspaces.pull({
        workspaceId: props.workspaceFile.workspaceId,
        authInfo,
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
    authInfo,
    octokit.gists,
    workspacePromise.data?.descriptor.name,
    workspaces,
    props.workspaceFile.workspaceId,
    gitConfig,
    successfullyCreateGistAlert,
    errorAlert,
  ]);

  const forkGitHubGist = useCallback(async () => {
    try {
      if (!authSession || !authInfo || !gitHubGist?.id || !workspacePromise.data) {
        return;
      }

      setGitHubGistLoading(true);

      // Fork Gist
      const gist = await octokit.gists.fork({
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
        gitConfig,
      });

      // Push to forked gist remote
      await workspaces.push({
        workspaceId: props.workspaceFile.workspaceId,
        remote: remoteName,
        ref: workspacePromise.data.descriptor.origin.branch,
        remoteRef: `refs/heads/${workspacePromise.data.descriptor.origin.branch}`,
        force: true,
        authInfo,
      });

      // Redirect to import workspace
      navigationBlockersBypass.execute(() => {
        history.push({
          pathname: routes.import.path({}),
          search: routes.import.queryString({
            url: gist.data.html_url,
            authSessionId: authSession.id,
          }),
        });
      });
    } catch (err) {
      errorAlert.show();
      throw err;
    } finally {
      setGitHubGistLoading(false);
    }
  }, [
    authSession,
    authInfo,
    gitHubGist,
    workspacePromise.data,
    octokit.gists,
    workspaces,
    props.workspaceFile.workspaceId,
    gitConfig,
    navigationBlockersBypass,
    history,
    routes.import,
    errorAlert,
  ]);

  const openEmbedModal = useCallback(() => {
    setEmbedModalOpen(true);
  }, []);

  const workspaceHasNestedDirectories = useMemo(
    () => workspacePromise.data?.files.filter((f) => f.relativePath !== f.name).length !== 0,
    [workspacePromise]
  );

  const isGitHubGistOwner = useMemo(() => {
    return authInfo?.username && gitHubGist?.owner?.login === authInfo.username;
  }, [authInfo, gitHubGist]);

  const canCreateGitRepository = useMemo(() => authProvider?.type === "github", [authProvider?.type]);

  const canCreateGitHubGist = useMemo(
    () =>
      authProvider?.type === "github" &&
      workspacePromise.data?.descriptor.origin.kind === WorkspaceKind.LOCAL &&
      !workspaceHasNestedDirectories,
    [authProvider?.type, workspacePromise.data?.descriptor.origin.kind, workspaceHasNestedDirectories]
  );

  const canUpdateGitHubGist = useMemo(
    () =>
      authProvider?.type === "github" &&
      !!isGitHubGistOwner &&
      workspacePromise.data?.descriptor.origin.kind === WorkspaceKind.GITHUB_GIST &&
      !workspaceHasNestedDirectories,
    [
      authProvider?.type,
      isGitHubGistOwner,
      workspacePromise.data?.descriptor.origin.kind,
      workspaceHasNestedDirectories,
    ]
  );

  const canForkGitHubGist = useMemo(
    () =>
      authProvider?.type === "github" &&
      !isGitHubGistOwner &&
      workspacePromise.data?.descriptor.origin.kind === WorkspaceKind.GITHUB_GIST &&
      !workspaceHasNestedDirectories,
    [
      authProvider?.type,
      isGitHubGistOwner,
      workspacePromise.data?.descriptor.origin.kind,
      workspaceHasNestedDirectories,
    ]
  );

  const changeGitAuthSessionId = useCallback(
    (newGitAuthSessionId: React.SetStateAction<string | undefined>, lastAuthSessionId: string | undefined) => {
      workspaces.changeGitAuthSessionId({
        workspaceId: props.workspaceFile.workspaceId,
        gitAuthSessionId:
          typeof newGitAuthSessionId === "function" ? newGitAuthSessionId(lastAuthSessionId) : newGitAuthSessionId,
      });
    },
    [props.workspaceFile.workspaceId, workspaces]
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
      ...(shouldIncludeEmbedDropdownItem
        ? [
            <Divider key={"divider-other-group"} />,
            <DropdownGroup key={"other-group"} label="Other">
              <DropdownItem
                key={`dropdown-embed`}
                data-testid="dropdown-embed"
                component="button"
                onClick={openEmbedModal}
                icon={<ColumnsIcon />}
              >
                {i18n.editorToolbar.embed}...
              </DropdownItem>
            </DropdownGroup>,
          ]
        : []),
      ...(workspacePromise.data?.descriptor.origin.kind === WorkspaceKind.LOCAL ||
      workspacePromise.data?.descriptor.origin.kind === WorkspaceKind.GITHUB_GIST
        ? [
            <DropdownGroup key={"github-group"} label={i18n.names.github}>
              <Tooltip
                data-testid={"create-github-repository-tooltip"}
                key={`dropdown-create-github-repository`}
                content={<div>{`You need to select an authentication source to be able to Create a repository.`}</div>}
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
              </Tooltip>
              {!canPushToGitRepository && (
                <Alert
                  isInline={true}
                  variant={"default"}
                  title={"Can't Create Repository or Gist without selecting an authentication source"}
                  actionLinks={
                    <AuthSessionSelect
                      title={`Select Git authentication for '${workspacePromise.data.descriptor.name}'...`}
                      position={SelectPosition.right}
                      isPlain={false}
                      authSessionId={workspacePromise.data.descriptor.gitAuthSessionId}
                      setAuthSessionId={(newAuthSessionId) => {
                        changeGitAuthSessionId(newAuthSessionId, workspacePromise.data?.descriptor.gitAuthSessionId);
                        setTimeout(() => {
                          accountsDispatch({ kind: AccountsDispatchActionKind.CLOSE });
                          setShareDropdownOpen(true);
                          setSmallKebabOpen(true);
                        }, 0);
                      }}
                      filter={authSessionSelectFilter}
                    />
                  }
                >
                  {`Select an authentication source for '${workspacePromise.data.descriptor.name}' to be able to Create Repository or Gist.`}
                </Alert>
              )}
            </DropdownGroup>,
          ]
        : []),
    ],
    [
      onDownload,
      props.workspaceFile.name,
      shouldIncludeDownloadSvgDropdownItem,
      downloadSvg,
      downloadWorkspaceZip,
      shouldIncludeEmbedDropdownItem,
      openEmbedModal,
      i18n.editorToolbar.embed,
      i18n.editorToolbar.cantCreateGistTooltip,
      i18n.editorToolbar.createGist,
      i18n.names.github,
      workspacePromise.data?.descriptor,
      canCreateGitRepository,
      canCreateGitHubGist,
      createGitHubGist,
      canPushToGitRepository,
      authSessionSelectFilter,
      changeGitAuthSessionId,
      accountsDispatch,
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
        return (
          f.relativePath !== props.workspaceFile.relativePath && editorEnvelopeLocator.hasMappingFor(f.relativePath)
        );
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
  }, [routes, history, workspacePromise.data, props.workspaceFile, workspaces, editorEnvelopeLocator]);

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
      <DropdownItem key={"delete-dropdown-item"} onClick={deleteWorkspaceFile}>
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

  const pushingAlert = useAlert(
    props.alerts,
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

  const comittingAlert = useAlert(
    props.alerts,
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

  const commitSuccessAlert = useAlert(
    props.alerts,
    useCallback(({ close }) => {
      return <Alert variant="success" title={`Commit created.`} />;
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
            gitConfig,
          });
          comittingAlert.close();
          commitSuccessAlert.show();
        }}
        description={"Create a save point"}
      >
        Commit
      </DropdownItem>
    );
  }, [comittingAlert, workspaces, props.workspaceFile.workspaceId, gitConfig, commitSuccessAlert]);

  const pushSuccessAlert = useAlert(
    props.alerts,
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

  const pushErrorAlert = useAlert(
    props.alerts,
    useCallback(
      ({ close }) => {
        if (workspacePromise.data?.descriptor.origin.kind !== WorkspaceKind.GIT) {
          return <></>;
        }

        return (
          <Alert
            variant="danger"
            title={`Error Pushing to '${workspacePromise.data?.descriptor.origin.url}'`}
            actionClose={<AlertActionCloseButton onClose={close} />}
          />
        );
      },
      [workspacePromise]
    )
  );

  const pullingAlert = useAlert(
    props.alerts,
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

  const pullSuccessAlert = useAlert(
    props.alerts,
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
      if (!authInfo || !workspacePromise.data) {
        return;
      }

      try {
        pushingAlert.show();

        await workspaces.createSavePoint({
          workspaceId: props.workspaceFile.workspaceId,
          gitConfig,
        });

        await workspaces.branch({ workspaceId: props.workspaceFile.workspaceId, checkout: false, name: newBranchName });

        await workspaces.push({
          workspaceId: props.workspaceFile.workspaceId,
          remote: GIT_ORIGIN_REMOTE_NAME,
          remoteRef: `refs/heads/${newBranchName}`,
          ref: newBranchName,
          force: false,
          authInfo,
        });

        history.push({
          pathname: routes.import.path({}),
          search: routes.import.queryString({
            url: `${workspacePromise.data.descriptor.origin.url}`,
            branch: newBranchName,
            authSessionId: workspacePromise.data.descriptor.gitAuthSessionId,
          }),
        });
      } catch (e) {
        pushErrorAlert.show();
      } finally {
        pushingAlert.close();
      }
    },
    [
      authInfo,
      workspacePromise.data,
      pushingAlert,
      workspaces,
      props.workspaceFile.workspaceId,
      gitConfig,
      history,
      routes.import,
      pushErrorAlert,
    ]
  );

  const pullErrorAlert = useAlert<{ newBranchName: string; onTryAgain: () => any }>(
    props.alerts,
    useCallback(
      ({ close }, { newBranchName }) => {
        if (workspacePromise.data?.descriptor.origin.kind !== WorkspaceKind.GIT) {
          return <></>;
        }

        return (
          <Alert
            variant="danger"
            title={`Error Pulling from '${workspacePromise.data?.descriptor.origin.url}'`}
            actionClose={<AlertActionCloseButton onClose={close} />}
            actionLinks={<></>}
          >
            {`This usually happens when your branch has conflicts with the upstream branch or you don't have permission to Pull.`}
            <br />
            <br />
            {`You can save your work to a new branch.`}
            <br />
            <br />
            <Tooltip
              data-testid={"gist-it-tooltip"}
              content={<div>{`You need select an authentication source to be able to Push to a new branch.`}</div>}
              trigger={!canPushToGitRepository ? "mouseenter click" : ""}
              position="left"
            >
              <Button
                onClick={() => pushNewBranch(newBranchName)}
                variant={ButtonVariant.link}
                style={{ paddingLeft: 0 }}
                isSmall={true}
                isDisabled={!canPushToGitRepository}
              >
                {`Switch to '${newBranchName}'`}
              </Button>
            </Tooltip>
            <br />
            <br />

            {`Or change the authentication source for '${workspacePromise.data?.descriptor.name}' and try again`}
            <br />
          </Alert>
        );
      },
      [canPushToGitRepository, pushNewBranch, workspacePromise.data]
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
        gitConfig,
      });

      try {
        await workspaces.pull({
          workspaceId: props.workspaceFile.workspaceId,
          authInfo,
        });

        if (args.showAlerts) {
          pullSuccessAlert.show();
        }
      } catch (e) {
        console.error(e);
        if (args.showAlerts) {
          const randomString = (Math.random() + 1).toString(36).substring(7);
          const newBranchName = `${workspacePromise.data?.descriptor.origin.branch}-${randomString}`;
          pullErrorAlert.show({ newBranchName, onTryAgain: () => pullFromGitRepository({ showAlerts: true }) });
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
      gitConfig,
      authInfo,
      workspacePromise.data?.descriptor.origin.branch,
    ]
  );

  const pushToGitRepository = useCallback(async () => {
    pushingAlert.close();
    pushErrorAlert.close();
    pushSuccessAlert.close();

    if (!authInfo) {
      return;
    }

    pushingAlert.show();
    try {
      const workspaceId = props.workspaceFile.workspaceId;
      await workspaces.createSavePoint({
        workspaceId: workspaceId,
        gitConfig,
      });

      const workspace = await workspaces.getWorkspace({ workspaceId });
      await workspaces.push({
        workspaceId: props.workspaceFile.workspaceId,
        ref: workspace.origin.branch,
        remote: GIST_ORIGIN_REMOTE_NAME,
        remoteRef: `refs/heads/${workspace.origin.branch}`,
        force: false,
        authInfo,
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
    pushingAlert,
    pushErrorAlert,
    pushSuccessAlert,
    authInfo,
    props.workspaceFile.workspaceId,
    workspaces,
    gitConfig,
    pullFromGitRepository,
  ]);

  const isGistWorkspace = useMemo(
    () => workspacePromise.data?.descriptor.origin.kind === WorkspaceKind.GITHUB_GIST,
    [workspacePromise.data?.descriptor.origin.kind]
  );
  const navigationStatus = useNavigationStatus();
  const navigationStatusToggle = useNavigationStatusToggle();
  const confirmNavigationAlert = useAlert<{ lastBlockedLocation: Location }>(
    props.alerts,
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
                  changeGitAuthSessionId={changeGitAuthSessionId}
                  workspaceDescriptor={workspacePromise.data?.descriptor}
                  canPush={isGistWorkspace ? canUpdateGitHubGist : canPushToGitRepository}
                  remoteRef={`${GIT_ORIGIN_REMOTE_NAME}/${workspacePromise.data?.descriptor.origin.branch}`}
                  authSessionSelectFilter={authSessionSelectFilter}
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
        changeGitAuthSessionId,
        isGistWorkspace,
        canUpdateGitHubGist,
        canPushToGitRepository,
        authSessionSelectFilter,
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

  const createRepositorySuccessAlert = useAlert<{ url: string }>(
    props.alerts,
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
      resolved={(workspace) => {
        return (
          <>
            <Alerts ref={props.alertsRef} width={"500px"} />
            <PageSection type={"nav"} variant={"light"} padding={{ default: "noPadding" }}>
              {workspace && canSeeWorkspaceToolbar && (
                <Flex
                  justifyContent={{ default: "justifyContentSpaceBetween" }}
                  flexWrap={{ default: "nowrap" }}
                  spaceItems={{ default: "spaceItemsMd" }}
                >
                  <FlexItem style={{ minWidth: 0 }}>
                    <Flex
                      justifyContent={{ default: "justifyContentFlexStart" }}
                      flexWrap={{ default: "nowrap" }}
                      spaceItems={{ default: "spaceItemsSm" }}
                      alignItems={{ default: "alignItemsCenter" }}
                    >
                      <FlexItem>
                        <Button
                          className={"kie-tools--masthead-hoverable"}
                          variant={ButtonVariant.plain}
                          onClick={() => history.push({ pathname: routes.home.path({}) })}
                        >
                          <AngleLeftIcon />
                        </Button>
                      </FlexItem>
                      <FlexItem>
                        <AuthSessionSelect
                          title={`Select Git authentication for '${workspace.descriptor.name}'...`}
                          isPlain={true}
                          authSessionId={workspace.descriptor.gitAuthSessionId}
                          setAuthSessionId={(newAuthSessionId) => {
                            changeGitAuthSessionId(newAuthSessionId, workspace.descriptor.gitAuthSessionId);
                            accountsDispatch({ kind: AccountsDispatchActionKind.CLOSE });
                          }}
                          filter={authSessionSelectFilter}
                        />
                      </FlexItem>
                      <FlexItem>
                        <WorkspaceLabel descriptor={workspace.descriptor} />
                      </FlexItem>
                      <FlexItem
                        style={{ minWidth: 0, padding: "0 8px 0 8px", flexShrink: 0 }}
                        className={"kie-tools--masthead-hoverable"}
                      >
                        <FolderIcon style={{ marginRight: "8px", verticalAlign: "middle" }} />
                        <div
                          data-testid={"toolbar-title-workspace"}
                          className={"kogito--editor__toolbar-name-container"}
                          style={{ display: "inline-block", verticalAlign: "middle" }}
                        >
                          <Title
                            aria-label={"EmbeddedEditorFile name"}
                            headingLevel={"h3"}
                            size={"md"}
                            style={{
                              fontStyle: "italic",
                            }}
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
                            style={{ fontStyle: "italic", top: "4px", height: "calc(100% - 8px)" }}
                          />
                        </div>
                      </FlexItem>
                      <FlexItem>
                        <WorkspaceStatusIndicator workspace={workspace} />
                      </FlexItem>
                    </Flex>
                  </FlexItem>
                  {/*<Divider inset={{ default: "insetMd" }} isVertical={true} />*/}
                  {workspace.descriptor.origin.kind === WorkspaceKind.GIT &&
                    workspaceImportableUrl.type === UrlType.GITHUB_DOT_COM && (
                      <FlexItem
                        style={{
                          minWidth: "137px",
                        }}
                      >
                        <Dropdown
                          className={"kie-tools--masthead-hoverable"}
                          isPlain={true}
                          onSelect={() => setVsCodeDropdownOpen(false)}
                          isOpen={isVsCodeDropdownOpen}
                          position={"right"}
                          toggle={
                            <DropdownToggle toggleIndicator={null} onToggle={setVsCodeDropdownOpen}>
                              <Flex flexWrap={{ default: "nowrap" }}>
                                <FlexItem
                                  style={{
                                    minWidth: 0 /* This is to make the flex parent not overflow horizontally */,
                                  }}
                                >
                                  <Tooltip distance={5} position={"top-start"} content={workspace.descriptor.name}>
                                    <TextContent>
                                      <Text
                                        component={TextVariants.small}
                                        style={{
                                          whiteSpace: "nowrap",
                                          overflow: "hidden",
                                          textOverflow: "ellipsis",
                                        }}
                                      >
                                        <img
                                          style={{
                                            minWidth: "14px",
                                            maxWidth: "14px",
                                            marginTop: "-2px",
                                            verticalAlign: "middle",
                                          }}
                                          alt="vscode-logo-blue"
                                          src={routes.static.images.vscodeLogoBlue.path({})}
                                        />
                                        &nbsp;&nbsp;
                                        {`Open "${workspace.descriptor.name}"`}
                                      </Text>
                                    </TextContent>
                                  </Tooltip>
                                </FlexItem>
                                <FlexItem>
                                  <CaretDownIcon />
                                </FlexItem>
                              </Flex>
                            </DropdownToggle>
                          }
                          dropdownItems={[
                            <DropdownGroup key={"open-in-vscode"}>
                              {navigationStatus.shouldBlockNavigationTo({ pathname: "__external" }) && (
                                <>
                                  <Alert
                                    isInline={true}
                                    variant={"warning"}
                                    title={"You have new changes to Push"}
                                    actionLinks={
                                      <PushToGitHubAlertActionLinks
                                        changeGitAuthSessionId={changeGitAuthSessionId}
                                        workspaceDescriptor={workspace.descriptor}
                                        canPush={canPushToGitRepository}
                                        authSessionSelectFilter={authSessionSelectFilter}
                                        remoteRef={`${GIT_ORIGIN_REMOTE_NAME}/${workspace.descriptor.origin.branch}`}
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
                                style={{ minWidth: "400px" }}
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
                <FlexItem style={{ minWidth: 0 }}>
                  <PageHeaderToolsItem visibility={{ default: "visible" }}>
                    <Flex flexWrap={{ default: "nowrap" }} alignItems={{ default: "alignItemsCenter" }}>
                      <FlexItem style={{ minWidth: 0 }}>
                        <FileSwitcher workspace={workspace} workspaceFile={props.workspaceFile} />
                      </FlexItem>
                      <FlexItem>
                        {(isEdited && (
                          <Tooltip content={"Saving in memory..."} position={"bottom"}>
                            <TextContent
                              style={{ color: "gray", ...(!props.workspaceFile ? { visibility: "hidden" } : {}) }}
                            >
                              <Text
                                style={{ display: "flex" }}
                                aria-label={"Saving in memory..."}
                                data-testid="is-saving-in-memory-indicator"
                                component={TextVariants.small}
                              >
                                <OutlinedClockIcon size={"sm"} style={{ margin: 0 }} />
                              </Text>
                            </TextContent>
                          </Tooltip>
                        )) || (
                          <Tooltip content={"File is in memory."} position={"bottom"}>
                            <TextContent
                              style={{ color: "gray", ...(!props.workspaceFile ? { visibility: "hidden" } : {}) }}
                            >
                              <Text
                                style={{ display: "flex" }}
                                aria-label={"File is in memory."}
                                data-testid="is-saved-in-memory-indicator"
                                component={TextVariants.small}
                              >
                                <DesktopIcon size={"sm"} style={{ margin: 0 }} />
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
                                style={{ display: "flex" }}
                                aria-label={"Writing file..."}
                                data-testid="is-writing-indicator"
                                component={TextVariants.small}
                              >
                                <OutlinedClockIcon size={"sm"} style={{ margin: 0 }} />
                              </Text>
                            </TextContent>
                          </Tooltip>
                        )) || (
                          <Tooltip content={"File is written on disk."} position={"bottom"}>
                            <TextContent
                              style={{ color: "gray", ...(!props.workspaceFile ? { visibility: "hidden" } : {}) }}
                            >
                              <Text
                                style={{ display: "flex" }}
                                aria-label={"File is written on disk."}
                                data-testid="is-written-indicator"
                                component={TextVariants.small}
                              >
                                <OutlinedHddIcon size={"sm"} style={{ margin: 0 }} />
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
                      <ToolbarGroup>
                        <ToolbarItem>
                          <ResponsiveDropdown
                            title={"Add file"}
                            onClose={() => setNewFileDropdownMenuOpen(false)}
                            position={"right"}
                            isOpen={isNewFileDropdownMenuOpen}
                            toggle={
                              <ResponsiveDropdownToggle
                                onToggle={() => setNewFileDropdownMenuOpen((prev) => !prev)}
                                isPrimary={true}
                                toggleIndicator={CaretDownIcon}
                              >
                                <PlusIcon />
                                &nbsp;&nbsp;New file
                              </ResponsiveDropdownToggle>
                            }
                          >
                            <NewFileDropdownMenu
                              alerts={props.alerts}
                              workspaceDescriptor={workspace.descriptor}
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
                          </ResponsiveDropdown>
                        </ToolbarItem>
                        <ToolbarItem visibility={hideWhenSmall}>
                          {props.workspaceFile.extension === "dmn" && (
                            <ToolbarGroup>
                              <KieSandboxExtendedServicesButtons
                                workspace={workspace}
                                workspaceFile={props.workspaceFile}
                                editorPageDock={props.editorPageDock}
                              />
                            </ToolbarGroup>
                          )}
                        </ToolbarItem>
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
                                  <Tooltip
                                    data-testid={"gist-it-tooltip"}
                                    content={<div>{i18n.editorToolbar.cantUpdateGistTooltip}</div>}
                                    trigger={!canUpdateGitHubGist ? "mouseenter click" : ""}
                                    position="left"
                                  >
                                    <>
                                      <DropdownItem
                                        style={{ minWidth: "300px" }}
                                        icon={<GithubIcon />}
                                        onClick={updateGitHubGist}
                                        isDisabled={!canUpdateGitHubGist}
                                      >
                                        Update Gist
                                      </DropdownItem>
                                      {canForkGitHubGist && (
                                        <>
                                          <Divider />
                                          <li role="menuitem">
                                            <Alert
                                              isInline={true}
                                              variant={"default"}
                                              title={
                                                <span style={{ whiteSpace: "nowrap" }}>
                                                  {"Can't update Gists you don't own"}
                                                </span>
                                              }
                                            >
                                              <br />
                                              {`You can create a fork of '${workspace.descriptor.name}' to save your updates.`}
                                              <br />
                                              <br />
                                              <Button
                                                onClick={forkGitHubGist}
                                                variant={ButtonVariant.link}
                                                isSmall={true}
                                                style={{ paddingLeft: 0 }}
                                              >
                                                {`Fork Gist`}
                                              </Button>
                                              <br />
                                              <br />
                                              {`Or you can change the authentication source for '${workspace.descriptor.name}' to be able to Update Gist.`}
                                              <br />
                                              <br />
                                              <AuthSessionSelect
                                                title={`Select Git authentication for '${workspace.descriptor.name}'...`}
                                                isPlain={false}
                                                authSessionId={workspace.descriptor.gitAuthSessionId}
                                                setAuthSessionId={(newAuthSessionId) => {
                                                  changeGitAuthSessionId(
                                                    newAuthSessionId,
                                                    workspace.descriptor.gitAuthSessionId
                                                  );
                                                  accountsDispatch({ kind: AccountsDispatchActionKind.CLOSE });
                                                  setTimeout(() => {
                                                    setSyncGitHubGistDropdownOpen(true);
                                                  }, 0);
                                                }}
                                                filter={authSessionSelectFilter}
                                              />
                                            </Alert>
                                          </li>
                                        </>
                                      )}
                                      {!canPushToGitRepository && (
                                        <>
                                          <Divider />
                                          <Alert
                                            isInline={true}
                                            variant={"default"}
                                            title={"Can't Update Gist without selecting an authentication source"}
                                            actionLinks={
                                              <AuthSessionSelect
                                                title={`Select Git authentication for '${workspace.descriptor.name}'...`}
                                                isPlain={false}
                                                authSessionId={workspace.descriptor.gitAuthSessionId}
                                                setAuthSessionId={(newAuthSessionId) => {
                                                  changeGitAuthSessionId(
                                                    newAuthSessionId,
                                                    workspace.descriptor.gitAuthSessionId
                                                  );
                                                  accountsDispatch({ kind: AccountsDispatchActionKind.CLOSE });
                                                  setTimeout(() => {
                                                    setSyncGitHubGistDropdownOpen(true);
                                                  }, 0);
                                                }}
                                                filter={authSessionSelectFilter}
                                              />
                                            }
                                          >
                                            {`Select an authentication source for '${workspace.descriptor.name}' to be able to Update Gist.`}
                                          </Alert>
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
                                      <div>{`You need to select an authentication source to Push to this repository.`}</div>
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
                                          <Alert
                                            isInline={true}
                                            variant={"default"}
                                            title={"Can't Push without selecting an authentication source"}
                                            actionLinks={
                                              <AuthSessionSelect
                                                title={`Select Git authentication for '${workspace.descriptor.name}'...`}
                                                isPlain={false}
                                                authSessionId={workspace.descriptor.gitAuthSessionId}
                                                setAuthSessionId={(newAuthSessionId) => {
                                                  changeGitAuthSessionId(
                                                    newAuthSessionId,
                                                    workspace.descriptor.gitAuthSessionId
                                                  );
                                                  accountsDispatch({ kind: AccountsDispatchActionKind.CLOSE });
                                                  setTimeout(() => {
                                                    setSyncGitRepositoryDropdownOpen(true);
                                                  });
                                                }}
                                                filter={authSessionSelectFilter}
                                              />
                                            }
                                          >
                                            {`Select an authentication source for '${workspace.descriptor.name}' to be able to Push.`}
                                          </Alert>
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
                              ...(props.workspaceFile.extension !== "dmn"
                                ? []
                                : [
                                    <Divider key={"divider-2"} />,
                                    <KieSandboxExtendedServicesDropdownGroup
                                      workspace={workspace}
                                      key="kie-sandbox-extended-services-group"
                                    />,
                                  ]),
                            ]}
                          />
                        </ToolbarItem>
                      </ToolbarGroup>
                    </ToolbarContent>
                  </Toolbar>
                </FlexItem>
              </Flex>
            </PageSection>
            <EmbedModal
              workspace={workspace.descriptor}
              workspaceFile={props.workspaceFile}
              isOpen={isEmbedModalOpen}
              onClose={() => setEmbedModalOpen(false)}
            />
            <CreateGitHubRepositoryModal
              workspace={workspace.descriptor}
              isOpen={isCreateGitHubRepositoryModalOpen}
              onClose={() => setCreateGitHubRepositoryModalOpen(false)}
              onSuccess={({ url }) => {
                createRepositorySuccessAlert.show({ url });
              }}
            />
            <textarea ref={copyContentTextArea} style={{ height: 0, position: "absolute", zIndex: -1 }} />
            <a ref={downloadRef} />
            <a ref={downloadAllRef} />
            <a ref={downloadPreviewRef} />
          </>
        );
      }}
    />
  );
}

export function PushToGitHubAlertActionLinks(props: {
  onPush: () => void;
  canPush?: boolean;
  remoteRef?: string;
  workspaceDescriptor: WorkspaceDescriptor | undefined;
  changeGitAuthSessionId: (a: React.SetStateAction<string | undefined>, b: string | undefined) => void;
  authSessionSelectFilter: AuthSessionSelectFilter;
}) {
  const accountsDispatch = useAccountsDispatch();
  if (props.workspaceDescriptor?.origin.kind === WorkspaceKind.GIT && !props.remoteRef) {
    throw new Error("Should specify remoteRef for GIT workspaces");
  }

  const pushButton = useMemo(
    () => (
      <AlertActionLink onClick={props.onPush} style={{ fontWeight: "bold" }} isDisabled={!props.canPush}>
        {props.workspaceDescriptor?.origin.kind === WorkspaceKind.GIT ? `Push to '${props.remoteRef}'` : `Update Gist`}
      </AlertActionLink>
    ),
    [props]
  );

  return (
    <>
      {!props.canPush && (
        <Alert
          isInline={true}
          variant={"default"}
          title={"Can't Push without selecting an authentication source"}
          actionLinks={
            <>
              <AuthSessionSelect
                title={`Select Git authentication for '${props.workspaceDescriptor?.name}'...`}
                position={SelectPosition.right}
                isPlain={false}
                authSessionId={props.workspaceDescriptor?.gitAuthSessionId}
                setAuthSessionId={(newAuthSessionId) => {
                  props.changeGitAuthSessionId(newAuthSessionId, props.workspaceDescriptor?.gitAuthSessionId);
                  accountsDispatch({ kind: AccountsDispatchActionKind.CLOSE });
                }}
                filter={props.authSessionSelectFilter}
              />
              <br />
              <br />
              {pushButton}
            </>
          }
        >
          {`Select an authentication source for '${props.workspaceDescriptor?.name}' to be able to Push.`}
          <br />
        </Alert>
      )}
      {props.canPush && pushButton}
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
