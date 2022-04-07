import * as React from "react";
import { useState, useCallback, useEffect, useMemo } from "react";
import { Toolbar, ToolbarContent, ToolbarItem } from "@patternfly/react-core/dist/js/components/Toolbar";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { useOpenShift } from "../../openshift/OpenShiftContext";
import { useSettings } from "../../settings/SettingsContext";
import { Alert, AlertActionCloseButton } from "@patternfly/react-core/dist/js/components/Alert";
import { ActiveWorkspace } from "../../workspace/model/ActiveWorkspace";
import { useWorkspaces } from "../../workspace/WorkspacesContext";
import { useAlert } from "../../alerts/Alerts";
import { useAlertsController } from "../../alerts/AlertsProvider";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { OpenShiftInstanceStatus } from "../../openshift/OpenShiftInstanceStatus";
import { EmbeddedEditorRef } from "@kie-tools-core/editor/dist/embedded";

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
  const [canDeploy, setCanDeploy] = useState(false);

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
          title={
            <>
              <Spinner size={"sm"} />
              &nbsp;&nbsp; The deployment has been started. Its associated OpenAPI spec will be uploaded to Service
              Registry as soon as the deployment is up and running. Please do not close this browser tab until the
              operation is completed.
            </>
          }
          aria-live="polite"
          data-testid="alert-deploy-success"
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, [])
  );

  const openApiUploadSuccess = useAlert(
    alerts,
    useCallback(({ close }) => {
      return (
        <Alert
          className="pf-u-mb-md"
          variant="success"
          title={"The OpenAPI spec has been uploaded to Service Registry successfully."}
          aria-live="polite"
          data-testid="alert-upload-success"
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, [])
  );

  const fetchOpenApiSpec = useCallback(
    async (deploymentResourceName: string) => {
      if (props.workspace.files.find((file) => file.name.includes("openapi"))) {
        return true;
      }

      const openApiContents = await openshift.fetchOpenApiFile(settings.openshift.config, deploymentResourceName);

      if (!openApiContents) {
        return false;
      }

      await workspaces.addFile({
        fs: await workspaces.fsService.getWorkspaceFs(props.workspace.descriptor.workspaceId),
        workspaceId: props.workspace.descriptor.workspaceId,
        name: "openapi",
        destinationDirRelativePath: ".",
        content: openApiContents,
        extension: "json",
      });

      await openshift.uploadOpenApiToServiceRegistry(
        openApiContents,
        `${props.workspace.descriptor.name} ${deploymentResourceName}`
      );

      return true;
    },
    [openshift, props.workspace.descriptor, props.workspace.files, settings.openshift.config, workspaces]
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

    const status = resourceName ? FormValiationOptions.SUCCESS : FormValiationOptions.ERROR;
    setDeployStatus(status);

    if (status === FormValiationOptions.SUCCESS) {
      await workspaces.descriptorService.setDeploymentResourceName(
        props.workspace.descriptor.workspaceId,
        resourceName
      );
      setCanDeploy(false);
      setDeploySuccess.show();

      const fetchOpenApiTask = window.setInterval(async () => {
        const success = await fetchOpenApiSpec(resourceName);

        if (success) {
          window.clearInterval(fetchOpenApiTask);
          setDeploySuccess.close();
          openApiUploadSuccess.show();
        }
      }, 5000);
    } else {
      setDeployError.show();
    }
  }, [
    fetchOpenApiSpec,
    openshift,
    props.editor,
    props.workspace.descriptor,
    setDeployError,
    setDeploySuccess,
    openApiUploadSuccess,
    settings.apacheKafka.config,
    settings.openshift.config,
    settings.serviceAccount.config,
    workspaces.descriptorService,
  ]);

  useEffect(() => {
    if (settings.openshift.status !== OpenShiftInstanceStatus.CONNECTED) {
      setCanDeploy(false);
      return;
    }

    if (!props.workspace.descriptor.deploymentResourceName) {
      setCanDeploy(true);
      return;
    }

    setCanDeploy(false);
    fetchOpenApiSpec(props.workspace.descriptor.deploymentResourceName);
  }, [
    fetchOpenApiSpec,
    props.workspace.descriptor.deploymentResourceName,
    settings.openshift.config,
    settings.openshift.status,
  ]);

  const onFetchArtifacts = useCallback(async () => {
    const artifacts = await openshift.fetchServiceRegistryArtifacts();
    console.log(artifacts);
  }, [openshift]);

  return (
    <Flex>
      <FlexItem>
        <Toolbar>
          <ToolbarContent style={{ paddingLeft: 0 }}>
            <ToolbarItem>
              <Button
                isDisabled={!canDeploy}
                key="deploy"
                variant="primary"
                onClick={onDeploy}
                isLoading={isLoading}
                spinnerAriaValueText={isLoading ? "Loading" : undefined}
              >
                {isLoading ? "Deploying" : "Deploy"}
              </Button>
              {/* Temporary Button to check fetching artifacts */}
              <Button key="fetch-artifacts" variant="primary" onClick={onFetchArtifacts}>
                {"Fetch artifacts"}
              </Button>
            </ToolbarItem>
          </ToolbarContent>
        </Toolbar>
      </FlexItem>
    </Flex>
  );
}
