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
import { Button, PageSection, TextInput, Title, Toolbar, ToolbarGroup, ToolbarItem } from "@patternfly/react-core";
import { EditIcon } from "@patternfly/react-icons";
import { useLocation } from "react-router";

interface Props {
  onFileNameChanged: (fileName: string) => void;
  onFullScreen: () => void;
  onSave: () => void;
  onDownload: () => void;
  onClose: () => void;
}

export function EditorToolbar(props: Props) {
  const context = useContext(GlobalContext);
  const location = useLocation();
  const [editingName, setEditingName] = useState(false);
  const [name, setName] = useState(context.file.fileName);

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

  return (
    <PageSection type="nav" className="kogito--editor__toolbar-section">
      <Toolbar>
        {!editingName && (
          <ToolbarGroup>
            <ToolbarItem>
              <Title headingLevel="h3" size="xl" onClick={editName} title="Rename">
                {context.file.fileName + "." + editorType}
              </Title>
            </ToolbarItem>

            {!context.readonly && (
              <ToolbarItem>
                <Button variant="plain" aria-label="Edit the file name" onClick={editName}>
                  <EditIcon />
                </Button>
              </ToolbarItem>
            )}
          </ToolbarGroup>
        )}
        {editingName && (
          <ToolbarGroup>
            <ToolbarItem>
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
            </ToolbarItem>
          </ToolbarGroup>
        )}
        <ToolbarGroup className="kogito--right">
          <ToolbarItem>
            <Button variant="link" onClick={props.onFullScreen}>
              Full Screen
            </Button>
          </ToolbarItem>
        </ToolbarGroup>
        <ToolbarGroup>
          {context.external && !context.readonly && (
            <ToolbarItem className="pf-u-mr-sm">
              <Button variant="primary" onClick={props.onSave}>
                Send changes to GitHub
              </Button>
            </ToolbarItem>
          )}
          <ToolbarItem className="pf-u-mr-sm">
            <Button variant={context.external ? "secondary" : "primary"} onClick={props.onDownload}>
              Download
            </Button>
          </ToolbarItem>
          {!context.external && (
            <ToolbarItem>
              <Button variant="secondary" onClick={props.onClose}>
                Close
              </Button>
            </ToolbarItem>
          )}
        </ToolbarGroup>
      </Toolbar>
    </PageSection>
  );
}
