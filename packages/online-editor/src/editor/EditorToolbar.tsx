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
import {
  PageHeader,
  PageHeaderTools,
  PageHeaderToolsGroup,
  PageHeaderToolsItem,
} from "@patternfly/react-core/dist/js/components/Page";
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
import { File } from "@kie-tooling-core/editor/dist/channel";
import { EmbeddedEditorRef, useDirtyState } from "@kie-tooling-core/editor/dist/embedded";
import { UpdateGistErrors } from "../settings/GithubService";
import { QueryParams } from "../common/Routes";
import { useHistory } from "react-router";
import { useQueryParams } from "../queryParams/QueryParamsContext";
import { EmbedModal } from "./EmbedModal";
import { AlertsController, useAlert } from "./Alerts/Alerts";
import { Alert, AlertActionCloseButton } from "@patternfly/react-core/dist/js/components/Alert";
import { useWorkspaces } from "../workspace/WorkspaceContext";
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import { dirname } from "path";
import { GitHubRepositoryOrigin, WorkspaceKind } from "../workspace/model/WorkspaceOrigin";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { CheckIcon } from "@patternfly/react-icons/dist/js/icons/check-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";

export interface Props {
  alerts: AlertsController | undefined;
  editor: EmbeddedEditorRef | undefined;
  currentFile: File;
  onRename: (newName: string) => void;
  onClose: () => void;
}

