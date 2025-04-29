/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Switch } from "@patternfly/react-core/dist/esm/components/Switch";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { useSettings, useSettingsDispatch } from "./SettingsContext";
import { useCallback } from "react";
import ExclamationTriangleIcon from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";

export function EditorsSettingsTab() {
  const { settings } = useSettings();
  const settingsDispatch = useSettingsDispatch();

  const onChange = useCallback(
    (event, isChecked: boolean) => {
      settingsDispatch.set((settings) => {
        settings.editors.useLegacyDmnEditor = isChecked;
      });
    },
    [settingsDispatch]
  );

  return (
    <>
      <PageSection>
        <PageSection variant={"light"} isFilled={true}>
          <Form>
            <FormGroup label={"Use classic DMN Editor?"}>
              <Switch isChecked={settings.editors.useLegacyDmnEditor} onChange={onChange} />
            </FormGroup>
            <TextContent>
              <Text component={"small"}>
                The classic DMN Editor will be removed in future versions, but it is going to be available for some time
                until the new DMN Editor gets stable.
              </Text>
              <Text component={"small"}>
                <ExclamationTriangleIcon />
                &nbsp; Files created and/or modified on the new DMN Editor will{" "}
                <u>
                  <b>not</b>
                </u>{" "}
                be compatible with the classic DMN Editor, as they always saved as DMN 1.5. The classic DMN Editor is
                only compatible with DMN 1.0, 1.1, and 1.2.
              </Text>
            </TextContent>
          </Form>
        </PageSection>
      </PageSection>
    </>
  );
}
