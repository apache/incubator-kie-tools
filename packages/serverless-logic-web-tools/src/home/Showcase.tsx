/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { TextContent, Text } from "@patternfly/react-core/dist/js/components/Text";
import { Sample, SampleCard, SampleType } from "./SampleCard";
import { ReactComponent as GreetingsSvg } from "../../static/samples/greetings/greetings.svg";
import { ReactComponent as GreetingsKafkaSvg } from "../../static/samples/greetings-kafka/greetings-kafka.svg";
import { ReactComponent as ProductsDashboardSvg } from "../../static/samples/products-dashboard/products-dashboard.svg";
import { ReactComponent as SwfReportSvg } from "../../static/samples/swf-report/swf-report.svg";
import { ReactComponent as KitchensinkSvg } from "../../static/samples/kitchensink/kitchensink.svg";
import { ReactComponent as CompensationSvg } from "../../static/samples/compensation/compensation.svg";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";

export const samples: Array<Sample> = [
  {
    name: "Greetings",
    fileName: "greetings",
    svg: GreetingsSvg,
    description: `This example shows a single Operation State with one action that calls the "greeting" function. The workflow data input is assumed to be the name of the person to greet. The results of the action is assumed to be the greeting for the provided persons name, which is added to the states data and becomes the workflow data output.`,
    type: SampleType.SW_JSON,
  },
  {
    name: "Greetings with Kafka events",
    fileName: "greetings-kafka",
    svg: GreetingsKafkaSvg,
    description: `This example is similar to the Greetings sample, but this time the "greeting" function is triggered via an Apache Kafka event. The event payload is assumed to be the name of the person to greet and in which language. The results of the action is assumed to be the greeting for the provided persons name, which is added to the states data and becomes the workflow data output.`,
    type: SampleType.SW_JSON,
  },
  {
    name: "Compensation",
    fileName: "compensation",
    svg: CompensationSvg,
    description: `This example contains a simple workflow service that illustrate compensation handling. This is simple workflow that expects a boolean shouldCompensate to indicate if compensation segment (which is composed by two inject states) should be executed or not. The process result is a boolean field compensated which value should match shouldCompensate.`,
    type: SampleType.SW_JSON,
  },
  {
    name: "Dashbuilder Kitchensink",
    fileName: "kitchensink",
    svg: KitchensinkSvg,
    description: `Explore all Dashbuilder components. Navigate in tabs to learn about Dashbuilder concepts and check how to use and the look of all visual components available for use in Dashbuilder.`,
    type: SampleType.DASH_YML,
  },
  {
    name: "Products Dashboard",
    fileName: "products-dashboard",
    svg: ProductsDashboardSvg,
    description:
      'In this example we show a dashboard from a "products" dataset. It displays the dataset information in a bar chart grouping products by counting them. At the end we have a table listing all products.',
    type: SampleType.DASH_YML,
  },
  {
    name: "Serverless Workflow Report",
    fileName: "swf-report",
    svg: SwfReportSvg,
    description:
      "This example is for Serverless Workflow Data Index. It connects to data index to retrieve information about running and completed workflows. The top row shows a general overview of workflow states, the middle row contains charts to compare workflows execution. This information can be filtered using the small combo box on right side. Finally a table shows all the workflows details.",
    type: SampleType.DASH_YML,
  },
];

export function Showcase() {
  return (
    <>
      <TextContent>
        <Text component="h1">Samples Showcase</Text>
      </TextContent>
      <br />
      <Gallery
        hasGutter={true}
        minWidths={{ sm: "calc(100%/3.1 - 16px)", default: "100%" }}
        style={{
          overflowX: "auto",
          gridAutoFlow: "column",
          gridAutoColumns: "minmax(calc(100%/3.1 - 16px),1fr)",
          paddingBottom: "8px",
          paddingRight: "var(--pf-c-page__main-section--xl--PaddingRight)",
        }}
      >
        {samples.map((sample) => (
          <SampleCard sample={sample} key={sample.fileName} />
        ))}
      </Gallery>
    </>
  );
}
