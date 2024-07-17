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

import React, { useCallback, useRef, useState, useEffect } from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { SceSimEditorWrapper, SceSimEditorWrapperProps } from "../scesimEditorStoriesWrapper";
import { Button, Flex, FlexItem, Page, PageSection } from "@patternfly/react-core/dist/js";
import { TestScenarioEditorRef } from "../../src/TestScenarioEditor";
import { SceSimMarshaller } from "../../../scesim-marshaller/src/index";
import { SceSimModel, getMarshaller } from "@kie-tools/scesim-marshaller";
import { emptySceSim } from "../misc/empty/Empty.stories";
import { isOldEnoughDrl } from "../useCases/IsOldEnoughRule.stories";
import { trafficViolationDmn } from "../useCases/TrafficViolationDmn.stories";
import { useArgs } from "@storybook/preview-api";

import "./DevWebApp.css";

type DevWebAppProps = SceSimEditorWrapperProps;

function DevWebApp(props: DevWebAppProps) {
  const ref = useRef<TestScenarioEditorRef>(null);
  const [args, updateArgs] = useArgs<DevWebAppProps>();

  const [state, setState] = useState<{ marshaller: SceSimMarshaller; stack: SceSimModel[]; pointer: number }>(() => {
    const emptySceSimMarshaller = getMarshaller(emptySceSim);
    return {
      marshaller: emptySceSimMarshaller,
      stack: [emptySceSimMarshaller.parser.parse()],
      pointer: 0,
    };
  });

  const onSelectModel = useCallback((newModel) => {
    const model = getMarshaller(newModel).parser.parse();
    setState((prev) => {
      const newStack = prev.stack.slice(0, prev.pointer + 1);
      return {
        ...prev,
        stack: [...newStack, model],
        pointer: newStack.length,
      };
    });
  }, []);

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
    const marshaller = getMarshaller(emptySceSim);
    setState({
      marshaller,
      stack: [marshaller.parser.parse()],
      pointer: 0,
    });
  }, []);

  const currentModel = state.stack[state.pointer];

  const downloadRef = useRef<HTMLAnchorElement>(null);
  const downloadAsXml = useCallback(() => {
    if (downloadRef.current) {
      const fileBlob = new Blob([state.marshaller.builder.build(currentModel)], { type: "text/xml" });
      downloadRef.current.download = `scesim-${makeid(10)}.scesim`;
      downloadRef.current.href = URL.createObjectURL(fileBlob);
      downloadRef.current.click();
    }
  }, [currentModel, state.marshaller.builder]);

  const copyAsXml = useCallback(() => {
    navigator.clipboard.writeText(state.marshaller.builder.build(currentModel));
  }, [currentModel, state.marshaller.builder]);

  useEffect(() => {
    updateArgs({ content: state.marshaller.builder.build(state.stack[state.stack.length - 1]) });
  }, [state.marshaller.builder, state.stack, updateArgs]);

  useEffect(() => {
    onSelectModel(args.content);
  }, [args.content, onSelectModel]);

  return (
    <>
      <div>
        <Page onDragOver={onDragOver} onDrop={onDrop}>
          <PageSection
            aria-label={"dev-app-header"}
            variant={"light"}
            isFilled={false}
            padding={{ default: "padding" }}
          >
            <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
              <FlexItem shrink={{ default: "shrink" }}>
                <h3>Test Scenario Editor :: Dev WebApp</h3>
              </FlexItem>
              <FlexItem>
                <h5>(Drag & drop a file anywhere to open it)</h5>
              </FlexItem>
              <FlexItem shrink={{ default: "shrink" }}>
                <Button onClick={() => onSelectModel(emptySceSim)}>Empty</Button>
                &nbsp; &nbsp;
                <Button onClick={() => onSelectModel(isOldEnoughDrl)}>Are They Old Enough?</Button>
                &nbsp; &nbsp;
                <Button onClick={() => onSelectModel(trafficViolationDmn)}>Traffic Violation</Button>
                &nbsp; &nbsp; | &nbsp; &nbsp;
                <button onClick={reset}>Reset</button>
                &nbsp; &nbsp;
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
            {SceSimEditorWrapper({
              pathRelativeToTheWorkspaceRoot: "dev.scesim",
              content: state.marshaller.builder.build(state.stack[state.stack.length - 1]),
            })}
          </PageSection>
        </Page>
      </div>
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

const meta: Meta<DevWebAppProps> = {
  title: "Dev/Web App",
  component: DevWebApp,
};

export default meta;
type Story = StoryObj<DevWebAppProps>;

export const WebApp: Story = {
  render: (args) => DevWebApp(args),
  args: {
    pathRelativeToTheWorkspaceRoot: "dev.scesim",
    content: emptySceSim,
  },
};
