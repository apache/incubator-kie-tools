import { ActiveWorkspace } from "../model/ActiveWorkspace";
import { useWorkspaceGitStatusPromise } from "../hooks/WorkspaceHooks";
import * as React from "react";
import { useCallback, useEffect, useMemo } from "react";
import { WorkspaceKind } from "../model/WorkspaceOrigin";
import { PromiseStateWrapper } from "../hooks/PromiseState";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { SyncIcon } from "@patternfly/react-icons/dist/js/icons/sync-icon";
import { SecurityIcon } from "@patternfly/react-icons/dist/js/icons/security-icon";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { usePrevious } from "../../common/Hooks";
import { useNavigationBlocker } from "../../navigation/Hooks";
import { matchPath } from "react-router";
import { useGlobals } from "../../common/GlobalContext";

function Indicator(props: { workspace: ActiveWorkspace; isSynced: boolean; hasLocalChanges: boolean }) {
  return (
    <>
      {(props.workspace.descriptor.origin.kind === WorkspaceKind.GITHUB_GIST ||
        props.workspace.descriptor.origin.kind === WorkspaceKind.GIT) && (
        <>
          {(!props.isSynced && (
            <Title headingLevel={"h6"} style={{ display: "inline", padding: "10px", cursor: "default" }}>
              <Tooltip content={`There are new changes since your last sync.`} position={"right"}>
                <small>
                  <SecurityIcon color={"gray"} />
                </small>
              </Tooltip>
            </Title>
          )) || (
            <Title headingLevel={"h6"} style={{ display: "inline", padding: "10px", cursor: "default" }}>
              <Tooltip content={`All files are synced.`} position={"right"}>
                <small>
                  <CheckCircleIcon color={"green"} />
                </small>
              </Tooltip>
            </Title>
          )}
        </>
      )}
      {props.hasLocalChanges && (
        <Title headingLevel={"h6"} style={{ display: "inline", padding: "10px", cursor: "default" }}>
          <Tooltip content={"You have local changes."} position={"right"}>
            <small>
              <i>M</i>
            </small>
          </Tooltip>
        </Title>
      )}
    </>
  );
}

export function WorkspaceStatusIndicator(props: { workspace: ActiveWorkspace }) {
  const globals = useGlobals();
  const workspaceGitStatusPromise = useWorkspaceGitStatusPromise(props.workspace);

  const isEverythingPersistedByTheUser = useMemo(() => {
    return (
      workspaceGitStatusPromise.data &&
      workspaceGitStatusPromise.data.isSynced &&
      !workspaceGitStatusPromise.data.hasLocalChanges
    );
  }, [workspaceGitStatusPromise]);

  // // Prevent from closing accidentally
  // TODO: Enable this via env var.
  // useEffect(() => {
  //   if (isEverythingPersistedByTheUser) {
  //     return;
  //   }
  //
  //   window.onbeforeunload = () => "You have unsaved changes.";
  //   return () => {
  //     window.onbeforeunload = null;
  //   };
  // }, [props.workspace, isEverythingPersistedByTheUser]);

  // Prevent from navigating away
  useNavigationBlocker(
    `block-navigation-for-${props.workspace.descriptor.workspaceId}`,
    useCallback(
      ({ location }) => {
        const match = matchPath<{ workspaceId: string }>(location.pathname, {
          strict: true,
          exact: true,
          sensitive: false,
          path: globals.routes.workspaceWithFilePath.path({
            workspaceId: ":workspaceId",
            fileRelativePath: ":fileRelativePath",
            extension: ":extension",
          }),
        });

        if (match?.params.workspaceId === props.workspace.descriptor.workspaceId) {
          return false;
        }

        return !isEverythingPersistedByTheUser;
      },
      [globals, isEverythingPersistedByTheUser, props.workspace.descriptor.workspaceId]
    )
  );

  // We use this trick to prevent the icon from blinking while updating.
  const prev = usePrevious(workspaceGitStatusPromise);

  return (
    <PromiseStateWrapper
      promise={workspaceGitStatusPromise}
      pending={
        <>
          {(prev?.data && (
            <Indicator
              workspace={props.workspace}
              hasLocalChanges={prev.data.hasLocalChanges}
              isSynced={prev.data.isSynced}
            />
          )) || (
            <Title headingLevel={"h6"} style={{ display: "inline", padding: "10px", cursor: "default" }}>
              <Tooltip content={"Checking status..."} position={"right"}>
                <small>
                  <SyncIcon color={"gray"} />
                </small>
              </Tooltip>
            </Title>
          )}
        </>
      }
      resolved={({ hasLocalChanges, isSynced }) => (
        <Indicator workspace={props.workspace} hasLocalChanges={hasLocalChanges} isSynced={isSynced} />
      )}
    />
  );
}
