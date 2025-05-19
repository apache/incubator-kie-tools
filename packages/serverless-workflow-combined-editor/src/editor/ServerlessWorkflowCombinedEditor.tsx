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

import {
  ChannelType,
  EditorEnvelopeLocator,
  EditorTheme,
  EnvelopeContent,
  EnvelopeContentType,
  EnvelopeMapping,
  StateControlCommand,
  useKogitoEditorEnvelopeContext,
} from "@kie-tools-core/editor/dist/api";
import { EmbeddedEditorFile } from "@kie-tools-core/editor/dist/channel";
import { EmbeddedEditor, useEditorRef, useStateControlSubscription } from "@kie-tools-core/editor/dist/embedded";
import { LoadingScreen } from "@kie-tools-core/editor/dist/envelope";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { useSharedValue } from "@kie-tools-core/envelope-bus/dist/hooks";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { WorkspaceEdit } from "@kie-tools-core/workspace/dist/api";
import {
  ServerlessWorkflowDiagramEditorChannelApi,
  ServerlessWorkflowDiagramEditorEnvelopeApi,
} from "@kie-tools/serverless-workflow-diagram-editor-envelope/dist/api";
import { SwfStunnerEditorAPI } from "@kie-tools/serverless-workflow-diagram-editor-envelope/dist/api/SwfStunnerEditorAPI";
import { SwfStunnerEditor } from "@kie-tools/serverless-workflow-diagram-editor-envelope/dist/envelope/ServerlessWorkflowStunnerEditor";
import {
  ServerlessWorkflowTextEditorChannelApi,
  ServerlessWorkflowTextEditorEnvelopeApi,
} from "@kie-tools/serverless-workflow-text-editor/dist/api";
import {
  Drawer,
  DrawerContent,
  DrawerContentBody,
  DrawerPanelBody,
  DrawerPanelContent,
} from "@patternfly/react-core/dist/js/components/Drawer";
import { basename, extname } from "path-browserify";
import * as React from "react";
import {
  forwardRef,
  ForwardRefRenderFunction,
  useCallback,
  useEffect,
  useImperativeHandle,
  useMemo,
  useRef,
  useState,
} from "react";
import { Position } from "monaco-editor";
import {
  ServerlessWorkflowCombinedEditorChannelApi,
  ServerlessWorkflowCombinedEditorEnvelopeApi,
  SwfPreviewOptions,
} from "../api";
import { useSwfDiagramEditorChannelApi } from "./hooks/useSwfDiagramEditorChannelApi";
import { UseSwfTextEditorChannelApiArgs, useSwfTextEditorChannelApi } from "./hooks/useSwfTextEditorChannelApi";
import { colorNodes } from "./helpers/ColorNodes";
import "./styles.scss";

interface Props {
  locale: string;
  isReadOnly: boolean;
  channelType: ChannelType;
  resourcesPathPrefix: string;
  onNewEdit: (edit: WorkspaceEdit) => void;
  onStateControlCommandUpdate: (command: StateControlCommand) => void;
}

export type ServerlessWorkflowCombinedEditorRef = {
  setContent(normalizedPosixPathRelativeToTheWorkspaceRoot: string, content: string): Promise<void>;
  colorNodes(nodeNames: string[], color: string, colorConnectedEnds: boolean): void;
  moveCursorToPosition(position: Position): void;
};

interface File {
  path: string;
  content: string;
}

const ENVELOPE_LOCATOR_TYPE = "swf";

declare global {
  interface Window {
    editor: SwfStunnerEditorAPI;
  }
}

const RefForwardingServerlessWorkflowCombinedEditor: ForwardRefRenderFunction<
  ServerlessWorkflowCombinedEditorRef | undefined,
  Props