export function EditorToolbar(props: Props) {
  const globals = useGlobals();
  const settings = useSettings();
  const history = useHistory();
  const queryParams = useQueryParams();
  const [fileName, setFileName] = useState(props.currentFile.fileName);
  const [isShareMenuOpen, setShareMenuOpen] = useState(false);
  const [isKebabOpen, setKebabOpen] = useState(false);
  const [isEmbedModalOpen, setEmbedModalOpen] = useState(false);
  const { i18n } = useOnlineI18n();
  const isEdited = useDirtyState(props.editor);

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

  const queryParamUrl = useMemo(() => {
    return queryParams.get(QueryParams.URL);
  }, [queryParams]);

  const cancelNewName = useCallback(() => {
    setFileName(props.currentFile.fileName);
  }, [props.currentFile.fileName]);

  useEffect(() => {
    setFileName(props.currentFile.fileName);
  }, [props.currentFile.fileName]);

  const onNameInputKeyUp = useCallback(
    (e: React.KeyboardEvent<HTMLInputElement>) => {
      if (e.keyCode === 13 /* Enter */) {
        props.onRename(fileName);
        e.currentTarget.blur();
      } else if (e.keyCode === 27 /* ESC */) {
        cancelNewName();
        e.currentTarget.blur();
      }
    },
    [props, fileName, cancelNewName]
  );

  const includeDownloadSVGDropdownItem = useMemo(() => {
    return props.currentFile.fileExtension.toLowerCase() !== "pmml";
  }, [props.currentFile]);

  const includeEmbedDropdownItem = useMemo(() => {
    return includeDownloadSVGDropdownItem;
  }, [includeDownloadSVGDropdownItem]);

  const onSendChangesToGitHub = useCallback(() => {
    props.editor?.getContent().then((content) => {
      window.dispatchEvent(
        new CustomEvent("saveOnlineEditor", {
          detail: {
            fileName: `${props.currentFile.fileName}.${props.currentFile.fileExtension}`,
            fileContent: content,
            senderTabId: globals.senderTabId!,
          },
        })
      );
    });
  }, [props.currentFile, globals.senderTabId, props.editor]);

  const onDownload = useCallback(() => {
    props.editor?.getStateControl().setSavedCommand();
    props.alerts?.closeAll();
    props.editor?.getContent().then((content) => {
      if (downloadRef.current) {
        const fileBlob = new Blob([content], { type: "text/plain" });
        downloadRef.current.href = URL.createObjectURL(fileBlob);
        downloadRef.current.click();
      }
    });
  }, [props.editor, props.alerts]);

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
      const content = await props.editor.getContent();

      // update gist
      if (queryParamUrl && settings.github.service.isGist(queryParamUrl)) {
        const userLogin = settings.github.service.extractUserLoginFromFileUrl(queryParamUrl);
        if (userLogin === settings.github.user) {
          try {
            const filename = `${props.currentFile.fileName}.${props.currentFile.fileExtension}`;
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
                pathname: globals.routes.editor.path({ extension: props.currentFile.fileExtension }),
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
          filename: `${props.currentFile.fileName}.${props.currentFile.fileExtension}`,
          content: content,
          description: `${props.currentFile.fileName}.${props.currentFile.fileExtension}`,
          isPublic: true,
        });

        successCreateGistAlert.show();

        history.push({
          pathname: globals.routes.editor.path({ extension: props.currentFile.fileExtension }),
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
    props.currentFile,
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
    props.editor?.getContent().then((content) => {
      if (copyContentTextArea.current) {
        copyContentTextArea.current.value = content;
        copyContentTextArea.current.select();
        if (document.execCommand("copy")) {
          copySuccessfulAlert.show();
        }
      }
    });
  }, [props.editor, copySuccessfulAlert]);

  const shareItems = useCallback(
    (dropdownId: string) => [
      <DropdownItem key={`dropdown-${dropdownId}-copy-source`} component={"button"} onClick={onCopyContentToClipboard}>
        {i18n.editorToolbar.copySource}
      </DropdownItem>,
      <React.Fragment key={`dropdown-${dropdownId}-fragment-download-svg`}>
        {includeDownloadSVGDropdownItem && (
          <DropdownItem
            key={`dropdown-${dropdownId}-download-svg`}
            data-testid="dropdown-download-svg"
            component="button"
            onClick={onPreview}
          >
            {i18n.editorToolbar.downloadSVG}
          </DropdownItem>
        )}
      </React.Fragment>,
      <React.Fragment key={`dropdown-${dropdownId}-fragment-embed`}>
        {includeEmbedDropdownItem && (
          <DropdownItem
            key={`dropdown-${dropdownId}-embed`}
            data-testid="dropdown-embed"
            component="button"
            onClick={onEmbed}
          >
            {i18n.editorToolbar.embed}
          </DropdownItem>
        )}
      </React.Fragment>,
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
          {globals.externalFile && !props.currentFile.isReadOnly && (
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
      props.currentFile,
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

  const downloadRef = useRef<HTMLAnchorElement>(null);
  const downloadAllRef = useRef<HTMLAnchorElement>(null);
  const downloadPreviewRef = useRef<HTMLAnchorElement>(null);
  const copyContentTextArea = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    if (downloadRef.current) {
      downloadRef.current.download = `${props.currentFile.fileName}.${props.currentFile.fileExtension}`;
    }
    if (downloadAllRef.current && workspaces.active) {
      downloadAllRef.current.download = `${workspaces.active.descriptor.name}.zip`;
    }
    if (downloadPreviewRef.current) {
      downloadPreviewRef.current.download = `${props.currentFile.fileName}-svg.svg`;
    }
  }, [props.currentFile]);

  //

  const workspaces = useWorkspaces();
  const [isWorkspaceFilesMenuOpen, setWorkspaceFilesMenuOpen] = useState(false);
  const onDownloadAll = useCallback(async () => {
    if (!props.editor) {
      return;
    }

    const zipBlob = await workspaces.prepareZip();
    if (downloadAllRef.current) {
      downloadAllRef.current.href = URL.createObjectURL(zipBlob);
      downloadAllRef.current.click();
    }
  }, [props.editor, workspaces]);

  const fileItems = useCallback(() => {
    if (!workspaces.active || workspaces.active.files.length === 0) {
      return [
        <DropdownItem key="disabled link" isDisabled>
          <i>Loading files ...</i>
        </DropdownItem>,
      ];
    }

    return [
      workspaces.active?.descriptor.origin.kind === WorkspaceKind.GITHUB_REPOSITORY ? (
        <DropdownGroup key={"github-group"} label="GitHub">
          <DropdownItem
            onClick={workspaces.syncWorkspace}
            key={"push-changes-item"}
            description={`Push all changes to ${(workspaces.active.descriptor.origin as GitHubRepositoryOrigin).url}`}
          >
            Push
          </DropdownItem>
        </DropdownGroup>
      ) : (
        []
      ),
      <DropdownGroup key={"download-group"} label="Download">
        <DropdownItem
          onClick={onDownload}
          key={"donwload-file-item"}
          description={`${props.currentFile.fileName}.${props.currentFile.fileExtension} will be downloaded`}
        >
          Current file
        </DropdownItem>
        <DropdownItem
          onClick={onDownloadAll}
          key={"download-zip-item"}
          description={`A zip file including all files will be downloaded`}
        >
          All files
        </DropdownItem>
      </DropdownGroup>,
      <DropdownGroup key={"new-file-group"} label="New file">
        <DropdownItem
          onClick={async () => await workspaces.addEmptyFile("bpmn")}
          key={"new-bpmn-item"}
          description="BPMN files are used to generate business processes"
        >
          Workflow (.BPMN)
        </DropdownItem>
        <DropdownItem
          onClick={async () => await workspaces.addEmptyFile("dmn")}
          key={"new-dmn-item"}
          description="DMN files are used to generate decision models"
        >
          Decision model (.DMN)
        </DropdownItem>
        <DropdownItem
          onClick={async () => await workspaces.addEmptyFile("pmml")}
          key={"new-pmml-item"}
          description="PMML files are used to generate scorecards"
        >
          Scorecard model (.PMML)
        </DropdownItem>
      </DropdownGroup>,
      <DropdownGroup key={"workspace-group"} label="Workspace">
        {workspaces.active.files
          .sort((a: File, b: File) => a.path!.localeCompare(b.path!))
          .map((file: File, idx: number) => (
            <DropdownItem
              onClick={() => workspaces.onFileChanged(file)}
              description={
                "/ " +
                dirname(file.path!)
                  .replace(`/${workspaces.active!.descriptor.context}`, "")
                  .substring(1)
                  .replace(/\//g, " > ")
              }
              key={`file-item-${idx}`}
              icon={
                <ExternalLinkAltIcon
                  className="kogito--editor__workspace-files-dropdown-open"
                  onClick={(e) => {
                    e.stopPropagation();
                    workspaces.goToFileInNewWindow(file);
                  }}
                />
              }
            >
              <span style={{ fontWeight: props.currentFile.path === file.path ? "bold" : "normal" }}>
                {`${file.fileName}.${file.fileExtension}`}
              </span>
              <EyeIcon
                style={{
                  height: "0.8em",
                  marginLeft: "10px",
                  visibility: props.currentFile.path === file.path ? "visible" : "hidden",
                }}
              />
            </DropdownItem>
          ))}
      </DropdownGroup>,
    ];
  }, [props, workspaces]);

  useEffect(() => {
    setFileName(props.currentFile.fileName);
  }, [props.currentFile]);

  return (
    <>
      <PageHeader
        logo={
          <Brand
            src={globals.routes.static.images.editorLogo.path({ type: props.currentFile.fileExtension })}
            alt={`${props.currentFile.fileExtension} kogito logo`}
          />
        }
        logoProps={{ onClick: props.onClose }}
        headerTools={
          <PageHeaderTools>
            {props.currentFile.fileExtension === "dmn" && (
              <PageHeaderToolsGroup>
                <PageHeaderToolsItem
                  visibility={{
                    default: "hidden",
                    "2xl": "visible",
                    xl: "visible",
                    lg: "hidden",
                    md: "hidden",
                    sm: "hidden",
                  }}
                >
                  <KieToolingExtendedServicesButtons />
                </PageHeaderToolsItem>
              </PageHeaderToolsGroup>
            )}
            <PageHeaderToolsGroup>
              <PageHeaderToolsItem
                visibility={{
                  default: "hidden",
                  "2xl": "visible",
                  xl: "visible",
                  lg: "hidden",
                  md: "hidden",
                  sm: "hidden",
                }}
              >
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
                  isPlain={true}
                  className={"kogito--editor__toolbar dropdown"}
                  isOpen={isShareMenuOpen}
                  dropdownItems={shareItems("lg")}
                  position={DropdownPosition.right}
                />
                {workspaces.active && (
                  <Dropdown
                    onSelect={() => setWorkspaceFilesMenuOpen(false)}
                    toggle={
                      <DropdownToggle
                        id={"files-id-lg"}
                        data-testid={"files-menu"}
                        onToggle={(isOpen) => setWorkspaceFilesMenuOpen(isOpen)}
                      >
                        {`${workspaces.active?.files.length} ${
                          (workspaces.active?.files.length ?? 0) === 1 ? "File" : "Files"
                        }`}
                      </DropdownToggle>
                    }
                    isPlain={true}
                    className={"kogito--editor__toolbar dropdown pf-u-ml-sm"}
                    isOpen={isWorkspaceFilesMenuOpen}
                    dropdownItems={fileItems()}
                    position={DropdownPosition.right}
                  />
                )}
              </PageHeaderToolsItem>
            </PageHeaderToolsGroup>
            {!workspaces.active && (
              <PageHeaderToolsGroup>
                <PageHeaderToolsItem>
                  <Button
                    variant={"primary"}
                    className={"kogito--editor__toolbar button"}
                    onClick={() =>
                      workspaces.createWorkspaceFromLocal(
                        [
                          {
                            ...props.currentFile,
                            path: `${props.currentFile.fileName}.${props.currentFile.fileExtension}`,
                            kind: "local",
                          },
                        ],
                        false
                      )
                    }
                  >
                    Make Workspace
                  </Button>
                </PageHeaderToolsItem>
              </PageHeaderToolsGroup>
            )}
            <PageHeaderToolsGroup>
              <PageHeaderToolsItem>
                <SettingsButton />
              </PageHeaderToolsItem>
              <PageHeaderToolsItem
                visibility={{
                  default: "visible",
                  "2xl": "hidden",
                  xl: "hidden",
                  lg: "visible",
                  md: "visible",
                  sm: "visible",
                }}
              >
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
                    <DropdownGroup key={"share-group"} label={i18n.editorToolbar.share}>
                      {...shareItems("sm")}
                    </DropdownGroup>,
                    (props.currentFile.fileExtension === "dmn" && (
                      <KieToolingExtendedServicesDropdownGroup key="kie-tooling-extended-services-group" />
                    )) || <React.Fragment key="kie-tooling-extended-services-group" />,
                  ]}
                  position={DropdownPosition.right}
                />
              </PageHeaderToolsItem>
            </PageHeaderToolsGroup>
          </PageHeaderTools>
        }
        topNav={
          <Flex>
            {!props.currentFile.isReadOnly && (
              <>
                <FlexItem>
                  <div data-testid={"toolbar-title"} className={"kogito--editor__toolbar-name-container"}>
                    <Title aria-label={"File name"} headingLevel={"h3"} size={"2xl"}>
                      {fileName}
                    </Title>
                    <TextInput
                      value={fileName}
                      type={"text"}
                      aria-label={"Edit file name"}
                      className={"kogito--editor__toolbar-title"}
                      onChange={setFileName}
                      onKeyUp={onNameInputKeyUp}
                      onBlur={() => props.onRename(fileName)}
                    />
                  </div>
                </FlexItem>
                <FlexItem>
                  <TextContent>
                    <Text
                      style={{ color: "gray", ...(isEdited || !workspaces.active ? { visibility: "hidden" } : {}) }}
                      component={"small"}
                      aria-label={"File is saved"}
                      data-testid="is-saved-indicator"
                    >
                      {`Saved`} <CheckIcon size={"sm"} />
                    </Text>
                  </TextContent>
                </FlexItem>
              </>
            )}
            {props.currentFile.isReadOnly && (
              <>
                <FlexItem>
                  <div data-testid={"toolbar-title"} className={"kogito--editor__toolbar-name-container readonly"}>
                    <Title
                      className="kogito--editor__toolbar-title"
                      aria-label={"File name"}
                      headingLevel={"h3"}
                      size={"2xl"}
                    >
                      {fileName}
                    </Title>
                  </div>
                </FlexItem>
                <FlexItem>
                  <TextContent>
                    <Text
                      style={{ color: "gray" }}
                      component={"small"}
                      aria-label={"File is readonly"}
                      data-testid="is-readonly-indicator"
                    >
                      {i18n.terms.readonly}
                    </Text>
                  </TextContent>
                </FlexItem>
              </>
            )}
          </Flex>
        }
        className={"kogito--editor__toolbar"}
        aria-label={"Page header"}
      />

      <EmbedModal
        currentFile={props.currentFile}
        isOpen={isEmbedModalOpen}
        onClose={() => setEmbedModalOpen(false)}
        editor={props.editor}
      />
      <textarea ref={copyContentTextArea} style={{ height: 0, position: "absolute", zIndex: -1 }} />
      <a ref={downloadRef} />
      <a ref={downloadAllRef} />
      <a ref={downloadPreviewRef} />
    </>
  );
}
