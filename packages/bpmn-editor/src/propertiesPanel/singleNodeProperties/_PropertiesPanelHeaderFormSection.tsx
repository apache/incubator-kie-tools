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

import { FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { SectionHeader } from "@kie-tools/xyflow-react-kie-diagram/dist/propertiesPanel/SectionHeader";
import * as React from "react";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { useBpmnEditorStoreApi } from "../../store/StoreContext";
import { useBpmnEditorI18n } from "../../i18n";

export function PropertiesPanelHeaderFormSection({
  icon,
  children,
  title,
  shouldStartExpanded,
}: React.PropsWithChildren<{ title: undefined | string; icon: React.ReactNode; shouldStartExpanded?: boolean }>) {
  const { i18n, locale } = useBpmnEditorI18n();
  const [isSectionExpanded, setSectionExpanded] = React.useState<boolean>(shouldStartExpanded ?? true);

  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  return (
    <FormSection
      title={
        <SectionHeader
          icon={icon}
          expands={true}
          isSectionExpanded={isSectionExpanded}
          toogleSectionExpanded={() => setSectionExpanded((prev) => !prev)}
          title={title}
          action={
            <Button
              title={i18n.singleNodeProperties.close}
              variant={ButtonVariant.plain}
              onClick={() => {
                bpmnEditorStoreApi.setState((state) => {
                  state.propertiesPanel.isOpen = false;
                });
              }}
            >
              <TimesIcon />
            </Button>
          }
          locale={locale}
        />
      }
    >
      {isSectionExpanded && (
        <FormSection
          style={{
            paddingLeft: "20px",
            marginTop: "20px",
          }}
        >
          {children}
        </FormSection>
      )}
    </FormSection>
  );
}
