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
import { useCallback } from "react";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup, InputGroupItem } from "@patternfly/react-core/dist/js/components/InputGroup";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { useEnv } from "../env/hooks/EnvContext";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers";

export function CorsProxySettingsTab() {
  const { env } = useEnv();

  const onSubmit = useCallback((e: any) => {
    e.preventDefault();
  }, []);

  return (
    <>
      <Page>
        <PageSection>
          <PageSection variant={"light"} isFilled={true} style={{ height: "100%" }}>
            <Form onSubmit={onSubmit}>
              <FormGroup
                isRequired={true}
                // helperTextInvalid={""}
                // validated={"default"}
                label={"URL"}
                fieldId={"url-input"}
              >
                <InputGroup>
                  <InputGroupItem isFill>
                    <TextInput
                      isDisabled={true}
                      id="url-input"
                      name="url"
                      aria-describedby="url-text-input-helper"
                      placeholder={""}
                      validated={"default"}
                      value={env.KIE_SANDBOX_CORS_PROXY_URL}
                      autoFocus={true}
                    />
                  </InputGroupItem>
                </InputGroup>
                <HelperText>
                  {/* (
                  <HelperTextItem variant="default" icon={ValidatedOptions.default}>
                    {""}
                  </HelperTextItem>
                  ) */}
                </HelperText>
              </FormGroup>
              <TextContent>
                <Text component={"small"}>
                  {`The CORS Proxy allows ${env.KIE_SANDBOX_APP_NAME} to communicate with Git and Cloud providers.`}
                </Text>
              </TextContent>
            </Form>
          </PageSection>
        </PageSection>
      </Page>
    </>
  );
}
