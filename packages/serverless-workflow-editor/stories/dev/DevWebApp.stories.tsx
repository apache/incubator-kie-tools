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
import { useCallback, useRef, useState } from "react";
import type { Meta, StoryObj } from "@storybook/react";
import "@patternfly/react-core/dist/styles/base.css";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { SwfEditorWrapper } from "../swfEditorStoriesWrapper";
import { SwfEditorProps, OnSwfModelChange } from "../../src/SwfEditor";
import { Specification } from "@serverlessworkflow/sdk-typescript";

const initialContent = `id: helloworld
version: '1.0'
specVersion: '0.8'
name: Hello World Workflow
description: Inject Hello World
start: Hello State
states:
  - type: inject
    name: Hello State
    data:
      result: Hello World!
    end: true`;

function DevWebApp(args: SwfEditorProps) {
  const [state, setState] = useState<{
    stack: Specification.IWorkflow[];
    pointer: number;
  }>(() => {
    const model = Specification.Workflow.fromSource(initialContent, true);
    return {
      stack: !model ? [] : [model],
      pointer: 0,
    };
  });

  const onDrop = useCallback((e: React.DragEvent) => {
    console.log("SWF Editor :: Dev webapp :: File(s) dropped! Opening it.");

    e.preventDefault(); // Necessary to disable the browser's default 'onDrop' handling.

    if (e.dataTransfer.items) {
      // Use DataTransferItemList interface to access the file(s)
      [...e.dataTransfer.items].forEach((item, i) => {
        if (item.kind === "file") {
          const reader = new FileReader();
          reader.addEventListener("load", ({ target }) => {
            const model = Specification.Workflow.fromSource(target?.result as string, true);
            setState({ stack: [model.normalize()], pointer: 0 });
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
    const model = Specification.Workflow.fromSource(initialContent, true);
    setState({
      stack: [model.normalize()],
      pointer: 0,
    });
  }, []);

  const currentModel = state.stack[state.pointer];

  const downloadRef = useRef<HTMLAnchorElement>(null);

  const downloadAsJson = useCallback(() => {
    if (downloadRef.current) {
      const fileBlob = new Blob([Specification.Workflow.toJson(currentModel.normalize())], {
        type: "application/json",
      });
      downloadRef.current.download = `${createId(10)}.sw.json`;
      downloadRef.current.href = URL.createObjectURL(fileBlob);
      downloadRef.current.click();
    }
  }, [currentModel]);

  const downloadAsYaml = useCallback(() => {
    if (downloadRef.current) {
      const fileBlob = new Blob([Specification.Workflow.toYaml(currentModel.normalize())], {
        type: "application/yaml",
      });
      downloadRef.current.download = `${createId(10)}.sw.yaml`;
      downloadRef.current.href = URL.createObjectURL(fileBlob);
      downloadRef.current.click();
    }
  }, [currentModel]);

  const copyAsJson = useCallback(() => {
    navigator.clipboard.writeText(Specification.Workflow.toJson(currentModel.normalize()));
  }, [currentModel]);

  const copyAsYaml = useCallback(() => {
    navigator.clipboard.writeText(Specification.Workflow.toYaml(currentModel.normalize()));
  }, [currentModel]);

  const undo = useCallback(() => {
    setState((prev) => ({ ...prev, pointer: Math.max(0, prev.pointer - 1) }));
  }, []);

  const redo = useCallback(() => {
    setState((prev) => ({ ...prev, pointer: Math.min(prev.stack.length - 1, prev.pointer + 1) }));
  }, []);

  const onModelChange = useCallback<OnSwfModelChange>((model) => {
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
    (newContent) => {
      onModelChange(Specification.Workflow.fromSource(newContent, true));
    },
    [onModelChange]
  );

  const isUndoEnabled = state.pointer > 0;
  const isRedoEnabled = state.pointer !== state.stack.length - 1;

  return (
    <>
      {currentModel && (
        <div style={{ width: "100vw", height: "100vh" }}>
          <Page onDragOver={onDragOver} onDrop={onDrop}>
            <PageSection variant={"light"} isFilled={false} padding={{ default: "padding" }}>
              <div>
                <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
                  <FlexItem shrink={{ default: "shrink" }}>
                    <h3>SWF Editor :: Dev webapp </h3>
                  </FlexItem>
                  <FlexItem>
                    <h5>(Drag & drop a file anywhere to open it)</h5>
                  </FlexItem>
                  <FlexItem shrink={{ default: "shrink" }}>
                    <button onClick={() => onSelectModel(initialContent)}>Empty</button>
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
                    <button onClick={copyAsJson}>Copy JSON</button>
                    &nbsp; &nbsp;
                    <button onClick={copyAsYaml}>Copy YAML</button>
                    &nbsp; &nbsp;
                    <button onClick={downloadAsJson}>Download JSON</button>
                    &nbsp; &nbsp;
                    <button onClick={downloadAsYaml}>Download YAML</button>
                  </FlexItem>
                </Flex>
                <a ref={downloadRef} />
              </div>
            </PageSection>
            <hr />
            <PageSection
              variant={"light"}
              isFilled={true}
              hasOverflowScroll={true}
              aria-label={"editor"}
              padding={{ default: "noPadding" }}
            >
              {SwfEditorWrapper({
                model: currentModel,
                onModelChange,
                issueTrackerHref: args.issueTrackerHref,
                isReadOnly: args.isReadOnly,
              })}
            </PageSection>
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
const meta: Meta<typeof DevWebApp> = {
  title: "Dev/Web App",
  component: DevWebApp,
};

export default meta;
type Story = StoryObj<typeof DevWebApp>;

// More on writing stories with args: https://storybook.js.org/docs/writing-stories/args
export const WebApp: Story = {
  render: (args) => DevWebApp(args),
  args: {
    model: Specification.Workflow.fromSource(initialContent, true),
    issueTrackerHref: "https://github.com/apache/incubator-kie-issues/issues/new",
    isReadOnly: false,
  },
};
