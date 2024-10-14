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
import { useEffect, useMemo, useState } from "react";
import * as __path from "path";
import {
  imperativePromiseHandle,
  PromiseImperativeHandle,
} from "@kie-tools-core/react-hooks/dist/useImperativePromiseHandler";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { ResourceContent, SearchType, WorkspaceChannelApi, WorkspaceEdit } from "@kie-tools-core/workspace/dist/api";
import { KeyboardShortcutsService } from "@kie-tools-core/keyboard-shortcuts/dist/envelope/KeyboardShortcutsService";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { normalize } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { getMarshaller as getDmnMarshaller } from "@kie-tools/dmn-marshaller";
import * as TestScenarioEditor from "@kie-tools/scesim-editor/dist/TestScenarioEditor";
import { getMarshaller, SceSimMarshaller, SceSimModel } from "@kie-tools/scesim-marshaller";
import { EMPTY_ONE_EIGHT } from "@kie-tools/scesim-editor/dist/resources/EmptyScesimFile";

export const DMN_MODELS_SEARCH_GLOB_PATTERN = "**/*.{dmn}";

export type TestScenarioEditorRootProps = {
  exposing: (s: TestScenarioEditorRoot) => void;
  isReadOnly: boolean;
  keyboardShortcutsService: KeyboardShortcutsService | undefined;
  onNewEdit: (edit: WorkspaceEdit) => void;
  onRequestWorkspaceFilesList: WorkspaceChannelApi["kogitoWorkspace_resourceListRequest"];
  onRequestWorkspaceFileContent: WorkspaceChannelApi["kogitoWorkspace_resourceContentRequest"];
  onOpenFileFromNormalizedPosixPathRelativeToTheWorkspaceRoot: WorkspaceChannelApi["kogitoWorkspace_openFile"];
  workspaceRootAbsolutePosixPath: string;
};

export type TestScenarioEditorRootState = {
  error: Error | undefined;
  externalModelsByNamespace: TestScenarioEditor.ExternalModelsIndex;
  externalModelsManagerDoneBootstraping: boolean;
  isReadOnly: boolean;
  keyboardShortcutsRegistred: boolean;
  keyboardShortcutsRegisterIds: number[];
  marshaller: SceSimMarshaller | undefined;
  openFilenormalizedPosixPathRelativeToTheWorkspaceRoot: string | undefined;
  pointer: number;
  stack: SceSimModel[];
};

export class TestScenarioEditorRoot extends React.Component<TestScenarioEditorRootProps, TestScenarioEditorRootState> {
  private readonly externalModelsManagerDoneBootstraping = imperativePromiseHandle<void>();

  private readonly testScenarioEditorRef: React.RefObject<TestScenarioEditor.TestScenarioEditorRef>;

  constructor(props: TestScenarioEditorRootProps) {
    super(props);
    props.exposing(this);
    this.testScenarioEditorRef = React.createRef();
    this.state = {
      error: undefined,
      externalModelsByNamespace: {},
      externalModelsManagerDoneBootstraping: false,
      isReadOnly: props.isReadOnly,
      keyboardShortcutsRegisterIds: [],
      keyboardShortcutsRegistred: false,
      marshaller: undefined,
      openFilenormalizedPosixPathRelativeToTheWorkspaceRoot: undefined,
      pointer: -1,
      stack: [],
    };
  }

  // Exposed API

  public async undo(): Promise<void> {
    this.setState((prev) => ({ ...prev, pointer: Math.max(0, prev.pointer - 1) }));
  }

  public async redo(): Promise<void> {
    this.setState((prev) => ({ ...prev, pointer: Math.min(prev.stack.length - 1, prev.pointer + 1) }));
  }

  public async getDiagramSvg(): Promise<string | undefined> {
    return this.testScenarioEditorRef.current?.getDiagramSvg();
  }

