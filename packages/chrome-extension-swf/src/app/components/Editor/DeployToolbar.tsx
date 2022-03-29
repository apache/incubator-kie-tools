import * as React from "react";
import { useState, useCallback, useEffect } from "react";
import { Toolbar, ToolbarContent, ToolbarItem } from "@patternfly/react-core/dist/js/components/Toolbar";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { useOpenShift } from "../../openshift/OpenShiftContext";
import { useSettings } from "../../settings/SettingsContext";
import { EmbeddedEditorRef } from "@kie-tools-core/editor/dist/embedded";
import { Alert, AlertActionCloseButton } from "@patternfly/react-core/dist/js/components/Alert";
import { isConfigValid } from "../../settings/openshift/OpenShiftSettingsConfig";
import { ActiveWorkspace } from "../../workspace/model/ActiveWorkspace";
import { useWorkspaces } from "../../workspace/WorkspacesContext";
import { useAlert } from "../../alerts/Alerts";
import { useAlertsController } from "../../alerts/AlertsProvider";

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
  const [alerts] = useAlertsController();

  const setDeployError = useAlert(
    alerts,
    useCallback(({ close }) => {
      return (
        <Alert
          className="pf-u-mb-md"
          variant="danger"
          title={"Something went wrong while deploying. Check your OpenShift connection and try again."}
          aria-live="polite"
          data-testid="alert-deploy-error"
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, [])
  );

  const setDeploySuccess = useAlert(
    alerts,
    useCallback(({ close }) => {
      return (
        <Alert
          className="pf-u-mb-md"
          variant="info"
          title={"The deployment has been started successfully"}
          aria-live="polite"
          data-testid="alert-deploy-success"
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, [])
  );

  const onDeploy = useCallback(async () => {
    setDeployStatus(FormValiationOptions.INITIAL);

    const content = await props.editor?.getContent();

    if (!content) {
      setDeployStatus(FormValiationOptions.ERROR);
      setDeployError.show();
      return;
    }

    setLoading(true);
    const resourceName = await openshift.deploy({
      openShiftConfig: settings.openshift.config,
      workflow: {
        name: (props.workspace.descriptor.name ?? "workflow") + ".sw.json",
        content: content,
      },
      kafkaConfig: settings.apacheKafka.config,
      serviceAccountConfig: settings.serviceAccount.config,
      resourceName: props.workspace.descriptor.deploymentResourceName,
    });
    setLoading(false);

    const deployStatus = resourceName ? FormValiationOptions.SUCCESS : FormValiationOptions.ERROR;
    setDeployStatus(deployStatus);
    if (deployStatus === FormValiationOptions.SUCCESS) {
      setDeploySuccess.show();
    } else {
      setDeployError.show();
    }
    if (resourceName) {
      workspaces.descriptorService.setDeploymentResourceName(props.workspace.descriptor.workspaceId, resourceName);
    }
  }, [
    props.editor,
    props.workspace.descriptor.name,
    props.workspace.descriptor.deploymentResourceName,
    props.workspace.descriptor.workspaceId,
    openshift,
    settings.openshift.config,
    settings.apacheKafka.config,
    settings.serviceAccount.config,
    setDeployError,
    setDeploySuccess,
    workspaces.descriptorService,
  ]);

  useEffect(() => {
    if (props.workspace.files.find((file) => file.name.includes("openapi"))) {
      return;
    }
    const fetchOpenApiSpec = async () => {
      if (!props.workspace.descriptor.deploymentResourceName) {
        return;
      }
      const openApiContents = await openshift.fetchOpenApiFile(
        settings.openshift.config,
        props.workspace.descriptor.deploymentResourceName
      );
      if (openApiContents) {
        await workspaces.addFile({
          fs: await workspaces.fsService.getWorkspaceFs(props.workspace.descriptor.workspaceId),
          workspaceId: props.workspace.descriptor.workspaceId,
          name: "openapi",
          destinationDirRelativePath: ".",
          content: openApiContents,
          extension: "yml",
        });
      }
    };
    fetchOpenApiSpec();
  }, [
    openshift,
    props.workspace.descriptor.deploymentResourceName,
    props.workspace.descriptor.workspaceId,
    props.workspace.files,
    settings.openshift.config,
    workspaces,
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
        </Toolbar>
      </FlexItem>
    </Flex>
  );
}
