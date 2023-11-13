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
import { useCallback, useEffect, useRef } from "react";

import { TestScenarioEditor, TestScenarioEditorRef } from "../../src/TestScenarioEditor";

import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";

import "./DevWebApp.css";

export function DevWebApp() {
  const ref = useRef<TestScenarioEditorRef>(null);

  useEffect(() => {
    /* Simulating a call from "Foundation" code */
    setTimeout(() => {
      ref.current?.setContent("Untitled.scesim", "");
    }, 1000);
  }, [ref]);

  const onDrop = useCallback((e: React.DragEvent) => {
    e.preventDefault(); // Necessary to disable the browser's default 'onDrop' handling.

    if (e.dataTransfer.items) {
      // Use DataTransferItemList interface to access the file(s)
      [...e.dataTransfer.items].forEach((item, i) => {
        if (item.kind === "file") {
          const fileName = item.getAsFile()?.name;
          const reader = new FileReader();
          reader.addEventListener("load", ({ target }) =>
            ref.current?.setContent(fileName ?? "", target?.result as string)
          );
          reader.readAsText(item.getAsFile() as any);
        }
      });
    }
  }, []);

  const onDragOver = useCallback((e: React.DragEvent) => {
    e.preventDefault(); // Necessary to disable the browser's default 'onDrop' handling.
  }, []);

  const copyAsXml = useCallback(() => {
    navigator.clipboard.writeText(ref.current?.getContent() || "");
  }, []);

  const downloadRef = useRef<HTMLAnchorElement>(null);
  const downloadAsXml = useCallback(() => {
    if (downloadRef.current) {
      const fileBlob = new Blob([ref.current?.getContent() || ""], { type: "text/xml" });
      downloadRef.current.download = `scesim-${makeid(10)}.scesim`;
      downloadRef.current.href = URL.createObjectURL(fileBlob);
      downloadRef.current.click();
    }
  }, []);

  return (
    <>
      <Page onDragOver={onDragOver} onDrop={onDrop}>
        <PageSection aria-label={"dev-app-header"} variant={"light"} isFilled={false} padding={{ default: "padding" }}>
          <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
            <FlexItem shrink={{ default: "shrink" }}>
              <h3>Test Scenario Editor :: Dev WebApp</h3>
            </FlexItem>
            <FlexItem>
              <h5>(Drag & drop a file anywhere to open it)</h5>
            </FlexItem>
            <FlexItem shrink={{ default: "shrink" }}>
              <button onClick={copyAsXml}>Copy as XML</button>
              &nbsp; &nbsp;
              <button onClick={downloadAsXml}>Download as XML</button>
            </FlexItem>
          </Flex>
          <a ref={downloadRef} />
        </PageSection>
        <hr />
        <PageSection
          aria-label={"dev-app-body"}
          className={"section-body"}
          isFilled={true}
          hasOverflowScroll={true}
          variant={"light"}
        >
          <TestScenarioEditor ref={ref} />
        </PageSection>
      </Page>
    </>
  );
}

function makeid(length: number) {
  let result = "";
  const characters = "abcdefghijklmnopqrstuvwxyz0123456789";
  const charactersLength = characters.length;
  let counter = 0;
  while (counter < length) {
    result += characters.charAt(Math.floor(Math.random() * charactersLength));
    counter += 1;
  }
  return result;
}
