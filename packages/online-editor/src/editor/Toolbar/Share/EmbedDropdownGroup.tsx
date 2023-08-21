/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import React, { useCallback, useMemo } from "react";
import { DropdownGroup, DropdownItem } from "@patternfly/react-core/dist/js/components/Dropdown";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import ColumnsIcon from "@patternfly/react-icons/dist/js/icons/columns-icon";
import { useOnlineI18n } from "../../../i18n";
import { useEditorToolbarDispatchContext } from "../EditorToolbarContextProvider";

type Props = {
  workspaceFile: WorkspaceFile;
  workspace: ActiveWorkspace;
};

export const EmbedDropdownGroup = (props: Props) => {
  const { i18n } = useOnlineI18n();
  const { setEmbedModalOpen } = useEditorToolbarDispatchContext();

  const shouldIncludeEmbedDropdownItem = useMemo(() => {
    return props.workspaceFile.extension.toLowerCase() !== "pmml";
  }, [props.workspaceFile]);

  const openEmbedModal = useCallback(() => {
    setEmbedModalOpen(true);
  }, [setEmbedModalOpen]);

  if (!shouldIncludeEmbedDropdownItem) {
    return null;
  }

  return (
    <>
      <Divider key={"divider-other-group"} />
      <DropdownGroup key={"other-group"} label="Other">
        <DropdownItem
          key={`dropdown-embed`}
          data-testid="dropdown-embed"
          component="button"
          onClick={openEmbedModal}
          icon={<ColumnsIcon />}
        >
          {i18n.editorToolbar.embed}...
        </DropdownItem>
      </DropdownGroup>
    </>
  );
};
