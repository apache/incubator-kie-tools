import * as React from "react";
import { DropdownGroup, DropdownItem } from "@patternfly/react-core/dist/js/components/Dropdown";
import { ActiveWorkspace } from "../workspace/model/ActiveWorkspace";
import { WorkspaceFile } from "../workspace/WorkspacesContext";
import {FileLabel} from "../workspace/pages/FileLabel";

export function NewFileDropdownItems(props: {
  addEmptyWorkspaceFile: (extension: string) => Promise<WorkspaceFile>;
  workspace: ActiveWorkspace | undefined;
}) {
  return (
    <DropdownGroup key={"new-file-group"} label="New file">
      <DropdownItem
        onClick={async () => {
          if (!props.workspace) {
            return;
          }
          await props.addEmptyWorkspaceFile("bpmn");
        }}
        key={"new-bpmn-item"}
        description="BPMN files are used to generate business processes"
      >
        Process <FileLabel extension={"bpmn"}/>
      </DropdownItem>
      <DropdownItem
        onClick={async () => {
          if (!props.workspace) {
            return;
          }
          await props.addEmptyWorkspaceFile("dmn");
        }}
        key={"new-dmn-item"}
        description="DMN files are used to generate decision models"
      >
        Decision <FileLabel extension={"dmn"}/>
      </DropdownItem>
      <DropdownItem
        onClick={async () => {
          if (!props.workspace) {
            return;
          }
          await props.addEmptyWorkspaceFile("pmml");
        }}
        key={"new-pmml-item"}
        description="PMML files are used to generate scorecards"
      >
        Scorecard <FileLabel extension={"pmml"}/>
      </DropdownItem>
    </DropdownGroup>
  );
}
