import * as React from "react";
import { DmnRunnerDrawerPanelContent } from "./DmnRunnerDrawerPanelContent";
import { Drawer, DrawerContent, DrawerContentBody } from "@patternfly/react-core/dist/js/components/Drawer";
import { useDmnRunner } from "./DmnRunnerContext";
import { EmbeddedEditorRef } from "@kie-tooling-core/editor/dist/embedded";

export function DmnRunnerDrawer(props: { editor?: EmbeddedEditorRef; children: React.ReactNode }) {
  const dmnRunner = useDmnRunner();
  return (
    <Drawer isInline={true} isExpanded={dmnRunner.isDrawerExpanded}>
      <DrawerContent
        className={
          !dmnRunner.isDrawerExpanded ? "kogito--editor__drawer-content-onClose" : "kogito--editor__drawer-content-open"
        }
        panelContent={<DmnRunnerDrawerPanelContent editor={props.editor} />}
      >
        <DrawerContentBody className={"kogito--editor__drawer-content-body"}>{props.children}</DrawerContentBody>
      </DrawerContent>
    </Drawer>
  );
}
