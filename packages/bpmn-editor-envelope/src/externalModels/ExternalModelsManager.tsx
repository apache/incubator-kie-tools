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

import * as __path from "path";
import * as React from "react";
import * as BpmnEditor from "@kie-tools/bpmn-editor/dist/BpmnEditor";
import { PromiseImperativeHandle } from "@kie-tools-core/react-hooks/dist/useImperativePromiseHandler";
import { ResourceContent, SearchType } from "@kie-tools-core/workspace/dist/api";
import { getMarshaller as getDmnMarshaller } from "@kie-tools/dmn-marshaller";
import { normalize as normalizeDmn } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { domParser } from "@kie-tools/xml-parser-ts";
import { useCallback, useEffect, useState } from "react";
import { EXTERNAL_MODELS_SEARCH_GLOB_PATTERN, TARGET_DIRECTORY } from "../BpmnEditorRoot";
import { useKogitoEditorEnvelopeContext } from "@kie-tools-core/editor/dist/api";

export function ExternalModelsManager({
  thisBpmnsNormalizedPosixPathRelativeToTheWorkspaceRoot,
  onChange,
  doneBootstrapping,
}: {
  thisBpmnsNormalizedPosixPathRelativeToTheWorkspaceRoot: string | undefined;
  onChange: (externalModelsByNamespace: BpmnEditor.ExternalModelsIndex) => void;
  doneBootstrapping: PromiseImperativeHandle<void>;
}) {
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

  const getDmnsByNamespace = useCallback((resources: (ResourceContent | undefined)[]) => {
    const ret = new Map<string, ResourceContent>();
    for (let i = 0; i < resources.length; i++) {
      const resource = resources[i];
      if (!resource) {
        continue;
      }

      const content = resource.content ?? "";
      const ext = __path.extname(resource.normalizedPosixPathRelativeToTheWorkspaceRoot);
      if (ext === ".dmn") {
        const namespace = domParser.getDomDocument(content).documentElement.getAttribute("namespace");
        if (namespace) {
          // Check for multiplicity of namespaces on DMN models
          if (ret.has(namespace)) {
            console.warn(
              `BPMN EDITOR ROOT: Multiple DMN models encountered with the same namespace '${namespace}': '${
                resource.normalizedPosixPathRelativeToTheWorkspaceRoot
              }' and '${
                ret.get(namespace)!.normalizedPosixPathRelativeToTheWorkspaceRoot
              }'. The latter will be considered.`
            );
          }

          ret.set(namespace, resource);
        }
      }
    }

    return ret;
  }, []);

  // This effect actually populates `externalModelsByNamespace` through the `onChange` call.
  useEffect(() => {
    let canceled = false;

    if (!thisBpmnsNormalizedPosixPathRelativeToTheWorkspaceRoot) {
      return;
    }

    envelopeContext.channelApi.requests
      .kogitoWorkspace_resourceListRequest({
        pattern: EXTERNAL_MODELS_SEARCH_GLOB_PATTERN,
        opts: { type: SearchType.TRAVERSAL },
      })
      .then((list) => {
        const resources: Array<Promise<ResourceContent | undefined>> = [];
        for (let i = 0; i < list.normalizedPosixPathsRelativeToTheWorkspaceRoot.length; i++) {
          const normalizedPosixPathRelativeToTheWorkspaceRoot = list.normalizedPosixPathsRelativeToTheWorkspaceRoot[i];

          // Do not show this BPMN on the list and filter out assets into target/classes directory
          if (
            normalizedPosixPathRelativeToTheWorkspaceRoot === thisBpmnsNormalizedPosixPathRelativeToTheWorkspaceRoot ||
            normalizedPosixPathRelativeToTheWorkspaceRoot.includes(TARGET_DIRECTORY)
          ) {
            continue;
          }

          resources.push(
            envelopeContext.channelApi.requests.kogitoWorkspace_resourceContentRequest({
              normalizedPosixPathRelativeToTheWorkspaceRoot,
              opts: { type: "text" },
            })
          );
        }
        return Promise.all(resources);
      })
      .then((resources) => {
        const externalModelsIndex: BpmnEditor.ExternalModelsIndex = {};

        const loadedDmnsByPathRelativeToTheWorkspaceRoot = new Set<string>();
        const dmnsByNamespace = getDmnsByNamespace(resources);

        for (let i = 0; i < resources.length; i++) {
          const resource = resources[i];
          if (!resource) {
            continue;
          }

          const ext = __path.extname(resource.normalizedPosixPathRelativeToTheWorkspaceRoot);

          const resourceContent = resource.content ?? "";

          // DMN Files
          if (ext === ".dmn") {
            const namespaceOfTheResourceFile = domParser
              .getDomDocument(resourceContent)
              .documentElement.getAttribute("namespace");

            if (namespaceOfTheResourceFile) {
              checkIfNamespaceIsAlreadyLoaded({
                externalModelsIndex,
                namespaceOfTheResourceFile,
                normalizedPosixPathRelativeToTheWorkspaceRoot: resource.normalizedPosixPathRelativeToTheWorkspaceRoot,
              });

              loadModel({
                includedModelContent: resourceContent,
                includedModelNamespace: namespaceOfTheResourceFile,
                externalModelsIndex,
                thisBpmnsNormalizedPosixPathRelativeToTheWorkspaceRoot,
                loadedDmnsByPathRelativeToTheWorkspaceRoot,
                normalizedPosixPathRelativeToTheWorkspaceRoot: resource.normalizedPosixPathRelativeToTheWorkspaceRoot,
                resourcesByNamespace: dmnsByNamespace,
              });
            }
          }

          // Unknown files
          else {
            throw new Error(`Unknown extension '${ext}'.`);
          }
        }

        if (!canceled) {
          onChange(externalModelsIndex);
        }
        doneBootstrapping.resolve();
      });

    return () => {
      canceled = true;
    };
  }, [
    onChange,
    thisBpmnsNormalizedPosixPathRelativeToTheWorkspaceRoot,
    externalUpdatesCount, // Hack. See above.
    doneBootstrapping,
    getDmnsByNamespace,
    envelopeContext.channelApi.requests,
  ]);

  return <></>;
}

