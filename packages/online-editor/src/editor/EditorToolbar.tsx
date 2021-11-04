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
  ToolbarItem,
  ToolbarItemProps,
} from "@patternfly/react-core/dist/js/components/Toolbar";
import { EllipsisVIcon } from "@patternfly/react-icons/dist/js/icons/ellipsis-v-icon";
import { SaveIcon } from "@patternfly/react-icons/dist/js/icons/save-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useOnlineI18n } from "../common/i18n";
import { KieToolingExtendedServicesButtons } from "./KieToolingExtendedServices/KieToolingExtendedServicesButtons";
import { useGlobals } from "../common/GlobalContext";
import { AuthStatus, useSettings } from "../settings/SettingsContext";
import { EmbeddedEditorRef, useDirtyState } from "@kie-tooling-core/editor/dist/embedded";
import { useHistory } from "react-router";
import { EmbedModal } from "./EmbedModal";
import { Alerts, AlertsController, useAlert } from "./Alerts/Alerts";
import { Alert, AlertActionCloseButton, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import { useWorkspaces, WorkspaceFile } from "../workspace/WorkspacesContext";
import { SecurityIcon } from "@patternfly/react-icons/dist/js/icons/security-icon";
import { SyncIcon } from "@patternfly/react-icons/dist/js/icons/sync-icon";
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
import { FileLabel } from "../workspace/components/FileLabel";
import { useIsWorkspaceModifiedPromise, useWorkspacePromise } from "../workspace/hooks/WorkspaceHooks";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { FileSwitcher } from "./FileSwitcher";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { KieToolingExtendedServicesDropdownGroup } from "./KieToolingExtendedServices/KieToolingExtendedServicesDropdownGroup";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons/trash-icon";
import { CaretDownIcon } from "@patternfly/react-icons/dist/js/icons/caret-down-icon";
import { GIST_DEFAULT_BRANCH, GIST_ORIGIN_REMOTE_NAME } from "../workspace/services/GitService";
import { WorkspaceKind } from "../workspace/model/WorkspaceOrigin";
import { PromiseStateWrapper } from "../workspace/hooks/PromiseState";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { ActiveWorkspace } from "../workspace/model/ActiveWorkspace";
import { WorkspaceLabel } from "../workspace/components/WorkspaceLabel";
import { EditorPageDockDrawerController } from "./EditorPageDockDrawer";
import { SyncAltIcon } from "@patternfly/react-icons/dist/js/icons/sync-alt-icon";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";

export interface Props {
  alerts: AlertsController | undefined;
  alertsRef: (controller: AlertsController) => void;
  editor: EmbeddedEditorRef | undefined;
  workspaceFile: WorkspaceFile;
  editorPageDock: EditorPageDockDrawerController | undefined;
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

function WorkspaceStatusIndicator(props: { workspace: ActiveWorkspace }) {
  const isWorkspaceModifiedPromise = useIsWorkspaceModifiedPromise(props.workspace);

  const isModifiedText = useMemo(() => {
    switch (props.workspace.descriptor.origin.kind) {
      case WorkspaceKind.LOCAL:
        return "There are new changes since your last download.";
      case WorkspaceKind.GIST:
      case WorkspaceKind.GITHUB:
        return "There are new changes since you last synced.";
      default:
        throw new Error();
    }
  }, [props.workspace]);

  const isSyncedText = useMemo(() => {
    switch (props.workspace.descriptor.origin.kind) {
      case WorkspaceKind.LOCAL:
        return "All changes were downloaded.";
      case WorkspaceKind.GIST:
      case WorkspaceKind.GITHUB:
        return "All files are synced.";
      default:
        throw new Error();
    }
  }, [props.workspace]);

  return (
    <PromiseStateWrapper
      promise={isWorkspaceModifiedPromise}
      pending={
        <Title headingLevel={"h6"} style={{ display: "inline", padding: "10px", cursor: "default" }}>
          <Tooltip content={"Checking status..."} position={"right"}>
            <small>
              <SyncIcon color={"gray"} />
            </small>
          </Tooltip>
        </Title>
      }
      resolved={(isModified) => (
        <>
          {(isModified && (
            <Title headingLevel={"h6"} style={{ display: "inline", padding: "10px", cursor: "default" }}>
              <Tooltip content={isModifiedText} position={"right"}>
                <small>
                  <SecurityIcon color={"gray"} />
                </small>
              </Tooltip>
            </Title>
          )) || (
            <Title headingLevel={"h6"} style={{ display: "inline", padding: "10px", cursor: "default" }}>
              <Tooltip content={isSyncedText} position={"right"}>
                <small>
                  <CheckCircleIcon color={"green"} />
                </small>
              </Tooltip>
            </Title>
          )}
        </>
      )}
    />
  );
}

export function EditorToolbar(props: Props) {
  const globals = useGlobals();
  const settings = useSettings();
  const history = useHistory();
  const workspaces = useWorkspaces();
  const [isShareDropdownOpen, setShareDropdownOpen] = useState(false);
  const [isSyncGitHubGistDropdownOpen, setSyncGitHubGistDropdownOpen] = useState(false);
  const [isSyncGitHubRepositoryDropdownOpen, setSyncGitHubRepositoryDropdownOpen] = useState(false);
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

  const successfullyCreateGistAlert = useAlert(
    props.alerts,
    useCallback(
      ({ close }) => {
        if (workspacePromise.data?.descriptor.origin.kind !== WorkspaceKind.GIST) {
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
        if (workspacePromise.data?.descriptor.origin.kind !== WorkspaceKind.GIST) {
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
      [i18n, workspacePromise]
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
        if (workspacePromise.data?.descriptor.origin.kind !== WorkspaceKind.GIST) {
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

  const requestDownload = useCallback(() => {
    props.editor?.getStateControl().setSavedCommand();
    props.workspaceFile
      .getFileContents()
      .then((content) => {
        if (downloadRef.current) {
          const fileBlob = new Blob([content], { type: "text/plain" });
          downloadRef.current.href = URL.createObjectURL(fileBlob);
          downloadRef.current.click();
        }
      })
      .then(() => {
        history.push({ pathname: globals.routes.home.path({}) });
      });
  }, [props.editor, props.workspaceFile, history, globals.routes.home]);

  const closeWithoutSaving = useCallback(() => {
    history.push({ pathname: globals.routes.home.path({}) });
  }, [globals, history]);

  const unsavedAlert = useAlert(
    props.alerts,
    useCallback(
      ({ close }) => (
        <Alert
          data-testid="unsaved-alert"
          variant="warning"
          title={i18n.editorPage.alerts.unsaved.title}
          actionClose={<AlertActionCloseButton data-testid="unsaved-alert-close-button" onClose={close} />}
          actionLinks={
            <>
              <AlertActionLink data-testid="unsaved-alert-save-button" onClick={requestDownload}>
                {i18n.terms.save}
              </AlertActionLink>
              <AlertActionLink data-testid="unsaved-alert-close-without-save-button" onClick={closeWithoutSaving}>
                {i18n.editorPage.alerts.unsaved.closeWithoutSaving}
              </AlertActionLink>
            </>
          }
        >
          <p>{i18n.editorPage.alerts.unsaved.message}</p>
        </Alert>
      ),
      [i18n, requestDownload, closeWithoutSaving]
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

    const fs = await workspaces.fsService.getWorkspaceFs(props.workspaceFile.workspaceId);
    const zipBlob = await workspaces.prepareZip({ fs, workspaceId: props.workspaceFile.workspaceId });
    if (downloadAllRef.current) {
      downloadAllRef.current.href = URL.createObjectURL(zipBlob);
      downloadAllRef.current.click();
    }
    if (workspacePromise.data?.descriptor.origin.kind === WorkspaceKind.LOCAL) {
      await workspaces.createSavePoint({ fs, workspaceId: props.workspaceFile.workspaceId });
    }
  }, [props.editor, props.workspaceFile, workspaces, workspacePromise.data]);

  const downloadSvg = useCallback(() => {
    props.editor?.getPreview().then((previewSvg) => {
      if (downloadPreviewRef.current && previewSvg) {
        const fileBlob = new Blob([previewSvg], { type: "image/svg+xml" });
        downloadPreviewRef.current.href = URL.createObjectURL(fileBlob);
        downloadPreviewRef.current.click();
      }
    });
  }, [props.editor]);

  const updateGitHubGist = useCallback(async () => {
    try {
      setGitHubGistLoading(true);
      const fs = await workspaces.fsService.getWorkspaceFs(props.workspaceFile.workspaceId);

      //TODO: Check if there are new changes in the Gist before force-pushing.

      await workspaces.gitService.push({
        fs,
        dir: await workspaces.getAbsolutePath({ workspaceId: props.workspaceFile.workspaceId }),
        remote: GIST_ORIGIN_REMOTE_NAME,
        remoteRef: `refs/heads/${GIST_DEFAULT_BRANCH}`,
        force: true,
        authInfo: {
          name: settings.github.user!.name,
          email: settings.github.user!.email,
          onAuth: () => ({
            username: settings.github.user!.login,
            password: settings.github.token!,
          }),
        },
      });

      await workspaces.createSavePoint({ fs, workspaceId: props.workspaceFile.workspaceId });
    } catch (e) {
      errorAlert.show();
      throw e;
    } finally {
      setGitHubGistLoading(false);
      setSyncGitHubGistDropdownOpen(false);
    }

    successfullyUpdateGistAlert.show();
  }, [successfullyUpdateGistAlert, workspaces, props.workspaceFile, settings.github, errorAlert]);

  const createGitHubGist = useCallback(async () => {
    try {
      setGitHubGistLoading(true);
      const gist = await settings.github.octokit.gists.create({
        description: workspacePromise.data?.descriptor.name ?? "",
        public: true,

        // This file is used just for creating the Gist. The `push -f` overwrites it.
        files: {
          "README.md": {
            content: `
This Gist was created from Kogito Tooling .NEW. 

This file is temporary and you should not be seeing it. 
If you are, it means that creating this Gist failed and it can safely be deleted.
`,
          },
        },
      });

      if (!gist.data.git_push_url) {
        throw new Error("Gist creation failed.");
      }

      await workspaces.descriptorService.turnIntoGist(props.workspaceFile.workspaceId, new URL(gist.data.git_push_url));

      const fs = await workspaces.fsService.getWorkspaceFs(props.workspaceFile.workspaceId);
      const workspaceRootDirPath = await workspaces.getAbsolutePath({ workspaceId: props.workspaceFile.workspaceId });

      await workspaces.gitService.addRemote({
        fs,
        dir: workspaceRootDirPath,
        url: gist.data.git_push_url,
        name: GIST_ORIGIN_REMOTE_NAME,
        force: true,
      });

      await workspaces.gitService.branch({
        fs,
        dir: workspaceRootDirPath,
        checkout: true,
        name: GIST_DEFAULT_BRANCH,
      });

      await workspaces.createSavePoint({
        fs: fs,
        workspaceId: props.workspaceFile.workspaceId,
      });

      await workspaces.gitService.push({
        fs: fs,
        dir: workspaceRootDirPath,
        remote: GIST_ORIGIN_REMOTE_NAME,
        remoteRef: `refs/heads/${GIST_DEFAULT_BRANCH}`,
        force: true,
        authInfo: {
          name: settings.github.user!.name,
          email: settings.github.user!.email,
          onAuth: () => ({
            username: settings.github.user!.login,
            password: settings.github.token!,
          }),
        },
      });

      successfullyCreateGistAlert.show();

      return;
    } catch (err) {
      errorAlert.show();
      throw err;
    } finally {
      setGitHubGistLoading(false);
    }
  }, [props.workspaceFile, settings.github, workspacePromise, workspaces, successfullyCreateGistAlert, errorAlert]);

  const openEmbedModal = useCallback(() => {
    setEmbedModalOpen(true);
  }, []);

  const workspaceHasNestedDirectories = useMemo(
    () => workspacePromise.data?.files.filter((f) => f.relativePath !== f.name).length !== 0,
    [workspacePromise]
  );

  const canCreateGitHubRepository = useMemo(
    () =>
      settings.github.authStatus === AuthStatus.SIGNED_IN &&
      (workspacePromise.data?.descriptor.origin.kind === WorkspaceKind.LOCAL ||
        workspacePromise.data?.descriptor.origin.kind === WorkspaceKind.GIST),
    [workspacePromise, settings.github.authStatus]
  );

  const canCreateGitHubGist = useMemo(
    () =>
      settings.github.authStatus === AuthStatus.SIGNED_IN &&
      workspacePromise.data?.descriptor.origin.kind === WorkspaceKind.LOCAL &&
      !workspaceHasNestedDirectories,
    [workspacePromise, settings.github.authStatus, workspaceHasNestedDirectories]
  );

  const canUpdateGitHubGist = useMemo(
    () =>
      settings.github.authStatus === AuthStatus.SIGNED_IN &&
      workspacePromise.data?.descriptor.origin.kind === WorkspaceKind.GIST &&
      !workspaceHasNestedDirectories,
    [workspacePromise, settings.github.authStatus, workspaceHasNestedDirectories]
  );

  const shareDropdownItems = useMemo(
    () => [
      <DropdownGroup key={"download-group"} label="Download">
        <DropdownItem
          onClick={onDownload}
          key={"download-file-item"}
          description={`${props.workspaceFile.name} will be downloaded`}
          icon={<DownloadIcon />}
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
      workspacePromise.data?.descriptor.origin.kind === WorkspaceKind.GIST
        ? [
            <DropdownGroup key={"github-group"} label={i18n.names.github}>
              <Tooltip
                data-testid={"create-github-repository-tooltip"}
                key={`dropdown-create-github-repository`}
                content={<div>{`You can't create a repository because you're not authenticated with GitHub.`}</div>}
                trigger={!canCreateGitHubRepository ? "mouseenter click" : ""}
                position="left"
              >
                <DropdownItem
                  icon={<GithubIcon />}
                  data-testid={"create-github-repository-button"}
                  component="button"
                  onClick={() => {
                    console.info("Creating GitHub repo...");
                  }}
                  isDisabled={!canCreateGitHubRepository}
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
            </DropdownGroup>,
          ]
        : []),
    ],
    [
      onDownload,
      workspacePromise,
      props.workspaceFile,
      shouldIncludeDownloadSvgDropdownItem,
      downloadSvg,
      downloadWorkspaceZip,
      shouldIncludeEmbedDropdownItem,
      openEmbedModal,
      i18n,
      canCreateGitHubGist,
      canCreateGitHubRepository,
      createGitHubGist,
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
      history.push({ pathname: globals.routes.home.path({}) });
      return;
    }

    const nextFile = workspacePromise.data.files
      .filter((f) => {
        return (
          f.relativePath !== props.workspaceFile.relativePath &&
          Array.from(globals.editorEnvelopeLocator.mapping.keys()).includes(f.extension)
        );
      })
      .pop();

    if (!nextFile) {
      history.push({ pathname: globals.routes.home.path({}) });
      return;
    }

    await workspaces.deleteFile({
      fs: await workspaces.fsService.getWorkspaceFs(props.workspaceFile.workspaceId),
      file: props.workspaceFile,
    });

    history.push({
      pathname: globals.routes.workspaceWithFilePath.path({
        workspaceId: nextFile.workspaceId,
        fileRelativePath: nextFile.relativePathWithoutExtension,
        extension: nextFile.extension,
      }),
    });
  }, [globals, history, workspacePromise.data, props.workspaceFile, workspaces]);

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

  const createSavePointDropdownItem = useMemo(() => {
    return (
      <DropdownItem
        icon={<SaveIcon />}
        onClick={async () =>
          workspaces.createSavePoint({
            fs: await workspaces.fsService.getWorkspaceFs(props.workspaceFile.workspaceId),
            workspaceId: props.workspaceFile.workspaceId,
          })
        }
        description={"Create a save point"}
      >
        Commit
      </DropdownItem>
    );
  }, [workspaces, props.workspaceFile]);

  const canUpdateGitHubRepository = useMemo(() => false, []);

  const pushGitHubRepository = useCallback(() => {}, []);

  const fetchGitHubRepository = useCallback(() => {}, []);

  return (
    <PromiseStateWrapper
      promise={workspacePromise}
      resolved={(workspace) => (
        <>
          <Alerts ref={props.alertsRef} width={"500px"} />
          <PageSection type={"nav"} variant={"light"} padding={{ default: "noPadding" }}>
            {workspace && workspace.files.length > 1 && (
              <Flex justifyContent={{ default: "justifyContentSpaceBetween" }} style={{ marginLeft: "16px" }}>
                <FlexItem>
                  <WorkspaceLabel descriptor={workspace.descriptor} />
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
                    />
                  </div>
                  {workspace.descriptor.origin.kind !== WorkspaceKind.LOCAL && (
                    <WorkspaceStatusIndicator workspace={workspace} />
                  )}
                </FlexItem>
                {/*<Divider inset={{ default: "insetMd" }} isVertical={true} />*/}
                {workspace.descriptor.origin.kind === WorkspaceKind.GITHUB && (
                  <FlexItem>
                    <Toolbar style={{ padding: 0 }}>
                      <ToolbarItem>
                        <a
                          href={`https://vscode.dev/github${workspace.descriptor.origin.url.pathname}`}
                          target={"_blank"}
                        >
                          <Button
                            variant={ButtonVariant.secondary}
                            icon={
                              <img
                                style={{ width: "14px" }}
                                alt="vscode-logo-blue"
                                src={globals.routes.static.images.vscodeLogoBlue.path({})}
                              />
                            }
                          >
                            {`Open "${workspace.descriptor.name}" in vscode.dev...`}
                          </Button>
                        </a>
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
                        <Tooltip content={"Saving file..."} position={"bottom"}>
                          <TextContent
                            style={{ color: "gray", ...(!props.workspaceFile ? { visibility: "hidden" } : {}) }}
                          >
                            <Text
                              aria-label={"Saving file..."}
                              data-testid="is-saving-indicator"
                              component={TextVariants.small}
                            >
                              <span>
                                <SyncIcon size={"sm"} />
                              </span>
                              &nbsp;
                              <span>Saving...</span>
                            </Text>
                          </TextContent>
                        </Tooltip>
                      )) || (
                        <Tooltip content={"File is saved"} position={"bottom"}>
                          <TextContent
                            style={{ color: "gray", ...(!props.workspaceFile ? { visibility: "hidden" } : {}) }}
                          >
                            <Text
                              aria-label={"File is saved"}
                              data-testid="is-saved-indicator"
                              component={TextVariants.small}
                            >
                              <span>
                                <CheckCircleIcon size={"sm"} />
                              </span>
                              <ToolbarItem visibility={hideWhenTiny}>
                                &nbsp;
                                <span>Saved</span>
                              </ToolbarItem>
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
                            isPrimary={true}
                            toggleIndicator={CaretDownIcon}
                          >
                            <PlusIcon />
                            &nbsp;&nbsp;New file
                          </DropdownToggle>
                        }
                      >
                        <NewFileDropdownMenu
                          alerts={props.alerts}
                          workspaceId={props.workspaceFile.workspaceId}
                          destinationDirPath={props.workspaceFile.relativeDirPath}
                          onAddFile={async (file) => {
                            setNewFileDropdownMenuOpen(false);
                            if (!file) {
                              return;
                            }

                            history.push({
                              pathname: globals.routes.workspaceWithFilePath.path({
                                workspaceId: file.workspaceId,
                                fileRelativePath: file.relativePathWithoutExtension,
                                extension: file.extension,
                              }),
                            });
                          }}
                        />
                      </Dropdown>
                    </ToolbarItem>
                    <ToolbarItem visibility={hideWhenSmall}>
                      {props.workspaceFile.extension === "dmn" && (
                        <KieToolingExtendedServicesButtons
                          workspace={workspace}
                          editorPageDock={props.editorPageDock}
                        />
                      )}
                    </ToolbarItem>
                    {workspace.descriptor.origin.kind === WorkspaceKind.GIST && (
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
                                <DropdownItem
                                  icon={<GithubIcon />}
                                  onClick={updateGitHubGist}
                                  isDisabled={!canUpdateGitHubGist}
                                >
                                  Update Gist
                                </DropdownItem>
                              </Tooltip>
                            </DropdownGroup>,
                          ]}
                        />
                      </ToolbarItem>
                    )}
                    {workspace.descriptor.origin.kind === WorkspaceKind.GITHUB && (
                      <ToolbarItem>
                        <Dropdown
                          onSelect={() => setSyncGitHubRepositoryDropdownOpen(false)}
                          isOpen={isSyncGitHubRepositoryDropdownOpen}
                          position={DropdownPosition.right}
                          toggle={
                            <DropdownToggle
                              id={"sync-dropdown"}
                              data-testid={"sync-dropdown"}
                              onToggle={(isOpen) => setSyncGitHubRepositoryDropdownOpen(isOpen)}
                            >
                              Sync
                            </DropdownToggle>
                          }
                          dropdownItems={[
                            <DropdownGroup key={"sync-gist-dropdown-group"}>
                              <DropdownItem
                                icon={<SyncAltIcon />}
                                onClick={fetchGitHubRepository}
                                description={"Get new changes made upstream."}
                              >
                                Fetch...
                              </DropdownItem>
                              <Tooltip
                                data-testid={"gist-it-tooltip"}
                                content={<div>{``}</div>}
                                trigger={!canUpdateGitHubRepository ? "mouseenter click" : ""}
                                position="left"
                              >
                                <DropdownItem
                                  icon={<ArrowCircleUpIcon />}
                                  onClick={pushGitHubRepository}
                                  isDisabled={!canUpdateGitHubRepository}
                                  description={"Send your changes upstream."}
                                >
                                  Push
                                </DropdownItem>
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
                                <KieToolingExtendedServicesDropdownGroup
                                  workspace={workspace}
                                  key="kie-tooling-extended-services-group"
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
          <EmbedModal
            workspace={workspace.descriptor}
            workspaceFile={props.workspaceFile}
            isOpen={isEmbedModalOpen}
            onClose={() => setEmbedModalOpen(false)}
          />
          <textarea ref={copyContentTextArea} style={{ height: 0, position: "absolute", zIndex: -1 }} />
          <a ref={downloadRef} />
          <a ref={downloadAllRef} />
          <a ref={downloadPreviewRef} />
        </>
      )}
    />
  );
}

export function KebabDropdown(props: {
  id: string;
  items: React.ReactNode[];
  state: [boolean, React.Dispatch<React.SetStateAction<boolean>>];
}) {
  return (
    <Dropdown
      className={"kogito-tooling--masthead-hoverable"}
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
