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
import { PageSection, PageSectionVariants } from "@patternfly/react-core";
import { Header } from "../../Header/molecules";
import { Scorecard } from "@kogito-tooling/pmml-editor-marshaller";
import { coalesce } from "../../../utils";
import { CorePropertiesTable } from "../organisms";

interface EditorPageProps {
  path: string;
  model: Scorecard;
}

export const ScorecardEditorPage = (props: EditorPageProps) => {
  return (
    <div data-testid="editor-page">
      <PageSection variant={PageSectionVariants.light}>
        <Header title={coalesce(props.model.modelName, "<Unnamed>")} />
      </PageSection>

      <PageSection isFilled={true}>
        <CorePropertiesTable
          baselineScore={props.model.baselineScore}
          baselineMethod={props.model.baselineMethod}
          initialScore={props.model.initialScore}
          useReasonCodes={props.model.useReasonCodes}
        />
      </PageSection>
    </div>
  );
};
