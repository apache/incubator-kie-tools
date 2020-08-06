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
import { removeFileExtension } from "../common/utils";

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
  const [name, setName] = useState(removeFileExtension(context.file.fileName));
  const [isMenuOpen, setMenuOpen] = useState(false);
  const [isKebabOpen, setKebabOpen] = useState(false);

  const logoProps = useMemo(() => {
    return { onClick: props.onClose };
  }, [props.onClose]);

  const fileExtension = useMemo(() => {
    return context.routes.editor.args(location.pathname).type;
  }, [location]);

  const saveNewName = useCallback(() => {
    props.onFileNameChanged(`${name}.${fileExtension}`);
  }, [props.onFileNameChanged, name]);

  const cancelNewName = useCallback(() => {
    setName(removeFileExtension(context.file.fileName));
  }, [context.file.fileName]);

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
          Save
        </DropdownItem>,
        <DropdownItem
          key={`dropdown-${dropdownId}-fullscreen`}
          component="button"
          onClick={props.onFullScreen}
          className={"pf-u-display-none-on-xl"}
        >
          Full Screen
        </DropdownItem>,
        <React.Fragment key={`dropdown-${dropdownId}-fragment`}>
          {context.external && !context.readonly && (
            <DropdownItem
              key={`dropdown-${dropdownId}-send-changes-to-github`}
              component={"button"}
              onClick={props.onSave}
            >
              Send changes to GitHub
            </DropdownItem>
          )}
        </React.Fragment>,
        <DropdownItem
          key={`dropdown-${dropdownId}-copy-source`}
          component={"button"}
          onClick={props.onCopyContentToClipboard}
        >
          Copy source
        </DropdownItem>,
        <DropdownItem key={`dropdown-${dropdownId}-download-svg`} component="button" onClick={props.onPreview}>
          Download SVG
        </DropdownItem>,
        <DropdownItem key={`dropdown-${dropdownId}-export-gist`} component="button" onClick={props.onExportGist}>
          Gist it!
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
      <div data-testid={"toolbar-title"} className={"kogito--editor__toolbar-name-container"}>
        <Title headingLevel={"h3"} size={"2xl"}>
          {name}
        </Title>
        <TextInput
          value={name}
          type={"text"}
          aria-label={"File name"}
          className={"kogito--editor__toolbar-title"}
          onChange={setName}
          onKeyUp={onNameInputKeyUp}
          onBlur={saveNewName}
        />
      </div>
      {props.isEdited && (
        <span aria-label={"File was edited"} className={"kogito--editor__toolbar-edited"} data-testid="is-dirty-indicator">
          - Edited
        </span>
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
            Save
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
                File actions
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
      headerTools={headerToolbar}
      topNav={filenameInput}
      className={"kogito--editor__toolbar"}
      aria-label={"Page header"}
    />
  ) : null;
}
