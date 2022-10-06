/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { WorkspaceKind } from "../worker/api/WorkspaceOrigin";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { GithubIcon } from "@patternfly/react-icons/dist/js/icons/github-icon";
import { GitlabIcon } from "@patternfly/react-icons/dist/js/icons/gitlab-icon";
import { CodeBranchIcon } from "@patternfly/react-icons/dist/js/icons/code-branch-icon";
import { PendingIcon } from "@patternfly/react-icons/dist/js/icons/pending-icon";
import * as React from "react";
import { useMemo } from "react";
import { WorkspaceDescriptor } from "../worker/api/WorkspaceDescriptor";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { CodeIcon } from "@patternfly/react-icons/dist/js/icons/code-icon";
import { UrlType, useImportableUrl } from "../hooks/ImportableUrlHooks";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";

export function WorkspaceLabel(props: { descriptor?: WorkspaceDescriptor }) {
  const workspaceImportableUrl = useImportableUrl(props.descriptor?.origin.url?.toString());

  const gitLabel = useMemo(() => {
    if (props.descriptor?.origin.kind !== WorkspaceKind.GIT) {
      return <></>;
    }

    if (workspaceImportableUrl.type === UrlType.GITHUB) {
      return (
        <Label>
          <GithubIcon />
          &nbsp;&nbsp;Repository
        </Label>
      );
    } else if (props.descriptor?.origin.url.toString().includes("gitlab")) {
      return (
        <Label>
          <GitlabIcon />
          &nbsp;&nbsp;Repository
        </Label>
      );
    } else {
      return (
        <Label>
          <CodeIcon />
          &nbsp;&nbsp;Git repository
        </Label>
      );
    }
  }, [props.descriptor, workspaceImportableUrl]);

  return (
    <Flex wrap={"nowrap"} style={{ display: "inline-flex" }}>
      {props.descriptor?.origin.kind === WorkspaceKind.GIT && (
        <FlexItem>
          <Tooltip
            content={`'${
              props.descriptor?.name
            }' is linked to a Git Repository. ${props.descriptor?.origin.url.toString()}`}
            position={"right"}
          >
            <>
              {gitLabel}
              &nbsp;&nbsp;
              <Label>
                <CodeBranchIcon />
                &nbsp;&nbsp;{props.descriptor?.origin.branch}
              </Label>
            </>
          </Tooltip>
        </FlexItem>
      )}
      {props.descriptor?.origin.kind === WorkspaceKind.GITHUB_GIST && (
        <FlexItem>
          <Tooltip
            content={`'${
              props.descriptor?.name
            }' is linked to a GitHub Gist. ${props.descriptor?.origin.url.toString()}`}
            position={"right"}
          >
            <Label>
              <GithubIcon />
              &nbsp;&nbsp;Gist
            </Label>
          </Tooltip>
        </FlexItem>
      )}
      {props.descriptor?.origin.kind === WorkspaceKind.LOCAL && (
        <FlexItem>
          <Tooltip
            content={`'${props.descriptor?.name}' is saved directly in the browser. Incognito windows don't have access to it.`}
            position={"right"}
          >
            <Label>
              <PendingIcon />
              &nbsp;&nbsp;Ephemeral
            </Label>
          </Tooltip>
        </FlexItem>
      )}
    </Flex>
  );
}
