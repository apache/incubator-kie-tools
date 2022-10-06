/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { WorkspaceFile } from "../workspace/WorkspacesContext";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { FileLabel } from "./FileLabel";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import * as React from "react";
import {
  DataList,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
} from "@patternfly/react-core/dist/js/components/DataList";
import { Link } from "react-router-dom";
import { useRoutes } from "../navigation/Hooks";
import { TaskIcon } from "@patternfly/react-icons/dist/js/icons/task-icon";
import { WorkspaceDescriptor } from "../workspace/worker/api/WorkspaceDescriptor";
import { WorkspaceDescriptorDates } from "../workspace/components/WorkspaceDescriptorDates";

const FILE_DATA_LIST_HEIGHTS = {
  atRoot: 55 + 24,
  atSubDir: 74 + 24,
};

export function getFileDataListHeight(file: WorkspaceFile) {
  return file.relativePath.indexOf("/") >= 0 ? FILE_DATA_LIST_HEIGHTS.atSubDir : FILE_DATA_LIST_HEIGHTS.atRoot;
}

export function FileListItem(props: { file: WorkspaceFile; isEditable: boolean }) {
  const fileDirPath = props.file.relativeDirPath.split("/").join(" > ");
  const fileName = props.isEditable ? props.file.nameWithoutExtension : props.file.name;
  return (
    <>
      <Flex flexWrap={{ default: "nowrap" }}>
        <FlexItem style={{ minWidth: 0 /* This is to make the flex parent not overflow horizontally */ }}>
          <Tooltip distance={5} position={"top-start"} content={fileName}>
            <TextContent>
              <Text
                component={TextVariants.p}
                style={{
                  whiteSpace: "nowrap",
                  overflow: "hidden",
                  textOverflow: "ellipsis",
                }}
              >
                {fileName}
              </Text>
            </TextContent>
          </Tooltip>
        </FlexItem>
        <FlexItem>
          <FileLabel extension={props.file.extension} />
        </FlexItem>
      </Flex>
      <TextContent>
        <Text
          component={TextVariants.small}
          style={{
            whiteSpace: "nowrap",
            overflow: "hidden",
            textOverflow: "ellipsis",
          }}
        >
          <Tooltip distance={5} position={"top-start"} content={fileDirPath}>
            <span>{fileDirPath}</span>
          </Tooltip>
        </Text>
      </TextContent>
    </>
  );
}

export function FileDataListItem(props: { file: WorkspaceFile; isEditable: boolean }) {
  return (
    <DataListItemRow>
      <DataListItemCells
        dataListCells={[
          <DataListCell key="link" isFilled={false}>
            <FileListItem file={props.file} isEditable={props.isEditable} />
          </DataListCell>,
        ]}
      />
    </DataListItemRow>
  );
}

export function FileDataList(props: { file: WorkspaceFile; isEditable: boolean; style?: React.CSSProperties }) {
  return (
    <DataList aria-label="file-data-list" style={props.style}>
      <DataListItem style={{ border: 0 }}>
        {(!props.isEditable && (
          <FileDataListItem key={props.file.relativePath} file={props.file} isEditable={props.isEditable} />
        )) || (
          <FileLink file={props.file}>
            <FileDataListItem file={props.file} isEditable={props.isEditable} />
          </FileLink>
        )}
      </DataListItem>
    </DataList>
  );
}

export function FileLink(props: React.PropsWithChildren<{ file: WorkspaceFile; style?: React.CSSProperties }>) {
  const routes = useRoutes();
  return (
    <Link
      key={props.file.relativePath}
      to={routes.workspaceWithFilePath.path({
        workspaceId: props.file.workspaceId,
        fileRelativePath: props.file.relativePathWithoutExtension,
        extension: props.file.extension,
      })}
      style={props.style}
    >
      {props.children}
    </Link>
  );
}

export function SingleFileWorkspaceDataList(props: { workspaceDescriptor: WorkspaceDescriptor; file: WorkspaceFile }) {
  return (
    <DataList aria-label="file-data-list">
      <DataListItem style={{ border: 0 }}>
        <FileLink file={props.file}>
          <DataListItemRow>
            <DataListItemCells
              dataListCells={[
                <DataListCell key="link" isFilled={false}>
                  <SingleFileWorkspaceListItem
                    isBig={false}
                    file={props.file}
                    workspaceDescriptor={props.workspaceDescriptor}
                  />
                </DataListCell>,
              ]}
            />
          </DataListItemRow>
        </FileLink>
      </DataListItem>
    </DataList>
  );
}

export function SingleFileWorkspaceListItem(props: {
  isBig: boolean;
  workspaceDescriptor: WorkspaceDescriptor;
  file: WorkspaceFile;
}) {
  return (
    <>
      <Flex flexWrap={{ default: "nowrap" }}>
        <FlexItem style={{ minWidth: 0 /* This is to make the flex parent not overflow horizontally */ }}>
          <Tooltip distance={5} position={"top-start"} content={props.file.nameWithoutExtension}>
            <TextContent>
              <Text
                component={props.isBig ? TextVariants.h3 : TextVariants.p}
                style={{
                  whiteSpace: "nowrap",
                  overflow: "hidden",
                  textOverflow: "ellipsis",
                }}
              >
                <TaskIcon />
                &nbsp;&nbsp;
                {props.file.nameWithoutExtension}
              </Text>
            </TextContent>
          </Tooltip>
        </FlexItem>
        <FlexItem>
          <FileLabel extension={props.file.extension} />
        </FlexItem>
      </Flex>
      <WorkspaceDescriptorDates workspaceDescriptor={props.workspaceDescriptor} />
    </>
  );
}
