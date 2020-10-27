/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import * as React from "react";
import { Model } from "@kogito-tooling/pmml-editor-marshaller";
import { Header } from "../../Header/molecules";
import { Level, PageSection, PageSectionVariants } from "@patternfly/react-core";

interface UnsupportedModelPageProps {
  path: string;
  model: Model;
}

export const UnsupportedModelPage = (props: UnsupportedModelPageProps) => {
  return (
    <>
      <div data-testid="unsupported-model-page">
        <PageSection variant={PageSectionVariants.light}>
          <Header title={props.path} />
        </PageSection>

        <PageSection isFilled={true}>
          <Level>
            <pre>{JSON.stringify(props.model, undefined, 2)}</pre>
          </Level>
        </PageSection>
      </div>
    </>
  );
};
