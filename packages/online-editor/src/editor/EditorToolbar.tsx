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
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useOnlineI18n } from "../common/i18n";
import { SettingsButton } from "../settings/SettingsButton";
import { KieToolingExtendedServicesButtons } from "./KieToolingExtendedServices/KieToolingExtendedServicesButtons";
import { KieToolingExtendedServicesDropdownGroup } from "./KieToolingExtendedServices/KieToolingExtendedServicesDropdownGroup";
import { useGlobals } from "../common/GlobalContext";
import { AuthStatus, useSettings } from "../settings/SettingsContext";
import { SettingsTabs } from "../settings/SettingsModalBody";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { File } from "@kie-tooling-core/editor/dist/channel";

export interface Props {
  currentFile: File;
  onFullScreen: () => void;
  onSave: () => void;
  onDownload: () => void;
  onPreview: () => void;
  onGistIt: () => void;
  onEmbed: () => void;
  onClose: () => void;
  onCopyContentToClipboard: () => void;
  isPageFullscreen: boolean;
  isEdited: boolean;
  onRename: (newName: string) => void;
}

export function EditorToolbar(props: Props) {
  const globals = useGlobals();
  const settings = useSettings();
  const [fileName, setFileName] = useState(props.currentFile.fileName);
  const [isShareMenuOpen, setShareMenuOpen] = useState(false);
  const [isViewKebabOpen, setViewKebabOpen] = useState(false);
  const [isKebabOpen, setKebabOpen] = useState(false);
  const { i18n } = useOnlineI18n();

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

  const viewItems = useCallback(
    (dropdownId: string) => [
      <React.Fragment key={`dropdown-${dropdownId}-close`}>
        {!globals.externalFile && (
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
    [i18n, globals, props]
  );

  const includeDownloadSVGDropdownItem = useMemo(() => {
    return props.currentFile.fileExtension.toLowerCase() !== "pmml";
  }, [props.currentFile]);

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
            trigger={settings.github.authStatus !== AuthStatus.SIGNED_IN ? "mouseenter click" : ""}
            position="left"
          >
            <DropdownItem
              data-testid={"gist-it-button"}
              component="button"
              onClick={props.onGistIt}
              isDisabled={settings.github.authStatus !== AuthStatus.SIGNED_IN}
            >
              {i18n.editorToolbar.gistIt}
            </DropdownItem>
          </Tooltip>
          {globals.externalFile && !props.currentFile.isReadOnly && (
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
          onClick={() => settings.open(SettingsTabs.GITHUB)}
        >
          {i18n.editorToolbar.setGitHubToken}
        </DropdownItem>
      </DropdownGroup>,
    ],
    [
      props.onDownload,
      props.onCopyContentToClipboard,
      props.onPreview,
      props.onEmbed,
      props.onGistIt,
      props.onSave,
      includeDownloadSVGDropdownItem,
      includeEmbedDropdownItem,
      i18n,
      settings,
      globals,
    ]
  );

  return !props.isPageFullscreen ? (
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
                <Button
                  data-testid="save-button"
                  variant={"primary"}
                  onClick={props.onDownload}
                  aria-label={"Save and Download button"}
                  className={"kogito--editor__toolbar button"}
                  ouiaId="save-and-download-button"
                >
                  {i18n.terms.save}
                </Button>
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
                    (props.currentFile.fileExtension === "dmn" && (
                      <KieToolingExtendedServicesDropdownGroup key="kie-tooling-extended-services-group" />
                    )) || <></>,
                  ]}
                  position={DropdownPosition.right}
                />
              </PageHeaderToolsItem>
              <PageHeaderToolsItem>
                <SettingsButton />
              </PageHeaderToolsItem>
            </PageHeaderToolsGroup>
          </PageHeaderTools>
        }
        topNav={
          <>
            <Label variant="outline">{props.currentFile.kind}</Label>
            &nbsp;
            {!props.currentFile.isReadOnly && (
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
                    onBlur={() => props.onRename(fileName)}
                  />
                </div>
                {props.isEdited && (
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
            {props.currentFile.isReadOnly && (
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
    </>
  ) : null;
}