> = (props, forwardedRef) => {
  const { onStateControlCommandUpdate, onNewEdit } = props;
  const [file, setFile] = useState<File | undefined>(undefined);
  const [embeddedTextEditorFile, setEmbeddedTextEditorFile] = useState<EmbeddedEditorFile>();
  const [embeddedDiagramEditorFile, setEmbeddedDiagramEditorFile] = useState<EmbeddedEditorFile>();
  const editorEnvelopeCtx = useKogitoEditorEnvelopeContext<
    ServerlessWorkflowCombinedEditorEnvelopeApi,
    ServerlessWorkflowCombinedEditorChannelApi
  >();
  const [diagramEditorEnvelopeContent] = useSharedValue<string>(
    editorEnvelopeCtx.channelApi.shared.kogitoSwfGetDiagramEditorEnvelopeContent
  );
  const [textEditorEnvelopeContent] = useSharedValue<string>(
    editorEnvelopeCtx.channelApi.shared.kogitoSwfGetTextEditorEnvelopeContent
  );

  const { editor: textEditor, editorRef: textEditorRef } = useEditorRef();
  const { editor: diagramEditor, editorRef: diagramEditorRef } = useEditorRef();

  const [theme] = useSharedValue(editorEnvelopeCtx.channelApi?.shared.kogitoEditor_theme);

  const [previewOptions] = useSharedValue<SwfPreviewOptions>(
    editorEnvelopeCtx.channelApi?.shared.kogitoSwfPreviewOptions_get
  );
  const lastContent = useRef<string>();

  const [isTextEditorReady, setTextEditorReady] = useState(false);
  const [isDiagramEditorReady, setDiagramEditorReady] = useState(false);

  const isVscode = useMemo(
    () => props.channelType === ChannelType.VSCODE_DESKTOP || props.channelType === ChannelType.VSCODE_WEB,
    [props.channelType]
  );
  const isStandalone = useMemo(() => props.channelType === ChannelType.STANDALONE, [props.channelType]);

  const targetOrigin = useMemo(() => (isVscode ? "vscode" : window.location.origin), [isVscode]);

  const applyEditorTheme = useCallback(
    (theme: EditorTheme) => Promise.all([textEditor?.setTheme(theme), diagramEditor?.setTheme(theme)]),
    [textEditor, diagramEditor]
  );

  useEffect(() => {
    if (theme === undefined) {
      return;
    }
    applyEditorTheme(theme);
  }, [theme, applyEditorTheme]);

  const isCombinedEditorReady = useMemo(() => {
    if (previewOptions?.editorMode === "diagram") {
      return isDiagramEditorReady;
    } else if (previewOptions?.editorMode === "text") {
      return isTextEditorReady;
    } else {
      return isTextEditorReady && isDiagramEditorReady;
    }
  }, [isDiagramEditorReady, isTextEditorReady, previewOptions]);

  const buildEnvelopeContent = useCallback(
    (content: string, path: string): EnvelopeContent => {
      if (isStandalone) {
        return {
          type: EnvelopeContentType.CONTENT,
          content: content,
        };
      } else {
        return {
          type: EnvelopeContentType.PATH,
          path,
        };
      }
    },
    [isStandalone]
  );

  const textEditorEnvelopeLocator = useMemo(
    () =>
      new EditorEnvelopeLocator(targetOrigin, [
        new EnvelopeMapping({
          type: ENVELOPE_LOCATOR_TYPE,
          filePathGlob: "**/*.sw.+(json|yml|yaml)",
          resourcesPathPrefix: props.resourcesPathPrefix + "/text",
          envelopeContent: buildEnvelopeContent(
            textEditorEnvelopeContent ?? "",
            props.resourcesPathPrefix + "/serverless-workflow-text-editor-envelope.html"
          ),
        }),
      ]),
    [props.resourcesPathPrefix, targetOrigin, textEditorEnvelopeContent, buildEnvelopeContent]
  );

  const diagramEditorEnvelopeLocator = useMemo(
    () =>
      new EditorEnvelopeLocator(targetOrigin, [
        new EnvelopeMapping({
          type: ENVELOPE_LOCATOR_TYPE,
          filePathGlob: "**/*.sw.+(json|yml|yaml)",
          resourcesPathPrefix: props.resourcesPathPrefix + "/diagram",
          envelopeContent: buildEnvelopeContent(
            diagramEditorEnvelopeContent ?? "",
            props.resourcesPathPrefix + "/serverless-workflow-diagram-editor-envelope.html"
          ),
        }),
      ]),
    [props.resourcesPathPrefix, targetOrigin, diagramEditorEnvelopeContent, buildEnvelopeContent]
  );

  const textEditorEnvelopeApi = useMemo(
    () =>
      textEditor &&
      (textEditor.getEnvelopeServer()
        .envelopeApi as unknown as MessageBusClientApi<ServerlessWorkflowTextEditorEnvelopeApi>),
    [textEditor]
  );

  useImperativeHandle(forwardedRef, () => {
    return {
      setContent: async (normalizedPosixPathRelativeToTheWorkspaceRoot: string, content: string) => {
        try {
          const match = /\.sw\.(json|yml|yaml)$/.exec(normalizedPosixPathRelativeToTheWorkspaceRoot.toLowerCase());
          const dotExtension = match ? match[0] : extname(normalizedPosixPathRelativeToTheWorkspaceRoot);
          const extension = dotExtension.slice(1);
          const fileName = basename(normalizedPosixPathRelativeToTheWorkspaceRoot);
          const getFileContentsFn = async () => content;

          setFile({ content, path: normalizedPosixPathRelativeToTheWorkspaceRoot });
          setEmbeddedTextEditorFile({
            normalizedPosixPathRelativeToTheWorkspaceRoot,
            getFileContents: getFileContentsFn,
            isReadOnly: props.isReadOnly,
            fileExtension: extension,
            fileName: fileName,
          });

          setEmbeddedDiagramEditorFile({
            normalizedPosixPathRelativeToTheWorkspaceRoot,
            getFileContents: getFileContentsFn,
            isReadOnly: true,
            fileExtension: extension,
            fileName: fileName,
          });
        } catch (e) {
          console.error(e);
          throw e;
        }
      },
      getContent: async () => file?.content ?? "",
      getPreview: async () => diagramEditor?.getPreview() ?? "",
      undo: async () => {
        await Promise.all([textEditor?.undo(), diagramEditor?.undo()]);
      },
      redo: async () => {
        await Promise.all([textEditor?.redo(), diagramEditor?.redo()]);
      },
      validate: async (): Promise<Notification[]> => textEditor?.validate() ?? [],
      setTheme: async (theme: EditorTheme) => applyEditorTheme(theme),
      colorNodes: (nodeNames: string[], color: string, colorConnectedEnds: boolean) => {
        colorNodes(nodeNames, color, colorConnectedEnds);
      },
      moveCursorToPosition: (position: Position) => {
        textEditorEnvelopeApi?.notifications.kogitoSwfTextEditor__moveCursorToPosition.send(position);
      },
    };
  }, [diagramEditor, file, props.isReadOnly, textEditor, textEditorEnvelopeApi, applyEditorTheme]);

  useStateControlSubscription(
    textEditor,
    useCallback(
      async (_isDirty) => {
        if (!textEditor) {
          return;
        }

        const content = await textEditor.getContent();
        setFile((prevState) => ({
          ...prevState!,
          content,
        }));
      },
      [textEditor]
    )
  );

  useStateControlSubscription(
    diagramEditor,
    useCallback(
      async (_isDirty) => {
        if (!diagramEditor) {
          return;
        }

        const content = await diagramEditor.getContent();
        setFile((prevState) => ({
          ...prevState!,
          content,
        }));
      },
      [diagramEditor]
    )
  );

  const updateEditors = useCallback(
    async (f: File) => {
      if (!textEditor || !diagramEditor) {
        return;
      }

      // No need to update textEditor as long as diagramEditor is readonly
      // await textEditor.setContent(f.path, f.content);
      await diagramEditor.setContent(f.path, f.content);
    },
    [diagramEditor, textEditor]
  );

  useEffect(() => {
    if (file?.content === undefined || file.content === lastContent.current) {
      return;
    }

    lastContent.current = file.content;
    updateEditors(file);
  }, [file, props, updateEditors]);

  const onTextEditorReady = useCallback(() => {
    setTextEditorReady(true);
  }, []);

  const onTextEditorSetContentError = useCallback(() => {
    console.error("Error setting content on text editor");
  }, []);

  const onDiagramEditorReady = useCallback(() => {
    setDiagramEditorReady(true);
  }, []);

  const onDiagramEditorSetContentError = useCallback(() => {
    console.error("Error setting content on diagram editor");
  }, []);

  const useSwfDiagramEditorChannelApiArgs = useMemo(
    () => ({
      channelApi:
        editorEnvelopeCtx.channelApi as unknown as MessageBusClientApi<ServerlessWorkflowDiagramEditorChannelApi>,
      locale: props.locale,
      embeddedEditorFile: embeddedDiagramEditorFile,
      onEditorReady: onDiagramEditorReady,
      swfTextEditorEnvelopeApi: textEditor?.getEnvelopeServer()
        .envelopeApi as unknown as MessageBusClientApi<ServerlessWorkflowTextEditorEnvelopeApi>,
    }),
    [editorEnvelopeCtx, embeddedDiagramEditorFile, onDiagramEditorReady, textEditor, props.locale]
  );

  const onTextEditorNewEdit = useCallback(
    (edit: WorkspaceEdit) => {
      onNewEdit(edit);
    },
    [onNewEdit]
  );

  const onTextEditorStateControlCommandUpdate = useCallback(
    (command: StateControlCommand) => {
      switch (command) {
        case StateControlCommand.UNDO:
          onStateControlCommandUpdate(StateControlCommand.UNDO);
          break;
        case StateControlCommand.REDO:
          onStateControlCommandUpdate(StateControlCommand.REDO);
          break;
        default:
          console.info(`Unknown message type received: ${command}`);
          break;
      }
    },
    [onStateControlCommandUpdate]
  );

  const useSwfTextEditorChannelApiArgs = useMemo<UseSwfTextEditorChannelApiArgs>(
    () => ({
      apiOverrides: {
        kogitoWorkspace_newEdit: onTextEditorNewEdit,
        kogitoEditor_stateControlCommandUpdate: onTextEditorStateControlCommandUpdate,
        kogitoEditor_ready: onTextEditorReady,
        kogitoEditor_setContentError: onTextEditorSetContentError,
      },
      channelApi:
        editorEnvelopeCtx.channelApi as unknown as MessageBusClientApi<ServerlessWorkflowTextEditorChannelApi>,
      locale: props.locale,
      embeddedEditorFile: embeddedTextEditorFile,
      swfDiagramEditorEnvelopeApi: diagramEditor?.getEnvelopeServer()
        .envelopeApi as unknown as MessageBusClientApi<ServerlessWorkflowDiagramEditorEnvelopeApi>,
    }),
    [
      onTextEditorNewEdit,
      onTextEditorStateControlCommandUpdate,
      onTextEditorReady,
      onTextEditorSetContentError,
      editorEnvelopeCtx.channelApi,
      props.locale,
      embeddedTextEditorFile,
      diagramEditor,
    ]
  );

  const { stateControl: diagramEditorStateControl, channelApi: diagramEditorChannelApi } =
    useSwfDiagramEditorChannelApi(useSwfDiagramEditorChannelApiArgs);

  const { stateControl: textEditorStateControl, channelApi: textEditorChannelApi } =
    useSwfTextEditorChannelApi(useSwfTextEditorChannelApiArgs);

  const renderTextEditor = () => {
    return (
      embeddedTextEditorFile && (
        <EmbeddedEditor
          ref={textEditorRef}
          file={embeddedTextEditorFile}
          channelType={props.channelType}
          editorEnvelopeLocator={textEditorEnvelopeLocator}
          locale={props.locale}
          customChannelApiImpl={textEditorChannelApi}
          stateControl={textEditorStateControl}
          isReady={isTextEditorReady}
        />
      )
    );
  };

  const renderDiagramEditor = () => {
    return (
      embeddedDiagramEditorFile && (
        <EmbeddedEditor
          ref={diagramEditorRef}
          file={embeddedDiagramEditorFile}
          channelType={props.channelType}
          kogitoEditor_ready={onDiagramEditorReady}
          kogitoEditor_setContentError={onDiagramEditorSetContentError}
          editorEnvelopeLocator={diagramEditorEnvelopeLocator}
          locale={props.locale}
          customChannelApiImpl={diagramEditorChannelApi}
          stateControl={diagramEditorStateControl}
        />
      )
    );
  };

  window.editor = useMemo(
    () =>
      new SwfStunnerEditor(
        diagramEditor?.getEnvelopeServer()
          .envelopeApi as unknown as MessageBusClientApi<ServerlessWorkflowDiagramEditorEnvelopeApi>
      ),
    [diagramEditor]
  );

  useEffect(() => {
    if (isCombinedEditorReady) {
      editorEnvelopeCtx.channelApi.notifications.kogitoSwfCombinedEditor_combinedEditorReady.send();
    }
  }, [editorEnvelopeCtx, isCombinedEditorReady]);

  const themeStyle = getThemeStyle(theme!);

  return (
    <div style={{ height: "100%", background: themeStyle.backgroundColor }}>
      <LoadingScreen loading={!isCombinedEditorReady} styleTag={themeStyle.loadScreen} />
      {previewOptions?.editorMode === "diagram" ? (
        renderDiagramEditor()
      ) : previewOptions?.editorMode === "text" ? (
        renderTextEditor()
      ) : (
        <Drawer isExpanded={true} isInline={true} className={themeStyle.drawer}>
          <DrawerContent
            panelContent={
              <DrawerPanelContent isResizable={true} defaultSize={previewOptions?.defaultWidth ?? "50%"}>
                <DrawerPanelBody style={{ padding: 0 }}>{renderDiagramEditor()}</DrawerPanelBody>
              </DrawerPanelContent>
            }
          >
            <DrawerContentBody>{renderTextEditor()}</DrawerContentBody>
          </DrawerContent>
        </Drawer>
      )}
    </div>
  );
};

interface ThemeStyleTag {
  drawer: string;
  loadScreen: string;
  backgroundColor: string;
}

function getThemeStyle(theme: EditorTheme): ThemeStyleTag {
  switch (theme) {
    case EditorTheme.DARK: {
      return {
        drawer: "dark",
        loadScreen: "vscode-dark",
        backgroundColor: "black",
      };
    }
    default: {
      return {
        drawer: "",
        loadScreen: "",
        backgroundColor: "",
      };
    }
  }
}

export const ServerlessWorkflowCombinedEditor = forwardRef(RefForwardingServerlessWorkflowCombinedEditor);