function loadModel(args: {
  thisBpmnsNormalizedPosixPathRelativeToTheWorkspaceRoot: string;
  resourcesByNamespace: Map<string, ResourceContent>;
  normalizedPosixPathRelativeToTheWorkspaceRoot: string;
  includedModelNamespace: string;
  loadedDmnsByPathRelativeToTheWorkspaceRoot: Set<string>;
  includedModelContent: string;
  externalModelsIndex: BpmnEditor.ExternalModelsIndex;
}) {
  const normalizedPosixPathRelativeToTheOpenFile = __path.relative(
    __path.dirname(args.thisBpmnsNormalizedPosixPathRelativeToTheWorkspaceRoot),
    args.normalizedPosixPathRelativeToTheWorkspaceRoot
  );

  try {
    const includedModel = normalizeDmn(
      getDmnMarshaller(args.includedModelContent, { upgradeTo: "latest" }).parser.parse()
    );

    args.externalModelsIndex[args.includedModelNamespace] = {
      normalizedPosixPathRelativeToTheOpenFile,
      model: includedModel,
      type: "dmn",
      svg: "",
    };

    args.loadedDmnsByPathRelativeToTheWorkspaceRoot.add(args.normalizedPosixPathRelativeToTheWorkspaceRoot);
  } catch (e) {
    console.info(
      `BPMN EDITOR: External Models Manager: Error loading DMN '${normalizedPosixPathRelativeToTheOpenFile}'. Ignoring.`,
      e
    );
  }
}

function checkIfNamespaceIsAlreadyLoaded(args: {
  externalModelsIndex: BpmnEditor.ExternalModelsIndex;
  namespaceOfTheResourceFile: string;
  normalizedPosixPathRelativeToTheWorkspaceRoot: string;
}) {
  if (args.externalModelsIndex[args.namespaceOfTheResourceFile]) {
    console.warn(
      `BPMN EDITOR ROOT: Multiple DMN models encountered with the same namespace '${args.namespaceOfTheResourceFile}': '${
        args.normalizedPosixPathRelativeToTheWorkspaceRoot
      }' and '${
        args.externalModelsIndex[args.namespaceOfTheResourceFile]!.normalizedPosixPathRelativeToTheOpenFile
      }'. The latter will be considered.`
    );
  }
}
