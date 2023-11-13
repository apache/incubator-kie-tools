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
import { Model } from "@kie-tools/pmml-editor-marshaller";
import { LandingPageHeader } from "../../LandingPage/molecules";
import { PageSection, PageSectionVariants } from "@patternfly/react-core/dist/js/components/Page";
import { Level } from "@patternfly/react-core/dist/js/layouts/Level";

interface UnsupportedModelPageProps {
  path: string;
  model: Model;
}

export const UnsupportedModelPage = (props: UnsupportedModelPageProps) => {
  return (
    <>
      <div data-testid="unsupported-model-page">
        <PageSection variant={PageSectionVariants.light}>
          <LandingPageHeader title={props.path} />
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
