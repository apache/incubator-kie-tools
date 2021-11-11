import * as React from "react";
import { useCallback, useState } from "react";
import { ActionGroup, Form, FormAlert, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup } from "@patternfly/react-core/dist/js/components/InputGroup";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { useSettings, useSettingsDispatch } from "./SettingsContext";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { useKieToolingExtendedServices } from "../editor/KieToolingExtendedServices/KieToolingExtendedServicesContext";
import { KieToolingExtendedServicesStatus } from "../editor/KieToolingExtendedServices/KieToolingExtendedServicesStatus";

export function KieToolingExtendedServicesSettingsTab() {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const kieToolingExtendedServices = useKieToolingExtendedServices();
  const [port, setPort] = useState(settings.kieToolingExtendedServices.port);

  const onSubmit = useCallback(
    (e: any) => {
      e.preventDefault();
      settingsDispatch.kieToolingExtendedServices.setPort(port);
    },
    [settingsDispatch, port]
  );

  return (
    <>
      <Page>
        <PageSection>
          <PageSection variant={"light"} isFilled={true} style={{ height: "100%" }}>
            <Form onSubmit={onSubmit}>
              <FormAlert>
                {kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING && (
                  <Alert
                    variant="success"
                    title={"You are connected to KIE Tooling Extended Services"}
                    aria-live="polite"
                    isInline
                  />
                )}
                {kieToolingExtendedServices.status !== KieToolingExtendedServicesStatus.RUNNING && (
                  <Alert
                    variant="danger"
                    title={"You are not connected to KIE Tooling Extended Services"}
                    aria-live="polite"
                    isInline
                  />
                )}
              </FormAlert>
              <FormGroup
                isRequired={true}
                helperTextInvalid={""}
                validated={"default"}
                label={"Port"}
                fieldId={"port-input"}
              >
                <InputGroup>
                  <TextInput
                    id="port-input"
                    name="port"
                    aria-describedby="port-text-input-helper"
                    placeholder={""}
                    validated={"default"}
                    value={port}
                    onChange={setPort}
                    autoFocus={true}
                  />
                </InputGroup>
              </FormGroup>
              <ActionGroup>
                <Button
                  id="dmn-dev-sandbox-config-save-button"
                  key="save"
                  variant="primary"
                  onClick={onSubmit}
                  data-testid="save-config-button"
                >
                  Change
                </Button>
              </ActionGroup>
            </Form>
          </PageSection>
        </PageSection>
      </Page>
    </>
  );
}
