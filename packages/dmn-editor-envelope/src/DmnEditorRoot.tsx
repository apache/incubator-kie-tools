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
import { useEffect, useMemo, useState } from "react";
import * as DmnEditor from "@kie-tools/dmn-editor/dist/DmnEditor";
import { normalize, Normalized } from "@kie-tools/dmn-editor/dist/normalization/normalize";
import { DMN_LATEST_VERSION, DmnLatestModel, DmnMarshaller, getMarshaller } from "@kie-tools/dmn-marshaller";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import {
  ContentType,
  ResourceContent,
  SearchType,
  WorkspaceChannelApi,
  WorkspaceEdit,
} from "@kie-tools-core/workspace/dist/api";
import { DMN15_SPEC } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";
import { domParser } from "@kie-tools/xml-parser-ts";
import { ns as dmn15ns } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/meta";
import { XML2PMML } from "@kie-tools/pmml-editor-marshaller";
import { getPmmlNamespace } from "@kie-tools/dmn-editor/dist/pmml/pmml";
import { getNamespaceOfDmnImport } from "@kie-tools/dmn-editor/dist/includedModels/importNamespaces";
import {
  imperativePromiseHandle,
  PromiseImperativeHandle,
} from "@kie-tools-core/react-hooks/dist/useImperativePromiseHandler";
import { KeyboardShortcutsService } from "@kie-tools-core/keyboard-shortcuts/dist/envelope/KeyboardShortcutsService";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Title } from "@patternfly/react-core/dist/js/components/Title";

export const EXTERNAL_MODELS_SEARCH_GLOB_PATTERN = "**/*.{dmn,pmml}";

export const EMPTY_DMN = () => `<?xml version="1.0" encoding="UTF-8"?>
<definitions
  xmlns="${dmn15ns.get("")}"
  expressionLanguage="${DMN15_SPEC.expressionLanguage.default}"
  namespace="https://kie.org/dmn/${generateUuid()}"
  id="${generateUuid()}"
  name="DMN${generateUuid()}">
</definitions>`;

export type DmnEditorRootProps = {
  exposing: (s: DmnEditorRoot) => void;
  onNewEdit: (edit: WorkspaceEdit) => void;
  onRequestWorkspaceFilesList: WorkspaceChannelApi["kogitoWorkspace_resourceListRequest"];
  onRequestWorkspaceFileContent: WorkspaceChannelApi["kogitoWorkspace_resourceContentRequest"];
  onOpenFileFromNormalizedPosixPathRelativeToTheWorkspaceRoot: WorkspaceChannelApi["kogitoWorkspace_openFile"];
  workspaceRootAbsolutePosixPath: string;
  keyboardShortcutsService: KeyboardShortcutsService | undefined;
  isReadOnly: boolean;
};

export type DmnEditorRootState = {
  marshaller: DmnMarshaller<typeof DMN_LATEST_VERSION> | undefined;
  stack: Normalized<DmnLatestModel>[];
  pointer: number;
  openFilenormalizedPosixPathRelativeToTheWorkspaceRoot: string | undefined;
  externalModelsByNamespace: DmnEditor.ExternalModelsIndex;
  isReadOnly: boolean;
  externalModelsManagerDoneBootstraping: boolean;
  keyboardShortcutsRegisterIds: number[];
  keyboardShortcutsRegistred: boolean;
  error: Error | undefined;
};

export class DmnEditorRoot extends React.Component<DmnEditorRootProps, DmnEditorRootState> {
  private readonly externalModelsManagerDoneBootstraping = imperativePromiseHandle<void>();

  private readonly dmnEditorRef: React.RefObject<DmnEditor.DmnEditorRef>;

