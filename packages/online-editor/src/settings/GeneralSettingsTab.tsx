import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import * as React from "react";
import { useMemo, useState } from "react";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { useSettings } from "./SettingsContext";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { onlineI18nDictionaries, useOnlineI18n } from "../common/i18n";

export function GeneralSettingsTab() {
  const settings = useSettings();
  const { locale, setLocale } = useOnlineI18n();
  const [isLanguageSelectOpen, setLanguageSelectOpen] = useState(false);
  const availableLanguages = useMemo(() => Array.from(onlineI18nDictionaries.keys()), []);

  return (
    <Page>
      <PageSection>
        <PageSection variant={"light"} isFilled={true} style={{ height: "100%" }}>
          <Form>
            <FormGroup fieldId={"settings-general--language"}>
              <b>Language</b>
              <br />
              <Select
                variant={SelectVariant.single}
                aria-label="Language"
                onToggle={() => setLanguageSelectOpen((prev) => !prev)}
                onSelect={(e, v) => {
                  setLocale(v as string);
                  setLanguageSelectOpen(false);
                }}
                selections={availableLanguages.filter((k) => locale.startsWith(k))}
                isOpen={isLanguageSelectOpen}
              >
                {availableLanguages.map((language) => (
                  <SelectOption key={language} value={language} description="" />
                ))}
              </Select>
            </FormGroup>
          </Form>

          <br />
          <br />

          <b>Editors</b>
          <br />
          <Checkbox
            id="settings-general--is-dmn-guided-tour-enabled"
            isChecked={settings.general.guidedTourEnabled.get}
            onChange={settings.general.guidedTourEnabled.set}
            label={"Enable DMN Guided Tour"}
            description={"Show guided tour next time the DMN Editor is launched."}
          />
        </PageSection>
      </PageSection>
    </Page>
  );
}
