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
  DropdownToggle
} from "@patternfly/react-core";
import { CloseIcon, ExpandIcon, EllipsisVIcon } from "@patternfly/react-icons";
import * as React from "react";
import { useCallback, useContext, useMemo, useState } from "react";
import { GlobalContext } from "../common/GlobalContext";
import { useLocation } from "react-router";
import { useOnlineI18n } from "../common/i18n";
import { I18nHtml } from "@kogito-tooling/i18n/src";

interface Props {
  onFileNameChanged: (fileName: string) => void;
  onFullScreen: () => void;
  onSave: () => void;
  onDownload: () => void;
  onPreview: () => void;
  onExportGist: () => void;
  onClose: () => void;
  onCopyContentToClipboard: () => void;
  isPageFullscreen: boolean;
  isEdited: boolean;
}

export function EditorToolbar(props: Props) {
  const context = useContext(GlobalContext);
  const location = useLocation();
  const [editingName, setEditingName] = useState(false);
  const [name, setName] = useState(context.file.fileName);
  const [isMenuOpen, setMenuOpen] = useState(false);
  const [isKebabOpen, setKebabOpen] = useState(false);
  const { i18n } = useOnlineI18n();

  const logoProps = useMemo(() => {
    return { onClick: props.onClose };
  }, [props.onClose]);

  const editorType = useMemo(() => {
    return context.routes.editor.args(location.pathname).type;
  }, [location]);

  const saveNewName = useCallback(() => {
    props.onFileNameChanged(name);
    setEditingName(false);
  }, [props.onFileNameChanged, name]);

  const cancelNewName = useCallback(() => {
    setEditingName(false);
    setName(context.file.fileName);
  }, [context.file.fileName]);

  const editName = useCallback(() => {
    if (!context.readonly) {
      setEditingName(true);
    }
  }, [context.readonly]);

  const onNameInputKeyUp = useCallback(
    (e: React.KeyboardEvent<HTMLInputElement>) => {
      if (e.keyCode === 13 /* Enter */) {
        saveNewName();
      } else if (e.keyCode === 27 /* ESC */) {
        cancelNewName();
      }
    },
    [saveNewName, cancelNewName]
  );

  const kebabItems = (dropdownId: string) =>
    useMemo(
      () => [
        <DropdownItem
          key={`dropdown-${dropdownId}-save`}
          component={"button"}
          onClick={props.onDownload}
          className={"pf-u-display-none-on-xl"}
        >
          <I18nHtml>{i18n.terms.save}</I18nHtml>
        </DropdownItem>,
        <DropdownItem
          key={`dropdown-${dropdownId}-fullscreen`}
          component="button"
          onClick={props.onFullScreen}
          className={"pf-u-display-none-on-xl"}
        >
          <I18nHtml>{i18n.terms.fullScreen}</I18nHtml>
        </DropdownItem>,
        <React.Fragment key={`dropdown-${dropdownId}-fragment`}>
          {context.external && !context.readonly && (
            <DropdownItem
              key={`dropdown-${dropdownId}-send-changes-to-github`}
              component={"button"}
              onClick={props.onSave}
            >
              <I18nHtml>{i18n.editorToolbar.sendChangesToGitHub}</I18nHtml>
            </DropdownItem>
          )}
        </React.Fragment>,
        <DropdownItem
          key={`dropdown-${dropdownId}-copy-source`}
          component={"button"}
          onClick={props.onCopyContentToClipboard}
        >
          <I18nHtml>{i18n.editorToolbar.copySource}</I18nHtml>
        </DropdownItem>,
        <DropdownItem key={`dropdown-${dropdownId}-download-svg`} component="button" onClick={props.onPreview}>
          <I18nHtml>{i18n.editorToolbar.downloadSVG}</I18nHtml>
        </DropdownItem>,
        <DropdownItem key={`dropdown-${dropdownId}-export-gist`} component="button" onClick={props.onExportGist}>
          <I18nHtml>{i18n.editorToolbar.gistIt}</I18nHtml>
        </DropdownItem>
      ],
      [
        context.external,
        context.readonly,
        props.onSave,
        props.onDownload,
        props.onCopyContentToClipboard,
        props.onExportGist
      ]
    );

  const filenameInput = (
    <>
      {!editingName && (
        <div data-testid="toolbar-title" className="kogito--editor__toolbar-title">
          <Title headingLevel={"h3"} size={"xl"} onClick={editName} title={"Rename"} aria-label={"File name"}>
            {context.file.fileName + "." + editorType}
          </Title>
          {props.isEdited && (
            <span className={"kogito--editor__toolbar-edited"} data-testid="is-dirty-indicator">
              <I18nHtml>{` - ${i18n.terms.edited}`}</I18nHtml>
            </span>
          )}
        </div>
      )}
      {editingName && (
        <div className={"kogito--editor__toolbar-name-container"}>
          <Title headingLevel={"h3"} size={"xl"}>
            {name + "." + editorType}
          </Title>
          <TextInput
            autoFocus={true}
            value={name}
            type={"text"}
            aria-label={"File name"}
            className={"pf-c-title pf-m-xl"}
            onChange={setName}
            onKeyUp={onNameInputKeyUp}
            onBlur={saveNewName}
          />
        </div>
      )}
    </>
  );

  const headerToolbar = (
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
            <I18nHtml>{i18n.terms.save}</I18nHtml>
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
                className={"kogito--editor__toolbar-toggle-icon-button"}
                onToggle={isOpen => setMenuOpen(isOpen)}
              >
                <I18nHtml>{i18n.editorToolbar.fileActions}</I18nHtml>
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
      logo={<Brand src={`images/${editorType}_kogito_logo.svg`} alt={`${editorType} kogito logo`} />}
      logoProps={logoProps}
      headerTools={headerToolbar}
      topNav={filenameInput}
      className={"kogito--editor__toolbar"}
      aria-label={"Page header"}
    />
  ) : null;
}
