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
import { useCallback, useMemo, useRef, useState } from "react";
import type { Meta, StoryObj } from "@storybook/react";
import "@patternfly/react-core/dist/styles/base.css";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { DmnLatestModel, DmnMarshaller, getMarshaller } from "@kie-tools/dmn-marshaller";
import { normalize, Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { availableModelsByPath, modelsByNamespace } from "./availableModelsToInclude";
import { generateEmptyDmn15 } from "../misc/empty/Empty.stories";
import { loanPreQualificationDmn } from "../useCases/loanPreQualification/LoanPreQualification.stories";
import { DmnEditorWrapper } from "../dmnEditorStoriesWrapper";
import {
  DmnEditorProps,
  ExternalModelsIndex,
  OnDmnModelChange,
  OnRequestExternalModelByPath,
  OnRequestExternalModelsAvailableToInclude,
  OnRequestToJumpToPath,
} from "../../src/DmnEditor";

const initialModel = generateEmptyDmn15();

const emptyDrd = `<?xml version="1.0" encoding="UTF-8" ?>
<definitions xmlns="https://www.omg.org/spec/DMN/20230324/MODEL/" xmlns:dmndi="https://www.omg.org/spec/DMN/20230324/DMNDI/" xmlns:dc="http://www.omg.org/spec/DMN/20180521/DC/" xmlns:di="http://www.omg.org/spec/DMN/20180521/DI/" xmlns:kie="https://kie.org/dmn/extensions/1.0" xmlns:included0="https://kie.org/dmn/_125A5475-65CE-4574-822C-9CB2268F1393" expressionLanguage="https://www.omg.org/spec/DMN/20230324/FEEL/" namespace="https://kie.org/dmn/_2B849D68-E816-42F9-898A-1938B5D6B297" id="_A06623F7-6F03-49B9-9215-B9F99817C3ED" name="DMN_48A8D068-DBF3-4AE6-94E2-496DFC4B3E46">
  <import id="_8079D96B-F569-4F4E-830B-7462B6AFE492" name="u" importType="http://www.omg.org/spec/DMN/20180521/MODEL/" namespace="https://kie.org/dmn/_125A5475-65CE-4574-822C-9CB2268F1393" locationURI="./Untitled-4.dmn" />
  <inputData name="My Input" id="_9392B01E-8C6B-4E29-9CC4-21C16EFB2F6B">
    <variable name="My Input" id="_9483BABF-708A-4357-AD78-18C7A770E292" typeRef="&lt;Undefined&gt;" />
  </inputData>
  <decision name="My Decision" id="_83A0C6FA-0951-4E1E-9DF1-74A9D2A95E98">
    <variable id="_01C70F45-2955-474A-9FAC-14967ABAF475" typeRef="&lt;Undefined&gt;" name="My Decision" />
    <informationRequirement id="_A7EAFD5D-BDF7-4D09-81A9-9C22711847C0">
      <requiredInput href="https://kie.org/dmn/_125A5475-65CE-4574-822C-9CB2268F1393#_D9138F6E-E9DA-47AB-8DEF-5CD531B94ABE" />
    </informationRequirement>
    <informationRequirement id="_E4FE78BB-996B-46C4-9F9B-018163E9017A">
      <requiredInput href="#_9392B01E-8C6B-4E29-9CC4-21C16EFB2F6B" />
    </informationRequirement>
    <informationRequirement id="_E52D5C34-172E-4E33-B2FE-7B2A7AFDF52C">
      <requiredInput href="#_4072ADC3-E7CF-4D22-8179-7494EE22157C" />
    </informationRequirement>
  </decision>
  <inputData name="Another Input" id="_4072ADC3-E7CF-4D22-8179-7494EE22157C">
    <variable name="Another Input" id="_7490876B-8FA9-4FEC-B078-7563EF04F52B" typeRef="&lt;Undefined&gt;" />
  </inputData>
</definitions>
`;

function DevWebApp(args: DmnEditorProps) {
  const [state, setState] = useState<{
    marshaller: DmnMarshaller;
    stack: Normalized<DmnLatestModel>[];
    pointer: number;
  }>(() => {
    const initialDmnMarshaller = getMarshaller(initialModel, { upgradeTo: "latest" });
    return {
      marshaller: initialDmnMarshaller,
      stack: [normalize(initialDmnMarshaller.parser.parse())],
      pointer: 0,
    };
  });

  const onDrop = useCallback((e: React.DragEvent) => {
    console.log("DMN Editor :: Dev webapp :: File(s) dropped! Opening it.");

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
    const marshaller = getMarshaller(generateEmptyDmn15(), { upgradeTo: "latest" });
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
      downloadRef.current.download = `dmn-${createId(10)}.dmn`;
      downloadRef.current.href = URL.createObjectURL(fileBlob);
      downloadRef.current.click();
    }
  }, [currentModel, state.marshaller.builder]);

  const copyAsXml = useCallback(() => {
    navigator.clipboard.writeText(state.marshaller.builder.build(currentModel));
  }, [currentModel, state.marshaller.builder]);

  const undo = useCallback(() => {
    setState((prev) => ({ ...prev, pointer: Math.max(0, prev.pointer - 1) }));
  }, []);

  const redo = useCallback(() => {
    setState((prev) => ({ ...prev, pointer: Math.min(prev.stack.length - 1, prev.pointer + 1) }));
  }, []);

  const onModelChange = useCallback<OnDmnModelChange>((model) => {
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

  const externalModelsByNamespace = useMemo<ExternalModelsIndex>(() => {
    return (currentModel.definitions.import ?? []).reduce((acc, i) => {
      acc[i["@_namespace"]] = modelsByNamespace[i["@_namespace"]];
      return acc;
    }, {} as ExternalModelsIndex);
  }, [currentModel.definitions.import]);

  const onRequestExternalModelByPath = useCallback<OnRequestExternalModelByPath>(async (path) => {
    return availableModelsByPath[path] ?? null;
  }, []);

  const onRequestExternalModelsAvailableToInclude = useCallback<OnRequestExternalModelsAvailableToInclude>(async () => {
    return Object.keys(availableModelsByPath);
  }, []);

  const isUndoEnabled = state.pointer > 0;
  const isRedoEnabled = state.pointer !== state.stack.length - 1;

  return (
    <>
      {currentModel && (
        <div style={{ width: "100vw", height: "100vh" }}>
          <Page onDragOver={onDragOver} onDrop={onDrop}>
            <PageSection variant={"light"} isFilled={false} padding={{ default: "padding" }}>
              <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
                <FlexItem shrink={{ default: "shrink" }}>
                  <h3>DMN Editor :: Dev webapp </h3>
                </FlexItem>
                <FlexItem>
                  <h5>(Drag & drop a file anywhere to open it)</h5>
                </FlexItem>
                <FlexItem shrink={{ default: "shrink" }}>
                  <button onClick={() => onSelectModel(generateEmptyDmn15())}>Empty</button>
                  &nbsp; &nbsp;
                  <button onClick={() => onSelectModel(loanPreQualificationDmn)}>Loan Pre Qualification</button>
                  &nbsp; &nbsp;
                  <button onClick={() => onSelectModel(emptyDrd)}>Empty DRD</button>
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
                  <button onClick={copyAsXml}>Copy as XML</button>
                  &nbsp; &nbsp;
                  <button onClick={downloadAsXml}>Download as XML</button>
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
              {DmnEditorWrapper({
                model: currentModel,
                originalVersion: args.originalVersion,
                onModelChange,
                onRequestExternalModelByPath,
                onRequestExternalModelsAvailableToInclude,
                externalModelsByNamespace: externalModelsByNamespace,
                externalContextName: args.externalContextName,
                externalContextDescription: args.externalContextDescription,
                validationMessages: args.validationMessages,
                evaluationResultsByNodeId: args.evaluationResultsByNodeId,
                issueTrackerHref: args.issueTrackerHref,
                onRequestToJumpToPath,
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
    model: getMarshaller(initialModel, { upgradeTo: "latest" }).parser.parse(),
    originalVersion: "1.5",
    evaluationResultsByNodeId: new Map(),
    externalContextDescription:
      "You're using the DMN Dev webapp, so there's only two simple external models that can be included.",
    externalContextName: "Dev webapp",
    externalModelsByNamespace: {},
    issueTrackerHref: "https://github.com/apache/incubator-kie-issues/issues/new",
    validationMessages: {},
    isReadOnly: false,
  },
};
