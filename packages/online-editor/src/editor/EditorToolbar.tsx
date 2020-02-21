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
import { Button, PageSection, TextInput, Title, Toolbar, ToolbarGroup, ToolbarItem, PageHeader, Brand, DropdownToggle } from "@patternfly/react-core";
import { EditIcon, CloseIcon, ExpandIcon, CaretDownIcon } from "@patternfly/react-icons";
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

  const headerToolbar = (
      <Toolbar>
        {!editingName && (
            <ToolbarGroup>
              <ToolbarItem>
                <Title headingLevel="h3" size="xl" onClick={editName} title="Rename">
                  {context.file.fileName + "." + editorType}
                </Title>
              </ToolbarItem>

              {/* {!context.readonly && (
                <ToolbarItem>
                  <Button variant="plain" aria-label="Edit the file name" onClick={editName}>
                    <EditIcon />
                  </Button>
                </ToolbarItem>
              )} */}
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
        {/* THIS IS FROM THE LANDING PAGE
        <ToolbarGroup>
          <ToolbarItem className="pf-u-display-none pf-u-display-flex-on-lg">
            <a href={"https://github.com/kiegroup/kogito-tooling/releases"} target={"_blank"}>
              <Button variant="plain">
                Get GitHub Chrome extension
                <ExternalLinkAltIcon className="pf-u-mx-sm" />
              </Button>
            </a>
            <a href={"https://github.com/kiegroup/kogito-tooling/releases"} target={"_blank"}>
              <Button variant="plain">
                Get VSCode extension
                <ExternalLinkAltIcon className="pf-u-mx-sm" />
              </Button>
            </a>
          </ToolbarItem>
          <ToolbarItem className="pf-u-display-none-on-lg">
            <Dropdown
              isPlain={true}
              position="right"
              isOpen={isLinkDropdownOpen}
              toggle={
                <DropdownToggle
                  iconComponent={null}
                  onToggle={setIsLinkDropdownOpen}
                  aria-label="External links to extensions"
                >
                  <ExternalLinkAltIcon />
                </DropdownToggle>
              }
              dropdownItems={linkDropdownItems}
            />
          </ToolbarItem>
          <ToolbarItem>
            <Dropdown
              isPlain={true}
              position="right"
              isOpen={isUserDropdownOpen}
              toggle={
                <DropdownToggle iconComponent={null} onToggle={setIsUserDropdownOpen} aria-label="Links">
                  <OutlinedQuestionCircleIcon />
                </DropdownToggle>
              }
              dropdownItems={userDropdownItems}
            />
          </ToolbarItem>
        </ToolbarGroup> */}
      </Toolbar>
  );

  return (
    !isPageFullscreen ? 
      <PageHeader
      logo={<Brand src={"images/BPMN_Logo_342x76.svg"} alt="BPMN Logo" />}
      logoProps={logoProps}
      toolbar={headerToolbar}
      />
    :
      null
  );
    {/* vvv THIS IS OLD vvv */}
  //   <PageSection type="nav" className="kogito--editor__toolbar-section">
  //     <Toolbar>
  //       <ToolbarGroup
  //         className={"null"/* optional: Classes applied to toolbar group */}
  //       >
  //         {<>ReactNode</>/* optional: Anything that can be rendered as one toolbar group */}
  //       </ToolbarGroup>
  //       {!editingName && (
  //         <ToolbarGroup>
  //           <ToolbarItem>
  //             <Title headingLevel="h3" size="xl" onClick={editName} title="Rename">
  //               {context.file.fileName + "." + editorType}
  //             </Title>
  //           </ToolbarItem>

  //           {!context.readonly && (
  //             <ToolbarItem>
  //               <Button variant="plain" aria-label="Edit the file name" onClick={editName}>
  //                 <EditIcon />
  //               </Button>
  //             </ToolbarItem>
  //           )}
  //         </ToolbarGroup>
  //       )}
  //       {editingName && (
  //         <ToolbarGroup>
  //           <ToolbarItem>
  //             <div className="kogito--editor__toolbar-name-container">
  //               <Title headingLevel="h3" size="xl">
  //                 {name + "." + editorType}
  //               </Title>
  //               <TextInput
  //                 autoFocus={true}
  //                 value={name}
  //                 type="text"
  //                 aria-label="fileName"
  //                 className="pf-c-title pf-m-xl"
  //                 onChange={setName}
  //                 onKeyUp={onNameInputKeyUp}
  //                 onBlur={saveNewName}
  //               />
  //             </div>
  //           </ToolbarItem>
  //         </ToolbarGroup>
  //       )}
  //       <ToolbarGroup className="kogito--right">
  //         {context.external && !context.readonly && (
  //           <ToolbarItem className="pf-u-mr-sm">
  //             <Button variant="primary" onClick={props.onSave}>
  //               Send changes to GitHub
  //             </Button>
  //           </ToolbarItem>
  //         )}
  //         <ToolbarItem className="pf-u-mr-sm">
  //           <Button variant={context.external ? "secondary" : "primary"} onClick={props.onDownload}>
  //             Download
  //           </Button>
  //         </ToolbarItem>
  //         {!context.external && (
  //           <ToolbarItem>
  //             <Button variant="secondary" onClick={props.onClose}>
  //               Close
  //             </Button>
  //           </ToolbarItem>
  //         )}
  //         <ToolbarItem className="pf-u-mr-sm">
  //           <Dropdown
  //             onSelect={() => setKebabOpen(false)}
  //             toggle={<KebabToggle onToggle={isOpen => setKebabOpen(isOpen)} />}
  //             isOpen={isKebabOpen}
  //             isPlain={true}
  //             dropdownItems={kebabItems}
  //             position={DropdownPosition.right}
  //           />
  //         </ToolbarItem>
  //       </ToolbarGroup>
  //     </Toolbar>
  //   </PageSection>
  // );
}
