import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import * as React from "react";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { useSettings } from "./SettingsContext";

export function GeneralSettingsTab() {
  const settings = useSettings();
  return (
    <Page>
      <PageSection>
        <Checkbox
          id="settings-general--is-dmn-guided-tour-enabled"
          isChecked={settings.general.guidedTourEnabled.get}
          onChange={settings.general.guidedTourEnabled.set}
          label={"Enable DMN Guided Tour"}
          description={"Show guided tour next time the DMN Editor is launched."}
        />
      </PageSection>
    </Page>
  );
}
