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
import { useCallback, useEffect, useRef, useState } from "react";

import { TestScenarioEditor, TestScenarioEditorRef } from "../../src/TestScenarioEditor";

import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Dropdown, DropdownToggle, DropdownItem } from "@patternfly/react-core/dist/js/components/Dropdown";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";

import { LOAN_PRE_QUALIFICATION, TRAFFIC_VIOLATION } from "./ExternalDmnModels";
import { IS_OLD_ENOUGH_RULE, TRAFFIC_VIOLATION_DMN } from "./ExternalScesimModels";

import "./DevWebApp.css";

export function DevWebApp() {
  const ref = useRef<TestScenarioEditorRef>(null);
  const [isExampleDropdownOpen, setExampleDropdownIsOpen] = useState(false);

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

  const reset = useCallback(() => {
    ref.current?.setContent("Untitled.scesim", "");
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

  const onOpenStaticScesimExample = useCallback((fileName: string, content: string) => {
    ref.current?.setContent(fileName, content);
  }, []);

  const dropdownExamplesItems = [
    <DropdownItem
      key="TrafficViolationTest.scesim"
      component="button"
      onClick={() => onOpenStaticScesimExample("TrafficViolationTest.scesim", TRAFFIC_VIOLATION_DMN)}
    >
      DMN-Based: TrafficViolationTest
    </DropdownItem>,
    <DropdownItem
      key="AreTheyOldEnoughTest.scesim"
      component="button"
      onClick={() => onOpenStaticScesimExample("AreTheyOldEnoughTest.scesim", IS_OLD_ENOUGH_RULE)}
    >
      Rule-Based: AreTheyOldEnoughTest
    </DropdownItem>,
  ];

  const onExampleDropdownToggle = useCallback((isOpen: boolean) => {
    setExampleDropdownIsOpen(isOpen);
  }, []);

  const onExampleDropdownSelect = useCallback(() => {
    setExampleDropdownIsOpen(false);
    const element = document.getElementById("toggle-basic");
    element?.focus();
  }, []);

  // const onRequestExternalModelByPath = useCallback<Promise<string[]>>(async (path) => {
  //   return availableModelsByPath[path] ?? null;
  // }, []);

  // const onRequestExternalModelsAvailableToInclude =
  //   useCallback<DmnEditor.OnRequestExternalModelsAvailableToInclude>(async () => {
  //     return Object.keys(availableModelsByPath);
  //   }, []);

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
              &nbsp; &nbsp;
              <Dropdown
                className="dev-webapp--example-dropdown"
                onSelect={onExampleDropdownSelect}
                toggle={
                  <DropdownToggle id="toggle-basic" onToggle={onExampleDropdownToggle}>
                    Examples
                  </DropdownToggle>
                }
                isOpen={isExampleDropdownOpen}
                dropdownItems={dropdownExamplesItems}
              />
              &nbsp; &nbsp;
              <Button onClick={reset} variant="tertiary">
                Reset
              </Button>
              &nbsp; &nbsp;
              <Button onClick={copyAsXml} variant="tertiary">
                Copy as XML
              </Button>
              &nbsp; &nbsp;
              <Button onClick={downloadAsXml} variant="tertiary">
                Download as XML
              </Button>
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
