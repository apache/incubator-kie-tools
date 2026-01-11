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
import type { Meta, StoryObj } from "@storybook/react";
import { useCallback, useMemo, useRef, useState, useEffect } from "react";
import "@patternfly/react-core/dist/styles/base.css";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { BpmnLatestModel, getMarshaller, BpmnMarshaller } from "@kie-tools/bpmn-marshaller";
import * as DmnMarshaller from "@kie-tools/dmn-marshaller";
import { Normalized, normalize } from "@kie-tools/bpmn-editor/dist/normalization/normalize";
import { generateEmptyBpmn20 } from "../misc/empty/Empty.stories";
import { BpmnEditorWrapper } from "../bpmnEditorStoriesWrapper";
import {
  BpmnEditorProps,
  ExternalModelsIndex,
  OnBpmnModelChange,
  OnRequestExternalModelByPath,
  OnRequestExternalModelsAvailableToInclude,
  OnRequestToJumpToPath,
} from "../../src/BpmnEditor";

import "../tsDmnModule";
import DecisionDmnRaw from "!!raw-loader!../useCases/models/decision.dmn";
import AnotherDecisionDmnRaw from "!!raw-loader!../useCases/models/anotherDecision.dmn";
import { REST_API_CALL_TASK, GRPC_API_CALL_TASK } from "../features/CustomTasks/other/OtherCustomTasks";

const decisionDmnModel = DmnMarshaller.getMarshaller(DecisionDmnRaw, { upgradeTo: "latest" }).parser.parse();

const anotherDecisionDmnModel = DmnMarshaller.getMarshaller(AnotherDecisionDmnRaw, {
  upgradeTo: "latest",
}).parser.parse();

const initialModel = generateEmptyBpmn20();

