import * as React from "react";
import { useCallback } from "react";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { useSettings } from "./SettingsContext";
import { OpenShiftInstanceStatus } from "../editor/DmnDevSandbox/OpenShiftInstanceStatus";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { OpenShiftSettingsTabSimpleConfig } from "./OpenShiftSettingsTabSimpleConfig";
import { obfuscate } from "./GitHubSettingsTab";
import { saveConfigCookie } from "./OpenShiftSettingsConfig";

export function OpenShiftSettingsTab() {
  const settings = useSettings();

  const onDisconnect = useCallback(() => {
    settings.openshift.status.set(OpenShiftInstanceStatus.DISCONNECTED);
    const newConfig = {
      namespace: settings.openshift.config.get.namespace,
      host: settings.openshift.config.get.host,
      token: "",
    };
    settings.openshift.config.set(newConfig);
    saveConfigCookie(newConfig);
  }, [settings.openshift]);

  return (
    <Page>
      <PageSection>
        {settings.openshift.status.get === OpenShiftInstanceStatus.CONNECTED && (
          <EmptyState>
            <EmptyStateIcon icon={CheckCircleIcon} color={"var(--pf-global--success-color--100)"} />
            <TextContent>
              <Text component={"h2"}>{"You're connected to OpenShift."}</Text>
            </TextContent>
            <EmptyStateBody>
              <TextContent>Deploying DMN decisions is enabled.</TextContent>
              <br />
              <TextContent>
                <b>Token: </b>
                <i>{obfuscate(settings.openshift.config.get.token)}</i>
              </TextContent>
              <TextContent>
                <b>Host: </b>
                <i>{settings.openshift.config.get.host}</i>
              </TextContent>
              <TextContent>
                <b>Namespace (project): </b>
                <i>{settings.openshift.config.get.namespace}</i>
              </TextContent>
              <br />
              <Button variant={ButtonVariant.tertiary} onClick={onDisconnect}>
                Disconnect
              </Button>
            </EmptyStateBody>
          </EmptyState>
        )}
        {settings.openshift.status.get === OpenShiftInstanceStatus.DISCONNECTED && <OpenShiftSettingsTabSimpleConfig />}
      </PageSection>
    </Page>
  );
}
