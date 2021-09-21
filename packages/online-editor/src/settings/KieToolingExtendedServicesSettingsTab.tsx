import * as React from "react";
import { useCallback, useState } from "react";
import { ActionGroup, Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup } from "@patternfly/react-core/dist/js/components/InputGroup";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { useSettings } from "./SettingsContext";
import { Button } from "@patternfly/react-core/dist/js/components/Button";

export function KieToolingExtendedServicesSettingsTab() {
  const settings = useSettings();
  const [port, setPort] = useState(settings.kieToolingExtendedServices.port.get);

  const onSubmit = useCallback(
    (e: any) => {
      e.preventDefault();
      settings.kieToolingExtendedServices.port.set(port);
    },
    [settings, port]
  );

  return (
    <>
      <Page>
        <PageSection>
          <PageSection variant={"light"} isFilled={true} style={{ height: "100%" }}>
            <Form onSubmit={onSubmit}>
              <FormGroup
                isRequired={true}
                helperTextInvalid={""}
                validated={"default"}
                label={"Port"}
                fieldId={"github-pat"}
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
