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

import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { WorkspaceLabel } from "./WorkspaceLabel";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { FolderIcon } from "@patternfly/react-icons/dist/js/icons/folder-icon";
import { WorkspaceDescriptorDates } from "./WorkspaceDescriptorDates";
import * as React from "react";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";

export function WorkspaceListItem(props: {
  isBig: boolean;
  workspaceDescriptor: WorkspaceDescriptor;
  allFiles: WorkspaceFile[];
  editableFiles: WorkspaceFile[];
}) {
  return (
    <>
      <Flex>
        <WorkspaceLabel descriptor={props.workspaceDescriptor} />
        <TextContent>
          <Text
            component={TextVariants.small}
            style={{
              whiteSpace: "nowrap",
              overflow: "hidden",
              textOverflow: "ellipsis",
            }}
          >
            {`${props.allFiles.length} files, ${props.editableFiles.length} models`}
          </Text>
        </TextContent>
      </Flex>
      <br />
      <TextContent>
        <Text
          component={props.isBig ? TextVariants.h3 : TextVariants.p}
          style={{
            whiteSpace: "nowrap",
            overflow: "hidden",
            textOverflow: "ellipsis",
          }}
        >
          <Icon style={{ fontSize: "0.875rem", margin: 0 }}>
            <FolderIcon />
          </Icon>
          &nbsp;&nbsp;
          {props.workspaceDescriptor.name}
        </Text>
      </TextContent>
      <WorkspaceDescriptorDates workspaceDescriptor={props.workspaceDescriptor} />
    </>
  );
}
