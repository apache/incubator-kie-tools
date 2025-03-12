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

import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import { ServerErrors } from "@kie-tools/runtime-tools-components/dist/components/ServerErrors";
import { componentOuiaProps, OUIAProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { Card } from "@patternfly/react-core/dist/js/components/Card";
import {
  Drawer,
  DrawerContent,
  DrawerContentBody,
  DrawerHead,
  DrawerPanelContent,
} from "@patternfly/react-core/dist/js/components/Drawer";
import { Tab, Tabs, TabTitleText } from "@patternfly/react-core/dist/js/components/Tabs";
import isEmpty from "lodash/isEmpty";
import React, { useEffect, useRef, useState, useCallback } from "react";
import { Form, FormInfo } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { FormDetailsDriver } from "../../../api/FormDetailsDriver";
import FormDisplayerContainer from "../../containers/FormDisplayerContainer/FormDisplayerContainer";
import FormEditor from "../FormEditor/FormEditor";
import { useFormDetailsContext } from "../contexts/FormDetailsContext";

export interface FormDetailsProps {
  isEnvelopeConnectedToChannel: boolean;
  driver: FormDetailsDriver;
  formData: FormInfo;
  targetOrigin: string;
}

export interface ResizableContent {
  doResize: () => void;
}

const FormDetails: React.FC<FormDetailsProps & OUIAProps> = ({
  isEnvelopeConnectedToChannel,
  driver,
  formData,
  ouiaId,
  ouiaSafe,
  targetOrigin,
}) => {
  const [activeTab, setActiveTab] = useState<number>(0);
  const [formContent, setFormContent] = useState<Form>({} as Form);
  const [error, setError] = useState<any>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const editorResize = useRef<ResizableContent>({} as ResizableContent);
  const appContext = useFormDetailsContext();

  useEffect(() => {
    if (isEnvelopeConnectedToChannel) {
      init();
    }
  }, [isEnvelopeConnectedToChannel]);

  const init = useCallback(async () => {
    try {
      if (formData) {
        const response = await driver.getFormContent(formData.name);
        setFormContent(response);
        setIsLoading(false);
      }
    } catch (error) {
      setError(error);
    }
  }, [driver, formData]);

  const saveContent = useCallback(
    (args: { isSource?: boolean; isConfig?: boolean }) => (content: string) => {
      try {
        setFormContent((prev) => {
          if (args.isSource) {
            const newForm = { ...prev, source: content };
            driver.saveFormContent(formData.name, newForm);
            appContext.updateContent(newForm);
            return newForm;
          }
          if (args.isConfig) {
            const newForm = { ...prev, configuration: { ...prev.configuration, resources: JSON.parse(content) } };
            driver.saveFormContent(formData.name, newForm);
            appContext.updateContent(newForm);
            return newForm;
          }
          return prev;
        });
      } catch (error) {
        setError(error);
      }
    },
    [appContext, driver, formData.name]
  );

  const onTabSelect = useCallback((_event: any, tabIndex: number): void => {
    setActiveTab(tabIndex);
  }, []);

  const getSource = useCallback(() => {
    if (!isEmpty(formContent)) {
      return formContent?.source ?? "";
    }
    return "";
  }, [formContent]);

  const getType = useCallback(() => {
    if (!isEmpty(formData)) {
      return formData.type;
    }
    return "";
  }, [formData]);

  const getConfig = useCallback(() => {
    if (!isEmpty(formContent)) {
      return JSON.stringify(formContent?.configuration.resources, null, 2);
    }
    return "";
  }, [formContent]);

  const getFormLanguage = useCallback((args: { formType?: string; isSource?: boolean; isConfig?: boolean }) => {
    if (args.isSource && args.formType) {
      if (args.formType.toLowerCase() === "tsx") {
        return "typescript";
      }
      if (args.formType.toLowerCase() === "html") {
        return "html";
      }
    }
    if (args.isConfig) {
      return "json";
    }
    return "txt";
  }, []);

  return error ? (
    <ServerErrors error={error} variant={"large"} />
  ) : (
    <div {...componentOuiaProps(ouiaId, "form-details", ouiaSafe)} style={{ height: "100%" }}>
      {!isLoading ? (
        <Drawer isStatic>
          <DrawerContent
            panelContent={
              <DrawerPanelContent
                isResizable
                defaultSize={"800px"}
                onResize={() => {
                  editorResize?.current?.doResize();
                }}
              >
                <DrawerHead style={{ height: "100%" }}>
                  {formContent && Object.keys(formContent)[0] && Object.keys(formContent)[0].length > 0 && (
                    <span>
                      <FormDisplayerContainer formContent={formContent} targetOrigin={targetOrigin} />
                    </span>
                  )}
                </DrawerHead>
              </DrawerPanelContent>
            }
          >
            <Tabs isFilled activeKey={activeTab} onSelect={onTabSelect}>
              <Tab
                eventKey={0}
                title={<TabTitleText>Source</TabTitleText>}
                id="source-tab"
                aria-labelledby="source-tab"
              >
                <DrawerContentBody
                  style={{
                    padding: "0px",
                    background: "var(--pf-v5-c-page__main-section--BackgroundColor)",
                  }}
                >
                  {activeTab === 0 && (
                    <FormEditor
                      textContent={getSource()}
                      saveContent={saveContent({ isSource: true })}
                      formLanguage={getFormLanguage({ formType: getType(), isSource: true })}
                      ref={editorResize}
                    />
                  )}
                </DrawerContentBody>
              </Tab>
              <Tab
                eventKey={1}
                title={<TabTitleText>Connections</TabTitleText>}
                id="config-tab"
                aria-labelledby="config-tab"
              >
                <DrawerContentBody
                  style={{
                    padding: "0px",
                    background: "var(--pf-v5-c-page__main-section--BackgroundColor)",
                  }}
                >
                  {activeTab === 1 && (
                    <FormEditor
                      textContent={getConfig()}
                      saveContent={saveContent({ isConfig: true })}
                      formLanguage={getFormLanguage({ isConfig: true })}
                      ref={editorResize}
                    />
                  )}
                </DrawerContentBody>
              </Tab>
            </Tabs>
          </DrawerContent>
        </Drawer>
      ) : (
        <Card>
          <KogitoSpinner spinnerText="Loading form ..." />
        </Card>
      )}
    </div>
  );
};

export default FormDetails;
