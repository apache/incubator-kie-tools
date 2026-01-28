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
import * as BpmnEditor from "@kie-tools/bpmn-editor/dist/BpmnEditor";
import { AdhocAutostartCheckbox } from "@kie-tools/bpmn-editor/dist/propertiesPanel/adhocAutostartCheckbox/AdhocAutostartCheckbox";
import { AsyncCheckbox } from "@kie-tools/bpmn-editor/dist/propertiesPanel/asyncCheckbox/AsyncCheckbox";
import { BidirectionalDataMappingFormSection } from "@kie-tools/bpmn-editor/dist/propertiesPanel/dataMapping/DataMappingFormSection";
import { NameDocumentationAndId } from "@kie-tools/bpmn-editor/dist/propertiesPanel/nameDocumentationAndId/NameDocumentationAndId";
import { OnEntryAndExitScriptsFormSection } from "@kie-tools/bpmn-editor/dist/propertiesPanel/onEntryAndExitScripts/OnEntryAndExitScriptsFormSection";
import { PropertiesPanelHeaderFormSection } from "@kie-tools/bpmn-editor/dist/propertiesPanel/singleNodeProperties/_PropertiesPanelHeaderFormSection";
import { SlaDueDateInput } from "@kie-tools/bpmn-editor/dist/propertiesPanel/slaDueDate/SlaDueDateInput";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { useKogitoEditorEnvelopeContext } from "@kie-tools-core/editor/dist/api";
import { ResourceContent, SearchType } from "@kie-tools-core/workspace/dist/api";
import { useEffect, useState } from "react";
import { BpmnEditorRootProps, TARGET_DIRECTORY } from "../BpmnEditorRoot";
import { MILESTONE_TASK } from "./MilestoneTask";
import * as WidClientParser from "./WidClientParser";
import "@kie-tools/bpmn-marshaller/dist/drools-extension";
import { DataMapping, setInputAndOutputDataMapping } from "@kie-tools/bpmn-editor/dist/mutations/_dataMapping";
import {
  addOrGetItemDefinitions,
  DEFAULT_DATA_TYPES,
} from "@kie-tools/bpmn-editor/dist/mutations/addOrGetItemDefinitions";
import { BPMN20__tProcess } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { Normalized } from "@kie-tools/bpmn-editor/dist/normalization/normalize";
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";