function DevPlayground(args: BpmnEditorProps) {
  const [state, setState] = useState<{
    marshaller: BpmnMarshaller;
    stack: Normalized<BpmnLatestModel>[];
    pointer: number;
  }>(() => {
    const initialBpmnMarshaller = getMarshaller(initialModel, { upgradeTo: "latest" });
    return {
      marshaller: initialBpmnMarshaller,
      stack: [normalize(initialBpmnMarshaller.parser.parse())],
      pointer: 0,
    };
  });

  const [showXml, setShowXml] = useState(false);
  const [xmlContent, setXmlContent] = useState("");

  const onDrop = useCallback((e: React.DragEvent) => {
    console.log("BPMN Editor :: Playground :: File(s) dropped! Opening it.");

    e.preventDefault(); // Necessary to disable the browser's default 'onDrop' handling.

    if (e.dataTransfer.items) {
      // Use DataTransferItemList interface to access the file(s)
      [...e.dataTransfer.items].forEach((item, i) => {
        if (item.kind === "file") {
          const reader = new FileReader();
          reader.addEventListener("load", ({ target }) => {
            const marshaller = getMarshaller(target?.result as string, { upgradeTo: "latest" });
            setState({ marshaller, stack: [normalize(marshaller.parser.parse())], pointer: 0 });
          });
          reader.readAsText(item.getAsFile() as any);
        }
      });
    }
  }, []);

  const onDragOver = useCallback((e: React.DragEvent) => {
    e.preventDefault(); // Necessary to disable the browser's default 'onDrop' handling.
  }, []);

  const reset = useCallback(() => {
    const marshaller = getMarshaller(generateEmptyBpmn20(), { upgradeTo: "latest" });
    setState({
      marshaller,
      stack: [normalize(marshaller.parser.parse())],
      pointer: 0,
    });
  }, []);

  const currentModel = state.stack[state.pointer];

  const downloadRef = useRef<HTMLAnchorElement>(null);
  const downloadAsXml = useCallback(() => {
    if (downloadRef.current) {
      const fileBlob = new Blob([state.marshaller.builder.build(currentModel)], { type: "text/xml" });
      downloadRef.current.download = `bpmn-${createId(10)}.bpmn`;
      downloadRef.current.href = URL.createObjectURL(fileBlob);
      downloadRef.current.click();
    }
  }, [currentModel, state.marshaller.builder]);

  const copyAsXml = useCallback(() => {
    navigator.clipboard.writeText(state.marshaller.builder.build(currentModel));
  }, [currentModel, state.marshaller.builder]);

  useEffect(() => {
    if (showXml) {
      setXmlContent(state.marshaller.builder.build(currentModel));
    }
  }, [showXml, state.marshaller.builder, currentModel]);

  const undo = useCallback(() => {
    setState((prev) => ({ ...prev, pointer: Math.max(0, prev.pointer - 1) }));
  }, []);

  const redo = useCallback(() => {
    setState((prev) => ({ ...prev, pointer: Math.min(prev.stack.length - 1, prev.pointer + 1) }));
  }, []);

  const onModelChange = useCallback<OnBpmnModelChange>((model) => {
    setState((prev) => {
      const newStack = prev.stack.slice(0, prev.pointer + 1);
      return {
        ...prev,
        stack: [...newStack, model],
        pointer: newStack.length,
      };
    });
  }, []);

  const onSelectModel = useCallback(
    (newModel) => {
      onModelChange(normalize(getMarshaller(newModel, { upgradeTo: "latest" }).parser.parse()));
    },
    [onModelChange]
  );

  const onRequestToJumpToPath = useCallback<OnRequestToJumpToPath>((path) => {
    alert("Jumping to file " + path);
  }, []);

  const onRequestExternalModelsAvailableToInclude = useCallback<OnRequestExternalModelsAvailableToInclude>(() => {
    return Promise.resolve(["./decision.dmn", "../decisions/anotherDecision.dmn"]);
  }, []);

  const externalModelsByNamespace = useMemo<ExternalModelsIndex>(() => {
    return {
      [decisionDmnModel.definitions["@_namespace"]]: {
        type: "dmn",
        normalizedPosixPathRelativeToTheOpenFile: "./decision.dmn",
        model: decisionDmnModel,
        svg: "",
      },
      [anotherDecisionDmnModel.definitions["@_namespace"]]: {
        type: "dmn",
        normalizedPosixPathRelativeToTheOpenFile: "../decisions/anotherDecision.dmn",
        model: anotherDecisionDmnModel,
        svg: "",
      },
    };
  }, []);

  const onRequestExternalModelByPath = useCallback<OnRequestExternalModelByPath>(
    async (normalizedPosixPathRelativeToTheOpenFile) => {
      if (normalizedPosixPathRelativeToTheOpenFile === "./decision.dmn") {
        return externalModelsByNamespace[decisionDmnModel.definitions["@_namespace"]]!;
      } else if (normalizedPosixPathRelativeToTheOpenFile === "../decisions/anotherDecision.dmn") {
        return externalModelsByNamespace[anotherDecisionDmnModel.definitions["@_namespace"]]!;
      } else {
        return null;
      }
    },
    [externalModelsByNamespace]
  );

  const isUndoEnabled = state.pointer > 0;
  const isRedoEnabled = state.pointer !== state.stack.length - 1;

  return (
    <>
      {currentModel && (
        <div style={{ width: "100vw", height: "100vh" }}>
          <Page onDragOver={onDragOver} onDrop={onDrop}>
            <PageSection variant={"light"} isFilled={false} padding={{ default: "padding" }}>
              <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
                <FlexItem>
                  <h5>(Drag & drop a file anywhere to open it)</h5>
                </FlexItem>
                <FlexItem shrink={{ default: "shrink" }}>
                  &nbsp; &nbsp; | &nbsp; &nbsp;
                  <button disabled={!isUndoEnabled} style={{ opacity: isUndoEnabled ? 1 : 0.5 }} onClick={undo}>
                    {`Undo (${state.pointer})`}
                  </button>
                  &nbsp; &nbsp;
                  <button disabled={!isRedoEnabled} style={{ opacity: isRedoEnabled ? 1 : 0.5 }} onClick={redo}>
                    {`Redo (${state.stack.length - 1 - state.pointer})`}
                  </button>
                  &nbsp; &nbsp; | &nbsp; &nbsp;
                  <button onClick={reset}>Reset</button>
                  &nbsp; &nbsp;
                  <button onClick={copyAsXml}>Copy XML</button>
                  &nbsp; &nbsp;
                  <button onClick={() => setShowXml((currentValue) => !currentValue)}>Show XML</button>
                  &nbsp; &nbsp;
                  <button onClick={downloadAsXml}>Download as .bpmn</button>
                </FlexItem>
              </Flex>
              <a ref={downloadRef} />
            </PageSection>
            <hr />
            <PageSection
              variant={"light"}
              isFilled={true}
              hasOverflowScroll={true}
              aria-label={"editor"}
              padding={{ default: "noPadding" }}
            >
              {BpmnEditorWrapper({
                model: currentModel,
                originalVersion: args.originalVersion,
                onModelChange,
                externalContextName: args.externalContextName,
                externalContextDescription: args.externalContextDescription,
                issueTrackerHref: args.issueTrackerHref,
                onRequestToJumpToPath,
                onRequestExternalModelsAvailableToInclude,
                onRequestExternalModelByPath,
                externalModelsByNamespace,
              })}
            </PageSection>
            {showXml && (
              <PageSection>
                <div>
                  <pre>{xmlContent}</pre>
                </div>
              </PageSection>
            )}
          </Page>
        </div>
      )}
    </>
  );
}

function createId(length: number) {
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

// More on how to set up stories at: https://storybook.js.org/docs/writing-stories#default-export
const meta: Meta<typeof DevPlayground> = {
  title: "Dev/Playground",
  component: DevPlayground,
};

export default meta;
type Story = StoryObj<typeof DevPlayground>;

// More on writing stories with args: https://storybook.js.org/docs/writing-stories/args
export const Playground: Story = {
  render: (args) => DevPlayground(args),
  args: {
    model: getMarshaller(initialModel, { upgradeTo: "latest" }).parser.parse(),
    originalVersion: "2.0",
    externalContextDescription: "You're using the BPMN Editor Playground.",
    externalContextName: "Apache KIE :: BPMN Editor :: Storybook :: Playground",
    issueTrackerHref: "https://github.com/apache/incubator-kie-issues/issues/new",
    customTasks: [REST_API_CALL_TASK, GRPC_API_CALL_TASK],
  },
};
