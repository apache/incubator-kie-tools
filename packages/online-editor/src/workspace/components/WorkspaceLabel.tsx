import { WorkspaceKind } from "../model/WorkspaceOrigin";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { GithubIcon } from "@patternfly/react-icons/dist/js/icons/github-icon";
import { GitlabIcon } from "@patternfly/react-icons/dist/js/icons/gitlab-icon";
import { CodeBranchIcon } from "@patternfly/react-icons/dist/js/icons/code-branch-icon";
import { PendingIcon } from "@patternfly/react-icons/dist/js/icons/pending-icon";
import * as React from "react";
import { useMemo } from "react";
import { WorkspaceDescriptor } from "../model/WorkspaceDescriptor";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { CodeIcon } from "@patternfly/react-icons/dist/js/icons/code-icon";

export function WorkspaceLabel(props: { descriptor?: WorkspaceDescriptor }) {
  const gitLabel = useMemo(() => {
    if (props.descriptor?.origin.kind !== WorkspaceKind.GIT) {
      return <></>;
    }

    if (props.descriptor?.origin.url.toString().includes("github")) {
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
  }, [props.descriptor]);
  return (
    <>
      {props.descriptor?.origin.kind === WorkspaceKind.GIT && (
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
