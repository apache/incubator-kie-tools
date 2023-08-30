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

import React, { useState } from "react";
import {
  Dropdown,
  DropdownGroup,
  DropdownItem,
  DropdownToggle,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import CaretDownIcon from "@patternfly/react-icons/dist/js/icons/caret-down-icon";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { PushToGitAlertActionLinks } from "./GitIntegration/PushToGitAlertActionLinks";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import ExternalLinkAltIcon from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { useNavigationStatus, useRoutes } from "../../navigation/Hooks";
import { WorkspaceKind } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceOrigin";
import { UrlType } from "../../importFromUrl/ImportableUrlHooks";
import { GIT_ORIGIN_REMOTE_NAME } from "@kie-tools-core/workspaces-git-fs/dist/constants/GitConstants";
import { useGitIntegration } from "./GitIntegration/GitIntegrationContextProvider";

export function VsCodeDropdownMenu(props: { workspace: ActiveWorkspace }) {
  const routes = useRoutes();
  const navigationStatus = useNavigationStatus();
  const [isVsCodeDropdownOpen, setVsCodeDropdownOpen] = useState(false);

  const {
    workspaceImportableUrl,
    auth: { changeGitAuthSessionId, authSessionSelectFilter },
    git: { canPushToGitRepository, pushToGitRepository },
  } = useGitIntegration();

  if (
    props.workspace.descriptor.origin.kind !== WorkspaceKind.GIT ||
    workspaceImportableUrl.type !== UrlType.GITHUB_DOT_COM
  ) {
    return null;
  }

  return (
    <FlexItem
      style={{
        minWidth: "137px",
      }}
    >
      <Dropdown
        className={"kie-tools--masthead-hoverable"}
        isPlain={true}
        onSelect={() => setVsCodeDropdownOpen(false)}
        isOpen={isVsCodeDropdownOpen}
        position={"right"}
        toggle={
          <DropdownToggle toggleIndicator={null} onToggle={setVsCodeDropdownOpen}>
            <Flex flexWrap={{ default: "nowrap" }}>
              <FlexItem
                style={{
                  minWidth: 0 /* This is to make the flex parent not overflow horizontally */,
                }}
              >
                <Tooltip distance={5} position={"top-start"} content={props.workspace.descriptor.name}>
                  <TextContent>
                    <Text
                      component={TextVariants.small}
                      style={{
                        whiteSpace: "nowrap",
                        overflow: "hidden",
                        textOverflow: "ellipsis",
                      }}
                    >
                      <img
                        style={{
                          minWidth: "14px",
                          maxWidth: "14px",
                          marginTop: "-2px",
                          verticalAlign: "middle",
                        }}
                        alt="vscode-logo-blue"
                        src={routes.static.images.vscodeLogoBlue.path({})}
                      />
                      &nbsp;&nbsp;
                      {`Open "${props.workspace.descriptor.name}"`}
                    </Text>
                  </TextContent>
                </Tooltip>
              </FlexItem>
              <FlexItem>
                <CaretDownIcon />
              </FlexItem>
            </Flex>
          </DropdownToggle>
        }
        dropdownItems={[
          <DropdownGroup key={"open-in-vscode"}>
            {navigationStatus.shouldBlockNavigationTo({ pathname: "__external" }) && (
              <>
                <Alert
                  isInline={true}
                  variant={"warning"}
                  title={"You have new changes to Push"}
                  actionLinks={
                    <PushToGitAlertActionLinks
                      changeGitAuthSessionId={changeGitAuthSessionId}
                      workspaceDescriptor={props.workspace.descriptor}
                      canPush={canPushToGitRepository}
                      authSessionSelectFilter={authSessionSelectFilter}
                      remoteRef={`${GIT_ORIGIN_REMOTE_NAME}/${props.workspace.descriptor.origin.branch}`}
                      onPush={pushToGitRepository}
                    />
                  }
                >
                  {`Opening '${props.workspace.descriptor.name}' on vscode.dev won't show your latest changes.`}
                </Alert>
                <Divider />
              </>
            )}
            <DropdownItem
              style={{ minWidth: "400px" }}
              href={`https://vscode.dev/github${
                new URL(props.workspace.descriptor.origin.url).pathname.endsWith(".git")
                  ? new URL(props.workspace.descriptor.origin.url).pathname.replace(".git", "")
                  : new URL(props.workspace.descriptor.origin.url).pathname
              }/tree/${props.workspace.descriptor.origin.branch}`}
              target={"_blank"}
              icon={<ExternalLinkAltIcon />}
              description={`The '${props.workspace.descriptor.origin.branch}' branch will be opened.`}
            >
              vscode.dev
            </DropdownItem>
            <Divider />
            <DropdownItem
              href={`vscode://vscode.git/clone?url=${props.workspace.descriptor.origin.url.toString()}`}
              target={"_blank"}
              icon={<ExternalLinkAltIcon />}
              description={"The default branch will be opened."}
            >
              VS Code Desktop
            </DropdownItem>
          </DropdownGroup>,
        ]}
      />
    </FlexItem>
  );
}