  constructor(props: DmnEditorRootProps) {
    super(props);
    props.exposing(this);
    this.dmnEditorRef = React.createRef();
    this.state = {
      externalModelsByNamespace: {},
      marshaller: undefined,
      stack: [],
      pointer: -1,
      openFilenormalizedPosixPathRelativeToTheWorkspaceRoot: undefined,
      isReadOnly: props.isReadOnly,
      externalModelsManagerDoneBootstraping: false,
      keyboardShortcutsRegisterIds: [],
      keyboardShortcutsRegistred: false,
      error: undefined,
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
    return this.dmnEditorRef.current?.getDiagramSvg();
  }

  public async getContent(): Promise<string> {
    if (!this.state.marshaller || !this.model) {
      throw new Error(
        `DMN EDITOR ROOT: Content has not been set yet. Throwing an error to prevent returning a "default" content.`
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
    let savedStackPointer: Normalized<DmnLatestModel>[] = [];

    // Set the model and path for external models manager.
    this.setState((prev) => {
      savedStackPointer = [...prev.stack];
      return {
        stack: [normalize(marshaller.parser.parse())],
        openFilenormalizedPosixPathRelativeToTheWorkspaceRoot,
        pointer: 0,
      };
    });

    // Wait the external manager models to load.
    await this.externalModelsManagerDoneBootstraping.promise;

    // Set the valeus to render the DMN Editor.
    this.setState((prev) => {
      // External change to the same file.
      if (
        prev.openFilenormalizedPosixPathRelativeToTheWorkspaceRoot ===
        openFilenormalizedPosixPathRelativeToTheWorkspaceRoot
      ) {
        const newStack = savedStackPointer.slice(0, prev.pointer + 1);
        return {
          marshaller,
          openFilenormalizedPosixPathRelativeToTheWorkspaceRoot,
          stack: [...newStack, normalize(marshaller.parser.parse())],
          isReadOnly: prev.isReadOnly,
          pointer: newStack.length,
          externalModelsManagerDoneBootstraping: true,
        };
      }

      // Different file opened. Need to reset everything.
      else {
        return {
          marshaller,
          openFilenormalizedPosixPathRelativeToTheWorkspaceRoot,
          stack: [normalize(marshaller.parser.parse())],
          isReadOnly: prev.isReadOnly,
          pointer: 0,
          externalModelsManagerDoneBootstraping: true,
        };
      }
    });
  }

  public get model(): Normalized<DmnLatestModel> | undefined {
    return this.state.stack[this.state.pointer];
  }

  // Internal methods

  private getMarshaller(content: string) {
    try {
      return getMarshaller(content || EMPTY_DMN(), { upgradeTo: "latest" });
    } catch (e) {
      this.setState((s) => ({
        ...s,
        error: e,
      }));
      throw e;
    }
  }

  private setExternalModelsByNamespace = (externalModelsByNamespace: DmnEditor.ExternalModelsIndex) => {
    this.setState((prev) => ({ ...prev, externalModelsByNamespace }));
  };

  private onModelChange: DmnEditor.OnDmnModelChange = (model) => {
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

  private onRequestExternalModelsAvailableToInclude: DmnEditor.OnRequestExternalModelsAvailableToInclude = async () => {
    if (!this.state.openFilenormalizedPosixPathRelativeToTheWorkspaceRoot) {
      return [];
    }

    const list = await this.props.onRequestWorkspaceFilesList({
      pattern: EXTERNAL_MODELS_SEARCH_GLOB_PATTERN,
      opts: { type: SearchType.TRAVERSAL },
    });

    return list.normalizedPosixPathsRelativeToTheWorkspaceRoot.flatMap((p) =>
      // Do not show this DMN on the list
      p === this.state.openFilenormalizedPosixPathRelativeToTheWorkspaceRoot
        ? []
        : __path.relative(__path.dirname(this.state.openFilenormalizedPosixPathRelativeToTheWorkspaceRoot!), p)
    );
  };

  private onRequestToResolvePathRelativeToTheOpenFile: DmnEditor.OnRequestToResolvePath = (
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

  private onRequestExternalModelByPathsRelativeToTheOpenFile: DmnEditor.OnRequestExternalModelByPath = async (
    normalizedPosixPathRelativeToTheOpenFile
  ) => {
    const normalizedPosixPathRelativeToTheWorkspaceRoot = this.onRequestToResolvePathRelativeToTheOpenFile(
      normalizedPosixPathRelativeToTheOpenFile
    );
    const resource = await this.props.onRequestWorkspaceFileContent({
      normalizedPosixPathRelativeToTheWorkspaceRoot,
      opts: { type: ContentType.TEXT },
    });

    const ext = __path.extname(normalizedPosixPathRelativeToTheOpenFile);
    if (ext === ".dmn") {
      return {
        normalizedPosixPathRelativeToTheOpenFile,
        type: "dmn",
        model: normalize(getMarshaller(resource?.content ?? "", { upgradeTo: "latest" }).parser.parse()),
        svg: "",
      };
    } else if (ext === ".pmml") {
      return {
        normalizedPosixPathRelativeToTheOpenFile,
        type: "pmml",
        model: XML2PMML(resource?.content ?? ""),
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
    prevProps: Readonly<DmnEditorRootProps>,
    prevState: Readonly<DmnEditorRootState>,
    snapshot?: any
  ): void {
    if (this.props.keyboardShortcutsService === undefined || this.state.keyboardShortcutsRegistred === true) {
      return;
    }

    const commands = this.dmnEditorRef.current?.getCommands();
    if (commands === undefined) {
      return;
    }
    const cancelAction = this.props.keyboardShortcutsService.registerKeyPress("Escape", "Edit | Unselect", async () =>
      commands.cancelAction()
    );
    const deleteSelectionBackspace = this.props.keyboardShortcutsService.registerKeyPress(
      "Backspace",
      "Edit | Delete selection",
      async () => {}
    );
    const deleteSelectionDelete = this.props.keyboardShortcutsService.registerKeyPress(
      "Delete",
      "Edit | Delete selection",
      async () => {}
    );
    const selectAll = this.props.keyboardShortcutsService?.registerKeyPress(
      "A",
      "Edit | Select/Deselect all",
      async () => commands.selectAll()
    );
    const createGroup = this.props.keyboardShortcutsService?.registerKeyPress(
      "G",
      "Edit | Create group wrapping selection",
      async () => {
        console.log(" KEY GROUP PRESSED, ", commands);
        return commands.createGroup();
      }
    );
    const hideFromDrd = this.props.keyboardShortcutsService?.registerKeyPress("X", "Edit | Hide from DRD", async () =>
      commands.hideFromDrd()
    );
    const copy = this.props.keyboardShortcutsService?.registerKeyPress("Ctrl+C", "Edit | Copy nodes", async () =>
      commands.copy()
    );
    const cut = this.props.keyboardShortcutsService?.registerKeyPress("Ctrl+X", "Edit | Cut nodes", async () =>
      commands.cut()
    );
    const paste = this.props.keyboardShortcutsService?.registerKeyPress("Ctrl+V", "Edit | Paste nodes", async () =>
      commands.paste()
    );
    const togglePropertiesPanel = this.props.keyboardShortcutsService?.registerKeyPress(
      "I",
      "Misc | Open/Close properties panel",
      async () => commands.togglePropertiesPanel()
    );
    const toggleHierarchyHighlight = this.props.keyboardShortcutsService?.registerKeyPress(
      "H",
      "Misc | Toggle hierarchy highlights",
      async () => commands.toggleHierarchyHighlight()
    );
    const moveUp = this.props.keyboardShortcutsService.registerKeyPress(
      "Up",
      "Move | Move selection up",
      async () => {}
    );
    const moveDown = this.props.keyboardShortcutsService.registerKeyPress(
      "Down",
      "Move | Move selection down",
      async () => {}
    );
    const moveLeft = this.props.keyboardShortcutsService.registerKeyPress(
      "Left",
      "Move | Move selection left",
      async () => {}
    );
    const moveRight = this.props.keyboardShortcutsService.registerKeyPress(
      "Right",
      "Move | Move selection right",
      async () => {}
    );
    const bigMoveUp = this.props.keyboardShortcutsService.registerKeyPress(
      "Shift + Up",
      "Move | Move selection up a big distance",
      async () => {}
    );
    const bigMoveDown = this.props.keyboardShortcutsService.registerKeyPress(
      "Shift + Down",
      "Move | Move selection down a big distance",
      async () => {}
    );
    const bigMoveLeft = this.props.keyboardShortcutsService.registerKeyPress(
      "Shift + Left",
      "Move | Move selection left a big distance",
      async () => {}
    );
    const bigMoveRight = this.props.keyboardShortcutsService.registerKeyPress(
      "Shift + Right",
      "Move | Move selection right a big distance",
      async () => {}
    );
    const focusOnBounds = this.props.keyboardShortcutsService?.registerKeyPress(
      "B",
      "Navigate | Focus on selection",
      async () => commands.focusOnSelection()
    );
    const resetPosition = this.props.keyboardShortcutsService?.registerKeyPress(
      "Space",
      "Navigate | Reset position to origin",
      async () => commands.resetPosition()
    );
    const pan = this.props.keyboardShortcutsService?.registerKeyPress(
      "Right Mouse Button",
      "Navigate | Hold and drag to Pan",
      async () => {}
    );
    const zoom = this.props.keyboardShortcutsService?.registerKeyPress(
      "Ctrl",
      "Navigate | Hold and scroll to zoom in/out",
      async () => {}
    );
    const navigateHorizontally = this.props.keyboardShortcutsService?.registerKeyPress(
      "Shift",
      "Navigate | Hold and scroll to navigate horizontally",
      async () => {}
    );

    this.setState((prev) => ({
      ...prev,
      keyboardShortcutsRegistred: true,
      keyboardShortcutsRegisterIds: [
        bigMoveDown,
        bigMoveLeft,
        bigMoveRight,
        bigMoveUp,
        cancelAction,
        copy,
        createGroup,
        cut,
        deleteSelectionBackspace,
        deleteSelectionDelete,
        focusOnBounds,
        hideFromDrd,
        moveDown,
        moveLeft,
        moveRight,
        moveUp,
        navigateHorizontally,
        pan,
        paste,
        resetPosition,
        selectAll,
        toggleHierarchyHighlight,
        togglePropertiesPanel,
        zoom,
      ],
    }));
  }

  public componentWillUnmount() {
    const keyboardShortcuts = this.dmnEditorRef.current?.getCommands();
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
        {this.state.error && <DmnMarshallerFallbackError error={this.state.error} />}
        {this.model && (
          <>
            <DmnEditor.DmnEditor
              ref={this.dmnEditorRef}
              originalVersion={this.state.marshaller?.originalVersion}
              model={this.model}
              externalModelsByNamespace={this.state.externalModelsByNamespace}
              evaluationResults={[]}
              validationMessages={[]}
              externalContextName={""}
              externalContextDescription={""}
              issueTrackerHref={""}
              isReadOnly={this.state.isReadOnly}
              onModelChange={this.onModelChange}
              onRequestExternalModelsAvailableToInclude={this.onRequestExternalModelsAvailableToInclude}
              // (begin) All paths coming from inside the DmnEditor component are paths relative to the open file.
              onRequestExternalModelByPath={this.onRequestExternalModelByPathsRelativeToTheOpenFile}
              onRequestToJumpToPath={this.onOpenFileFromPathRelativeToTheOpenFile}
              onRequestToResolvePath={this.onRequestToResolvePathRelativeToTheOpenFile}
              // (end)
            />
            <ExternalModelsManager
              workspaceRootAbsolutePosixPath={this.props.workspaceRootAbsolutePosixPath}
              thisDmnsNormalizedPosixPathRelativeToTheWorkspaceRoot={
                this.state.openFilenormalizedPosixPathRelativeToTheWorkspaceRoot
              }
              model={this.model}
              onChange={this.setExternalModelsByNamespace}
              onRequestWorkspaceFilesList={this.props.onRequestWorkspaceFilesList}
              onRequestWorkspaceFileContent={this.props.onRequestWorkspaceFileContent}
              externalModelsManagerDoneBootstraping={this.externalModelsManagerDoneBootstraping}
            />
          </>
        )}
      </>
    );
  }
}

const NAMESPACES_EFFECT_SEPARATOR = " , ";

function ExternalModelsManager({
  workspaceRootAbsolutePosixPath,
  thisDmnsNormalizedPosixPathRelativeToTheWorkspaceRoot,
  model,
  onChange,
  onRequestWorkspaceFileContent,
  onRequestWorkspaceFilesList,
  externalModelsManagerDoneBootstraping,
}: {
  workspaceRootAbsolutePosixPath: string;
  thisDmnsNormalizedPosixPathRelativeToTheWorkspaceRoot: string | undefined;
  model: Normalized<DmnLatestModel>;
  onChange: (externalModelsByNamespace: DmnEditor.ExternalModelsIndex) => void;
  onRequestWorkspaceFileContent: WorkspaceChannelApi["kogitoWorkspace_resourceContentRequest"];
  onRequestWorkspaceFilesList: WorkspaceChannelApi["kogitoWorkspace_resourceListRequest"];
  externalModelsManagerDoneBootstraping: PromiseImperativeHandle<void>;
}) {
  const namespaces = useMemo(
    () =>
      (model.definitions.import ?? [])
        .map((i) => getNamespaceOfDmnImport({ dmnImport: i }))
        .join(NAMESPACES_EFFECT_SEPARATOR),
    [model.definitions.import]
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
      if (data?.relativePath === thisDmnsNormalizedPosixPathRelativeToTheWorkspaceRoot) {
        return;
      }

      setExternalUpdatesCount((prev) => prev + 1);
    };
    return () => {
      bc.close();
    };
  }, [thisDmnsNormalizedPosixPathRelativeToTheWorkspaceRoot]);

  // This effect actually populates `externalModelsByNamespace` through the `onChange` call.
  useEffect(() => {
    let canceled = false;

    if (!thisDmnsNormalizedPosixPathRelativeToTheWorkspaceRoot) {
      return;
    }

    onRequestWorkspaceFilesList({ pattern: EXTERNAL_MODELS_SEARCH_GLOB_PATTERN, opts: { type: SearchType.TRAVERSAL } })
      .then((list) => {
        const resources: Array<Promise<ResourceContent | undefined>> = [];
        for (let i = 0; i < list.normalizedPosixPathsRelativeToTheWorkspaceRoot.length; i++) {
          const normalizedPosixPathRelativeToTheWorkspaceRoot = list.normalizedPosixPathsRelativeToTheWorkspaceRoot[i];

          if (normalizedPosixPathRelativeToTheWorkspaceRoot === thisDmnsNormalizedPosixPathRelativeToTheWorkspaceRoot) {
            continue;
          }

          resources.push(
            onRequestWorkspaceFileContent({
              normalizedPosixPathRelativeToTheWorkspaceRoot,
              opts: { type: ContentType.TEXT },
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
            __path.dirname(thisDmnsNormalizedPosixPathRelativeToTheWorkspaceRoot),
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
                model: normalize(getMarshaller(content, { upgradeTo: "latest" }).parser.parse()),
                type: "dmn",
                svg: "",
              };
            }
          } else if (ext === ".pmml") {
            const namespace = getPmmlNamespace({ normalizedPosixPathRelativeToTheOpenFile });
            if (namespace && namespacesSet.has(namespace)) {
              // No need to check for namespaces being equal becuase there can't be two files with the same relativePath.
              externalModelsIndex[namespace] = {
                normalizedPosixPathRelativeToTheOpenFile,
                model: XML2PMML(content),
                type: "pmml",
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
    thisDmnsNormalizedPosixPathRelativeToTheWorkspaceRoot,
    externalUpdatesCount,
    workspaceRootAbsolutePosixPath,
    externalModelsManagerDoneBootstraping,
  ]);

  return <></>;
}

function DmnMarshallerFallbackError({ error }: { error: Error }) {
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
