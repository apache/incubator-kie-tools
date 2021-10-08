import * as React from "react";
import { DropdownGroup, DropdownItem } from "@patternfly/react-core/dist/js/components/Dropdown";
import { WorkspaceFile } from "../workspace/WorkspacesContext";
import { FileLabel } from "../workspace/pages/FileLabel";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { SupportedFileExtensions } from "../common/GlobalContext";

export function AddFileDropdownItems(props: {
  addEmptyWorkspaceFile: (extension: SupportedFileExtensions) => Promise<WorkspaceFile>;
}) {
  return (
    <>
      <DropdownGroup key={"add-file-group"} label="Add file">
        <DropdownItem
          onClick={() => props.addEmptyWorkspaceFile("bpmn")}
          key={"add-bpmn-item"}
          description="BPMN files are used to generate business processes"
        >
          <b>
            <Flex>
              <FlexItem>Process</FlexItem>
              <FlexItem>
                <FileLabel extension={"bpmn"} />
              </FlexItem>
            </Flex>
          </b>
        </DropdownItem>
      </DropdownGroup>
      <DropdownGroup key={"dmn-group"}>
        <DropdownItem
          onClick={() => props.addEmptyWorkspaceFile("dmn")}
          key={"add-dmn-item"}
          description="DMN files are used to generate decision models"
        >
          <b>
            <Flex>
              <FlexItem>Decision</FlexItem>
              <FlexItem>
                <FileLabel extension={"dmn"} />
              </FlexItem>
            </Flex>
          </b>
        </DropdownItem>
      </DropdownGroup>
      <DropdownGroup key={"pmml-group"}>
        <DropdownItem
          onClick={() => props.addEmptyWorkspaceFile("pmml")}
          key={"add-pmml-item"}
          description="PMML files are used to generate scorecards"
        >
          <b>
            <Flex>
              <FlexItem>Scorecard</FlexItem>
              <FlexItem>
                <FileLabel extension={"pmml"} />
              </FlexItem>
            </Flex>
          </b>
        </DropdownItem>
      </DropdownGroup>
    </>
  );
}
