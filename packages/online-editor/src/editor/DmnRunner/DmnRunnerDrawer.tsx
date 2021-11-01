import * as React from "react";
import { DmnRunnerDrawerPanelContent } from "./DmnRunnerDrawerPanelContent";
import { Drawer, DrawerContent, DrawerContentBody } from "@patternfly/react-core/dist/js/components/Drawer";
import { useDmnRunner } from "./DmnRunnerContext";
import { NotificationsPanelController } from "../NotificationsPanel/NotificationsPanel";
import { WorkspaceFile } from "../../workspace/WorkspacesContext";
import { EditorPageDockDrawerController } from "../EditorPageDockDrawer";

export function DmnRunnerDrawer(props: {
  workspaceFile: WorkspaceFile;
  editorPageDock: EditorPageDockDrawerController | undefined;
  notificationsPanel: NotificationsPanelController | undefined;
  children: React.ReactNode;
}) {
  const dmnRunner = useDmnRunner();
  return (
    <Drawer isInline={true} isExpanded={dmnRunner.isDrawerExpanded}>
      <DrawerContent
        className={
          !dmnRunner.isDrawerExpanded ? "kogito--editor__drawer-content-onClose" : "kogito--editor__drawer-content-open"
        }
        panelContent={
          <DmnRunnerDrawerPanelContent
            workspaceFile={props.workspaceFile}
            editorPageDock={props.editorPageDock}
            notificationsPanel={props.notificationsPanel}
          />
        }
      >
        <DrawerContentBody className={"kogito--editor__drawer-content-body"}>{props.children}</DrawerContentBody>
      </DrawerContent>
    </Drawer>
  );
}
