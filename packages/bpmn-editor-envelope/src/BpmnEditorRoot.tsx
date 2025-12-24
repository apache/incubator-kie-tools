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

import * as BpmnEditor from "@kie-tools/bpmn-editor/dist/BpmnEditor";
import { CustomTask } from "@kie-tools/bpmn-editor/dist/BpmnEditor";
import { normalize, Normalized } from "@kie-tools/bpmn-editor/dist/normalization/normalize";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { KeyboardShortcutsService } from "@kie-tools-core/keyboard-shortcuts/dist/envelope/KeyboardShortcutsService";
import {
  imperativePromiseHandle,
  PromiseImperativeHandle,
} from "@kie-tools-core/react-hooks/dist/useImperativePromiseHandler";
import { SearchType, WorkspaceChannelApi, WorkspaceEdit } from "@kie-tools-core/workspace/dist/api";
import {
  BPMN_LATEST_VERSION,
  BpmnLatestModel,
  BpmnMarshaller,
  getMarshaller as getBpmnMarshaller,
} from "@kie-tools/bpmn-marshaller";
import { ns as bpmn20ns } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/meta";
import { getMarshaller as getDmnMarshaller } from "@kie-tools/dmn-marshaller";
import { normalize as normalizeDmn } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import * as __path from "path";
import * as React from "react";
import { ExternalModelsManager } from "./externalModels/ExternalModelsManager";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateHeader,
  EmptyStateIcon,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { BpmnEditorEnvelopeI18n } from "./i18n";

export const EXTERNAL_MODELS_SEARCH_GLOB_PATTERN = "**/*.dmn";
export const TARGET_DIRECTORY = "target/classes/";

export const EMPTY_BPMN = () => `<?xml version="1.0" encoding="UTF-8"?>
<definitions
  xmlns="${bpmn20ns.get("")}"
  namespace="https://kie.org/bpmn/${generateUuid()}"
  id="${generateUuid()}"
  name="BPMN${generateUuid()}">
</definitions>`;

export type BpmnEditorRootProps = {
  exposing: (s: BpmnEditorRoot) => void;
  onNewEdit: (edit: WorkspaceEdit) => void;
  onRequestWorkspaceFilesList: WorkspaceChannelApi["kogitoWorkspace_resourceListRequest"];
  onRequestWorkspaceFileContent: WorkspaceChannelApi["kogitoWorkspace_resourceContentRequest"];
  onOpenFileFromNormalizedPosixPathRelativeToTheWorkspaceRoot: WorkspaceChannelApi["kogitoWorkspace_openFile"];
  customTasksManager: React.ComponentType<{
    onChange: React.Dispatch<React.SetStateAction<CustomTask[]>>;
    thisBpmnsNormalizedPosixPathRelativeToTheWorkspaceRoot: string | undefined;
    doneBootstrapping: PromiseImperativeHandle<void>;
  }>;
  workspaceRootAbsolutePosixPath: string;
  keyboardShortcutsService: KeyboardShortcutsService | undefined;
  isReadOnly: boolean;
  i18n: BpmnEditorEnvelopeI18n;
  locale: string;
};

export type BpmnEditorRootState = {
  marshaller: BpmnMarshaller<typeof BPMN_LATEST_VERSION> | undefined;
  stack: Normalized<BpmnLatestModel>[];
  pointer: number;
  openFileNormalizedPosixPathRelativeToTheWorkspaceRoot: string | undefined;
  externalModelsByNamespace: BpmnEditor.ExternalModelsIndex;
  customTasks: BpmnEditor.CustomTask[];
  isReadOnly: boolean;
  externalModelsManagerDoneBootstraping: boolean;
  keyboardShortcutsRegisterIds: number[];
  keyboardShortcutsRegistered: boolean;
  error: Error | undefined;
};

export class BpmnEditorRoot extends React.Component<BpmnEditorRootProps, BpmnEditorRootState> {
  private readonly externalModelsManagerDoneBootstraping = imperativePromiseHandle<void>();
  private readonly customTasksManagerDoneBootstraping = imperativePromiseHandle<void>();

  private readonly bpmnEditorRef: React.RefObject<BpmnEditor.BpmnEditorRef>;