  public async getContent(): Promise<string> {
    if (!this.state.marshaller || !this.model) {
      throw new Error(
        `Test Scenario EDITOR ROOT: Content has not been set yet. Throwing an error to prevent returning a "default" content.`
      );
    }

    return this.state.marshaller.builder.build(this.model);
  }

  public async setContent(
    openFilenormalizedPosixPathRelativeToTheWorkspaceRoot: string,
    content: string
  ): Promise<void> {
    const marshaller = this.getMarshaller(content);

    // Save stack
    let savedStackPointer: SceSimModel[] = [];

    // Set the model and path for external models manager.
    this.setState((prev) => {
      savedStackPointer = [...prev.stack];
      return {
        stack: [marshaller.parser.parse()],
        openFilenormalizedPosixPathRelativeToTheWorkspaceRoot,
        pointer: 0,
      };
    });

    // Wait the external manager models to load.
    await this.externalModelsManagerDoneBootstraping.promise;

    // Set the valeus to render the Test Scenario Editor.
    this.setState((prev) => {
      // External change to the same file.
      if (
        prev.openFilenormalizedPosixPathRelativeToTheWorkspaceRoot ===
        openFilenormalizedPosixPathRelativeToTheWorkspaceRoot
      ) {
        const newStack = savedStackPointer.slice(0, prev.pointer + 1);
        return {
          externalModelsManagerDoneBootstraping: true,
          isReadOnly: prev.isReadOnly,
          openFilenormalizedPosixPathRelativeToTheWorkspaceRoot,
          marshaller,
          pointer: newStack.length,
          stack: [...newStack, marshaller.parser.parse()],
        };
      }

      // Different file opened. Need to reset everything.
      else {
        return {
          externalModelsManagerDoneBootstraping: true,
          isReadOnly: prev.isReadOnly,
          marshaller,
          openFilenormalizedPosixPathRelativeToTheWorkspaceRoot,
          pointer: 0,
          stack: [marshaller.parser.parse()],
        };
      }
    });
  }

  public get model(): SceSimModel | undefined {
    return this.state.stack[this.state.pointer];
  }

  // Internal methods

  private getMarshaller(content: string) {
    try {
      return getMarshaller(content || EMPTY_ONE_EIGHT);
    } catch (e) {
      this.setState((s) => ({
        ...s,
        error: e,
      }));
      throw e;
    }
  }

  private setExternalModelsByNamespace = (externalModelsByNamespace: TestScenarioEditor.ExternalModelsIndex) => {
    this.setState((prev) => ({ ...prev, externalModelsByNamespace }));
  };

  private onModelChange: TestScenarioEditor.OnSceSimModelChange = (model) => {
    this.setState(
      (prev) => {
        const newStack = prev.stack.slice(0, prev.pointer + 1);
        return {
          ...prev,
          stack: [...newStack, model],
          pointer: newStack.length,
        };
      },
      () =>
        this.props.onNewEdit({
          id: `${this.state.openFilenormalizedPosixPathRelativeToTheWorkspaceRoot}__${generateUuid()}`,
        })
    );
  };

  private onRequestExternalModelsAvailableToInclude: TestScenarioEditor.OnRequestExternalModelsAvailableToInclude =
    async () => {
      if (!this.state.openFilenormalizedPosixPathRelativeToTheWorkspaceRoot) {
        return [];
      }

      const list = await this.props.onRequestWorkspaceFilesList({
        pattern: DMN_MODELS_SEARCH_GLOB_PATTERN,
        opts: { type: SearchType.TRAVERSAL },
      });

      return list.normalizedPosixPathsRelativeToTheWorkspaceRoot.flatMap((p) =>
        __path.relative(__path.dirname(this.state.openFilenormalizedPosixPathRelativeToTheWorkspaceRoot!), p)
      );
    };

