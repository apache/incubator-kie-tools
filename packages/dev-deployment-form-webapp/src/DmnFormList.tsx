/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { useEffect } from "react";
import { useApp } from "./AppContext";
import { Page } from "@patternfly/react-core/dist/js/components/Page";
import { DmnFormToolbar } from "./DmnFormToolbar";
import { useHistory } from "react-router";
import { routes } from "./Routes";

export function DmnFormList() {
  const app = useApp();
  const history = useHistory();

  useEffect(() => {
    if (app.data?.forms.length === 1) {
      history.push({
        pathname: routes.form.path({ modelName: app.data.forms[0].modelName }),
      });
    }
  }, [app.data?.forms, history]);

  return (
    <Page data-testid="dmn-form-list" header={<DmnFormToolbar />}>
      <div className="kogito--dmn-list">
        <div className="kogito--dmn-list__content">
          <Page className={"kogito--dmn-list__content-page"}>
            {/* <PageSection className={"kogito--dmn-list__content-header inputs"}>
              <TextContent>
                <Text component={TextVariants.h3}>{i18n.terms.inputs}</Text>
              </TextContent>
            </PageSection>
            <div className={"kogito--dmn-form__content-body"}>
              <PageSection className={"kogito--dmn-form__content-body-input"}>
                <FormDmn
                  formInputs={formInputs}
                  setFormInputs={setFormInputs}
                  formError={formError}
                  setFormError={setFormError}
                  formSchema={jsonSchema}
                  id={"form"}
                  showInlineError={true}
                  notificationsPanel={false}
                  onSubmit={onSubmit}
                  placeholder={true}
                  autoSave={true}
                  autoSaveDelay={AUTO_SAVE_DELAY}
                  submitField={() => <></>}
                  errorsField={() => <></>}
                  locale={locale}
                />
              </PageSection>
            </div> */}
          </Page>
        </div>
        <div className="kogito--dmn-list__content">
          <Page className={"kogito--dmn-list__content-page"}>
            {/* <PageSection className={"kogito--dmn-form__content-header"}>
              <TextContent>
                <Text component={TextVariants.h3}>{i18n.terms.outputs}</Text>
              </TextContent>
            </PageSection>
            <div className={"kogito--dmn-form__content-body"}>
              <PageSection isFilled={true} className="kogito--dmn-form__content-body-output">
                <FormDmnOutputs
                  results={formOutputs}
                  differences={formOutputDiffs}
                  locale={locale}
                  notificationsPanel={false}
                />
              </PageSection>
            </div> */}
          </Page>
        </div>
      </div>
    </Page>
  );
}
