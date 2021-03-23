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

import { File } from "@kie-tooling-core/editor/dist/channel";
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import {
  Dropdown,
  DropdownGroup,
  DropdownItem,
  DropdownPosition,
  DropdownToggle,
} from "@patternfly/react-core/dist/js/components/Dropdown";
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
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import { EyeIcon } from "@patternfly/react-icons/dist/js/icons/eye-icon";
import { dirname } from "path";
import * as React from "react";
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import { GlobalContext } from "../common/GlobalContext";
import { useOnlineI18n } from "../common/i18n";
import { GitHubRepositoryOrigin, WorkspaceKind } from "../workspace/model/WorkspaceOrigin";
import { useWorkspace } from "../workspace/WorkspaceContext";
import { KieToolingExtendedServicesButtons } from "./KieToolingExtendedServices/KieToolingExtendedServicesButtons";
import { useKieToolingExtendedServices } from "./KieToolingExtendedServices/KieToolingExtendedServicesContext";
import { KieToolingExtendedServicesDropdownGroup } from "./KieToolingExtendedServices/KieToolingExtendedServicesDropdownGroup";
import { KieToolingExtendedServicesStatus } from "./KieToolingExtendedServices/KieToolingExtendedServicesStatus";

interface Props {
  onFullScreen: () => void;
  onSave: () => void;
  onDownload: () => void;
  onDownloadAll: () => void;
  onPreview: () => void;
  onSetGitHubToken: () => void;
  onGistIt: () => void;
  onEmbed: () => void;
  onClose: () => void;
  onCopyContentToClipboard: () => void;
  isPageFullscreen: boolean;
  isEdited: boolean;
}

