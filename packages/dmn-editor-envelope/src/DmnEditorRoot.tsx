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
import { useCallback, useEffect, useMemo, useState } from "react";
import * as DmnEditor from "@kie-tools/dmn-editor/dist/DmnEditor";
import { normalize, Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { DMN_LATEST_VERSION, DmnLatestModel, DmnMarshaller, getMarshaller } from "@kie-tools/dmn-marshaller";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { ResourceContent, SearchType, WorkspaceChannelApi, WorkspaceEdit } from "@kie-tools-core/workspace/dist/api";
import { DMN16_SPEC } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_6/Dmn16Spec";
import { domParser } from "@kie-tools/xml-parser-ts";
import { ns as dmn16ns } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_6/ts-gen/meta";
import { XML2PMML } from "@kie-tools/pmml-editor-marshaller";
import { getPmmlNamespace } from "@kie-tools/dmn-editor/dist/pmml/pmml";
import { getNamespaceOfDmnImport } from "@kie-tools/dmn-editor/dist/includedModels/importNamespaces";
import {
  imperativePromiseHandle,
  PromiseImperativeHandle,
} from "@kie-tools-core/react-hooks/dist/useImperativePromiseHandler";
import { KeyboardShortcutsService } from "@kie-tools-core/keyboard-shortcuts/dist/envelope/KeyboardShortcutsService";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateHeader,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import {
  JavaCodeCompletionAccessor,
  JavaCodeCompletionClass,
} from "@kie-tools-core/vscode-java-code-completion/dist/api";
import { DmnEditorEnvelopeI18n } from "./i18n";

export const EXTERNAL_MODELS_SEARCH_GLOB_PATTERN = "**/*.{dmn,pmml}";
export const TARGET_DIRECTORY = "target/classes/";

export const EMPTY_DMN = () => `<?xml version="1.0" encoding="UTF-8"?>
<definitions
  xmlns="${dmn16ns.get("")}"
  expressionLanguage="${DMN16_SPEC.expressionLanguage.default}"
  namespace="https://kie.org/dmn/${generateUuid()}"
  id="${generateUuid()}"
  name="DMN${generateUuid()}">
</definitions>`;

export interface JavaCodeCompletionExposedInteropApi {
  getFields(fqcn: string): Promise<JavaCodeCompletionAccessor[]>;
  getClasses(query: string): Promise<JavaCodeCompletionClass[]>;
  isLanguageServerAvailable(): Promise<boolean>;
}

export type DmnEditorRootProps = {
  exposing: (s: DmnEditorRoot) => void;
  onNewEdit: (edit: WorkspaceEdit) => void;
  onRequestWorkspaceFilesList: WorkspaceChannelApi["kogitoWorkspace_resourceListRequest"];
  onRequestWorkspaceFileContent: WorkspaceChannelApi["kogitoWorkspace_resourceContentRequest"];
  onOpenFileFromNormalizedPosixPathRelativeToTheWorkspaceRoot: WorkspaceChannelApi["kogitoWorkspace_openFile"];
  onOpenedBoxedExpressionEditorNodeChange?: (newOpenedNodeId: string | undefined) => void;
  workspaceRootAbsolutePosixPath: string;
  keyboardShortcutsService: KeyboardShortcutsService | undefined;
  isEvaluationHighlightsSupported?: boolean;
  isReadOnly: boolean;
  isImportDataTypesFromJavaClassesSupported?: boolean;
  javaCodeCompletionService?: JavaCodeCompletionExposedInteropApi;
  i18n: DmnEditorEnvelopeI18n;
};

export type DmnEditorRootState = {
  marshaller: DmnMarshaller<typeof DMN_LATEST_VERSION> | undefined;
  stack: Normalized<DmnLatestModel>[];
  pointer: number;
  openFileNormalizedPosixPathRelativeToTheWorkspaceRoot: string | undefined;
  externalModelsByNamespace: DmnEditor.ExternalModelsIndex;
  isReadOnly: boolean;
  externalModelsManagerDoneBootstraping: boolean;
  keyboardShortcutsRegisterIds: number[];
  keyboardShortcutsRegistered: boolean;
  error: Error | undefined;
  evaluationResultsByNodeId: DmnEditor.EvaluationResultsByNodeId;
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
      openFileNormalizedPosixPathRelativeToTheWorkspaceRoot: undefined,
      isReadOnly: props.isReadOnly,
      externalModelsManagerDoneBootstraping: false,
      keyboardShortcutsRegisterIds: [],
      keyboardShortcutsRegistered: false,
      error: undefined,
      evaluationResultsByNodeId: new Map(),
    };
  }

  // Exposed API

  public openBoxedExpressionEditor(nodeId: string): void {
    this.dmnEditorRef.current?.openBoxedExpressionEditor(nodeId);
  }

  public showDmnEvaluationResults(evaluationResultsByNodeId: DmnEditor.EvaluationResultsByNodeId): void {
    this.setState((prev) => ({ ...prev, evaluationResultsByNodeId: evaluationResultsByNodeId }));
  }

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
    openFileNormalizedPosixPathRelativeToTheWorkspaceRoot: string,
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
        openFileNormalizedPosixPathRelativeToTheWorkspaceRoot,
        pointer: 0,
      };
    });

    // Wait the external manager models to load.
    await this.externalModelsManagerDoneBootstraping.promise;

    // Set the values to render the DMN Editor.
    this.setState((prev) => {
      // External change to the same file.
      if (
        prev.openFileNormalizedPosixPathRelativeToTheWorkspaceRoot ===
        openFileNormalizedPosixPathRelativeToTheWorkspaceRoot
      ) {
        const newStack = savedStackPointer.slice(0, prev.pointer + 1);
        return {
          marshaller,
          openFileNormalizedPosixPathRelativeToTheWorkspaceRoot,
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
          openFileNormalizedPosixPathRelativeToTheWorkspaceRoot,
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
          id: `${this.state.openFileNormalizedPosixPathRelativeToTheWorkspaceRoot}__${generateUuid()}`,
        })
    );
  };

  private onRequestExternalModelsAvailableToInclude: DmnEditor.OnRequestExternalModelsAvailableToInclude = async () => {
    if (!this.state.openFileNormalizedPosixPathRelativeToTheWorkspaceRoot) {
      return [];
    }

    const list = await this.props.onRequestWorkspaceFilesList({
      pattern: EXTERNAL_MODELS_SEARCH_GLOB_PATTERN,
      opts: { type: SearchType.TRAVERSAL },
    });

    return list.normalizedPosixPathsRelativeToTheWorkspaceRoot.flatMap((p) =>
      // Do not show this DMN on the list and filter out assets into target/classes directory
      p === this.state.openFileNormalizedPosixPathRelativeToTheWorkspaceRoot || p.includes(TARGET_DIRECTORY)
        ? []
        : __path.relative(__path.dirname(this.state.openFileNormalizedPosixPathRelativeToTheWorkspaceRoot!), p)
    );
  };

  private onRequestToResolvePathRelativeToTheOpenFile: DmnEditor.OnRequestToResolvePath = (
    normalizedPosixPathRelativeToTheOpenFile
  ) => {
    const normalizedPosixPathRelativeToTheWorkspaceRoot = __path
      .resolve(
        __path.dirname(this.state.openFileNormalizedPosixPathRelativeToTheWorkspaceRoot!),
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
      opts: { type: "text" },
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
    if (!this.state.openFileNormalizedPosixPathRelativeToTheWorkspaceRoot) {
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
    if (this.props.keyboardShortcutsService === undefined || this.state.keyboardShortcutsRegistered === true) {
      return;
    }

    const commands = this.dmnEditorRef.current?.getCommands();
    if (commands === undefined) {
      return;
    }
    const cancelAction = this.props.keyboardShortcutsService.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.escape,
      this.props.i18n.edit + " | " + this.props.i18n.unselect,
      async () => commands.cancelAction()
    );
    const deleteSelectionBackspace = this.props.keyboardShortcutsService.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.backspace,
      this.props.i18n.edit + " | " + this.props.i18n.deleteSelection,
      async () => {}
    );
    const deleteSelectionDelete = this.props.keyboardShortcutsService.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.delete,
      this.props.i18n.edit + " | " + this.props.i18n.deleteSelection,
      async () => {}
    );
    const selectAll = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.a,
      this.props.i18n.edit + " | " + this.props.i18n.selectDeselectAll,
      async () => commands.selectAll()
    );
    const createGroup = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.g,
      this.props.i18n.edit + " | " + this.props.i18n.createGroupWrappingSelection,
      async () => {
        console.log(" KEY GROUP PRESSED, ", commands);
        return commands.createGroup();
      }
    );
    const hideFromDrd = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.x,
      this.props.i18n.edit + " | " + this.props.i18n.hideFromDrd,
      async () => commands.hideFromDrd()
    );
    const copy = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.ctrlC,
      this.props.i18n.edit + " | " + this.props.i18n.copyNodes,
      async () => commands.copy()
    );
    const cut = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.ctrlX,
      this.props.i18n.edit + " | " + this.props.i18n.cutNodes,
      async () => commands.cut()
    );
    const paste = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.ctrlV,
      this.props.i18n.edit + " | " + this.props.i18n.pasteNodes,
      async () => commands.paste()
    );
    const togglePropertiesPanel = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.i,
      this.props.i18n.misc + " | " + this.props.i18n.openClosePropertiesPanel,
      async () => commands.togglePropertiesPanel()
    );
    const toggleHierarchyHighlight = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.h,
      this.props.i18n.misc + " | " + this.props.i18n.toggleHierarchyHighlights,
      async () => commands.toggleHierarchyHighlight()
    );
    const moveUp = this.props.keyboardShortcutsService.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.up,
      this.props.i18n.move + " | " + this.props.i18n.selectionUp,
      async () => {}
    );
    const moveDown = this.props.keyboardShortcutsService.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.down,
      this.props.i18n.move + " | " + this.props.i18n.selectionDown,
      async () => {}
    );
    const moveLeft = this.props.keyboardShortcutsService.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.left,
      this.props.i18n.move + " | " + this.props.i18n.selectionLeft,
      async () => {}
    );
    const moveRight = this.props.keyboardShortcutsService.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.right,
      this.props.i18n.move + " | " + this.props.i18n.selectionRight,
      async () => {}
    );
    const bigMoveUp = this.props.keyboardShortcutsService.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.shiftUp,
      this.props.i18n.move + " | " + this.props.i18n.selectionUpBigDistance,
      async () => {}
    );
    const bigMoveDown = this.props.keyboardShortcutsService.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.shiftDown,
      this.props.i18n.move + " | " + this.props.i18n.selectionDownBigDistance,
      async () => {}
    );
    const bigMoveLeft = this.props.keyboardShortcutsService.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.shiftLeft,
      this.props.i18n.move + " | " + this.props.i18n.selectionLeftBigDistance,
      async () => {}
    );
    const bigMoveRight = this.props.keyboardShortcutsService.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.shiftRight,
      this.props.i18n.move + " | " + this.props.i18n.selectionRightBigDistance,
      async () => {}
    );
    const focusOnBounds = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.b,
      this.props.i18n.navigate + " | " + this.props.i18n.focusOnSelection,
      async () => commands.focusOnSelection()
    );
    const resetPosition = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.space,
      this.props.i18n.navigate + " | " + this.props.i18n.resetPositionToOrigin,
      async () => commands.resetPosition()
    );
    const pan = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.rightMouseButton,
      this.props.i18n.navigate + " | " + this.props.i18n.holdAndDragtoPan,
      async () => {}
    );
    const zoom = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.ctrl,
      this.props.i18n.navigate + " | " + this.props.i18n.holdAndScrollToZoomInOut,
      async () => {}
    );
    const navigateHorizontally = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.shift,
      this.props.i18n.navigate + " | " + this.props.i18n.holdAndScrollToNavigateHorizontally,
      async () => {}
    );

    this.setState((prev) => ({
      ...prev,
      keyboardShortcutsRegistered: true,
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
              evaluationResultsByNodeId={this.state.evaluationResultsByNodeId}
              validationMessages={[]}
              externalContextName={""}
              externalContextDescription={""}
              issueTrackerHref={""}
              isEvaluationHighlightsSupported={this.props?.isEvaluationHighlightsSupported}
              isReadOnly={this.state.isReadOnly}
              isImportDataTypesFromJavaClassesSupported={this.props?.isImportDataTypesFromJavaClassesSupported}
              javaCodeCompletionService={this.props?.javaCodeCompletionService}
              onModelChange={this.onModelChange}
              onOpenedBoxedExpressionEditorNodeChange={this.props.onOpenedBoxedExpressionEditorNodeChange}
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
                this.state.openFileNormalizedPosixPathRelativeToTheWorkspaceRoot
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
    () => getIncludedNamespacesFromModel(model.definitions.import),
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
              `DMN EDITOR ROOT: Multiple DMN models encountered with the same namespace '${namespace}': '${
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

    if (!thisDmnsNormalizedPosixPathRelativeToTheWorkspaceRoot) {
      return;
    }

    onRequestWorkspaceFilesList({ pattern: EXTERNAL_MODELS_SEARCH_GLOB_PATTERN, opts: { type: SearchType.TRAVERSAL } })
      .then((list) => {
        const resources: Array<Promise<ResourceContent | undefined>> = [];
        for (let i = 0; i < list.normalizedPosixPathsRelativeToTheWorkspaceRoot.length; i++) {
          const normalizedPosixPathRelativeToTheWorkspaceRoot = list.normalizedPosixPathsRelativeToTheWorkspaceRoot[i];

          // Do not show this DMN on the list and filter out assets into target/classes directory
          if (
            normalizedPosixPathRelativeToTheWorkspaceRoot === thisDmnsNormalizedPosixPathRelativeToTheWorkspaceRoot ||
            normalizedPosixPathRelativeToTheWorkspaceRoot.includes(TARGET_DIRECTORY)
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
        const loadedDmnsByPathRelativeToTheWorkspaceRoot = new Set<string>();
        const dmnsByNamespace = getDmnsByNamespace(resources);

        for (let i = 0; i < resources.length; i++) {
          const resource = resources[i];
          if (!resource) {
            continue;
          }

          const ext = __path.extname(resource.normalizedPosixPathRelativeToTheWorkspaceRoot);
          const normalizedPosixPathRelativeToTheOpenFile = __path.relative(
            __path.dirname(thisDmnsNormalizedPosixPathRelativeToTheWorkspaceRoot),
            resource.normalizedPosixPathRelativeToTheWorkspaceRoot
          );

          const resourceContent = resource.content ?? "";

          // DMN Files
          if (ext === ".dmn") {
            const namespaceOfTheResourceFile = domParser
              .getDomDocument(resourceContent)
              .documentElement.getAttribute("namespace");

            if (namespaceOfTheResourceFile && namespacesSet.has(namespaceOfTheResourceFile)) {
              checkIfNamespaceIsAlreadyLoaded({
                externalModelsIndex,
                namespaceOfTheResourceFile,
                normalizedPosixPathRelativeToTheWorkspaceRoot: resource.normalizedPosixPathRelativeToTheWorkspaceRoot,
              });

              loadModel({
                includedModelContent: resourceContent,
                includedModelNamespace: namespaceOfTheResourceFile,
                externalModelsIndex,
                thisDmnsNormalizedPosixPathRelativeToTheWorkspaceRoot,
                loadedDmnsByPathRelativeToTheWorkspaceRoot,
                normalizedPosixPathRelativeToTheWorkspaceRoot: resource.normalizedPosixPathRelativeToTheWorkspaceRoot,
                resourcesByNamespace: dmnsByNamespace,
              });
            }
          }

          // PMML Files
          else if (ext === ".pmml") {
            const namespace = getPmmlNamespace({ normalizedPosixPathRelativeToTheOpenFile });
            if (namespace && namespacesSet.has(namespace)) {
              // No need to check for namespaces being equal becuase there can't be two files with the same relativePath.
              externalModelsIndex[namespace] = {
                normalizedPosixPathRelativeToTheOpenFile,
                model: XML2PMML(resourceContent),
                type: "pmml",
              };
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
    getDmnsByNamespace,
  ]);

  return <></>;
}

function DmnMarshallerFallbackError({ error }: { error: Error }) {
  return (
    <Flex justifyContent={{ default: "justifyContentCenter" }} style={{ marginTop: "100px" }}>
      <EmptyState style={{ maxWidth: "1280px" }}>
        <EmptyStateHeader
          titleText="Unable to open file."
          icon={<EmptyStateIcon icon={() => <div style={{ fontSize: "3em" }}>😕</div>} />}
          headingLevel={"h4"}
        />
        <br />
        <EmptyStateBody>Error details: {error.message}</EmptyStateBody>
      </EmptyState>
    </Flex>
  );
}

function getIncludedNamespacesFromModel(imports: Normalized<DmnLatestModel["definitions"]["import"]>) {
  return (imports ?? []).map((i) => getNamespaceOfDmnImport({ dmnImport: i })).join(NAMESPACES_EFFECT_SEPARATOR);
}

function loadModel(args: {
  thisDmnsNormalizedPosixPathRelativeToTheWorkspaceRoot: string;
  resourcesByNamespace: Map<string, ResourceContent>;
  normalizedPosixPathRelativeToTheWorkspaceRoot: string;
  includedModelNamespace: string;
  loadedDmnsByPathRelativeToTheWorkspaceRoot: Set<string>;
  includedModelContent: string;
  externalModelsIndex: DmnEditor.ExternalModelsIndex;
}) {
  const normalizedPosixPathRelativeToTheOpenFile = __path.relative(
    __path.dirname(args.thisDmnsNormalizedPosixPathRelativeToTheWorkspaceRoot),
    args.normalizedPosixPathRelativeToTheWorkspaceRoot
  );

  const includedModel = normalize(getMarshaller(args.includedModelContent, { upgradeTo: "latest" }).parser.parse());
  args.externalModelsIndex[args.includedModelNamespace] = {
    normalizedPosixPathRelativeToTheOpenFile,
    model: includedModel,
    type: "dmn",
    svg: "",
  };

  args.loadedDmnsByPathRelativeToTheWorkspaceRoot.add(args.normalizedPosixPathRelativeToTheWorkspaceRoot);

  loadDependentModels({
    ...args,
    model: includedModel,
  });
}

// Load all included models from the model and the included models of those models, recursively.
function loadDependentModels(args: {
  model: Normalized<DmnLatestModel>;
  externalModelsIndex: DmnEditor.ExternalModelsIndex;
  resourcesByNamespace: Map<string, ResourceContent>;
  loadedDmnsByPathRelativeToTheWorkspaceRoot: Set<string>;
  thisDmnsNormalizedPosixPathRelativeToTheWorkspaceRoot: string;
}) {
  const includedNamespaces = new Set(
    getIncludedNamespacesFromModel(args.model.definitions.import).split(NAMESPACES_EFFECT_SEPARATOR)
  );

  for (const includedNamespace of includedNamespaces) {
    if (!args.resourcesByNamespace.has(includedNamespace)) {
      console.warn(
        `DMN EDITOR ROOT: The included namespace '${includedNamespace}' for the model '${args.model.definitions["@_id"]}' can not be found.`
      );
      continue;
    }

    const resource = args.resourcesByNamespace.get(includedNamespace)!;
    if (args.loadedDmnsByPathRelativeToTheWorkspaceRoot.has(resource.normalizedPosixPathRelativeToTheWorkspaceRoot)) {
      continue;
    }

    checkIfNamespaceIsAlreadyLoaded({
      externalModelsIndex: args.externalModelsIndex,
      namespaceOfTheResourceFile: includedNamespace,
      normalizedPosixPathRelativeToTheWorkspaceRoot: resource.normalizedPosixPathRelativeToTheWorkspaceRoot,
    });

    loadModel({
      ...args,
      includedModelContent: resource.content ?? "",
      normalizedPosixPathRelativeToTheWorkspaceRoot: resource.normalizedPosixPathRelativeToTheWorkspaceRoot,
      includedModelNamespace: includedNamespace,
    });
  }
}

function checkIfNamespaceIsAlreadyLoaded(args: {
  externalModelsIndex: DmnEditor.ExternalModelsIndex;
  namespaceOfTheResourceFile: string;
  normalizedPosixPathRelativeToTheWorkspaceRoot: string;
}) {
  if (args.externalModelsIndex[args.namespaceOfTheResourceFile]) {
    console.warn(
      `DMN EDITOR ROOT: Multiple DMN models encountered with the same namespace '${args.namespaceOfTheResourceFile}': '${
        args.normalizedPosixPathRelativeToTheWorkspaceRoot
      }' and '${
        args.externalModelsIndex[args.namespaceOfTheResourceFile]!.normalizedPosixPathRelativeToTheOpenFile
      }'. The latter will be considered.`
    );
  }
}
