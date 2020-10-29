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
  Button,
  Dropdown,
  DropdownItem,
  DropdownPosition,
  TextInput,
  Title,
  PageHeaderTools,
  PageHeaderToolsItem,
  PageHeaderToolsGroup,
  PageHeader,
  Brand,
  Tooltip,
  DropdownToggle
} from "@patternfly/react-core";
import { CloseIcon, ExpandIcon, EllipsisVIcon } from "@patternfly/react-icons";
import * as React from "react";
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import { GlobalContext } from "../common/GlobalContext";
import { useLocation } from "react-router";
import { useOnlineI18n } from "../common/i18n";
import { useFileUrl } from "../common/Hooks";

interface Props {
  onFileNameChanged: (fileName: string, fileExtension: string) => void;
  onFullScreen: () => void;
  onSave: () => void;
  onDownload: () => void;
  onPreview: () => void;
  onSetGitHubToken: () => void;
  onExportGist: () => void;
  onUpdateGist: () => void;
  onClose: () => void;
  onCopyContentToClipboard: () => void;
  isPageFullscreen: boolean;
  isEdited: boolean;
}

export function EditorToolbar(props: Props) {
  const context = useContext(GlobalContext);
  const location = useLocation();
  const [fileName, setFileName] = useState(context.file.fileName);
  const [isMenuOpen, setMenuOpen] = useState(false);
  const [isKebabOpen, setKebabOpen] = useState(false);
  const { i18n } = useOnlineI18n();
  const fileUrl = useFileUrl();
  const [userCanUpdateGist, setUserCanUpdateGist] = useState(false);

  const logoProps = useMemo(() => {
    return { onClick: props.onClose };
  }, [props.onClose]);

  const fileExtension = useMemo(() => {
    return context.file.fileExtension;
  }, [location]);

  const saveNewName = useCallback(() => {
    props.onFileNameChanged(fileName, fileExtension);
  }, [props.onFileNameChanged, fileName, fileExtension]);

  const cancelNewName = useCallback(() => {
    setFileName(context.file.fileName);
  }, [context.file.fileName]);

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

  useEffect(() => {
    if (fileUrl) {
      const userLogin = context.githubService.extractUserLoginFromGistRawUrl(fileUrl);
      setUserCanUpdateGist(userLogin === context.githubService.getLogin());
    }
  }, [fileUrl, context.githubService.getLogin()]);

  const kebabItems = useCallback(
    (dropdownId: string) => [
      <DropdownItem
        key={`dropdown-${dropdownId}-save`}
        component={"button"}
        onClick={props.onDownload}
        className={"pf-u-display-none-on-xl"}
      >
        {i18n.terms.save}
      </DropdownItem>,
      <DropdownItem
        key={`dropdown-${dropdownId}-fullscreen`}
        component="button"
        onClick={props.onFullScreen}
        className={"pf-u-display-none-on-xl"}
      >
        {i18n.terms.fullScreen}
      </DropdownItem>,
      <React.Fragment key={`dropdown-${dropdownId}-fragment`}>
        {context.external && !context.readonly && (
          <DropdownItem
            key={`dropdown-${dropdownId}-send-changes-to-github`}
            component={"button"}
            onClick={props.onSave}
          >
            {i18n.editorToolbar.sendChangesToGitHub}
          </DropdownItem>
        )}
      </React.Fragment>,
      <DropdownItem
        key={`dropdown-${dropdownId}-copy-source`}
        component={"button"}
        onClick={props.onCopyContentToClipboard}
      >
        {i18n.editorToolbar.copySource}
      </DropdownItem>,
      <DropdownItem key={`dropdown-${dropdownId}-download-svg`} component="button" onClick={props.onPreview}>
        {i18n.editorToolbar.downloadSVG}
      </DropdownItem>,
      <DropdownItem
        data-testid={"set-github-token"}
        key={`dropdown-${dropdownId}-setup-github-token`}
        component="button"
        onClick={props.onSetGitHubToken}
      >
        {i18n.editorToolbar.setGitHubToken}
      </DropdownItem>,
      <Tooltip
        data-testid={"gist-it-tooltip"}
        key={`dropdown-${dropdownId}-export-gist`}
        content={<div>{i18n.editorToolbar.gistItTooltip}</div>}
        trigger={!context.githubService.isAuthenticated() ? "mouseenter click" : ""}
      >
        <DropdownItem
          component="button"
          onClick={props.onExportGist}
          isDisabled={!context.githubService.isAuthenticated()}
        >
          {i18n.editorToolbar.gistIt}
        </DropdownItem>
      </Tooltip>,
      <Tooltip
        data-testid={"update-gist-tooltip"}
        key={`dropdown-${dropdownId}-update-gist`}
        content={<div>{i18n.editorToolbar.updateGistTooltip}</div>}
        trigger={!userCanUpdateGist ? "mouseenter click" : ""}
      >
        <DropdownItem
          data-testid={"update-gist-button"}
          component="button"
          onClick={props.onUpdateGist}
          isDisabled={!userCanUpdateGist}
        >
          {i18n.editorToolbar.updateGist}
        </DropdownItem>
      </Tooltip>
    ],
    [
      context.external,
      context.readonly,
      props.onSave,
      props.onDownload,
      props.onCopyContentToClipboard,
      props.onExportGist,
      props.onUpdateGist,
      window.location,
      userCanUpdateGist
    ]
  );

  const topNav = (
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
  );

  const headerTools = (
    <PageHeaderTools>
      <PageHeaderToolsGroup>
        <PageHeaderToolsItem
          visibility={{
            default: "hidden",
            "2xl": "visible",
            xl: "visible",
            lg: "hidden",
            md: "hidden",
            sm: "hidden"
          }}
        >
          <Button data-testid="save-button" variant={"tertiary"} onClick={props.onDownload} aria-label={"Save button"}>
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
            sm: "hidden"
          }}
        >
          <Dropdown
            onSelect={() => setMenuOpen(false)}
            toggle={
              <DropdownToggle
                id={"toggle-id-lg"}
                data-testid={"file-actions"}
                className={"kogito--editor__toolbar-toggle-icon-button"}
                onToggle={isOpen => setMenuOpen(isOpen)}
              >
                {i18n.editorToolbar.fileActions}
              </DropdownToggle>
            }
            isOpen={isMenuOpen}
            isPlain={true}
            dropdownItems={kebabItems("lg")}
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
            sm: "hidden"
          }}
        >
          <Button
            className={"kogito--editor__toolbar-icon-button"}
            variant={"plain"}
            onClick={props.onFullScreen}
            aria-label={"Full screen"}
          >
            <ExpandIcon />
          </Button>
        </PageHeaderToolsItem>
        <PageHeaderToolsItem
          visibility={{
            default: "visible",
            "2xl": "hidden",
            xl: "hidden",
            lg: "visible",
            md: "visible",
            sm: "visible"
          }}
        >
          <Dropdown
            onSelect={() => setKebabOpen(false)}
            toggle={
              <DropdownToggle
                className={"kogito--editor__toolbar-toggle-icon-button"}
                id={"toggle-id-sm"}
                toggleIndicator={null}
                onToggle={isOpen => setKebabOpen(isOpen)}
              >
                <EllipsisVIcon />
              </DropdownToggle>
            }
            isOpen={isKebabOpen}
            isPlain={true}
            dropdownItems={kebabItems("sm")}
            position={DropdownPosition.right}
          />
        </PageHeaderToolsItem>
        {!context.external && (
          <PageHeaderToolsItem
            visibility={{
              default: "visible",
              "2xl": "visible",
              xl: "visible",
              lg: "visible",
              md: "visible",
              sm: "visible"
            }}
          >
            <Button
              className={"kogito--editor__toolbar-icon-button"}
              variant={"plain"}
              onClick={props.onClose}
              aria-label={"Close"}
              data-testid="close-editor-button"
            >
              <CloseIcon />
            </Button>
          </PageHeaderToolsItem>
        )}
      </PageHeaderToolsGroup>
    </PageHeaderTools>
  );

  return !props.isPageFullscreen ? (
    <PageHeader
      logo={<Brand src={`images/${fileExtension}_kogito_logo.svg`} alt={`${fileExtension} kogito logo`} />}
      logoProps={logoProps}
      headerTools={headerTools}
      topNav={topNav}
      className={"kogito--editor__toolbar"}
      aria-label={"Page header"}
    />
  ) : null;
}