export const WidEnabledCustomTasksManager: BpmnEditorRootProps["customTasksManager"] = ({
  thisBpmnsNormalizedPosixPathRelativeToTheWorkspaceRoot,
  onChange,
  doneBootstrapping,
}) => {
  const envelopeContext = useKogitoEditorEnvelopeContext();

  // This is a hack. Every time a file is updates in KIE Sandbox, the Shared Worker emits an event to this BroadcastChannel.
  // By listening to it, we can reload the `externalModelsByNamespace` object. This makes the BPMN Editor react to external changes,
  // Which is very important for multi-file editing.
  //
  // Now, this mechanism is not ideal. We would ideally only be notified on changes to relevant files, but this sub-system does not exist yet.
  // The consequence of this "hack" is some extra reloads.
  const [externalUpdatesCount, setExternalUpdatesCount] = useState(0);
  useEffect(() => {
    const bc = new BroadcastChannel("workspaces_files");
    bc.onmessage = ({ data }) => {
      // Changes to `thisBpmn` shouldn't update its references to external models.
      // Here, `data?.relativePath` is relative to the workspace root.
      if (data?.relativePath === thisBpmnsNormalizedPosixPathRelativeToTheWorkspaceRoot) {
        return;
      }

      setExternalUpdatesCount((prev) => prev + 1);
    };
    return () => {
      bc.close();
    };
  }, [thisBpmnsNormalizedPosixPathRelativeToTheWorkspaceRoot]);

  // This effect actually populates `externalModelsByNamespace` through the `onChange` call.
  useEffect(() => {
    let canceled = false;

    if (!thisBpmnsNormalizedPosixPathRelativeToTheWorkspaceRoot) {
      return;
    }

    Promise.all([
      envelopeContext.channelApi.requests.kogitoWorkspace_resourceListRequest({
        pattern: "*.wid",
        opts: { type: SearchType.TRAVERSAL },
      }),
      envelopeContext.channelApi.requests.kogitoWorkspace_resourceListRequest({
        pattern: "global/*.wid",
        opts: { type: SearchType.TRAVERSAL },
      }),
    ])
      .then((lists) => {
        const resources: Array<Promise<ResourceContent | undefined>> = [];
        for (let j = 0; j < lists.length; j++) {
          for (let i = 0; i < lists[j].normalizedPosixPathsRelativeToTheWorkspaceRoot.length; i++) {
            const normalizedPosixPathRelativeToTheWorkspaceRoot =
              lists[j].normalizedPosixPathsRelativeToTheWorkspaceRoot[i];

            // Filter out assets into target/classes directory
            if (normalizedPosixPathRelativeToTheWorkspaceRoot.includes(TARGET_DIRECTORY)) {
              continue;
            }

            resources.push(
              envelopeContext.channelApi.requests.kogitoWorkspace_resourceContentRequest({
                normalizedPosixPathRelativeToTheWorkspaceRoot,
                opts: { type: "text" },
              })
            );
          }
        }
        return Promise.all(resources);
      })
      .then((resources) => {
        const parsedWids: {
          wid: WidClientParser.WorkItemDefinition;
          iconContent: Promise<ResourceContent | undefined> | undefined;
        }[] = [];

        for (let i = 0; i < resources.length; i++) {
          const content = resources[i]?.content;
          if (content) {
            const wids = new WidClientParser.WorkItemDefinitionClientParser().parse(content);
            for (let j = 0; j < wids.length; j++) {
              const widPath = resources[i]?.normalizedPosixPathRelativeToTheWorkspaceRoot ?? "";
              const lastSlash = widPath.lastIndexOf("/");
              const iconPath =
                lastSlash !== -1
                  ? widPath.substring(0, lastSlash + 1) + wids[j].getIconDefinition().getUri()
                  : wids[j].getIconDefinition().getUri();
              parsedWids.push({
                wid: wids[j],
                iconContent:
                  wids[j].getIconDefinition().getUri() !== ""
                    ? envelopeContext.channelApi.requests.kogitoWorkspace_resourceContentRequest({
                        normalizedPosixPathRelativeToTheWorkspaceRoot: iconPath,
                        opts: { type: "binary" },
                      })
                    : undefined,
              });
            }
          }
        }

        return Promise.allSettled(parsedWids.map(({ iconContent }) => iconContent ?? Promise.resolve(undefined))).then(
          (settledIcon) =>
            parsedWids.map(({ wid }, i) => {
              if (
                settledIcon[i].status === "fulfilled" &&
                settledIcon[i].value !== undefined &&
                wid.getIconDefinition().getUri()
              ) {
                wid.getIconDefinition().setIconData(`data:image/png;base64,${settledIcon[i].value.content}`);
              } else if (
                settledIcon[i].status === "fulfilled" &&
                settledIcon[i].value === undefined &&
                wid.getIconDefinition().getIconData()
              ) {
                // no-op, iconData is already set
              } else {
                wid.getIconDefinition().setIconData("");
              }
              return wid;
            })
        );
      })
      .then((wids) => {
        if (!canceled) {
          onChange([MILESTONE_TASK, ...wids.map((wid) => toCustomTask(wid))]);
        }
        doneBootstrapping.resolve();
      });

    return () => {
      canceled = true;
    };
  }, [
    doneBootstrapping,
    externalUpdatesCount, // Hack. See above.
    envelopeContext.channelApi.requests,
    onChange,
    thisBpmnsNormalizedPosixPathRelativeToTheWorkspaceRoot,
  ]);

  return <></>;
};

const WidCustomTaskPropertiesPanel: BpmnEditor.CustomTask["propertiesPanelComponent"] = ({ task }) => {
  return (
    <>
      <PropertiesPanelHeaderFormSection title={task["@_name"] || "Milestone"} icon={<></>} shouldStartExpanded={true}>
        <NameDocumentationAndId element={task} />

        <AsyncCheckbox element={task} />

        <AdhocAutostartCheckbox element={task} />

        <SlaDueDateInput element={task} />
      </PropertiesPanelHeaderFormSection>

      <BidirectionalDataMappingFormSection element={task} />

      <OnEntryAndExitScriptsFormSection element={task} />
    </>
  );
};

