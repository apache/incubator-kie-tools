import * as React from "React";
import { useState, useCallback } from "react";
import {
  Toolbar,
  ToolbarContent,
  ToolbarItem,
  ToolbarItemProps,
} from "@patternfly/react-core/dist/js/components/Toolbar";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { EllipsisVIcon } from "@patternfly/react-icons/dist/js/icons/ellipsis-v-icon";
import { SaveIcon } from "@patternfly/react-icons/dist/js/icons/save-icon";
import { AngleLeftIcon } from "@patternfly/react-icons/dist/js/icons/angle-left-icon";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup, InputGroupText } from "@patternfly/react-core/dist/js/components/InputGroup";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import HelpIcon from "@patternfly/react-icons/dist/js/icons/help-icon";
import { useHistory } from "react-router";
import { useRoutes } from "../../navigation/Hooks";
import { PromiseState } from "../../workspace/hooks/PromiseState";
import { WorkspaceFile } from "../../workspace/WorkspacesContext";

export interface EditorToolbarProps {
  workspace: PromiseState<WorkspaceFile>;
}

export function EditorToolbar(props: EditorToolbarProps) {
  const routes = useRoutes();
  const history = useHistory();

  const [workspaceName, setWorkspaceName] = useState<string>("");

  const onWorkflowNameChange = useCallback((newValue: string) => {
    setWorkspaceName(newValue);
  }, []);

  return (
    <PageSection type={"nav"} variant={"light"} padding={{ default: "noPadding" }}>
      <Flex>
        <FlexItem>
          <Button
            className={"kie-tools--masthead-hoverable"}
            variant={ButtonVariant.plain}
            onClick={() => history.push({ pathname: routes.home.path({}) })}
          >
            <AngleLeftIcon />
          </Button>
        </FlexItem>
        <FlexItem>
          <Toolbar>
            <ToolbarContent style={{ paddingLeft: 0 }}>
              <ToolbarItem>
                <TextInput
                  autoComplete={"off"}
                  type="text"
                  id="workflow-name-field"
                  name="workflow-name-field"
                  aria-label="Workflow name field"
                  aria-describedby="workflow-name-field-helper"
                  value={workspaceName}
                  onChange={onWorkflowNameChange}
                  data-testid="workflow-name-text-field"
                  placeholder="Workspace Name"
                />
              </ToolbarItem>
            </ToolbarContent>
          </Toolbar>
        </FlexItem>
      </Flex>
    </PageSection>
  );
}
