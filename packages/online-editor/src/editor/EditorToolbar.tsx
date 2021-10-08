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

import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import {
  Dropdown,
  DropdownGroup,
  DropdownItem,
  DropdownPosition,
  DropdownToggle,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import { Toggle } from "@patternfly/react-core/dist/js/components/Dropdown/Toggle";
import { EyeIcon } from "@patternfly/react-icons/dist/js/icons/eye-icon";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { EllipsisVIcon } from "@patternfly/react-icons/dist/js/icons/ellipsis-v-icon";
import { CaretDownIcon } from "@patternfly/react-icons/dist/js/icons/caret-down-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useOnlineI18n } from "../common/i18n";
import { SettingsButton } from "../settings/SettingsButton";
import { KieToolingExtendedServicesButtons } from "./KieToolingExtendedServices/KieToolingExtendedServicesButtons";
import { KieToolingExtendedServicesDropdownGroup } from "./KieToolingExtendedServices/KieToolingExtendedServicesDropdownGroup";
import { useGlobals } from "../common/GlobalContext";
import { AuthStatus, useSettings } from "../settings/SettingsContext";
import { SettingsTabs } from "../settings/SettingsModalBody";
import { EmbeddedEditorRef, useDirtyState } from "@kie-tooling-core/editor/dist/embedded";
import { UpdateGistErrors } from "../settings/GithubService";
import { QueryParams } from "../common/Routes";
import { useHistory } from "react-router";
import { Link } from "react-router-dom";
import { useQueryParams } from "../queryParams/QueryParamsContext";
import { EmbedModal } from "./EmbedModal";
import { AlertsController, useAlert } from "./Alerts/Alerts";
import { Alert, AlertActionCloseButton, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import { useWorkspaces, WorkspaceFile } from "../workspace/WorkspacesContext";
import { dirname, join } from "path";
import { Masthead, MastheadBrand, MastheadMain } from "@patternfly/react-core/dist/js/components/Masthead";
import { CheckIcon } from "@patternfly/react-icons/dist/js/icons/check-icon";
import { CopyIcon } from "@patternfly/react-icons/dist/js/icons/copy-icon";
import { FolderIcon } from "@patternfly/react-icons/dist/js/icons/folder-icon";
import { ImageIcon } from "@patternfly/react-icons/dist/js/icons/image-icon";
import { DownloadIcon } from "@patternfly/react-icons/dist/js/icons/download-icon";
import { PlusIcon } from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { ColumnsIcon } from "@patternfly/react-icons/dist/js/icons/columns-icon";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { ActiveWorkspace } from "../workspace/model/ActiveWorkspace";
import { SUPPORTED_FILES_EDITABLE } from "../workspace/SupportedFiles";
import { AddFileDropdownItems } from "./AddFileDropdownItems";
import { PageHeaderToolsItem, PageHeaderToolsItemProps } from "@patternfly/react-core/dist/js/components/Page";
import { FileLabel } from "../workspace/pages/FileLabel";
import { DeleteDropdownWithConfirmation } from "./DeleteDropdownWithConfirmation";

export interface Props {
  alerts: AlertsController | undefined;
  editor: EmbeddedEditorRef | undefined;
  workspace: ActiveWorkspace;
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

  const onClose = useCallback(() => {
    if (isEdited) {
      unsavedAlert.show();
      return;
    }

    history.push({ pathname: globals.routes.home.path({}) });
  }, [unsavedAlert, globals, history, isEdited]);

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

    const zipBlob = await workspaces.prepareZip(props.workspace.descriptor.workspaceId);
    if (downloadAllRef.current) {
      downloadAllRef.current.href = URL.createObjectURL(zipBlob);
      downloadAllRef.current.click();
    }
  }, [props.editor, props.workspace, workspaces]);

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
      const content = await props.workspaceFile.getFileContents();

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
    props.workspaceFile.getFileContents().then((content) => {
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
          {props.workspace && (
            <DropdownItem
              onClick={onDownloadAll}
              key={"download-zip-item"}
              description={`A zip file including all files will be downloaded`}
              icon={<FolderIcon />}
            >
              All files
            </DropdownItem>
          )}
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
      props.workspace,
      props.workspaceFile,
      onCopyContentToClipboard,
      onPreview,
      onEmbed,
      onGistIt,
      includeDownloadSVGDropdownItem,
      includeEmbedDropdownItem,
      i18n,
      settings,
      globals,
    ]
  );

  useEffect(() => {
    if (downloadRef.current) {
      downloadRef.current.download = `${props.workspaceFile.name}`;
    }
    if (downloadAllRef.current) {
      downloadAllRef.current.download = `${props.workspace.descriptor.name}.zip`;
    }
    if (downloadPreviewRef.current) {
      downloadPreviewRef.current.download = `${props.workspaceFile.nameWithoutExtension}-svg.svg`;
    }
  }, [props.workspaceFile, props.workspace]);

  const deleteWorkspaceFile = useCallback(() => {
    if (props.workspace.files.length === 1) {
      workspaces.workspaceService
        .delete(props.workspace.descriptor.workspaceId, { broadcast: true })
        .then(() => history.push({ pathname: globals.routes.home.path({}) }));
      return;
    }

    const nextFile = props.workspace.files
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

    workspaces.workspaceService.deleteFile(props.workspaceFile, { broadcast: true }).then(() =>
      history.push({
        pathname: globals.routes.workspaceWithFilePath.path({
          workspaceId: nextFile.workspaceId,
          filePath: nextFile.relativePathWithoutExtension,
          extension: nextFile.extension,
        }),
      })
    );
  }, [globals, history, props.workspace, props.workspaceFile, workspaces.workspaceService]);

  return (
    <>
      <Masthead aria-label={"Page header"}>
        <MastheadMain>
          <PageHeaderToolsItem visibility={{ ...hideWhenSmall, sm: "visible", default: "visible" }}>
            <MastheadBrand>
              <Brand
                onClick={onClose}
                src={globals.routes.static.images.editorLogo.path({ type: props.workspaceFile.extension ?? "dmn" })}
                alt={`${props.workspaceFile.extension} kogito logo`}
              />
            </MastheadBrand>
          </PageHeaderToolsItem>
        </MastheadMain>
        <Flex
          justifyContent={{ default: "justifyContentSpaceBetween" }}
          alignItems={{ default: "alignItemsCenter" }}
          flexWrap={{ default: "nowrap" }}
        >
          <FlexItem style={{ marginRight: "auto" }} />
          <FlexItem>
            <PageHeaderToolsItem visibility={{ default: "visible" }}>
              <Flex flexWrap={{ default: "nowrap" }}>
                <FlexItem style={{ width: "60px" }}>
                  <TextContent>
                    <Text
                      style={{ color: "gray", ...(!props.workspaceFile ? { visibility: "hidden" } : {}) }}
                      component={"small"}
                      aria-label={"File is saved"}
                      data-testid="is-saved-indicator"
                    >
                      {isEdited ? (
                        <>{`Edited`}</>
                      ) : (
                        <>
                          {`Saved`} <CheckIcon size={"sm"} />
                        </>
                      )}
                    </Text>
                  </TextContent>
                </FlexItem>
                <FlexItem>
                  <WorkspaceAndWorkspaceFileNames workspace={props.workspace} workspaceFile={props.workspaceFile} />
                </FlexItem>
                <FlexItem>
                  <Dropdown
                    className={"kogito-tooling--masthead-hoverable"}
                    isFullHeight={true}
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
                            workspaceId: props.workspace.descriptor.workspaceId,
                            destinationDirRelativePath: props.workspaceFile.relativeDirPath,
                            extension,
                          });
                          history.push({
                            pathname: globals.routes.workspaceWithFilePath.path({
                              workspaceId: file.workspaceId,
                              filePath: file.relativePathWithoutExtension,
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
              </Flex>
            </PageHeaderToolsItem>
          </FlexItem>

          <FlexItem style={{ display: "flex", alignItems: "center", marginLeft: "auto" }}>
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
                {props.workspaceFile.extension === "dmn" && (
                  <>
                    <KieToolingExtendedServicesButtons />
                  </>
                )}
                &nbsp;&nbsp;&nbsp;
                <Dropdown
                  onSelect={() => setShareMenuOpen(false)}
                  isFullHeight={true}
                  isPlain={true}
                  style={{ marginRight: "2px" }}
                  className={"kogito--editor__toolbar dropdown"}
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
                isPlain={true}
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
              <SettingsButton />
            </PageHeaderToolsItem>
          </FlexItem>
        </Flex>
      </Masthead>
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

function WorkspaceAndWorkspaceFileNames(props: { workspace: ActiveWorkspace; workspaceFile: WorkspaceFile }) {
  const workspaces = useWorkspaces();
  const globals = useGlobals();
  const history = useHistory();
  const workspaceNameRef = useRef<HTMLInputElement>(null);
  const workspaceFileNameRef = useRef<HTMLInputElement>(null);
  const [newFileNameValid, setNewFileNameValid] = useState<boolean>(true);

  const resetWorkspaceFileName = useCallback(() => {
    if (workspaceFileNameRef.current) {
      workspaceFileNameRef.current.value = props.workspaceFile.nameWithoutExtension;
      setNewFileNameValid(true);
    }
  }, [props.workspaceFile]);

  const resetWorkspaceName = useCallback(() => {
    if (workspaceNameRef.current) {
      workspaceNameRef.current.value = props.workspace.descriptor.name;
    }
  }, [props.workspace]);

  const onRenameWorkspace = useCallback(
    (newName: string | undefined) => {
      if (!newName) {
        resetWorkspaceName();
        return;
      }

      if (newName === props.workspace.descriptor.name) {
        return;
      }

      workspaces.workspaceService.rename(props.workspace.descriptor.workspaceId, newName, { broadcast: true });
    },
    [props.workspace, workspaces.workspaceService, resetWorkspaceName]
  );

  const checkNewFileName = useCallback(
    async (newFileNameWithoutExtension: string) => {
      if (newFileNameWithoutExtension === props.workspaceFile.nameWithoutExtension) {
        setNewFileNameValid(true);
        return;
      }

      const newRelativePath = join(
        props.workspaceFile.relativeDirPath,
        `${newFileNameWithoutExtension}.${props.workspaceFile.extension}`
      );

      const exists = await workspaces.workspaceService.existsFile({
        workspaceId: props.workspaceFile.workspaceId,
        relativePath: newRelativePath,
      });
      setNewFileNameValid(!exists);
    },
    [props.workspaceFile, workspaces.workspaceService]
  );

  const onRenameWorkspaceFile = useCallback(
    (newName: string | undefined) => {
      if (!newName || !newFileNameValid) {
        resetWorkspaceFileName();
        return;
      }

      if (newName === props.workspaceFile.nameWithoutExtension) {
        return;
      }

      workspaces.renameFile(props.workspaceFile, newName);
    },
    [props.workspaceFile, workspaces, resetWorkspaceFileName, newFileNameValid]
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

  const onWorkspaceFileNameKeyDown = useCallback(
    (e: React.KeyboardEvent<HTMLInputElement>) => {
      e.stopPropagation();
      if (newFileNameValid && e.keyCode === 13 /* Enter */) {
        e.currentTarget.blur();
      } else if (e.keyCode === 27 /* ESC */) {
        resetWorkspaceFileName();
        e.currentTarget.blur();
      }
    },
    [newFileNameValid, resetWorkspaceFileName]
  );

  useEffect(resetWorkspaceName, [resetWorkspaceName]);
  useEffect(resetWorkspaceFileName, [resetWorkspaceFileName]);
  const [isFilesDropdownOpen, setFilesDropdownOpen] = useState(false);
  const filesDropdownItems = useMemo(
    () => [
      <DropdownGroup key={"workspace-group"} label="Files">
        {props.workspace.files
          .sort((a, b) => a.relativePath.localeCompare(b.relativePath))
          .filter((file) => SUPPORTED_FILES_EDITABLE.includes(file.extension))
          .map((file) => (
            <Link
              key={file.relativePath}
              style={{ fontWeight: props.workspaceFile.relativePath === file.relativePath ? "bold" : "normal" }}
              to={globals.routes.workspaceWithFilePath.path({
                workspaceId: file.workspaceId,
                filePath: file.relativePathWithoutExtension,
                extension: file.extension,
              })}
            >
              <DropdownItem description={"/ " + dirname(file.relativePath).split("/").join(" > ")}>
                <Flex flexWrap={{ default: "nowrap" }}>
                  <FlexItem>{file.nameWithoutExtension}</FlexItem>
                  <FlexItem
                    style={{
                      fontWeight: props.workspaceFile.relativePath === file.relativePath ? "bold" : "normal",
                    }}
                  >
                    <FileLabel extension={file.extension} />
                  </FlexItem>
                  <FlexItem>
                    <EyeIcon
                      style={{
                        height: "0.8em",
                        marginLeft: "10px",
                        visibility: props.workspaceFile.relativePath === file.relativePath ? "visible" : "hidden",
                      }}
                    />
                  </FlexItem>
                </Flex>
              </DropdownItem>
            </Link>
          ))}
      </DropdownGroup>,
    ],
    [globals, history, props.workspaceFile, props.workspace]
  );

  return (
    <>
      <Flex alignItems={{ default: "alignItemsCenter" }} flexWrap={{ default: "nowrap" }}>
        <FlexItem style={{ display: "flex", alignItems: "baseline" }}>
          {props.workspace.files.length > 1 && (
            <>
              <div data-testid={"toolbar-title-workspace"} className={"kogito--editor__toolbar-name-container"}>
                <Title aria-label={"EmbeddedEditorFile name"} headingLevel={"h3"} size={"2xl"}>
                  {props.workspace.descriptor.name}
                </Title>
                <TextInput
                  ref={workspaceNameRef}
                  type={"text"}
                  aria-label={"Edit workspace name"}
                  className={"kogito--editor__toolbar-title"}
                  onKeyDown={onWorkspaceNameKeyDown}
                  onBlur={(e) => onRenameWorkspace(e.target.value)}
                />
              </div>
              <Title headingLevel={"h3"} size={"2xl"} style={{ display: "inline", margin: "10px" }}>
                {`/`}
              </Title>
            </>
          )}
          {props.workspaceFile.relativePathWithoutExtension !== props.workspaceFile.nameWithoutExtension && (
            <>
              <Tooltip content={dirname(props.workspaceFile.relativePath)} position={"bottom"}>
                <Title
                  headingLevel={"h3"}
                  size={"2xl"}
                  style={{ display: "inline", padding: "10px" }}
                  className={"kogito-tooling--masthead-hoverable"}
                >
                  <span style={{ cursor: "default" }}>{`…`}</span>
                </Title>
              </Tooltip>
              <Title headingLevel={"h3"} size={"2xl"} style={{ display: "inline", margin: "10px" }}>
                {`/`}
              </Title>
            </>
          )}
          <Dropdown
            className={"kogito-tooling--masthead-hoverable"}
            isOpen={isFilesDropdownOpen}
            isPlain={true}
            position={DropdownPosition.right}
            dropdownItems={filesDropdownItems}
            onSelect={() => setFilesDropdownOpen(false)}
            toggle={
              <Toggle onToggle={setFilesDropdownOpen} id={"editor-page-masthead-files-dropdown-toggle"}>
                <Flex flexWrap={{ default: "nowrap" }} alignItems={{ default: "alignItemsCenter" }}>
                  <FlexItem>
                    <div
                      data-testid={"toolbar-title"}
                      className={`kogito--editor__toolbar-name-container ${newFileNameValid ? "" : "invalid"}`}
                    >
                      <Title aria-label={"EmbeddedEditorFile name"} headingLevel={"h3"} size={"2xl"}>
                        {props.workspaceFile.nameWithoutExtension}
                      </Title>
                      <Tooltip
                        content={
                          <Text component={TextVariants.p}>
                            {`A file already exists at this location. Please choose a different name.`}
                          </Text>
                        }
                        position={"bottom"}
                        trigger={"manual"}
                        isVisible={!newFileNameValid}
                        className="kogito--editor__light-tooltip"
                      >
                        <TextInput
                          onClick={(e) => e.stopPropagation()}
                          onKeyPress={(e) => e.stopPropagation()}
                          onKeyUp={(e) => e.stopPropagation()}
                          onKeyDown={onWorkspaceFileNameKeyDown}
                          onChange={checkNewFileName}
                          ref={workspaceFileNameRef}
                          type={"text"}
                          aria-label={"Edit file name"}
                          className={"kogito--editor__toolbar-title"}
                          onBlur={(e) => onRenameWorkspaceFile(e.target.value)}
                        />
                      </Tooltip>
                    </div>
                  </FlexItem>
                  <FlexItem>
                    <FileLabel extension={props.workspaceFile.extension} />
                  </FlexItem>
                  <FlexItem>
                    <CaretDownIcon />
                  </FlexItem>
                </Flex>
              </Toggle>
            }
          />
        </FlexItem>
      </Flex>
    </>
  );
}
