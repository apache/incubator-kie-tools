/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { Form } from "@patternfly/react-core/dist/js/components/Form";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { useCallback, useEffect, useState } from "react";
import { useSettings, useSettingsDispatch } from "../SettingsContext";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { saveConfigCookie } from "./FeaturePreviewConfig";

export function FeaturePreviewSettingsTab() {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const [config, setConfig] = useState(settings.featurePreview.config);
  const [stunnerEnabled, setStunnerEnabled] = useState(config.stunnerEnabled);

  useEffect(() => {
    settingsDispatch.featurePreview.setConfig(config);
    saveConfigCookie(config);
  }, [config, settingsDispatch.featurePreview]);

  const onStunnerEnabledChanged = useCallback(
    (isEnabled: boolean) => {
      setStunnerEnabled(isEnabled);
      setConfig({ ...config, stunnerEnabled: isEnabled });
    },
    [config]
  );

  return (
    <Page>
      <PageSection>
        <PageSection variant={"light"} isFilled={true} style={{ height: "100%" }}>
          <Form>
            <TextContent>
              <Text component={TextVariants.h3}>Feature Preview</Text>
            </TextContent>
            <TextContent>
              <Text component={TextVariants.small}>
                Data you provide here is necessary for configuring the preview of features that are not fully supported
                yet. All information is locally stored in your browser and never shared with anyone.
              </Text>
            </TextContent>
            <Checkbox
              id="feature-preview-enable-stunner"
              label="Kogito Serverless Workflow Visualization"
              description={"Enable/disable Kogito Serverless Workflow Visualization for JSON files."}
              isChecked={stunnerEnabled}
              onChange={onStunnerEnabledChanged}
            />
          </Form>
        </PageSection>
      </PageSection>
    </Page>
  );
}
