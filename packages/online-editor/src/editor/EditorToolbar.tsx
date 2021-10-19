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
  KebabToggle,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Breadcrumb, BreadcrumbItem } from "@patternfly/react-core/dist/js/components/Breadcrumb";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { EllipsisVIcon } from "@patternfly/react-icons/dist/js/icons/ellipsis-v-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useOnlineI18n } from "../common/i18n";
import { KieToolingExtendedServicesButtons } from "./KieToolingExtendedServices/KieToolingExtendedServicesButtons";
import { KieToolingExtendedServicesDropdownGroup } from "./KieToolingExtendedServices/KieToolingExtendedServicesDropdownGroup";
import { useGlobals } from "../common/GlobalContext";
import { AuthStatus, useSettings } from "../settings/SettingsContext";
import { SettingsTabs } from "../settings/SettingsModalBody";
import { EmbeddedEditorRef, useDirtyState } from "@kie-tooling-core/editor/dist/embedded";
import { UpdateGistErrors } from "../settings/GithubService";
import { QueryParams } from "../common/Routes";
import { useHistory } from "react-router";
import { useQueryParams } from "../queryParams/QueryParamsContext";
import { EmbedModal } from "./EmbedModal";
import { Alerts, AlertsController, useAlert } from "./Alerts/Alerts";
import { Alert, AlertActionCloseButton, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import { useWorkspaces, WorkspaceFile } from "../workspace/WorkspacesContext";
import { SecurityIcon } from "@patternfly/react-icons/dist/js/icons/security-icon";
import { SyncIcon } from "@patternfly/react-icons/dist/js/icons/sync-icon";
import { CopyIcon } from "@patternfly/react-icons/dist/js/icons/copy-icon";
import { FolderIcon } from "@patternfly/react-icons/dist/js/icons/folder-icon";
import { ImageIcon } from "@patternfly/react-icons/dist/js/icons/image-icon";
import { DownloadIcon } from "@patternfly/react-icons/dist/js/icons/download-icon";
import { PlusIcon } from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { ColumnsIcon } from "@patternfly/react-icons/dist/js/icons/columns-icon";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { AddFileDropdownItems } from "./AddFileDropdownItems";
import {
  PageHeaderToolsItem,
  PageHeaderToolsItemProps,
  PageSection,
} from "@patternfly/react-core/dist/js/components/Page";
import { FileLabel } from "../workspace/pages/FileLabel";
import { DeleteDropdownWithConfirmation } from "./DeleteDropdownWithConfirmation";
import { useWorkspaceIsModifiedPromise, useWorkspacePromise } from "../workspace/hooks/WorkspaceHooks";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { WorkspaceFileNameDropdown } from "./WorkspaceFileNameDropdown";

export interface Props {
  alerts: AlertsController | undefined;
  alertsRef: (controller: AlertsController) => void;
  editor: EmbeddedEditorRef | undefined;
  workspaceFile: WorkspaceFile;
}

const showWhenSmall: PageHeaderToolsItemProps["visibility"] = {
  default: "visible",
  "2xl": "hidden",
  xl: "hidden",
  lg: "visible",
  md: "visible",
  sm: "visible",
};

const hideWhenSmall: PageHeaderToolsItemProps["visibility"] = {
  default: "hidden",
  "2xl": "visible",
  xl: "visible",
  lg: "hidden",
  md: "hidden",
  sm: "hidden",
};

export function EditorToolbar(props: Props) {
  const globals = useGlobals();
  const settings = useSettings();
  const history = useHistory();
  const queryParams = useQueryParams();
  const workspaces = useWorkspaces();
  const [isShareMenuOpen, setShareMenuOpen] = useState(false);
  const [isKebabOpen, setKebabOpen] = useState(false);
  const [isEmbedModalOpen, setEmbedModalOpen] = useState(false);
  const { i18n } = useOnlineI18n();
  const isEdited = useDirtyState(props.editor);
  const downloadRef = useRef<HTMLAnchorElement>(null);
  const downloadAllRef = useRef<HTMLAnchorElement>(null);
  const downloadPreviewRef = useRef<HTMLAnchorElement>(null);
  const copyContentTextArea = useRef<HTMLTextAreaElement>(null);
  const [isWorkspaceAddFileMenuOpen, setWorkspaceAddFileMenuOpen] = useState(false);
  const workspacePromise = useWorkspacePromise(props.workspaceFile.workspaceId);

  const copySuccessfulAlert = useAlert(
    props.alerts,
    useCallback(
      ({ close }) => (
        <Alert
          variant="success"
          title={i18n.editorPage.alerts.copy}
          actionClose={
            <>
              <AlertActionCloseButton onClose={close} />
            </>
          }
        />
      ),
      [i18n]
    ),
    useMemo(() => ({ durationInSeconds: 4 }), [])
  );

  const successUpdateGistAlert = useAlert(
    props.alerts,
    useCallback(
      ({ close }) => (
        <Alert
          variant="success"
          title={i18n.editorPage.alerts.updateGist}
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      ),
      [i18n]
    )
  );
  const successCreateGistAlert = useAlert(
    props.alerts,
    useCallback(
      ({ close }) => (
        <Alert
          variant="success"
          title={i18n.editorPage.alerts.createGist}
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      ),
      [i18n]
    )
  );

  const invalidCurrentGistAlert = useAlert(
    props.alerts,
    useCallback(
      ({ close }) => (
        <Alert
          variant="danger"
          title={i18n.editorPage.alerts.invalidCurrentGist}
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      ),
      [i18n]
    )
  );

  const invalidGistFilenameAlert = useAlert(
    props.alerts,
    useCallback(
      ({ close }) => (
        <Alert
          variant="danger"
          title={i18n.editorPage.alerts.invalidGistFilename}
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      ),
      [i18n]
    )
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

  const queryParamUrl = useMemo(() => {
    return queryParams.get(QueryParams.URL);
  }, [queryParams]);

  const includeDownloadSVGDropdownItem = useMemo(() => {
    return props.workspaceFile.extension.toLowerCase() !== "pmml";
  }, [props.workspaceFile]);

  const includeEmbedDropdownItem = useMemo(() => {
    return includeDownloadSVGDropdownItem;
  }, [includeDownloadSVGDropdownItem]);

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

  const onDownloadAll = useCallback(async () => {
    if (!props.editor) {
      return;
    }

    const fs = await workspaces.fsService.getWorkspaceFs(props.workspaceFile.workspaceId);
    const zipBlob = await workspaces.prepareZip({ fs, workspaceId: props.workspaceFile.workspaceId });
    if (downloadAllRef.current) {
      downloadAllRef.current.href = URL.createObjectURL(zipBlob);
      downloadAllRef.current.click();
    }
    await workspaces.createSavePoint({ fs, workspaceId: props.workspaceFile.workspaceId });
  }, [props.editor, props.workspaceFile, workspaces]);

  const createSavePoint = useCallback(() => {
    return workspaces.createSavePoint({
      fs: workspaces.fsService.getWorkspaceFs(props.workspaceFile.workspaceId),
      workspaceId: props.workspaceFile.workspaceId,
    });
  }, [workspaces, props.workspaceFile]);

  const onPreview = useCallback(() => {
    props.editor?.getPreview().then((previewSvg) => {
      if (downloadPreviewRef.current && previewSvg) {
        const fileBlob = new Blob([previewSvg], { type: "image/svg+xml" });
        downloadPreviewRef.current.href = URL.createObjectURL(fileBlob);
        downloadPreviewRef.current.click();
      }
    });
  }, [props.editor]);

  const onGistIt = useCallback(async () => {
    if (props.editor) {
      const content = await props.workspaceFile.getFileContentsAsString();

      // update gist
      if (queryParamUrl && settings.github.service.isGist(queryParamUrl)) {
        const userLogin = settings.github.service.extractUserLoginFromFileUrl(queryParamUrl);
        if (userLogin === settings.github.user?.login) {
          try {
            const filename = props.workspaceFile.name;
            const response = await settings.github.service.updateGist(settings.github.octokit, {
              filename,
              content,
            });

            if (response === UpdateGistErrors.INVALID_CURRENT_GIST) {
              invalidCurrentGistAlert.show();
              return;
            }

            if (response === UpdateGistErrors.INVALID_GIST_FILENAME) {
              invalidGistFilenameAlert.show();
              return;
            }

            props.editor.getStateControl().setSavedCommand();
            if (filename !== settings.github.service.getCurrentGist()?.filename) {
              successUpdateGistAlert.show();
              history.push({
                pathname: globals.routes.editor.path({ extension: props.workspaceFile.extension }),
                search: globals.routes.editor.queryString({ url: response }).toString(),
              });
              return;
            }

            successUpdateGistAlert.show();
            return;
          } catch (err) {
            console.error(err);
            errorAlert.show();
            return;
          }
        }
      }

      // create gist
      try {
        const newGistUrl = await settings.github.service.createGist(settings.github.octokit, {
          filename: props.workspaceFile.name,
          content: content,
          description: props.workspaceFile.name,
          isPublic: true,
        });

        successCreateGistAlert.show();

        history.push({
          pathname: globals.routes.editor.path({ extension: props.workspaceFile.extension }),
          search: globals.routes.editor.queryString({ url: newGistUrl }).toString(),
        });
        return;
      } catch (err) {
        console.error(err);
        errorAlert.show();
        return;
      }
    }
  }, [
    errorAlert,
    successCreateGistAlert,
    successUpdateGistAlert,
    invalidCurrentGistAlert,
    invalidGistFilenameAlert,
    props.workspaceFile,
    history,
    globals,
    settings,
    queryParamUrl,
    props.editor,
  ]);

  const onEmbed = useCallback(() => {
    setEmbedModalOpen(true);
  }, []);

  const onCopyContentToClipboard = useCallback(() => {
    props.workspaceFile.getFileContentsAsString().then((content) => {
      if (copyContentTextArea.current) {
        copyContentTextArea.current.value = content;
        copyContentTextArea.current.select();
        if (document.execCommand("copy")) {
          copySuccessfulAlert.show();
        }
      }
    });
  }, [props.workspaceFile, copySuccessfulAlert]);

  const shareItems = useCallback(
    (dropdownId: string) => [
      <DropdownGroup key={"download-group"} label="Download">
        <DropdownItem
          onClick={onDownload}
          key={"donwload-file-item"}
          description={`${props.workspaceFile.name} will be downloaded`}
          icon={<DownloadIcon />}
        >
          Current file
        </DropdownItem>
        <React.Fragment key={`dropdown-${dropdownId}-fragment-download-svg`}>
          {includeDownloadSVGDropdownItem && (
            <DropdownItem
              key={`dropdown-${dropdownId}-download-svg`}
              data-testid="dropdown-download-svg"
              component="button"
              onClick={onPreview}
              description={`Image of ${props.workspaceFile.name} will be downloaded in SVG format`}
              icon={<ImageIcon />}
            >
              {"Current file's SVG"}
            </DropdownItem>
          )}
        </React.Fragment>
        <React.Fragment key={`dropdown-${dropdownId}-fragment-download-all`}>
          <DropdownItem
            onClick={onDownloadAll}
            key={"download-zip-item"}
            description={`A zip file including all files will be downloaded`}
            icon={<FolderIcon />}
          >
            All files
          </DropdownItem>
        </React.Fragment>
        <React.Fragment key={`dropdown-${dropdownId}-fragment-create-save-point`}>
          <DropdownItem onClick={createSavePoint} key={"create-save-point"}>
            Create Save point
          </DropdownItem>
        </React.Fragment>
      </DropdownGroup>,
      <DropdownGroup key={"other-group"} label="Other">
        <DropdownItem
          key={`dropdown-${dropdownId}-copy-source`}
          component={"button"}
          onClick={onCopyContentToClipboard}
          icon={<CopyIcon />}
        >
          {i18n.editorToolbar.copySource}
        </DropdownItem>
        <React.Fragment key={`dropdown-${dropdownId}-fragment-embed`}>
          {includeEmbedDropdownItem && (
            <DropdownItem
              key={`dropdown-${dropdownId}-embed`}
              data-testid="dropdown-embed"
              component="button"
              onClick={onEmbed}
              icon={<ColumnsIcon />}
            >
              {i18n.editorToolbar.embed}
            </DropdownItem>
          )}
        </React.Fragment>
      </DropdownGroup>,
      <DropdownGroup key={"github-group"} label={i18n.names.github}>
        <React.Fragment key={`dropdown-${dropdownId}-fragment-export-gist`}>
          <Tooltip
            data-testid={"gist-it-tooltip"}
            key={`dropdown-${dropdownId}-export-gist`}
            content={<div>{i18n.editorToolbar.gistItTooltip}</div>}
            trigger={settings.github.authStatus !== AuthStatus.SIGNED_IN ? "mouseenter click" : ""}
            position="left"
          >
            <DropdownItem
              data-testid={"gist-it-button"}
              component="button"
              onClick={onGistIt}
              isDisabled={settings.github.authStatus !== AuthStatus.SIGNED_IN}
            >
              {i18n.editorToolbar.gistIt}
            </DropdownItem>
          </Tooltip>
        </React.Fragment>
        <DropdownItem
          data-testid={"set-github-token"}
          key={`dropdown-${dropdownId}-setup-github-token`}
          component="button"
          onClick={() => settings.open(SettingsTabs.GITHUB)}
        >
          {i18n.editorToolbar.setGitHubToken}
        </DropdownItem>
      </DropdownGroup>,
    ],
    [
      onDownload,
      onDownloadAll,
      props.workspaceFile,
      onCopyContentToClipboard,
      onPreview,
      onEmbed,
      onGistIt,
      includeDownloadSVGDropdownItem,
      includeEmbedDropdownItem,
      i18n,
      settings,
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
      downloadPreviewRef.current.download = `${props.workspaceFile.nameWithoutExtension}-svg.svg`;
    }
  }, [props.workspaceFile, workspacePromise.data]);

  const deleteWorkspaceFile = useCallback(() => {
    if (!workspacePromise.data) {
      return;
    }

    if (workspacePromise.data.files.length === 1) {
      workspaces
        .deleteWorkspace({ workspaceId: props.workspaceFile.workspaceId })
        .then(() => history.push({ pathname: globals.routes.home.path({}) }));
      return;
    }

    const nextFile = workspacePromise.data.files
      .filter(
        (f) =>
          f.relativePath !== props.workspaceFile.relativePath &&
          Array.from(globals.editorEnvelopeLocator.mapping.keys()).includes(f.extension)
      )
      .pop();
    if (!nextFile) {
      history.push({ pathname: globals.routes.home.path({}) });
      return;
    }

    workspaces
      .deleteFile({
        fs: workspaces.fsService.getWorkspaceFs(props.workspaceFile.workspaceId),
        file: props.workspaceFile,
      })
      .then(() =>
        history.push({
          pathname: globals.routes.workspaceWithFilePath.path({
            workspaceId: nextFile.workspaceId,
            fileRelativePath: nextFile.relativePathWithoutExtension,
            extension: nextFile.extension,
          }),
        })
      );
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

      await workspaces.renameWorkspace({ workspaceId: workspacePromise.data.descriptor.workspaceId, newName });
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

  const workspaceIsModifiedPromise = useWorkspaceIsModifiedPromise(workspacePromise.data);

  return (
    <>
      <Alerts ref={props.alertsRef} />
      <PageSection type={"nav"} variant={"light"} padding={{ default: "noPadding" }}>
        {workspacePromise.data && workspacePromise.data.files.length > 1 && (
          <Flex justifyContent={{ default: "justifyContentFlexStart" }}>
            <FlexItem className={"kogito-tooling--masthead-hoverable"}>
              <Breadcrumb>
                <BreadcrumbItem component="button" />
                <BreadcrumbItem component="button">
                  <div data-testid={"toolbar-title-workspace"} className={"kogito--editor__toolbar-name-container"}>
                    <Title
                      aria-label={"EmbeddedEditorFile name"}
                      headingLevel={"h3"}
                      size={"md"}
                      style={{ fontStyle: "italic" }}
                    >
                      {workspacePromise.data.descriptor.name}
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

                  {workspaceIsModifiedPromise.data && (
                    <Title
                      headingLevel={"h6"}
                      style={{ display: "inline", padding: "10px", cursor: "default", color: "gray" }}
                    >
                      <Tooltip content={"There are new changes since your last download."} position={"right"}>
                        <small>
                          <SecurityIcon />
                        </small>
                      </Tooltip>
                    </Title>
                  )}
                </BreadcrumbItem>
              </Breadcrumb>
            </FlexItem>
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
                  {workspacePromise.data && (
                    <WorkspaceFileNameDropdown workspace={workspacePromise.data} workspaceFile={props.workspaceFile} />
                  )}
                </FlexItem>
                <FlexItem>
                  {(isEdited && (
                    <Tooltip content={"Saving file..."} position={"bottom"}>
                      <TextContent style={{ color: "gray", ...(!props.workspaceFile ? { visibility: "hidden" } : {}) }}>
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
                      <TextContent style={{ color: "gray", ...(!props.workspaceFile ? { visibility: "hidden" } : {}) }}>
                        <Text
                          aria-label={"File is saved"}
                          data-testid="is-saved-indicator"
                          component={TextVariants.small}
                        >
                          <span>
                            <CheckCircleIcon size={"sm"} />
                          </span>
                          &nbsp;
                          <span>Saved</span>
                        </Text>
                      </TextContent>
                    </Tooltip>
                  )}
                </FlexItem>
              </Flex>
            </PageHeaderToolsItem>
          </FlexItem>
          <FlexItem style={{ display: "flex", alignItems: "center", marginLeft: "auto" }}>
            <FlexItem>
              <Dropdown
                className={"kogito-tooling--masthead-hoverable"}
                isPlain={true}
                isOpen={isWorkspaceAddFileMenuOpen}
                onSelect={() => setWorkspaceAddFileMenuOpen(false)}
                toggle={
                  <DropdownToggle toggleIndicator={null} onToggle={setWorkspaceAddFileMenuOpen}>
                    <PlusIcon />
                  </DropdownToggle>
                }
                dropdownItems={[
                  <AddFileDropdownItems
                    key={"new-file-dropdown-items"}
                    addEmptyWorkspaceFile={async (extension) => {
                      const file = await workspaces.addEmptyFile({
                        fs: workspaces.fsService.getWorkspaceFs(props.workspaceFile.workspaceId),
                        workspaceId: props.workspaceFile.workspaceId,
                        destinationDirRelativePath: props.workspaceFile.relativeDirPath,
                        extension,
                      });
                      history.push({
                        pathname: globals.routes.workspaceWithFilePath.path({
                          workspaceId: file.workspaceId,
                          fileRelativePath: file.relativePathWithoutExtension,
                          extension: file.extension,
                        }),
                      });
                      return file;
                    }}
                  />,
                ]}
                position={DropdownPosition.right}
              />
            </FlexItem>
            <DeleteDropdownWithConfirmation
              onDelete={deleteWorkspaceFile}
              item={
                <Flex flexWrap={{ default: "nowrap" }}>
                  <FlexItem>
                    Delete <b>{`"${props.workspaceFile.nameWithoutExtension}"`}</b>
                  </FlexItem>
                  <FlexItem>
                    <b>
                      <FileLabel extension={props.workspaceFile.extension} />
                    </b>
                  </FlexItem>
                </Flex>
              }
            />
            <>
              &nbsp;&nbsp;&nbsp;
              <PageHeaderToolsItem visibility={hideWhenSmall}>
                {props.workspaceFile.extension === "dmn" && <KieToolingExtendedServicesButtons />}
                <Dropdown
                  onSelect={() => setShareMenuOpen(false)}
                  isOpen={isShareMenuOpen}
                  dropdownItems={shareItems("lg")}
                  position={DropdownPosition.right}
                  toggle={
                    <DropdownToggle
                      id={"share-id-lg"}
                      data-testid={"share-menu"}
                      onToggle={(isOpen) => setShareMenuOpen(isOpen)}
                    >
                      {i18n.editorToolbar.share}
                    </DropdownToggle>
                  }
                />
              </PageHeaderToolsItem>
            </>
            &nbsp;&nbsp;&nbsp;
            <PageHeaderToolsItem visibility={showWhenSmall}>
              <Dropdown
                className={"kogito-tooling--masthead-hoverable"}
                isOpen={isKebabOpen}
                position={DropdownPosition.right}
                onSelect={() => setKebabOpen(false)}
                toggle={
                  <DropdownToggle
                    data-testid={"kebab-sm"}
                    className={"kogito--editor__toolbar-icon-button"}
                    id={"kebab-id-sm"}
                    toggleIndicator={null}
                    onToggle={(isOpen) => setKebabOpen(isOpen)}
                    ouiaId="small-toolbar-button"
                  >
                    <EllipsisVIcon />
                  </DropdownToggle>
                }
                dropdownItems={[
                  ...shareItems("sm"),
                  (props.workspaceFile.extension === "dmn" && (
                    <KieToolingExtendedServicesDropdownGroup key="kie-tooling-extended-services-group" />
                  )) || <React.Fragment key="kie-tooling-extended-services-group" />,
                ]}
              />
            </PageHeaderToolsItem>
            <PageHeaderToolsItem>
              <Dropdown
                className={"kogito-tooling--masthead-hoverable"}
                toggle={<KebabToggle onToggle={() => {}} />}
                isOpen={false}
                isPlain={true}
                dropdownItems={[]}
              />
            </PageHeaderToolsItem>
          </FlexItem>
        </Flex>
      </PageSection>
      <EmbedModal
        workspaceFile={props.workspaceFile}
        isOpen={isEmbedModalOpen}
        onClose={() => setEmbedModalOpen(false)}
      />
      <textarea ref={copyContentTextArea} style={{ height: 0, position: "absolute", zIndex: -1 }} />
      <a ref={downloadRef} />
      <a ref={downloadAllRef} />
      <a ref={downloadPreviewRef} />
    </>
  );
}
