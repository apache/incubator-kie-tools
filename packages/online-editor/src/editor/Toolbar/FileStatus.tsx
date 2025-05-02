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

import React, { useEffect, useMemo } from "react";
import { EmbeddedEditorRef, useDirtyState } from "@kie-tools-core/editor/dist/embedded";
import { WorkspaceFile, useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import OutlinedClockIcon from "@patternfly/react-icons/dist/js/icons/outlined-clock-icon";
import DesktopIcon from "@patternfly/react-icons/dist/js/icons/desktop-icon";
import { useSharedValue } from "@kie-tools-core/envelope-bus/dist/hooks";
import OutlinedHddIcon from "@patternfly/react-icons/dist/js/icons/outlined-hdd-icon";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";

type Props = {
  workspace: ActiveWorkspace;
  workspaceFile: WorkspaceFile;
  editor: EmbeddedEditorRef | undefined;
};

export function FileStatus(props: Props) {
  const workspaces = useWorkspaces();
  const isEdited = useDirtyState(props.editor);

  const [flushes] = useSharedValue(
    workspaces.workspacesSharedWorker.workspacesWorkerBus.clientApi.shared.kieSandboxWorkspacesStorage_flushes
  );

  const isSaved = useMemo(() => {
    return !isEdited && flushes && !flushes.some((f) => f.includes(props.workspaceFile.workspaceId));
  }, [isEdited, flushes, props.workspaceFile.workspaceId]);

  // Prevent from closing without flushing before.
  useEffect(() => {
    if (isSaved) {
      return;
    }

    window.onbeforeunload = () => "Some changes are not written to disk yet.";
    return () => {
      window.onbeforeunload = null;
    };
  }, [isSaved]);

  return (
    <Flex gap={{ default: "gapMd" }}>
      <FlexItem>
        {(isEdited && (
          <Tooltip content={"Saving in memory..."} position={"bottom"}>
            <TextContent style={{ color: "gray", ...(!props.workspaceFile ? { visibility: "hidden" } : {}) }}>
              <Text
                style={{ display: "flex" }}
                aria-label={"Saving in memory..."}
                data-testid="is-saving-in-memory-indicator"
                component={TextVariants.small}
              >
                <Icon style={{ fontSize: "0.875rem", margin: 0 }}>
                  <OutlinedClockIcon style={{ margin: 0 }} />
                </Icon>
              </Text>
            </TextContent>
          </Tooltip>
        )) || (
          <Tooltip content={"File is in memory."} position={"bottom"}>
            <TextContent style={{ color: "gray", ...(!props.workspaceFile ? { visibility: "hidden" } : {}) }}>
              <Text
                style={{ display: "flex" }}
                aria-label={"File is in memory."}
                data-testid="is-saved-in-memory-indicator"
                component={TextVariants.small}
              >
                <Icon style={{ fontSize: "0.875rem", margin: 0 }}>
                  <DesktopIcon style={{ margin: 0 }} />
                </Icon>
              </Text>
            </TextContent>
          </Tooltip>
        )}
      </FlexItem>
      <FlexItem>
        {(!isSaved && (
          <Tooltip content={"Writing file..."} position={"bottom"}>
            <TextContent style={{ color: "gray", ...(!props.workspaceFile ? { visibility: "hidden" } : {}) }}>
              <Text
                style={{ display: "flex" }}
                aria-label={"Writing file..."}
                data-testid="is-writing-indicator"
                component={TextVariants.small}
              >
                <Icon style={{ fontSize: "0.875rem", margin: 0 }}>
                  <OutlinedClockIcon style={{ margin: 0 }} />
                </Icon>
              </Text>
            </TextContent>
          </Tooltip>
        )) || (
          <Tooltip content={"File is written on disk."} position={"bottom"}>
            <TextContent style={{ color: "gray", ...(!props.workspaceFile ? { visibility: "hidden" } : {}) }}>
              <Text
                style={{ display: "flex" }}
                aria-label={"File is written on disk."}
                data-testid="is-written-indicator"
                component={TextVariants.small}
              >
                <Icon style={{ fontSize: "0.875rem", margin: 0 }}>
                  <OutlinedHddIcon style={{ margin: 0 }} />
                </Icon>
              </Text>
            </TextContent>
          </Tooltip>
        )}
      </FlexItem>
    </Flex>
  );
}
