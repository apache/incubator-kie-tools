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

import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { CodeBranchIcon } from "@patternfly/react-icons/dist/js/icons/code-branch-icon";
import { GithubIcon } from "@patternfly/react-icons/dist/js/icons/github-icon";
import { GitlabIcon } from "@patternfly/react-icons/dist/js/icons/gitlab-icon";
import { PendingIcon } from "@patternfly/react-icons/dist/js/icons/pending-icon";
import { WorkspaceKind } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceOrigin";
import * as React from "react";
import { useMemo } from "react";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { CodeIcon } from "@patternfly/react-icons/dist/js/icons/code-icon";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import BitbucketIcon from "@patternfly/react-icons/dist/js/icons/bitbucket-icon";
import { UrlType, useImportableUrl } from "../../importFromUrl/ImportableUrlHooks";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";

export function WorkspaceLabel(props: { descriptor?: WorkspaceDescriptor }) {
  const workspaceImportableUrl = useImportableUrl(props.descriptor?.origin.url?.toString());

  const gitLabel = useMemo(() => {
    if (props.descriptor?.origin.kind !== WorkspaceKind.GIT) {
      return <></>;
    }

    if (workspaceImportableUrl.type === UrlType.GITHUB_DOT_COM) {
      return (
        <Label>
          <Icon size="md">
            <GithubIcon />
          </Icon>
          &nbsp;&nbsp;Repository
        </Label>
      );
    } else if (props.descriptor?.origin.url.toString().includes("gitlab")) {
      return (
        <Label>
          <Icon size="md">
            <GitlabIcon />
          </Icon>
          &nbsp;&nbsp;Repository
        </Label>
      );
    } else if (props.descriptor?.origin.url.toString().includes("bitbucket")) {
      return (
        <Label>
          <Icon size="md">
            <BitbucketIcon />
          </Icon>
          &nbsp;&nbsp;Repository
        </Label>
      );
    } else {
      return (
        <Label>
          <Icon size="md">
            <CodeIcon />
          </Icon>
          &nbsp;&nbsp;Git repository
        </Label>
      );
    }
  }, [props.descriptor, workspaceImportableUrl]);

  return (
    <Flex
      flexWrap={{ default: "nowrap" }}
      justifyContent={{ default: "justifyContentFlexStart" }}
      spaceItems={{ default: "spaceItemsSm" }}
      style={{ display: "inline-flex" }}
    >
      {switchExpression(props.descriptor?.origin.kind, {
        GIT: (
          <>
            <FlexItem>
              <Tooltip
                content={`'${props.descriptor?.name}' is linked to a Git Repository. ${props.descriptor?.origin.url}`}
                position={"right"}
              >
                {gitLabel}
              </Tooltip>
            </FlexItem>
            <FlexItem>
              <Label>
                <Icon size="md">
                  <CodeBranchIcon />
                </Icon>
                &nbsp;&nbsp;{props.descriptor?.origin.branch}
              </Label>
            </FlexItem>
          </>
        ),
        GITHUB_GIST: (
          <FlexItem>
            <Tooltip
              content={`'${props.descriptor?.name}' is linked to a GitHub Gist. ${props.descriptor?.origin.url}`}
              position={"right"}
            >
              <Label>
                <Icon size="md">
                  <GithubIcon />
                </Icon>
                &nbsp;&nbsp;Gist
              </Label>
            </Tooltip>
          </FlexItem>
        ),
        BITBUCKET_SNIPPET: (
          <FlexItem>
            <Tooltip
              content={`'${props.descriptor?.name}' is linked to a Bitbucket Snippet. ${props.descriptor?.origin.url}`}
              position={"right"}
            >
              <Label>
                <Icon size="md">
                  <BitbucketIcon />
                </Icon>
                &nbsp;&nbsp;Snippet
              </Label>
            </Tooltip>
          </FlexItem>
        ),
        LOCAL: (
          <FlexItem>
            <Tooltip
              content={`'${props.descriptor?.name}' is saved directly in the browser. Incognito windows don't have access to it.`}
              position={"right"}
            >
              <Label>
                <Icon size="md">
                  <PendingIcon />
                </Icon>
                &nbsp;&nbsp;Ephemeral
              </Label>
            </Tooltip>
          </FlexItem>
        ),
      })}
    </Flex>
  );
}
