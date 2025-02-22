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

import React, { useCallback, useMemo, useRef, useState } from "react";
import type { Meta, StoryObj } from "@storybook/react";
import "@patternfly/react-core/dist/styles/base.css";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Stack, StackItem } from "@patternfly/react-core/dist/js";
import { SceSimMarshaller, SceSimModel, getMarshaller } from "@kie-tools/scesim-marshaller";
import {
  ExternalDmnsIndex,
  OnRequestExternalModelByPath,
  OnRequestExternalModelsAvailableToInclude,
  OnRequestToJumpToPath,
  OnSceSimModelChange,
  TestScenarioEditorProps,
} from "../../src/TestScenarioEditor";
import { EMPTY_ONE_EIGHT } from "../../src/resources/EmptyScesimFile";
import { SceSimEditorWrapper } from "../scesimEditorStoriesWrapper";
import { emptyFileName } from "../misc/empty/Empty.stories";
import { isOldEnoughDrl, isOldEnoughDrlFileName } from "../useCases/IsOldEnoughRule.stories";
import { trafficViolationDmn, trafficViolationDmnFileName } from "../useCases/TrafficViolationDmn.stories";
import { availableModels, availableModelsByPath } from "../examples/AvailableDMNModels";

function DevWebApp(props: TestScenarioEditorProps) {
  const [fileName, setFileName] = useState<string | undefined>("Untitled.scesim");
  const [state, setState] = useState<{
    marshaller: SceSimMarshaller;
    pointer: number;
    stack: SceSimModel[];
  }>(() => {
    const emptySceSimMarshaller = getMarshaller(EMPTY_ONE_EIGHT);
    return {
      marshaller: emptySceSimMarshaller,
      pointer: 0,
      stack: [emptySceSimMarshaller.parser.parse()],
    };
  });

  const currentModel = state.stack[state.pointer];
  const downloadRef = useRef<HTMLAnchorElement>(null);
  const isUndoEnabled = state.pointer > 0;
  const isRedoEnabled = state.pointer !== state.stack.length - 1;

  const copyAsXml = useCallback(() => {
    navigator.clipboard.writeText(state.marshaller.builder.build(currentModel));
  }, [currentModel, state.marshaller.builder]);

  const downloadAsXml = useCallback(() => {
    if (downloadRef.current) {
      const fileBlob = new Blob([state.marshaller.builder.build(currentModel)], { type: "text/xml" });
      downloadRef.current.download = fileName ?? `scesim-test-${makeid(10)}.scesim`;
      downloadRef.current.href = URL.createObjectURL(fileBlob);
      downloadRef.current.click();
    }
  }, [currentModel, fileName, state.marshaller.builder]);

  const externalModelsByNamespace = useMemo<ExternalDmnsIndex | undefined>(() => {
    if (currentModel.ScenarioSimulationModel.settings.dmnNamespace) {
      const dmnModel = availableModels.find(
        (model) =>
          model.model.definitions["@_namespace"] ===
          currentModel.ScenarioSimulationModel.settings.dmnNamespace?.__$$text
      );
      return new Map([[currentModel.ScenarioSimulationModel.settings.dmnNamespace?.__$$text, dmnModel]]);
    }
    return undefined;
  }, [currentModel.ScenarioSimulationModel.settings.dmnNamespace]);

  const onDragOver = useCallback((e: React.DragEvent) => {
    e.preventDefault(); // Necessary to disable the browser's default 'onDrop' handling.
  }, []);

  const onDrop = useCallback((e: React.DragEvent) => {
    console.log("Test Scenario Editor :: Dev webapp :: File(s) dropped! Opening it.");

    e.preventDefault(); // Necessary to disable the browser's default 'onDrop' handling.

    if (e.dataTransfer.items) {
      // Use DataTransferItemList interface to access the file(s)
      [...e.dataTransfer.items].forEach((item, i) => {
        if (item.kind === "file") {
          const reader = new FileReader();
          setFileName(item.getAsFile()?.name);
          reader.addEventListener("load", ({ target }) => {
            const marshaller = getMarshaller(target?.result as string);
            setState({
              marshaller,
              pointer: 0,
              stack: [marshaller.parser.parse()],
            });
          });
          reader.readAsText(item.getAsFile() as any);
        }
      });
    }
  }, []);

  const onModelChange = useCallback<OnSceSimModelChange>((model) => {
    setState((prev) => {
      const newStack = prev.stack.slice(0, prev.pointer + 1);
      return {
        ...prev,
        stack: [...newStack, model],
        pointer: newStack.length,
      };
    });
  }, []);

  const onRequestToJumpToPath = useCallback<OnRequestToJumpToPath>((path) => {
    alert("A request to open this file: " + path);
  }, []);

  const onSelectModel = useCallback(
    (newModel, fileName) => {
      onModelChange(getMarshaller(newModel).parser.parse());
      setFileName(fileName);
    },
    [onModelChange]
  );

  const redo = useCallback(() => {
    setState((prev) => ({ ...prev, pointer: Math.min(prev.stack.length - 1, prev.pointer + 1) }));
  }, []);

  const reset = useCallback(() => {
    const marshaller = getMarshaller(EMPTY_ONE_EIGHT);
    setState({
      marshaller,
      pointer: 0,
      stack: [marshaller.parser.parse()],
    });
  }, []);

  const undo = useCallback(() => {
    setState((prev) => ({ ...prev, pointer: Math.max(0, prev.pointer - 1) }));
  }, []);

  const onRequestExternalModelByPath = useCallback<OnRequestExternalModelByPath>(async (path) => {
    return availableModelsByPath[path] ?? null;
  }, []);

  const onRequestExternalModelsAvailableToInclude = useCallback<OnRequestExternalModelsAvailableToInclude>(async () => {
    return Object.keys(availableModelsByPath);
  }, []);

  return (
    <div style={{ width: "100vw", height: "100vh" }}>
      <Page onDragOver={onDragOver} onDrop={onDrop}>
        <PageSection aria-label={"dev-app-header"} variant={"light"} isFilled={false}>
          <Stack hasGutter>
            <StackItem>
              <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
                <FlexItem shrink={{ default: "shrink" }}>
                  <h3>Test Scenario Editor :: Dev WebApp</h3>
                </FlexItem>
                <FlexItem>
                  <h5>(Drag & drop a file anywhere to open it)</h5>
                </FlexItem>
              </Flex>
            </StackItem>
            <StackItem>
              <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
                <FlexItem shrink={{ default: "shrink" }}>
                  <Button onClick={() => onSelectModel(EMPTY_ONE_EIGHT, emptyFileName)}>Empty</Button>
                  &nbsp; &nbsp;
                  <Button onClick={() => onSelectModel(isOldEnoughDrl, isOldEnoughDrlFileName)}>
                    Are They Old Enough?
                  </Button>
                  &nbsp; &nbsp;
                  <Button onClick={() => onSelectModel(trafficViolationDmn, trafficViolationDmnFileName)}>
                    Traffic Violation
                  </Button>
                  &nbsp; &nbsp; | &nbsp; &nbsp;
                  <Button
                    onClick={undo}
                    disabled={!isUndoEnabled}
                    style={{ opacity: isUndoEnabled ? 1 : 0.5 }}
                    variant="secondary"
                  >
                    {`Undo (${state.pointer})`}
                  </Button>
                  &nbsp; &nbsp;
                  <Button
                    onClick={redo}
                    disabled={!isRedoEnabled}
                    style={{ opacity: isRedoEnabled ? 1 : 0.5 }}
                    variant="secondary"
                  >
                    {`Redo (${state.stack.length - 1 - state.pointer})`}
                  </Button>
                  &nbsp; &nbsp; | &nbsp; &nbsp;
                  <Button onClick={reset} variant="tertiary">
                    Reset
                  </Button>
                  &nbsp; &nbsp;
                  <Button onClick={copyAsXml} variant="tertiary">
                    Copy as XML
                  </Button>
                  &nbsp; &nbsp;
                  <Button onClick={downloadAsXml} variant="tertiary">
                    Download
                  </Button>
                </FlexItem>
              </Flex>
            </StackItem>
          </Stack>
          <a ref={downloadRef} />
        </PageSection>
        <hr />
        <PageSection
          aria-label={"editor"}
          isFilled={true}
          hasOverflowScroll={true}
          padding={{ default: "noPadding" }}
          variant={"light"}
        >
          {SceSimEditorWrapper({
            issueTrackerHref: props.issueTrackerHref,
            externalModelsByNamespace: externalModelsByNamespace,
            model: currentModel,
            onModelChange: onModelChange,
            onRequestExternalModelsAvailableToInclude: onRequestExternalModelsAvailableToInclude,
            onRequestExternalModelByPath: onRequestExternalModelByPath,
            onRequestToJumpToPath: onRequestToJumpToPath,
            openFileNormalizedPosixPathRelativeToTheWorkspaceRoot: fileName,
          })}
        </PageSection>
      </Page>
    </div>
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

const meta: Meta<typeof DevWebApp> = {
  title: "Dev/Web App",
  component: DevWebApp,
};

export default meta;
type Story = StoryObj<typeof DevWebApp>;

export const WebApp: Story = {
  render: (args) => DevWebApp(args),
  args: {
    issueTrackerHref: "https://github.com/apache/incubator-kie-issues/issues/new",
    model: getMarshaller(emptyFileName).parser.parse(),
    openFileNormalizedPosixPathRelativeToTheWorkspaceRoot: "Untitled.scesim",
  },
};