  private onRequestToResolvePathRelativeToTheOpenFile: TestScenarioEditor.OnRequestToResolvePath = (
    normalizedPosixPathRelativeToTheOpenFile
  ) => {
    const normalizedPosixPathRelativeToTheWorkspaceRoot = __path
      .resolve(
        __path.dirname(this.state.openFilenormalizedPosixPathRelativeToTheWorkspaceRoot!),
        normalizedPosixPathRelativeToTheOpenFile
      )
      .substring(1); // Remove leading slash.

    return normalizedPosixPathRelativeToTheWorkspaceRoot;

    // Example:
    // this.state.openFileAbsolutePath = /Users/ljmotta/packages/dmns/Dmn.dmn
    // normalizedPosixPathRelativeToTheOpenFile = ../../tmp/Tmp.dmn
    // workspaceRootAbsolutePosixPath = /Users/ljmotta
    // resolvedAbsolutePath = /Users/ljmotta/tmp/Tmp.dmn
    // return (which is the normalizedPosixPathRelativeToTheWorkspaceRoot) = tmp/Tmp.dmn
  };

  private onRequestExternalModelByPathsRelativeToTheOpenFile: TestScenarioEditor.OnRequestExternalModelByPath = async (
    normalizedPosixPathRelativeToTheOpenFile
  ) => {
    const normalizedPosixPathRelativeToTheWorkspaceRoot = this.onRequestToResolvePathRelativeToTheOpenFile(
      normalizedPosixPathRelativeToTheOpenFile
    );
    const resource = await this.props.onRequestWorkspaceFileContent({
      normalizedPosixPathRelativeToTheWorkspaceRoot,
      opts: { type: "text" },
    });

    const ext = __path.extname(normalizedPosixPathRelativeToTheOpenFile);
    if (ext === ".dmn") {
      return {
        normalizedPosixPathRelativeToTheOpenFile,
        type: "dmn",
        model: normalize(getDmnMarshaller(resource?.content ?? "", { upgradeTo: "latest" }).parser.parse()),
        svg: "",
      };
    } else {
      throw new Error(`Unknown extension '${ext}'.`);
    }
  };

  private onOpenFileFromPathRelativeToTheOpenFile = (normalizedPosixPathRelativeToTheOpenFile: string) => {
    if (!this.state.openFilenormalizedPosixPathRelativeToTheWorkspaceRoot) {
      return;
    }

    this.props.onOpenFileFromNormalizedPosixPathRelativeToTheWorkspaceRoot(
      this.onRequestToResolvePathRelativeToTheOpenFile(normalizedPosixPathRelativeToTheOpenFile)
    );
  };

  public componentDidUpdate(
    prevProps: Readonly<TestScenarioEditorRootProps>,
    prevState: Readonly<TestScenarioEditorRootState>,
    snapshot?: any
  ): void {
    if (this.props.keyboardShortcutsService === undefined || this.state.keyboardShortcutsRegistred === true) {
      return;
    }

    const commands = this.testScenarioEditorRef.current?.getCommands();
    if (commands === undefined) {
      return;
    }
    const togglePropertiesPanel = this.props.keyboardShortcutsService?.registerKeyPress(
      "I",
      "Misc | Open/Close dock panel",
      async () => commands.toggleTestScenarioDock()
    );

    this.setState((prev) => ({
      ...prev,
      keyboardShortcutsRegistred: true,
      keyboardShortcutsRegisterIds: [togglePropertiesPanel],
    }));
  }

  public componentWillUnmount() {
    const keyboardShortcuts = this.testScenarioEditorRef.current?.getCommands();
    if (keyboardShortcuts === undefined) {
      return;
    }

    this.state.keyboardShortcutsRegisterIds.forEach((id) => {
      this.props.keyboardShortcutsService?.deregister(id);
    });
  }

