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
import { useEffect, useState } from "react";
import { TextContent, Text } from "@patternfly/react-core/dist/js/components/Text";
import { SampleCard } from "./SampleCard";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";
import { Sample } from "./sampleApi";
import { SampleCardSkeleton } from "./SampleCardSkeleton";
import { SamplesLoadError } from "./SamplesLoadError";
import { useSampleDispatch } from "./hooks/SampleContext";

const priority = {
  ["serverless-workflow"]: 1,
  ["dashbuilder"]: 2,
  ["serverless-decision"]: 3,
};

export function Showcase() {
  const sampleDispatch = useSampleDispatch();
  const [loading, setLoading] = useState<boolean>(true);
  const [samples, setSamples] = useState<Sample[]>([]);
  const [sampleLoadingError, setSampleLoadingError] = useState("");

  useEffect(() => {
    sampleDispatch
      .getSamples()
      .then((data) => {
        const sortedSamples = data.sort(
          (a: Sample, b: Sample) => priority[a.definition.category] - priority[b.definition.category]
        );
        setSamples([...sortedSamples]);
      })
      .catch((e) => {
        setSampleLoadingError(e.toString());
      })
      .finally(() => {
        setLoading(false);
      });
  }, [sampleDispatch]);

  return (
    <>
      {sampleLoadingError && <SamplesLoadError errors={[sampleLoadingError]} />}
      {!sampleLoadingError && (
        <>
          <TextContent>
            <Text component="h1">Samples Showcase</Text>
          </TextContent>
          <br />
          {loading ? (
            <SampleCardSkeleton numberOfCards={4} />
          ) : (
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
                <SampleCard sample={sample} key={`sample-${sample.sampleId}`} />
              ))}
            </Gallery>
          )}
        </>
      )}
    </>
  );
}
