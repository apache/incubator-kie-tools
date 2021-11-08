import { ActiveWorkspace } from "../model/ActiveWorkspace";
import { useIsWorkspaceModifiedPromise } from "../hooks/WorkspaceHooks";
import { useMemo } from "react";
import { WorkspaceKind } from "../model/WorkspaceOrigin";
import { PromiseStateWrapper } from "../hooks/PromiseState";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { SyncIcon } from "@patternfly/react-icons/dist/js/icons/sync-icon";
import { SecurityIcon } from "@patternfly/react-icons/dist/js/icons/security-icon";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import * as React from "react";

export function WorkspaceStatusIndicator(props: { workspace: ActiveWorkspace }) {
  const isWorkspaceModifiedPromise = useIsWorkspaceModifiedPromise(props.workspace);

  const isModifiedText = useMemo(() => {
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

  const isSyncedText = useMemo(() => {
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
      promise={isWorkspaceModifiedPromise}
      pending={
        <Title headingLevel={"h6"} style={{ display: "inline", padding: "10px", cursor: "default" }}>
          <Tooltip content={"Checking status..."} position={"right"}>
            <small>
              <SyncIcon color={"gray"} />
            </small>
          </Tooltip>
        </Title>
      }
      resolved={(isModified) => (
        <>
          {(isModified && (
            <Title headingLevel={"h6"} style={{ display: "inline", padding: "10px", cursor: "default" }}>
              <Tooltip content={isModifiedText} position={"right"}>
                <small>
                  <SecurityIcon color={"gray"} />
                </small>
              </Tooltip>
            </Title>
          )) || (
            <Title headingLevel={"h6"} style={{ display: "inline", padding: "10px", cursor: "default" }}>
              <Tooltip content={isSyncedText} position={"right"}>
                <small>
                  <CheckCircleIcon color={"green"} />
                </small>
              </Tooltip>
            </Title>
          )}
        </>
      )}
    />
  );
}