  public render() {
    return (
      <>
        {this.state.error && <TestScenarioMarshallerFallbackError error={this.state.error} />}
        {this.model && (
          <>
            <TestScenarioEditor.TestScenarioEditor
              ref={this.testScenarioEditorRef}
              issueTrackerHref={""}
              model={this.model}
              onModelChange={this.onModelChange}
              onRequestExternalModelsAvailableToInclude={this.onRequestExternalModelsAvailableToInclude}
              onRequestExternalModelByPath={this.onRequestExternalModelByPathsRelativeToTheOpenFile}
              onRequestToJumpToPath={this.onOpenFileFromPathRelativeToTheOpenFile}
              onRequestToResolvePath={this.onRequestToResolvePathRelativeToTheOpenFile}
              openFilenormalizedPosixPathRelativeToTheWorkspaceRoot={
                this.state.openFilenormalizedPosixPathRelativeToTheWorkspaceRoot
              }
            />
            /
            {
              <ExternalModelsManager
                workspaceRootAbsolutePosixPath={this.props.workspaceRootAbsolutePosixPath}
                thisScesimNormalizedPosixPathRelativeToTheWorkspaceRoot={
                  this.state.openFilenormalizedPosixPathRelativeToTheWorkspaceRoot
                }
                model={this.model}
                onChange={this.setExternalModelsByNamespace}
                onRequestWorkspaceFilesList={this.props.onRequestWorkspaceFilesList}
                onRequestWorkspaceFileContent={this.props.onRequestWorkspaceFileContent}
                externalModelsManagerDoneBootstraping={this.externalModelsManagerDoneBootstraping}
              />
            }
          </>
        )}
      </>
    );
  }
}

const NAMESPACES_EFFECT_SEPARATOR = " , ";

