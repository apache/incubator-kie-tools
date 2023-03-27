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
import { Sample, SampleCard, SampleType } from "./SampleCard";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";
import { fetchFile, kieSamplesRepo, repoContentType } from "./api";
import { useSettingsDispatch } from "../settings/SettingsContext";
import { SampleCardSkeleton } from "./SampleCardSkeleton";

function RenderSvg(props: { svg: string }) {
  return (
    <div
      dangerouslySetInnerHTML={{ __html: props.svg }}
      style={{ height: "100%", maxWidth: "100%", maxHeight: "400px", paddingTop: "30px" }}
    />
  );
}

export function Showcase() {
  const settingsDispatch = useSettingsDispatch();
  const [loading, setLoading] = useState<boolean>(true);
  const [samples, setSamples] = useState<Sample[]>([]);

  const fetchSamples = async () => {
    const res = await fetchFile(
      settingsDispatch.github.octokit,
      kieSamplesRepo.org,
      kieSamplesRepo.repo,
      kieSamplesRepo.ref,
      decodeURIComponent(kieSamplesRepo.path)
    );

    const sampleDirs = ((res as any)?.data).filter((sample: repoContentType) => sample.name !== "template");

    const promises = sampleDirs.map((sample: repoContentType) => {
      return fetchFile(
        settingsDispatch.github.octokit,
        kieSamplesRepo.org,
        kieSamplesRepo.repo,
        kieSamplesRepo.ref,
        decodeURIComponent(`${kieSamplesRepo.path}/${sample.name}`)
      );
    });

    Promise.all(promises).then((promiseData) => {
      let svgElement: React.ReactElement;
      Promise.all(
        promiseData.map((sampleData) => {
          return Promise.all(
            sampleData.data.map(async (files: repoContentType) => {
              let svgResponse;
              let definitionRes;
              if (files.name === "definition.json") {
                const rawUrl = new URL((files as repoContentType).download_url);
                definitionRes = await fetch(rawUrl.toString());
                if (!definitionRes.ok) {
                  console.log(
                    `${definitionRes.status}${definitionRes.statusText ? `- ${definitionRes.statusText}` : ""}`
                  );
                  return;
                }
                const content = JSON.parse(await definitionRes?.text());
                return { name: content.title, description: content.description };
              } else if (files.name.split(".")[1] === "svg") {
                const rawUrl = new URL((files as repoContentType).download_url);
                svgResponse = await fetch(rawUrl.toString());
                if (!svgResponse.ok) {
                  console.log(`${svgResponse.status}${svgResponse.statusText ? `- ${svgResponse.statusText}` : ""}`);
                  return;
                }
                const svg = await svgResponse.text();
                if (svg) {
                  const base64data = btoa(unescape(encodeURIComponent(svg)));
                  svgElement = <RenderSvg svg={svg} />;
                  return { svg: svgElement };
                }
              } else {
                const file = files.name.split(".");
                const type = `${file[1]}.${file[2]}` as unknown as SampleType;
                const fileName = file[0];
                const repoUrl = new URL((files as repoContentType).download_url);
                return { type, fileName, repoUrl };
              }
            })
          ).then((data) => {
            return data?.reduce((r: any, c: any) => Object.assign(r, c), {});
          });
        })
      ).then((data) => {
        setSamples([...data]);
        setLoading(false);
      });
    });
  };

  useEffect(() => {
    fetchSamples();
  }, []);

  if (loading) {
    return <SampleCardSkeleton />;
  }

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