function toCustomTask(wid: WidClientParser.WorkItemDefinition): BpmnEditor.CustomTask {
  const iconImageHref =
    wid.getIconDefinition().getIconData() ||
    (wid.getIconDefinition().getUri() === "defaultservicenodeicon.png"
      ? WidClientParser.getDefaultIconData()
      : wid.getIconDefinition().getUri() ?? WidClientParser.getDefaultIconData());

  return {
    id: wid.getName(),
    displayGroup: wid.getCategory(),
    displayName: wid.getDisplayName(),
    displayDescription: wid.getDescription(),
    iconSvgElement: (
      <svg width="30" height="30" viewBox="0 0 30 30" xmlns="http://www.w3.org/2000/svg" fill="none">
        <image href={iconImageHref} height="30px" />
      </svg>
    ),
    dataInputReservedNames: [],
    dataOutputReservedNames: [],
    propertiesPanelComponent: WidCustomTaskPropertiesPanel,
    matches: (task) => task["@_drools:taskName"] === wid.getName(),
    produce: () => {
      const task: ElementFilter<Unpacked<NonNullable<Normalized<BPMN20__tProcess["flowElement"]>>>, "task"> = {
        __$$element: "task",
        "@_id": generateUuid(),
        "@_drools:taskName": wid.getName(),
        "@_name": wid.getDisplayName(),
        ioSpecification: {
          "@_id": generateUuid(),
          inputSet: [],
          outputSet: [],
        },
      };

      const inputs: DataMapping[] = [...wid.getParameters().entries()].map(([name, type]) => ({
        dtype: type, // This is not an actual Data Type. Data Types will be properly handled in `onAdded`.
        name,
        isExpression: false,
        variableRef: undefined,
      }));

      const outputs: DataMapping[] = [...wid.getResults().entries()].map(([name, type]) => ({
        dtype: type, // This is not an actual Data Type. Data Types will be properly handled in `onAdded`.
        name,
        isExpression: false,
        variableRef: undefined,
      }));

      setInputAndOutputDataMapping(new Map(), inputs, outputs, task);
      return task;
    },
    onAdded: (state, task) => {
      const inputs: DataMapping[] = [...wid.getParameters().entries()].map(([name, widType]) => {
        const dataType = fromWidTypeToDataType(widType);
        addOrGetItemDefinitions({ definitions: state.bpmn.model.definitions, dataType });
        return {
          dtype: dataType,
          name,
          isExpression: false,
          variableRef: undefined,
        };
      });

      const outputs: DataMapping[] = [...wid.getResults().entries()].map(([name, widType]) => {
        const dataType = fromWidTypeToDataType(widType);
        addOrGetItemDefinitions({ definitions: state.bpmn.model.definitions, dataType });
        return {
          dtype: dataType,
          name,
          isExpression: false,
          variableRef: undefined,
        };
      });

      const itemDefinitionIdByDataTypes = new Map(
        state.bpmn.model.definitions.rootElement
          ?.filter((r) => r.__$$element === "itemDefinition")
          .map((i) => [i["@_structureRef"]!, i["@_id"]])
      );

      setInputAndOutputDataMapping(itemDefinitionIdByDataTypes, inputs, outputs, task);
    },
  };
}

function fromWidTypeToDataType(widType: string) {
  switch (widType) {
    case "BooleanDataType":
      return DEFAULT_DATA_TYPES.BOOLEAN;
    case "EnumDataType":
      return DEFAULT_DATA_TYPES.OBJECT;
    case "FloatDataType":
      return DEFAULT_DATA_TYPES.FLOAT;
    case "IntegerDataType":
      return DEFAULT_DATA_TYPES.INTEGER;
    case "ListDataType":
      return "java.util.List"; // Special case only available on WIDs.
    case "ObjectDataType":
      return DEFAULT_DATA_TYPES.OBJECT;
    case "StringDataType":
      return DEFAULT_DATA_TYPES.STRING;
    case "UndefinedDataType":
      return DEFAULT_DATA_TYPES.OBJECT;
    default:
      return widType;
  }
}
