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

import * as React from "react";
import { useCallback, useContext, useMemo, useState } from "react";
import { GlobalContext } from "../common/GlobalContext";
import { Button, PageSection, TextInput, Title, Toolbar, ToolbarGroup, ToolbarItem, PageHeader, Brand, DropdownToggle, Level, LevelItem } from "@patternfly/react-core";
import { CloseIcon, ExpandIcon, CaretDownIcon, EditIcon } from "@patternfly/react-icons";
import { useLocation } from "react-router";
import {
  Dropdown,
  DropdownItem,
  DropdownPosition,
  KebabToggle
} from "@patternfly/react-core";

interface Props {
  onFileNameChanged: (fileName: string) => void;
  onFullScreen: () => void;
  onSave: () => void;
  onDownload: () => void;
  onClose: () => void;
  onCopyContentToClipboard: () => void;
  isPageFullscreen: boolean;
}

export function EditorToolbar(props: Props) {
  const context = useContext(GlobalContext);
  const location = useLocation();
  const [editingName, setEditingName] = useState(false);
  const [name, setName] = useState(context.file.fileName);
  const [isKebabOpen, setKebabOpen] = useState(false);

  const {isPageFullscreen} = props;

  const logoProps = {
    href: "/",
  };

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

  const kebabItems = useMemo(
    () => [
      <DropdownItem key="copy" component="button" onClick={props.onCopyContentToClipboard}>
        Copy source
      </DropdownItem>,
      <DropdownItem key="geturl" component="button" onClick={() => null}>
        Get shareable URL
      </DropdownItem>
    ],
    []
  );

  const filenameInput = (
    <>
    {!editingName && (
          <Title headingLevel="h3" size="xl" onClick={editName} title="Rename">
            {context.file.fileName + "." + editorType}
          </Title>
    )}
    {editingName && (
          <div className="kogito--editor__toolbar-name-container">
            <Title headingLevel="h3" size="xl">
              {name + "." + editorType}
            </Title>
            <TextInput
              autoFocus={true}
              value={name}
              type="text"
              aria-label="fileName"
              className="pf-c-title pf-m-xl"
              onChange={setName}
              onKeyUp={onNameInputKeyUp}
              onBlur={saveNewName}
            />
          </div>
    )}
    </>
  );

  const headerToolbar = (
      // TODO: The toolbar should be switched out for DataToolbar
      <Toolbar>
        <ToolbarGroup>
          {context.external && !context.readonly && (
            <ToolbarItem>
              <Button variant="primary" onClick={props.onSave}>
                Send changes to GitHub
              </Button>
            </ToolbarItem>
          )}
          <ToolbarItem>
            <Button variant={"secondary"} onClick={props.onDownload}>
              Download
            </Button>
          </ToolbarItem>
          </ToolbarGroup>
          <ToolbarGroup>
          <ToolbarItem>
            <Dropdown
              onSelect={() => setKebabOpen(false)}
              toggle={
                <DropdownToggle 
                id="toggle-id" 
                onToggle={isOpen => setKebabOpen(isOpen)} 
                iconComponent={CaretDownIcon}>
                  File actions
                </DropdownToggle>
              }
              isOpen={isKebabOpen}
              isPlain={true}
              dropdownItems={kebabItems}
              position={DropdownPosition.right}
            />
          </ToolbarItem>
          </ToolbarGroup>
          <ToolbarGroup>
            <ToolbarItem>
              <Button variant="plain" onClick={props.onFullScreen}>
                <ExpandIcon />
              </Button>
            </ToolbarItem>
            {!context.external && (
              <ToolbarItem>
                <Button variant="plain" onClick={props.onClose}>
                  <CloseIcon />
                </Button>
              </ToolbarItem>
            )}
        </ToolbarGroup>
      </Toolbar>
  );

  return (
    !isPageFullscreen ? 
      <PageHeader
      // TODO: switch between BPMN and DMN logo
      logo={<Brand src={"images/BPMN_Kogito_Logo.svg"} alt="BPMN Kogito Logo" />}
      logoProps={logoProps}
      toolbar={headerToolbar}
      topNav={filenameInput}
      className="kogito--editor__toolbar"
      />
    :
      null
  );
}
