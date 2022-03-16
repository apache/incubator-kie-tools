import * as React from "React";
import { useState, useCallback, useEffect } from "react";
import { Toolbar, ToolbarContent, ToolbarItem } from "@patternfly/react-core/dist/js/components/Toolbar";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { AngleLeftIcon } from "@patternfly/react-icons/dist/js/icons/angle-left-icon";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { useHistory } from "react-router";
import { useRoutes } from "../../navigation/Hooks";
import { PromiseState } from "../../workspace/hooks/PromiseState";
import { WorkspaceFile } from "../../workspace/WorkspacesContext";
import { useOpenShift } from "../../openshift/OpenShiftContext";
import { useSettings } from "../../settings/SettingsContext";
import { EmbeddedEditorRef } from "@kie-tools-core/editor/dist/embedded";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { isConfigValid } from "../../settings/openshift/OpenShiftSettingsConfig";
import { useWorkspacePromise } from "../../workspace/hooks/WorkspaceHooks";

enum FormValiationOptions {
  INITIAL = "INITIAL",
  ERROR = "ERROR",
  SUCCESS = "SUCCESS",
}
export interface EditorToolbarProps {
  workspaceFilePromise: PromiseState<WorkspaceFile>;
  editor?: EmbeddedEditorRef;
}

export function EditorToolbar(props: EditorToolbarProps) {
  const routes = useRoutes();
  const history = useHistory();
  const openshift = useOpenShift();
  const settings = useSettings();
  const workspacePromise = useWorkspacePromise(props.workspaceFilePromise.data?.workspaceId);
  const [deployStatus, setDeployStatus] = useState(FormValiationOptions.INITIAL);
  const [isLoading, setLoading] = useState(false);
  const [workspaceName, setWorkspaceName] = useState<string>("");

  useEffect(() => {
    if (workspacePromise.data?.descriptor.name) {
      setWorkspaceName(workspacePromise.data?.descriptor.name);
    }
  }, [workspacePromise.data?.descriptor.name]);

  const onWorkspaceNameChange = useCallback((newValue: string) => {
    setWorkspaceName(newValue);
  }, []);

  const onDeploy = useCallback(async () => {
    setDeployStatus(FormValiationOptions.INITIAL);

    const content = await props.editor?.getContent();

    if (!content) {
      setDeployStatus(FormValiationOptions.ERROR);
      return;
    }

    setLoading(true);
    const success = await openshift.deploy({
      openShiftConfig: settings.openshift.config,
      workflow: {
        name: workspaceName,
        content: content,
      },
      kafkaConfig: settings.apacheKafka.config,
    });
    setLoading(false);

    setDeployStatus(success ? FormValiationOptions.SUCCESS : FormValiationOptions.ERROR);
  }, [props.editor, workspaceName, openshift, settings.openshift.config, settings.apacheKafka.config]);

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
                  onChange={onWorkspaceNameChange}
                  data-testid="workflow-name-text-field"
                  placeholder="Workspace Name"
                />
              </ToolbarItem>
              <ToolbarItem>
                <Button
                  isDisabled={!isConfigValid(settings.openshift.config) || workspaceName.trim().length === 0}
                  key="deploy"
                  variant="primary"
                  onClick={onDeploy}
                  isLoading={isLoading}
                  spinnerAriaValueText={isLoading ? "Loading" : undefined}
                >
                  {isLoading ? "Deploying" : "Deploy"}
                </Button>
              </ToolbarItem>
            </ToolbarContent>
            {deployStatus === FormValiationOptions.ERROR && (
              <Alert
                className="pf-u-mb-md"
                variant="danger"
                title={"Something went wrong while deploying. Check your OpenShift connection and try again."}
                aria-live="polite"
                data-testid="alert-deploy-error"
              />
            )}
            {deployStatus === FormValiationOptions.SUCCESS && (
              <Alert
                className="pf-u-mb-md"
                variant="info"
                title={"The deployment has been started successfully"}
                aria-live="polite"
                data-testid="alert-deploy-success"
              />
            )}
          </Toolbar>
        </FlexItem>
      </Flex>
    </PageSection>
  );
}
