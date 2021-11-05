import { WorkspaceKind } from "../model/WorkspaceOrigin";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { GithubIcon } from "@patternfly/react-icons/dist/js/icons/github-icon";
import { CodeBranchIcon } from "@patternfly/react-icons/dist/js/icons/code-branch-icon";
import { PendingIcon } from "@patternfly/react-icons/dist/js/icons/pending-icon";
import * as React from "react";
import { WorkspaceDescriptor } from "../model/WorkspaceDescriptor";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";

export function WorkspaceLabel(props: { descriptor?: WorkspaceDescriptor }) {
  return (
    <>
      {props.descriptor?.origin.kind === WorkspaceKind.GITHUB && (
        <Tooltip
          content={`'${
            props.descriptor?.name
          }' is linked to a GitHub Repository. ${props.descriptor?.origin.url.toString()}`}
          position={"right"}
        >
          <>
            <Label>
              <GithubIcon />
              &nbsp;&nbsp;Repo
            </Label>
            &nbsp;&nbsp;
            <Label>
              <CodeBranchIcon />
              &nbsp;&nbsp;{props.descriptor?.origin.branch}
            </Label>
          </>
        </Tooltip>
      )}
      {props.descriptor?.origin.kind === WorkspaceKind.GITHUB_GIST && (
        <Tooltip
          content={`'${props.descriptor?.name}' is linked to a GitHub Gist. ${props.descriptor?.origin.url.toString()}`}
          position={"right"}
        >
          <Label>
            <GithubIcon />
            &nbsp;&nbsp;Gist
          </Label>
        </Tooltip>
      )}
      {props.descriptor?.origin.kind === WorkspaceKind.LOCAL && (
        <Tooltip
          content={`'${props.descriptor?.name}' is saved directly in the browser. Incognito windows don't have access to it.`}
          position={"right"}
        >
          <Label>
            <PendingIcon />
            &nbsp;&nbsp;Ephemeral
          </Label>
        </Tooltip>
      )}
    </>
  );
}
