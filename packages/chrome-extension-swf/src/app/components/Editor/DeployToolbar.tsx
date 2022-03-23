import * as React from "react";
import { useState, useCallback } from "react";
import { Toolbar, ToolbarContent, ToolbarItem } from "@patternfly/react-core/dist/js/components/Toolbar";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { useOpenShift } from "../../openshift/OpenShiftContext";
import { useSettings } from "../../settings/SettingsContext";
import { EmbeddedEditorRef } from "@kie-tools-core/editor/dist/embedded";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { isConfigValid } from "../../settings/openshift/OpenShiftSettingsConfig";
import { ActiveWorkspace } from "../../workspace/model/ActiveWorkspace";
import { useWorkspaces } from "../../workspace/WorkspacesContext";

enum FormValiationOptions {
  INITIAL = "INITIAL",
  ERROR = "ERROR",
  SUCCESS = "SUCCESS",
}
export interface DeployToolbarProps {
  workspace: ActiveWorkspace;
  editor?: EmbeddedEditorRef;
}

export function DeployToolbar(props: DeployToolbarProps) {
  const openshift = useOpenShift();
  const settings = useSettings();
  const workspaces = useWorkspaces();
  const [deployStatus, setDeployStatus] = useState(FormValiationOptions.INITIAL);
  const [isLoading, setLoading] = useState(false);

  const onDeploy = useCallback(async () => {
    setDeployStatus(FormValiationOptions.INITIAL);

    const content = await props.editor?.getContent();

    if (!content) {
      setDeployStatus(FormValiationOptions.ERROR);
      return;
    }

    setLoading(true);
    const resourceName = await openshift.deploy({
      openShiftConfig: settings.openshift.config,
      workflow: {
        name: props.workspace.descriptor.name ?? "workflow",
        content: content,
      },
      kafkaConfig: settings.apacheKafka.config,
      resourceName: props.workspace.descriptor.deploymentResourceName,
    });
    setLoading(false);

    setDeployStatus(resourceName ? FormValiationOptions.SUCCESS : FormValiationOptions.ERROR);
    if (resourceName) {
      workspaces.descriptorService.setDeploymentResourceName(props.workspace.descriptor.workspaceId, resourceName);
    }
  }, [
    props.editor,
    props.workspace.descriptor,
    openshift,
    settings.openshift.config,
    settings.apacheKafka.config,
    workspaces.descriptorService,
  ]);

  return (
    <Flex>
      <FlexItem>
        <Toolbar>
          <ToolbarContent style={{ paddingLeft: 0 }}>
            <ToolbarItem>
              <Button
                isDisabled={
                  !isConfigValid(settings.openshift.config) || props.workspace.descriptor.name.trim().length === 0
                }
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
  );
}
