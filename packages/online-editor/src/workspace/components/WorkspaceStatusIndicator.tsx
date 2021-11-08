import { ActiveWorkspace } from "../model/ActiveWorkspace";
import { useWorkspaceGitStatusPromise } from "../hooks/WorkspaceHooks";
import * as React from "react";
import { useMemo } from "react";
import { WorkspaceKind } from "../model/WorkspaceOrigin";
import { PromiseStateWrapper } from "../hooks/PromiseState";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { SyncIcon } from "@patternfly/react-icons/dist/js/icons/sync-icon";
import { SecurityIcon } from "@patternfly/react-icons/dist/js/icons/security-icon";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";

export function WorkspaceStatusIndicator(props: { workspace: ActiveWorkspace }) {
  const workspaceGitStatusPromise = useWorkspaceGitStatusPromise(props.workspace);

  const outOfSyncText = useMemo(() => {
    switch (props.workspace.descriptor.origin.kind) {
      case WorkspaceKind.LOCAL:
        return "There are new changes since your last download.";
      case WorkspaceKind.GITHUB_GIST:
      case WorkspaceKind.GIT:
        return "There are new changes since you last synced.";
      default:
        throw new Error();
    }
  }, [props.workspace]);

  const syncedText = useMemo(() => {
    switch (props.workspace.descriptor.origin.kind) {
      case WorkspaceKind.LOCAL:
        return "All changes were downloaded.";
      case WorkspaceKind.GITHUB_GIST:
      case WorkspaceKind.GIT:
        return "All files are synced.";
      default:
        throw new Error();
    }
  }, [props.workspace]);

  return (
    <PromiseStateWrapper
      promise={workspaceGitStatusPromise}
      pending={
        <Title headingLevel={"h6"} style={{ display: "inline", padding: "10px", cursor: "default" }}>
          <Tooltip content={"Checking status..."} position={"right"}>
            <small>
              <SyncIcon color={"gray"} />
            </small>
          </Tooltip>
        </Title>
      }
      resolved={({ hasLocalChanges, isSynced }) => (
        <>
          {(!isSynced && (
            <Title headingLevel={"h6"} style={{ display: "inline", padding: "10px", cursor: "default" }}>
              <Tooltip content={outOfSyncText} position={"right"}>
                <small>
                  <SecurityIcon color={"gray"} />
                </small>
              </Tooltip>
            </Title>
          )) || (
            <Title headingLevel={"h6"} style={{ display: "inline", padding: "10px", cursor: "default" }}>
              <Tooltip content={syncedText} position={"right"}>
                <small>
                  <CheckCircleIcon color={"green"} />
                </small>
              </Tooltip>
            </Title>
          )}
          {hasLocalChanges && (
            <Title headingLevel={"h6"} style={{ display: "inline", padding: "10px", cursor: "default" }}>
              <Tooltip content={"You have local changes."} position={"right"}>
                <small>
                  <i>M</i>
                </small>
              </Tooltip>
            </Title>
          )}
        </>
      )}
    />
  );
}
