import * as React from "react";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Switch } from "@patternfly/react-core/dist/esm/components/Switch";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { useSettings, useSettingsDispatch } from "./SettingsContext";
import { useCallback } from "react";
import ExclamationTriangleIcon from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";

export function EditorsSettingsTab() {
  const { settings } = useSettings();
  const settingsDispatch = useSettingsDispatch();

  const onChange = useCallback(
    (isChecked: boolean) => {
      settingsDispatch.set((settings) => {
        settings.editors.useLegacyDmnEditor = isChecked;
      });
    },
    [settingsDispatch]
  );

  return (
    <>
      <Page>
        <PageSection>
          <PageSection variant={"light"} isFilled={true} style={{ height: "100%" }}>
            <Form>
              <FormGroup label={"Use legacy DMN Editor?"}>
                <Switch isChecked={settings.editors.useLegacyDmnEditor} onChange={onChange} />
              </FormGroup>
              <TextContent>
                <Text component={"small"}>
                  The legacy DMN Editor will be removed in future versions, but it is going to be available for some
                  time until the new DMN Editor gets stable.
                </Text>
                <Text component={"small"}>
                  <ExclamationTriangleIcon />
                  &nbsp; Files created and/or modified on the new DMN Editor will{" "}
                  <u>
                    <b>not</b>
                  </u>{" "}
                  be compatible with the legacy DMN Editor, as they always saved as DMN 1.5. The legacy DMN Editor is
                  only compatible with DMN 1.0, 1.1, and 1.2.
                </Text>
              </TextContent>
            </Form>
          </PageSection>
        </PageSection>
      </Page>
    </>
  );
}