  constructor(props: BpmnEditorRootProps) {
    super(props);
    props.exposing(this);
    this.bpmnEditorRef = React.createRef();
    this.state = {
      externalModelsByNamespace: {},
      customTasks: [],
      marshaller: undefined,
      stack: [],
      pointer: -1,
      openFileNormalizedPosixPathRelativeToTheWorkspaceRoot: undefined,
      isReadOnly: props.isReadOnly,
      externalModelsManagerDoneBootstraping: false,
      keyboardShortcutsRegisterIds: [],
      keyboardShortcutsRegistered: false,
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
    return this.bpmnEditorRef.current?.getDiagramSvg();
  }

  public async getContent(): Promise<string> {
    if (!this.state.marshaller || !this.model) {
      throw new Error(
        `BPMN EDITOR ROOT: Content has not been set yet. Throwing an error to prevent returning a "default" content.`
      );
    }

    return this.state.marshaller.builder.build(this.model);
  }

  public async setContent(
    openFileNormalizedPosixPathRelativeToTheWorkspaceRoot: string,
    content: string
  ): Promise<void> {
    const marshaller = this.getBpmnMarshaller(content);

    // Save stack
    let savedStackPointer: Normalized<BpmnLatestModel>[] = [];

    // Set the model and path for external models manager.
    this.setState((prev) => {
      savedStackPointer = [...prev.stack];
      return {
        stack: [normalize(marshaller.parser.parse())],
        openFileNormalizedPosixPathRelativeToTheWorkspaceRoot,
        pointer: 0,
      };
    });

    // Wait the external models manager models to load.
    await this.externalModelsManagerDoneBootstraping.promise;

    // Wait the custom tasks manager to load.
    await this.customTasksManagerDoneBootstraping.promise;

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

  public get model(): Normalized<BpmnLatestModel> | undefined {
    return this.state.stack[this.state.pointer];
  }

  // Internal methods

  private getBpmnMarshaller(content: string) {
    try {
      return getBpmnMarshaller(content || EMPTY_BPMN(), { upgradeTo: "latest" });
    } catch (e) {
      this.setState((s) => ({
        ...s,
        error: e,
      }));
      throw e;
    }
  }

  private setExternalModelsByNamespace = (externalModelsByNamespace: BpmnEditor.ExternalModelsIndex) => {
    this.setState((prev) => ({ ...prev, externalModelsByNamespace }));
  };

  private setCustomTasks = (customTasks: BpmnEditor.CustomTask[]) => {
    this.setState((prev) => ({ ...prev, customTasks }));
  };

  private onModelChange: BpmnEditor.OnBpmnModelChange = (model) => {
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

  private onRequestExternalModelsAvailableToInclude: BpmnEditor.OnRequestExternalModelsAvailableToInclude =
    async () => {
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

  private onRequestToResolvePathRelativeToTheOpenFile: BpmnEditor.OnRequestToResolvePath = (
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

  private onRequestExternalModelByPathsRelativeToTheOpenFile: BpmnEditor.OnRequestExternalModelByPath = async (
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
        model: normalizeDmn(getDmnMarshaller(resource?.content ?? "", { upgradeTo: "latest" }).parser.parse()),
        svg: "",
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
    prevProps: Readonly<BpmnEditorRootProps>,
    prevState: Readonly<BpmnEditorRootState>,
    snapshot?: any
  ): void {
    if (this.props.keyboardShortcutsService === undefined || this.state.keyboardShortcutsRegistered === true) {
      return;
    }

    const commands = this.bpmnEditorRef.current?.getCommands();
    if (commands === undefined) {
      return;
    }

    const cancelAction = this.props.keyboardShortcutsService.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.escape,
      this.props.i18n.terms.edit + " | " + this.props.i18n.unselect,
      async () => commands.cancelAction()
    );
    const deleteSelectionBackspace = this.props.keyboardShortcutsService.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.backspace,
      this.props.i18n.terms.edit + " | " + this.props.i18n.deleteSelection,
      async () => {}
    );
    const deleteSelectionDelete = this.props.keyboardShortcutsService.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.delete,
      this.props.i18n.terms.edit + " | " + this.props.i18n.deleteSelection,
      async () => {}
    );
    const selectAll = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.a,
      this.props.i18n.terms.edit + " | " + this.props.i18n.selectDeselectAll,
      async () => commands.selectAll()
    );
    const createGroup = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.g,
      this.props.i18n.terms.edit + " | " + this.props.i18n.createGroupWrappingSelection,
      async () => {
        return commands.createGroup();
      }
    );
    const appendTaskNode = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.t,
      this.props.i18n.terms.edit + " | " + this.props.i18n.appendTasknode,
      async () => {
        return commands.appendTaskNode();
      }
    );
    const appendGatewayNode = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.q,
      this.props.i18n.terms.edit + " | " + this.props.i18n.appendGatewayNode,
      async () => {
        return commands.appendGatewayNode();
      }
    );
    const appendIntermediateCatchEventNode = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.w,
      this.props.i18n.terms.edit + " | " + this.props.i18n.appendIntermediateCatchEventNode,
      async () => {
        return commands.appendIntermediateCatchEventNode();
      }
    );
    const appendIntermediateThrowEventNode = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.r,
      this.props.i18n.terms.edit + " | " + this.props.i18n.appendIntermediateThrowEventNode,
      async () => {
        return commands.appendIntermediateThrowEventNode();
      }
    );
    const appendEndEventNode = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.e,
      this.props.i18n.terms.edit + " | " + this.props.i18n.appendEndEventNode,
      async () => {
        return commands.appendEndEventNode();
      }
    );
    const appendTextAnnotationNode = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.y,
      this.props.i18n.terms.edit + " | " + this.props.i18n.appendTextAnnotationNode,
      async () => {
        return commands.appendTextAnnotationNode();
      }
    );

    const copy = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.ctrlC,
      this.props.i18n.terms.edit + " | " + this.props.i18n.copyNodes,
      async () => commands.copy()
    );
    const cut = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.ctrlX,
      this.props.i18n.terms.edit + " | " + this.props.i18n.cutNodes,
      async () => commands.cut()
    );
    const paste = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.ctrlV,
      this.props.i18n.terms.edit + " | " + this.props.i18n.pasteNodes,
      async () => commands.paste()
    );
    const togglePropertiesPanel = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.i,
      this.props.i18n.terms.keyboardKeys.misc + " | " + this.props.i18n.openClosePropertiesPanel,
      async () => commands.togglePropertiesPanel()
    );
    const moveUp = this.props.keyboardShortcutsService.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.up,
      this.props.i18n.terms.keyboardKeys.move + " | " + this.props.i18n.selectionUp,
      async () => {}
    );
    const moveDown = this.props.keyboardShortcutsService.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.down,
      this.props.i18n.terms.keyboardKeys.move + " | " + this.props.i18n.selectionDown,
      async () => {}
    );
    const moveLeft = this.props.keyboardShortcutsService.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.left,
      this.props.i18n.terms.keyboardKeys.move + " | " + this.props.i18n.selectionLeft,
      async () => {}
    );
    const moveRight = this.props.keyboardShortcutsService.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.right,
      this.props.i18n.terms.keyboardKeys.move + " | " + this.props.i18n.selectionRight,
      async () => {}
    );
    const bigMoveUp = this.props.keyboardShortcutsService.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.shiftUp,
      this.props.i18n.terms.keyboardKeys.move + " | " + this.props.i18n.selectionUpBigDistance,
      async () => {}
    );
    const bigMoveDown = this.props.keyboardShortcutsService.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.shiftDown,
      this.props.i18n.terms.keyboardKeys.move + " | " + this.props.i18n.selectionDownBigDistance,
      async () => {}
    );
    const bigMoveLeft = this.props.keyboardShortcutsService.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.shiftLeft,
      this.props.i18n.terms.keyboardKeys.move + " | " + this.props.i18n.selectionLeftBigDistance,
      async () => {}
    );
    const bigMoveRight = this.props.keyboardShortcutsService.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.shiftRight,
      this.props.i18n.terms.keyboardKeys.move + " | " + this.props.i18n.selectionRightBigDistance,
      async () => {}
    );
    const focusOnBounds = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.b,
      this.props.i18n.terms.keyboardKeys.navigate + " | " + this.props.i18n.focusOnSelection,
      async () => commands.focusOnSelection()
    );
    const resetPosition = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.space,
      this.props.i18n.terms.keyboardKeys.navigate + " | " + this.props.i18n.resetPositionToOrigin,
      async () => commands.resetPosition()
    );
    const pan = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.rightMouseButton,
      this.props.i18n.terms.keyboardKeys.navigate + " | " + this.props.i18n.holdAndDragtoPan,
      async () => {}
    );
    const zoom = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.ctrl,
      this.props.i18n.terms.keyboardKeys.navigate + " | " + this.props.i18n.holdAndScrollToZoomInOut,
      async () => {}
    );
    const navigateHorizontally = this.props.keyboardShortcutsService?.registerKeyPress(
      this.props.i18n.terms.keyboardKeys.shift,
      this.props.i18n.terms.keyboardKeys.navigate + " | " + this.props.i18n.holdAndScrollToNavigateHorizontally,
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
        appendTaskNode,
        appendGatewayNode,
        appendIntermediateCatchEventNode,
        appendIntermediateThrowEventNode,
        appendEndEventNode,
        appendTextAnnotationNode,
        cut,
        deleteSelectionBackspace,
        deleteSelectionDelete,
        focusOnBounds,
        moveDown,
        moveLeft,
        moveRight,
        moveUp,
        navigateHorizontally,
        pan,
        paste,
        resetPosition,
        selectAll,
        togglePropertiesPanel,
        zoom,
      ],
    }));
  }

  public componentWillUnmount() {
    const keyboardShortcuts = this.bpmnEditorRef.current?.getCommands();
    if (keyboardShortcuts === undefined) {
      return;
    }

    this.state.keyboardShortcutsRegisterIds.forEach((id) => {
      this.props.keyboardShortcutsService?.deregister(id);
    });
  }

  public render() {
    const CustomTasksManager = this.props.customTasksManager;
    return (
      <>
        {this.state.error && <DmnMarshallerFallbackError error={this.state.error} i18n={this.props.i18n} />}
        {this.model && (
          <>
            <BpmnEditor.BpmnEditor
              ref={this.bpmnEditorRef}
              originalVersion={this.state.marshaller?.originalVersion}
              model={this.model}
              externalModelsByNamespace={this.state.externalModelsByNamespace}
              customTasks={this.state.customTasks}
              externalContextName={""}
              externalContextDescription={""}
              issueTrackerHref={""}
              // isReadOnly={this.state.isReadOnly}
              onModelChange={this.onModelChange}
              // (begin) All paths coming from inside the DmnEditor component are paths relative to the open file.
              onRequestExternalModelsAvailableToInclude={this.onRequestExternalModelsAvailableToInclude}
              onRequestExternalModelByPath={this.onRequestExternalModelByPathsRelativeToTheOpenFile}
              onRequestToJumpToPath={this.onOpenFileFromPathRelativeToTheOpenFile}
              onRequestToResolvePath={this.onRequestToResolvePathRelativeToTheOpenFile}
              locale={this.props.locale}
              // (end)
            />
            <ExternalModelsManager
              thisBpmnsNormalizedPosixPathRelativeToTheWorkspaceRoot={
                this.state.openFileNormalizedPosixPathRelativeToTheWorkspaceRoot
              }
              onChange={this.setExternalModelsByNamespace}
              doneBootstrapping={this.externalModelsManagerDoneBootstraping}
            />
            <CustomTasksManager
              thisBpmnsNormalizedPosixPathRelativeToTheWorkspaceRoot={
                this.state.openFileNormalizedPosixPathRelativeToTheWorkspaceRoot
              }
              onChange={this.setCustomTasks}
              doneBootstrapping={this.customTasksManagerDoneBootstraping}
            />
          </>
        )}
      </>
    );
  }
}

function DmnMarshallerFallbackError({ error, i18n }: { error: Error; i18n: BpmnEditorEnvelopeI18n }) {
  return (
    <Flex justifyContent={{ default: "justifyContentCenter" }} style={{ marginTop: "100px" }}>
      <EmptyState style={{ maxWidth: "1280px" }}>
        <EmptyStateHeader
          titleText={i18n.unableToOpenFile}
          icon={<EmptyStateIcon icon={() => <div style={{ fontSize: "3em" }}>😕</div>} />}
          headingLevel={"h4"}
        />
        <br />
        <EmptyStateBody>
          {i18n.errorMessage} {error.message}
        </EmptyStateBody>
      </EmptyState>
    </Flex>
  );
}