export function EditorToolbar(props: Props) {
  const context = useContext(GlobalContext);
  const workspaceContext = useWorkspace();
  const kieToolingExtendedServices = useKieToolingExtendedServices();
  const [fileName, setFileName] = useState(workspaceContext.file!.fileName);
  const [isShareMenuOpen, setShareMenuOpen] = useState(false);
  const [isWorkspaceFilesMenuOpen, setWorkspaceFilesMenuOpen] = useState(false);
  const [isViewKebabOpen, setViewKebabOpen] = useState(false);
  const [isKebabOpen, setKebabOpen] = useState(false);
  const { i18n } = useOnlineI18n();

  const logoProps = useMemo(() => {
    return { onClick: props.onClose };
  }, [props.onClose]);

  const fileExtension = useMemo(() => {
    return workspaceContext.file!.fileExtension;
  }, [workspaceContext.file]);

  const saveNewName = useCallback(() => {
    workspaceContext.onFileNameChanged(fileName).catch(() => {});
  }, [workspaceContext, fileName]);

  const cancelNewName = useCallback(() => {
    setFileName(workspaceContext.file!.fileName);
  }, [workspaceContext.file]);

  const onNameInputKeyUp = useCallback(
    (e: React.KeyboardEvent<HTMLInputElement>) => {
      if (e.keyCode === 13 /* Enter */) {
        saveNewName();
        e.currentTarget.blur();
      } else if (e.keyCode === 27 /* ESC */) {
        cancelNewName();
        e.currentTarget.blur();
      }
    },
    [saveNewName, cancelNewName]
  );

  const viewItems = useCallback(
    (dropdownId: string) => [
      <React.Fragment key={`dropdown-${dropdownId}-close`}>
        {!context.external && (
          <DropdownItem
            component={"button"}
            onClick={props.onClose}
            aria-label={"Close"}
            data-testid={"close-editor-button"}
            ouiaId="close-editor-button"
          >
            {i18n.editorToolbar.closeAndReturnHome}
          </DropdownItem>
        )}
      </React.Fragment>,
      <DropdownItem key={`dropdown-${dropdownId}-fullscreen`} component={"button"} onClick={props.onFullScreen}>
        {i18n.editorToolbar.enterFullScreenView}
      </DropdownItem>,
    ],
    [i18n, context, props]
  );

  const includeDownloadSVGDropdownItem = useMemo(() => {
    return fileExtension.toLowerCase() !== "pmml";
  }, [fileExtension]);

  const includeEmbedDropdownItem = useMemo(() => {
    return includeDownloadSVGDropdownItem;
  }, [includeDownloadSVGDropdownItem]);

  const shareItems = useCallback(
    (dropdownId: string) => [
      <DropdownItem
        key={`dropdown-${dropdownId}-save`}
        component={"button"}
        onClick={props.onDownload}
        className={"pf-u-display-none-on-xl"}
        ouiaId="save-and-download-dropdown-button"
      >
        {i18n.editorToolbar.saveAndDownload}
      </DropdownItem>,
      <DropdownItem
        key={`dropdown-${dropdownId}-copy-source`}
        component={"button"}
        onClick={props.onCopyContentToClipboard}
      >
        {i18n.editorToolbar.copySource}
      </DropdownItem>,
      <React.Fragment key={`dropdown-${dropdownId}-fragment-download-svg`}>
        {includeDownloadSVGDropdownItem && (
          <DropdownItem
            key={`dropdown-${dropdownId}-download-svg`}
            data-testid="dropdown-download-svg"
            component="button"
            onClick={props.onPreview}
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
            onClick={props.onEmbed}
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
            trigger={!context.githubService.isAuthenticated() ? "mouseenter click" : ""}
            position="left"
          >
            <DropdownItem
              data-testid={"gist-it-button"}
              component="button"
              onClick={props.onGistIt}
              isDisabled={!context.githubService.isAuthenticated()}
            >
              {i18n.editorToolbar.gistIt}
            </DropdownItem>
          </Tooltip>
          {context.external && !context.readonly && (
            <DropdownItem
              key={`dropdown-${dropdownId}-send-changes-to-github`}
              component={"button"}
              onClick={props.onSave}
            >
              {i18n.editorToolbar.sendChangesToGitHub}
            </DropdownItem>
          )}
        </React.Fragment>
        <DropdownItem
          data-testid={"set-github-token"}
          key={`dropdown-${dropdownId}-setup-github-token`}
          component="button"
          onClick={props.onSetGitHubToken}
        >
          {i18n.editorToolbar.setGitHubToken}
        </DropdownItem>
      </DropdownGroup>,
    ],
    [i18n, context, props.onSave, props.onDownload, props.onCopyContentToClipboard, props.onGistIt]
  );

  const fileItems = useCallback(() => {
    if (!workspaceContext.active || workspaceContext.active.files.length === 0) {
      return [
        <DropdownItem key="disabled link" isDisabled>
          <i>Loading files ...</i>
        </DropdownItem>,
      ];
    }

    return [
      workspaceContext.active?.kind === WorkspaceKind.GITHUB_REPOSITORY ? (
        <DropdownGroup key={"github-group"} label="GitHub">
          <DropdownItem
            onClick={workspaceContext.syncWorkspace}
            key={"push-changes-item"}
            description={`Push all changes to ${
              (workspaceContext.active.descriptor.origin as GitHubRepositoryOrigin).url
            }`}
          >
            Push
          </DropdownItem>
        </DropdownGroup>
      ) : (
        []
      ),
      <DropdownGroup key={"download-group"} label="Download">
        <DropdownItem
          onClick={props.onDownload}
          key={"donwload-file-item"}
          description={`${workspaceContext.file!.fileName}.${workspaceContext.file!.fileExtension} will be downloaded`}
        >
          Current file
        </DropdownItem>
        <DropdownItem
          onClick={props.onDownloadAll}
          key={"download-zip-item"}
          description={`A zip file including all files will be downloaded`}
        >
          All files
        </DropdownItem>
      </DropdownGroup>,
      <DropdownGroup key={"new-file-group"} label="New file">
        <DropdownItem
          onClick={async () => await workspaceContext.addEmptyFile("bpmn")}
          key={"new-bpmn-item"}
          description="BPMN files are used to generate business processes"
        >
          Workflow (.BPMN)
        </DropdownItem>
        <DropdownItem
          onClick={async () => await workspaceContext.addEmptyFile("dmn")}
          key={"new-dmn-item"}
          description="DMN files are used to generate decision models"
        >
          Decision model (.DMN)
        </DropdownItem>
        <DropdownItem
          onClick={async () => await workspaceContext.addEmptyFile("pmml")}
          key={"new-pmml-item"}
          description="PMML files are used to generate scorecards"
        >
          Scorecard model (.PMML)
        </DropdownItem>
      </DropdownGroup>,
      <DropdownGroup key={"workspace-group"} label="Workspace">
        {workspaceContext.active.files
          .sort((a: File, b: File) => a.path!.localeCompare(b.path!))
          .map((file: File, idx: number) => (
            <DropdownItem
              onClick={() => workspaceContext.onFileChanged(file)}
              description={
                "/ " +
                dirname(file.path!)
                  .replace(`/${workspaceContext.active!.descriptor.context}`, "")
                  .substring(1)
                  .replace(/\//g, " > ")
              }
              key={`file-item-${idx}`}
              icon={
                <ExternalLinkAltIcon
                  className="kogito--editor__workspace-files-dropdown-open"
                  onClick={(e) => {
                    window.open(`?path=${file.path}#/editor/${file.fileExtension}`, "_blank");
                    e.stopPropagation();
                  }}
                />
              }
            >
              <span style={{ fontWeight: workspaceContext.file!.path === file.path ? "bold" : "normal" }}>
                {`${file.fileName}.${file.fileExtension}`}
              </span>
              <EyeIcon
                style={{
                  height: "0.8em",
                  marginLeft: "10px",
                  visibility: workspaceContext.file!.path === file.path ? "visible" : "hidden",
                }}
              />
            </DropdownItem>
          ))}
      </DropdownGroup>,
    ];
  }, [props, workspaceContext]);

  useEffect(() => {
    setFileName(workspaceContext.file!.fileName);
  }, [workspaceContext.file]);

  return !props.isPageFullscreen ? (
    <PageHeader
      logo={<Brand src={`images/${fileExtension}_kogito_logo.svg`} alt={`${fileExtension} kogito logo`} />}
      logoProps={logoProps}
      headerTools={
        <PageHeaderTools>
          {kieToolingExtendedServices.status !== KieToolingExtendedServicesStatus.UNAVAILABLE && (
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
          {!workspaceContext.active && (
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
                <Button
                  data-testid="save-button"
                  variant={"primary"}
                  onClick={props.onDownload}
                  aria-label={"Save button"}
                  className={"kogito--editor__toolbar button"}
                  ouiaId="save-version-button"
                >
                  {i18n.terms.save}
                </Button>
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
              {workspaceContext.active && workspaceContext.active.files.length > 0 && (
                <Dropdown
                  onSelect={() => setWorkspaceFilesMenuOpen(false)}
                  toggle={
                    <DropdownToggle
                      id={"files-id-lg"}
                      data-testid={"files-menu"}
                      onToggle={(isOpen) => setWorkspaceFilesMenuOpen(isOpen)}
                    >
                      {`${workspaceContext.active.files.length} File${
                        workspaceContext.active.files.length > 1 ? "s" : ""
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
                onSelect={() => setViewKebabOpen(false)}
                toggle={
                  <DropdownToggle
                    data-testid={"view-kebab"}
                    className={"kogito--editor__toolbar-icon-button"}
                    id={"view-id-lg"}
                    toggleIndicator={null}
                    onToggle={(isOpen) => setViewKebabOpen(isOpen)}
                    ouiaId="toolbar-button"
                  >
                    <EllipsisVIcon />
                  </DropdownToggle>
                }
                isOpen={isViewKebabOpen}
                isPlain={true}
                dropdownItems={viewItems("lg")}
                position={DropdownPosition.right}
              />
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
                  ...viewItems("sm"),
                  <DropdownGroup key={"share-group"} label={i18n.editorToolbar.share}>
                    {...shareItems("sm")}
                  </DropdownGroup>,
                  <KieToolingExtendedServicesDropdownGroup key="kie-tooling-extended-services-group" />,
                ]}
                position={DropdownPosition.right}
              />
            </PageHeaderToolsItem>
          </PageHeaderToolsGroup>
        </PageHeaderTools>
      }
      topNav={
        <>
          {!context.readonly && (
            <>
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
                  onBlur={saveNewName}
                />
              </div>
              {props.isEdited && !workspaceContext.active && (
                <span
                  aria-label={"File was edited"}
                  className={"kogito--editor__toolbar-edited"}
                  data-testid="is-dirty-indicator"
                >
                  {` - ${i18n.terms.edited}`}
                </span>
              )}
            </>
          )}
          {context.readonly && (
            <>
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
              <span
                aria-label={"File is readonly"}
                className={"kogito--editor__toolbar-edited"}
                data-testid="is-readonly-indicator"
              >
                {` - ${i18n.terms.readonly}`}
              </span>
            </>
          )}
        </>
      }
      className={"kogito--editor__toolbar"}
      aria-label={"Page header"}
    />
  ) : null;
}
