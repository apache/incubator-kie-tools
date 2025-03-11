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
import { useCallback, useMemo, useState } from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { DmnLatestModel, DmnMarshaller, getMarshaller } from "@kie-tools/dmn-marshaller";
import { ns as dmn15ns } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/meta";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { DMN15_SPEC } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";
import {
  DmnEditor,
  DmnEditorProps,
  ExternalModelsIndex,
  OnRequestExternalModelByPath,
  OnRequestExternalModelsAvailableToInclude,
  OnDmnModelChange,
} from "@kie-tools/dmn-editor/dist/DmnEditor";
import { normalize, Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";

import { DmnEditorWrapper } from "../../dmnEditorStoriesWrapper";

import { availableModelsByPath, modelsByNamespace } from "./availableModelsToInclude";

export const generateEmptyDmn15 = () => `<?xml version="1.0" encoding="UTF-8"?>
<definitions
  xmlns="${dmn15ns.get("")}"
  expressionLanguage="${DMN15_SPEC.expressionLanguage.default}"
  namespace="https://kie.apache.org/dmn/${generateUuid()}"
  id="${generateUuid()}"
  name="DMN${generateUuid()}">
</definitions>`;

const initialModel = generateEmptyDmn15();

function EmptyStoryWithIncludedModels(args: DmnEditorProps) {
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

  const currentModel = state.stack[state.pointer];

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

  return (
    <>
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
      })}
    </>
  );
}

const meta: Meta<DmnEditorProps> = {
  title: "Misc/EmptyWithAvailableExternalModels",
  component: DmnEditor,
  includeStories: /^[A-Z]/,
};

export default meta;
type Story = StoryObj<typeof EmptyStoryWithIncludedModels>;

export const EmptyWithAvailableExternalModels: Story = {
  render: (args) => EmptyStoryWithIncludedModels(args),
  args: {
    model: getMarshaller(initialModel, { upgradeTo: "latest" }).parser.parse(),
    originalVersion: "1.5",
    evaluationResultsByNodeId: new Map(),
    externalContextDescription: "External context description",
    externalContextName: "Storybook - DMN Editor",
    externalModelsByNamespace: {},
    issueTrackerHref: "",
    validationMessages: {},
  },
};
