import { WorkspaceKind } from "../model/WorkspaceOrigin";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { GithubIcon } from "@patternfly/react-icons/dist/js/icons/github-icon";
import { PendingIcon } from "@patternfly/react-icons/dist/js/icons/pending-icon";
import * as React from "react";
import { WorkspaceDescriptor } from "../model/WorkspaceDescriptor";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";

export function WorkspaceLabel(props: { descriptor?: WorkspaceDescriptor }) {
  return (
    <>
      {props.descriptor?.origin.kind === WorkspaceKind.GIST && (
        <Tooltip content={`This Folder is linked to a GitHub Gist. ${props.descriptor?.origin.url}`} position={"right"}>
          <Label>
            <GithubIcon />
            &nbsp;&nbsp;Gist
          </Label>
        </Tooltip>
      )}
      {props.descriptor?.origin.kind === WorkspaceKind.LOCAL && (
        <Tooltip
          content={`This Folder is saved directly in the browser. Incognito windows don't have access to it.`}
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