function ExternalModelsManager({
  workspaceRootAbsolutePosixPath,
  thisScesimNormalizedPosixPathRelativeToTheWorkspaceRoot,
  model,
  onChange,
  onRequestWorkspaceFileContent,
  onRequestWorkspaceFilesList,
  externalModelsManagerDoneBootstraping,
}: {
  workspaceRootAbsolutePosixPath: string;
  thisScesimNormalizedPosixPathRelativeToTheWorkspaceRoot: string | undefined;
  model: SceSimModel;
  onChange: (externalModelsByNamespace: TestScenarioEditor.ExternalModelsIndex) => void;
  onRequestWorkspaceFileContent: WorkspaceChannelApi["kogitoWorkspace_resourceContentRequest"];
  onRequestWorkspaceFilesList: WorkspaceChannelApi["kogitoWorkspace_resourceListRequest"];
  externalModelsManagerDoneBootstraping: PromiseImperativeHandle<void>;
}) {
  const namespaces = useMemo(
    () => model.ScenarioSimulationModel.settings.dmnNamespace?.__$$text ?? [],
    [model.ScenarioSimulationModel.settings.dmnNamespace]
  );

  const [externalUpdatesCount, setExternalUpdatesCount] = useState(0);

  // This is a hack. Every time a file is updates in KIE Sandbox, the Shared Worker emits an event to this BroadcastChannel.
  // By listening to it, we can reload the `externalModelsByNamespace` object. This makes the DMN Editor react to external changes,
  // Which is very important for multi-file editing.
  //
  // Now, this mechanism is not ideal. We would ideally only be notified on changes to relevant files, but this sub-system does not exist yet.
  // The consequence of this "hack" is some extra reloads.
  useEffect(() => {
    const bc = new BroadcastChannel("workspaces_files");
    bc.onmessage = ({ data }) => {
      // Changes to `thisDmn` shouldn't update its references to external models.
      // Here, `data?.relativePath` is relative to the workspace root.
      if (data?.relativePath === thisScesimNormalizedPosixPathRelativeToTheWorkspaceRoot) {
        return;
      }
      //TODO doublecheck
      const ext = __path.extname(thisScesimNormalizedPosixPathRelativeToTheWorkspaceRoot!);
      if (ext !== "dmn") {
        return;
      }

      setExternalUpdatesCount((prev) => prev + 1);
    };
    return () => {
      bc.close();
    };
  }, [thisScesimNormalizedPosixPathRelativeToTheWorkspaceRoot]);

  // This effect actually populates `externalModelsByNamespace` through the `onChange` call.
  useEffect(() => {
    let canceled = false;

    if (!thisScesimNormalizedPosixPathRelativeToTheWorkspaceRoot) {
      return;
    }

    onRequestWorkspaceFilesList({ pattern: DMN_MODELS_SEARCH_GLOB_PATTERN, opts: { type: SearchType.TRAVERSAL } })
      .then((list) => {
        const resources: Array<Promise<ResourceContent | undefined>> = [];
        for (let i = 0; i < list.normalizedPosixPathsRelativeToTheWorkspaceRoot.length; i++) {
          const normalizedPosixPathRelativeToTheWorkspaceRoot = list.normalizedPosixPathsRelativeToTheWorkspaceRoot[i];

          if (
            normalizedPosixPathRelativeToTheWorkspaceRoot === thisScesimNormalizedPosixPathRelativeToTheWorkspaceRoot
          ) {
            continue;
          }

          resources.push(
            onRequestWorkspaceFileContent({
              normalizedPosixPathRelativeToTheWorkspaceRoot,
              opts: { type: "text" },
            })
          );
        }
        return Promise.all(resources);
      })
      .then((resources) => {
        const externalModelsIndex: DmnEditor.ExternalModelsIndex = {};

        const namespacesSet = new Set(namespaces.split(NAMESPACES_EFFECT_SEPARATOR));

        for (let i = 0; i < resources.length; i++) {
          const resource = resources[i];
          if (!resource) {
            continue;
          }

          const content = resource.content ?? "";

          const normalizedPosixPathRelativeToTheOpenFile = __path.relative(
            __path.dirname(thisScesimNormalizedPosixPathRelativeToTheWorkspaceRoot),
            resource.normalizedPosixPathRelativeToTheWorkspaceRoot
          );

          const ext = __path.extname(resource.normalizedPosixPathRelativeToTheWorkspaceRoot);
          if (ext === ".dmn") {
            const namespace = domParser.getDomDocument(content).documentElement.getAttribute("namespace");
            if (namespace && namespacesSet.has(namespace)) {
              // Check for multiplicity of namespaces on DMN models
              if (externalModelsIndex[namespace]) {
                console.warn(
                  `DMN EDITOR ROOT: Multiple DMN models encountered with the same namespace '${namespace}': '${
                    resource.normalizedPosixPathRelativeToTheWorkspaceRoot
                  }' and '${
                    externalModelsIndex[namespace]!.normalizedPosixPathRelativeToTheOpenFile
                  }'. The latter will be considered.`
                );
              }

              externalModelsIndex[namespace] = {
                normalizedPosixPathRelativeToTheOpenFile,
                model: normalize(getDmnMarshaller(content, { upgradeTo: "latest" }).parser.parse()),
                type: "dmn",
                svg: "",
              };
            }
          } else {
            throw new Error(`Unknown extension '${ext}'.`);
          }
        }

        if (!canceled) {
          onChange(externalModelsIndex);
        }
        externalModelsManagerDoneBootstraping.resolve();
      });

    return () => {
      canceled = true;
    };
  }, [
    namespaces,
    onChange,
    onRequestWorkspaceFileContent,
    onRequestWorkspaceFilesList,
    thisScesimNormalizedPosixPathRelativeToTheWorkspaceRoot,
    externalUpdatesCount,
    workspaceRootAbsolutePosixPath,
    externalModelsManagerDoneBootstraping,
  ]);

  return <></>;
}

function TestScenarioMarshallerFallbackError({ error }: { error: Error }) {
  return (
    <Flex justifyContent={{ default: "justifyContentCenter" }} style={{ marginTop: "100px" }}>
      <EmptyState style={{ maxWidth: "1280px" }}>
        <EmptyStateIcon icon={() => <div style={{ fontSize: "3em" }}>😕</div>} />
        <Title size={"lg"} headingLevel={"h4"}>
          Unable to open file.
        </Title>
        <br />
        <EmptyStateBody>Error details: {error.message}</EmptyStateBody>
      </EmptyState>
    </Flex>
  );
}
