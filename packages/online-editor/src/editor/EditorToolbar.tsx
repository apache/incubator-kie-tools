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
import { EyeIcon } from "@patternfly/react-icons/dist/js/icons/eye-icon";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { EllipsisVIcon } from "@patternfly/react-icons/dist/js/icons/ellipsis-v-icon";
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
import { useQueryParams } from "../queryParams/QueryParamsContext";
import { EmbedModal } from "./EmbedModal";
import { AlertsController, useAlert } from "./Alerts/Alerts";
import { Alert, AlertActionCloseButton, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import { useWorkspaces, WorkspaceFile } from "../workspace/WorkspacesContext";
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import { basename, dirname } from "path";
import { Masthead, MastheadBrand, MastheadMain } from "@patternfly/react-core/dist/js/components/Masthead";
import { CheckIcon } from "@patternfly/react-icons/dist/js/icons/check-icon";
import { CopyIcon } from "@patternfly/react-icons/dist/js/icons/copy-icon";
import { FolderIcon } from "@patternfly/react-icons/dist/js/icons/folder-icon";
import { ImageIcon } from "@patternfly/react-icons/dist/js/icons/image-icon";
import { DownloadIcon } from "@patternfly/react-icons/dist/js/icons/download-icon";
import { PlusIcon } from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { ColumnsIcon } from "@patternfly/react-icons/dist/js/icons/columns-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { ActiveWorkspace } from "../workspace/model/ActiveWorkspace";
import { SUPPORTED_FILES_EDITABLE } from "../workspace/SupportedFiles";
import { NewFileDropdownItems } from "./NewFileDropdownItems";
import { PageHeaderToolsItem, PageHeaderToolsItemProps } from "@patternfly/react-core/dist/js/components/Page";

export interface Props {
  alerts: AlertsController | undefined;
  editor: EmbeddedEditorRef | undefined;
  workspace: ActiveWorkspace | undefined;
  workspaceFile: WorkspaceFile | undefined;
}

const showWhenSmall: PageHeaderToolsItemProps["visibility"] = {
  default: "visible",
  "2xl": "hidden",
  xl: "visible",
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
  const [isWorkspaceFilesMenuOpen, setWorkspaceFilesMenuOpen] = useState(false);
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
      ?.getFileContents()
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
    return props.workspaceFile?.extension.toLowerCase() !== "pmml";
  }, [props.workspaceFile]);

  const includeEmbedDropdownItem = useMemo(() => {
    return includeDownloadSVGDropdownItem;
  }, [includeDownloadSVGDropdownItem]);

  const onSendChangesToGitHub = useCallback(() => {
    props.workspaceFile?.getFileContents().then((content) => {
      if (!props.workspaceFile) {
        return;
      }
      window.dispatchEvent(
        new CustomEvent("saveOnlineEditor", {
          detail: {
            fileName: props.workspaceFile.nameWithExtension,
            fileContent: content,
            senderTabId: globals.senderTabId!,
          },
        })
      );
    });
  }, [props.workspaceFile, globals.senderTabId]);

  const onDownload = useCallback(() => {
    props.editor?.getStateControl().setSavedCommand();
    props.alerts?.closeAll();
    props.workspaceFile?.getFileContents().then((content) => {
      if (downloadRef.current) {
        const fileBlob = new Blob([content], { type: "text/plain" });
        downloadRef.current.href = URL.createObjectURL(fileBlob);
        downloadRef.current.click();
      }
    });
  }, [props.editor, props.workspaceFile, props.alerts]);

  const onDownloadAll = useCallback(async () => {
    if (!props.editor || !props.workspace) {
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
    if (props.editor && props.workspaceFile) {
      const content = await props.workspaceFile.getFileContents();

      // update gist
      if (queryParamUrl && settings.github.service.isGist(queryParamUrl)) {
        const userLogin = settings.github.service.extractUserLoginFromFileUrl(queryParamUrl);
        if (userLogin === settings.github.user) {
          try {
            const filename = props.workspaceFile.nameWithExtension;
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
          filename: props.workspaceFile.nameWithExtension,
          content: content,
          description: props.workspaceFile.nameWithExtension,
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
    queryParams,
    props.editor,
  ]);

  const onEmbed = useCallback(() => {
    setEmbedModalOpen(true);
  }, []);

  const onCopyContentToClipboard = useCallback(() => {
    props.workspaceFile?.getFileContents().then((content) => {
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
          description={`${props.workspaceFile?.nameWithExtension} will be downloaded`}
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
              description={`Image of ${props.workspaceFile?.nameWithExtension} will be downloaded in SVG format`}
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
          {globals.externalFile && (
            <DropdownItem
              key={`dropdown-${dropdownId}-send-changes-to-github`}
              component={"button"}
              onClick={onSendChangesToGitHub}
            >
              {i18n.editorToolbar.sendChangesToGitHub}
            </DropdownItem>
          )}
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
      onSendChangesToGitHub,
      includeDownloadSVGDropdownItem,
      includeEmbedDropdownItem,
      i18n,
      settings,
      globals,
    ]
  );

  useEffect(() => {
    if (downloadRef.current) {
      downloadRef.current.download = `${props.workspaceFile?.nameWithExtension}`;
    }
    if (downloadAllRef.current && props.workspace) {
      downloadAllRef.current.download = `${props.workspace.descriptor.name}.zip`;
    }
    if (downloadPreviewRef.current) {
      downloadPreviewRef.current.download = `${props.workspaceFile?.nameWithoutExtension}-svg.svg`;
    }
  }, [props.workspaceFile, props.workspace]);

  const filesDropdownItems = useMemo(() => {
    if (!props.workspace || props.workspace.files.length === 0) {
      return [
        <DropdownItem key="disabled link" isDisabled>
          <i>Loading files ...</i>
        </DropdownItem>,
      ];
    }

    return [
      <DropdownGroup key={"workspace-group"} label="Files">
        {props.workspace.files
          .sort((a, b) => a.path.localeCompare(b.path))
          .filter((file) => SUPPORTED_FILES_EDITABLE.includes(file.extension))
          .map((file, idx: number) => (
            <DropdownItem
              onClick={() => {
                history.push({
                  pathname: globals.routes.workspaceWithFilePath.path({
                    workspaceId: file.workspaceId,
                    filePath: file.pathRelativeToWorkspaceRootWithoutExtension,
                    extension: file.extension,
                  }),
                });
              }}
              description={
                "/ " +
                dirname(file.path!)
                  .replace(`/${props.workspace!.descriptor.workspaceId}`, "")
                  .substring(1)
                  .replace(/\//g, " > ")
              }
              key={`file-item-${idx}`}
              icon={
                <ExternalLinkAltIcon
                  className="kogito--editor__workspace-files-dropdown-open"
                  onClick={(e) => {
                    e.stopPropagation();
                    window.open(
                      globals.routes.workspaceWithFilePath.url({
                        pathParams: {
                          workspaceId: file.workspaceId,
                          filePath: file.pathRelativeToWorkspaceRootWithoutExtension,
                          extension: file.extension,
                        },
                      }),
                      "_blank"
                    );
                  }}
                />
              }
            >
              <span style={{ fontWeight: props.workspaceFile?.path === file.path ? "bold" : "normal" }}>
                {`${basename(file.path)}`}
              </span>
              <EyeIcon
                style={{
                  height: "0.8em",
                  marginLeft: "10px",
                  visibility: props.workspaceFile?.path === file.path ? "visible" : "hidden",
                }}
              />
            </DropdownItem>
          ))}
      </DropdownGroup>,
    ];
  }, [globals, history, props.workspaceFile, props.workspace]);

  return (
    <>
      <Masthead aria-label={"Page header"}>
        {/*<MastheadToggle>*/}
        {/*  <Button*/}
        {/*    variant={ButtonVariant.plain}*/}
        {/*    onClick={() =>*/}
        {/*      history.push({*/}
        {/*        pathname: globals.routes.home.path({}),*/}
        {/*      })*/}
        {/*    }*/}
        {/*  >*/}
        {/*    <HomeIcon />*/}
        {/*  </Button>*/}
        {/*</MastheadToggle>*/}

        <MastheadMain>
          <PageHeaderToolsItem visibility={{ ...hideWhenSmall, sm: "visible" }}>
            <MastheadBrand>
              {props.workspaceFile && (
                <Brand
                  onClick={onClose}
                  src={globals.routes.static.images.editorLogo.path({ type: props.workspaceFile.extension ?? "dmn" })}
                  alt={`${props.workspaceFile?.extension} kogito logo`}
                />
              )}
            </MastheadBrand>
          </PageHeaderToolsItem>
        </MastheadMain>
        <Flex justifyContent={{ default: "justifyContentSpaceBetween" }} alignItems={{ default: "alignItemsCenter" }}>
          <FlexItem />
          <FlexItem>
            <PageHeaderToolsItem visibility={{ default: "visible" }}>
              <Flex>
                <FlexItem>
                  <WorkspaceAndWorkspaceFileNames workspace={props.workspace} workspaceFile={props.workspaceFile} />
                </FlexItem>
                <FlexItem>
                  <TextContent>
                    <Text
                      style={{ color: "gray", ...(!isEdited && !props.workspace ? { visibility: "hidden" } : {}) }}
                      component={"small"}
                      aria-label={"EmbeddedEditorFile is saved"}
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
              </Flex>
            </PageHeaderToolsItem>
          </FlexItem>
          <FlexItem style={{ display: "flex", alignItems: "center" }}>
            <PageHeaderToolsItem visibility={hideWhenSmall}>
              {props.workspaceFile?.extension === "dmn" && (
                <>
                  <KieToolingExtendedServicesButtons />
                </>
              )}
              &nbsp;&nbsp;&nbsp;
              <Dropdown
                onSelect={() => setShareMenuOpen(false)}
                toggle={
                  <DropdownToggle
                    id={"share-id-lg"}
                    data-testid={"share-menu"}
                    onToggle={(isOpen) => setShareMenuOpen(isOpen)}
                  >
                    {i18n.editorToolbar.share}
                  </DropdownToggle>
                }
                isFullHeight={true}
                isPlain={true}
                style={{ marginRight: "2px" }}
                className={"kogito--editor__toolbar dropdown"}
                isOpen={isShareMenuOpen}
                dropdownItems={shareItems("lg")}
                position={DropdownPosition.right}
              />
              {props.workspace && (
                <>
                  &nbsp;&nbsp;&nbsp;
                  <Dropdown
                    onSelect={() => setWorkspaceFilesMenuOpen(false)}
                    toggle={
                      <DropdownToggle
                        id={"files-id-lg"}
                        data-testid={"files-menu"}
                        onToggle={(isOpen) => setWorkspaceFilesMenuOpen(isOpen)}
                      >
                        {`${props.workspace?.files.length} ${
                          (props.workspace?.files.length ?? 0) === 1 ? "File" : "Files"
                        }`}
                      </DropdownToggle>
                    }
                    isFullHeight={true}
                    isPlain={true}
                    style={{ marginRight: "2px" }}
                    className={"kogito--editor__toolbar dropdown"}
                    isOpen={isWorkspaceFilesMenuOpen}
                    dropdownItems={filesDropdownItems}
                    position={DropdownPosition.right}
                  />
                  <Dropdown
                    onSelect={() => setWorkspaceAddFileMenuOpen(false)}
                    toggle={
                      <DropdownToggle toggleIndicator={null} onToggle={(isOpen) => setWorkspaceAddFileMenuOpen(isOpen)}>
                        <PlusIcon />
                      </DropdownToggle>
                    }
                    isFullHeight={true}
                    isPlain={true}
                    className={"kogito--editor__toolbar dropdown"}
                    isOpen={isWorkspaceAddFileMenuOpen}
                    dropdownItems={[
                      <NewFileDropdownItems
                        key={"new-file-dropdown-items"}
                        workspace={props.workspace}
                        addEmptyWorkspaceFile={async (extension) => {
                          if (!props.workspace) {
                            throw new Error("Can't add a file without a workspace");
                          }

                          const file = await workspaces.addEmptyFile(props.workspace.descriptor.workspaceId, extension);
                          history.push({
                            pathname: globals.routes.workspaceWithFilePath.path({
                              workspaceId: file.workspaceId,
                              filePath: file.pathRelativeToWorkspaceRootWithoutExtension,
                              extension: file.extension,
                            }),
                          });
                          return file;
                        }}
                      />,
                    ]}
                    position={DropdownPosition.right}
                  />
                </>
              )}
            </PageHeaderToolsItem>
            &nbsp;&nbsp;&nbsp;
            <PageHeaderToolsItem visibility={showWhenSmall}>
              <Dropdown
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
                isOpen={isKebabOpen}
                isPlain={true}
                dropdownItems={[
                  ...shareItems("sm"),
                  (props.workspaceFile?.extension === "dmn" && (
                    <KieToolingExtendedServicesDropdownGroup key="kie-tooling-extended-services-group" />
                  )) || <React.Fragment key="kie-tooling-extended-services-group" />,
                ]}
                position={DropdownPosition.right}
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

function WorkspaceAndWorkspaceFileNames(props: {
  workspace: ActiveWorkspace | undefined;
  workspaceFile: WorkspaceFile | undefined;
}) {
  const workspaces = useWorkspaces();
  const workspaceNameRef = useRef<HTMLInputElement>(null);
  const workspaceFileNameRef = useRef<HTMLInputElement>(null);

  const resetWorkspaceFileName = useCallback(() => {
    if (props.workspaceFile && workspaceFileNameRef.current) {
      workspaceFileNameRef.current.value = props.workspaceFile.nameWithoutExtension;
    }
  }, [props.workspaceFile]);

  const resetWorkspaceName = useCallback(() => {
    if (props.workspace && workspaceNameRef.current) {
      workspaceNameRef.current.value = props.workspace.descriptor.name;
    }
  }, [props.workspace]);

  const onRenameWorkspace = useCallback(
    (newName: string | undefined) => {
      if (!props.workspace) {
        throw new Error("Can't rename workspace");
      }

      if (!newName) {
        resetWorkspaceName();
        return;
      }

      if (newName === props.workspace.descriptor.name) {
        return;
      }

      workspaces.workspaceService.rename(props.workspace.descriptor, newName, { broadcast: true });
    },
    [props.workspace, workspaces.workspaceService, resetWorkspaceName]
  );

  const onRenameWorkspaceFile = useCallback(
    (newName: string | undefined) => {
      if (!props.workspaceFile) {
        throw new Error("Can't rename file");
      }

      if (!newName) {
        resetWorkspaceFileName();
        return;
      }

      if (newName === props.workspaceFile.nameWithoutExtension) {
        return;
      }

      workspaces.renameFile(props.workspaceFile, newName);
    },
    [props.workspaceFile, workspaces, resetWorkspaceFileName]
  );

  const onWorkspaceNameKeyUp = useCallback(
    (e: React.KeyboardEvent<HTMLInputElement>) => {
      if (e.keyCode === 13 /* Enter */) {
        e.currentTarget.blur();
      } else if (e.keyCode === 27 /* ESC */) {
        resetWorkspaceName();
        e.currentTarget.blur();
      }
    },
    [resetWorkspaceName]
  );

  const onWorkspaceFileNameKeyUp = useCallback(
    (e: React.KeyboardEvent<HTMLInputElement>) => {
      if (e.keyCode === 13 /* Enter */) {
        e.currentTarget.blur();
      } else if (e.keyCode === 27 /* ESC */) {
        resetWorkspaceFileName();
        e.currentTarget.blur();
      }
    },
    [resetWorkspaceFileName]
  );

  useEffect(resetWorkspaceName, [resetWorkspaceName]);
  useEffect(resetWorkspaceFileName, [resetWorkspaceFileName]);

  return (
    <>
      {props.workspace && props.workspace.files.length > 1 && (
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
              onKeyUp={onWorkspaceNameKeyUp}
              onBlur={(e) => onRenameWorkspace(e.target.value)}
            />
          </div>
          <Title headingLevel={"h3"} size={"2xl"} style={{ display: "inline", margin: "10px" }}>
            {`/`}
          </Title>
        </>
      )}
      <div data-testid={"toolbar-title"} className={"kogito--editor__toolbar-name-container"}>
        <Title aria-label={"EmbeddedEditorFile name"} headingLevel={"h3"} size={"2xl"}>
          {props.workspaceFile?.nameWithoutExtension}
        </Title>
        <TextInput
          ref={workspaceFileNameRef}
          type={"text"}
          aria-label={"Edit file name"}
          className={"kogito--editor__toolbar-title"}
          onKeyUp={onWorkspaceFileNameKeyUp}
          onBlur={(e) => onRenameWorkspaceFile(e.target.value)}
        />
      </div>
    </>
  );
}
