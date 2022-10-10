/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {
  ChannelType,
  EditorEnvelopeLocator,
  EditorTheme,
  EnvelopeMapping,
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
import { basename, extname } from "path";
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
import { ServerlessWorkflowCombinedEditorChannelApi } from "../api";
import { useSwfDiagramEditorChannelApi } from "./hooks/useSwfDiagramEditorChannelApi";
import { useSwfTextEditorChannelApi } from "./hooks/useSwfTextEditorChannelApi";

interface Props {
  locale: string;
  isReadOnly: boolean;
  channelType: ChannelType;
  resourcesPathPrefix: string;
  onNewEdit: (edit: WorkspaceEdit) => void;
}

export type ServerlessWorkflowCombinedEditorRef = {
  setContent(path: string, content: string): Promise<void>;
};

interface File {
  path: string;
  content: string;
}

const ENVELOPE_LOCATOR_TYPE = "swf";

const RefForwardingServerlessWorkflowCombinedEditor: ForwardRefRenderFunction<
  ServerlessWorkflowCombinedEditorRef | undefined,
  Props
> = (props, forwardedRef) => {
  const [file, setFile] = useState<File | undefined>(undefined);
  const [embeddedTextEditorFile, setEmbeddedTextEditorFile] = useState<EmbeddedEditorFile>();
  const [embeddedDiagramEditorFile, setEmbeddedDiagramEditorFile] = useState<EmbeddedEditorFile>();
  const { editor: textEditor, editorRef: textEditorRef } = useEditorRef();
  const { editor: diagramEditor, editorRef: diagramEditorRef } = useEditorRef();
  const editorEnvelopeCtx = useKogitoEditorEnvelopeContext<ServerlessWorkflowCombinedEditorChannelApi>();
  const [featureToggle] = useSharedValue(editorEnvelopeCtx.channelApi?.shared.kogitoSwfFeatureToggle_get);
  const [previewOptions] = useSharedValue(editorEnvelopeCtx.channelApi?.shared.kogitoSwfPreviewOptions_get);
  const lastContent = useRef<string>();

  const [isTextEditorReady, setTextEditorReady] = useState(false);
  const [isDiagramEditorReady, setDiagramEditorReady] = useState(false);

  const isVscode = useMemo(
    () => props.channelType === ChannelType.VSCODE_DESKTOP || props.channelType === ChannelType.VSCODE_WEB,
    [props.channelType]
  );

  const targetOrigin = useMemo(() => (isVscode ? "vscode" : window.location.origin), [isVscode]);

  const isCombinedEditorReady = useMemo(
    () => isTextEditorReady && isDiagramEditorReady,
    [isDiagramEditorReady, isTextEditorReady]
  );

  const textEditorEnvelopeLocator = useMemo(
    () =>
      new EditorEnvelopeLocator(targetOrigin, [
        new EnvelopeMapping({
          type: ENVELOPE_LOCATOR_TYPE,
          filePathGlob: "**/*.sw.+(json|yml|yaml)",
          resourcesPathPrefix: props.resourcesPathPrefix + "/text",
          envelopePath: props.resourcesPathPrefix + "/serverless-workflow-text-editor-envelope.html",
        }),
      ]),
    [props.resourcesPathPrefix, targetOrigin]
  );

  const diagramEditorEnvelopeLocator = useMemo(() => {
    const diagramEnvelopeMappingConfig =
      featureToggle && !featureToggle.stunnerEnabled
        ? {
            resourcesPathPrefix: props.resourcesPathPrefix + "/mermaid",
            envelopePath: props.resourcesPathPrefix + "/serverless-workflow-mermaid-viewer-envelope.html",
          }
        : {
            resourcesPathPrefix: props.resourcesPathPrefix + "/diagram",
            envelopePath: props.resourcesPathPrefix + "/serverless-workflow-diagram-editor-envelope.html",
          };
    return new EditorEnvelopeLocator(targetOrigin, [
      new EnvelopeMapping({
        type: ENVELOPE_LOCATOR_TYPE,
        filePathGlob: "**/*.sw.json",
        resourcesPathPrefix: diagramEnvelopeMappingConfig.resourcesPathPrefix,
        envelopePath: diagramEnvelopeMappingConfig.envelopePath,
      }),
      new EnvelopeMapping({
        type: ENVELOPE_LOCATOR_TYPE,
        filePathGlob: "**/*.sw.+(yml|yaml)",
        resourcesPathPrefix: props.resourcesPathPrefix + "/mermaid",
        envelopePath: props.resourcesPathPrefix + "/serverless-workflow-mermaid-viewer-envelope.html",
      }),
    ]);
  }, [featureToggle, props.resourcesPathPrefix, targetOrigin]);

  useImperativeHandle(
    forwardedRef,
    () => {
      return {
        setContent: async (path: string, content: string) => {
          try {
            const match = /\.sw\.(json|yml|yaml)$/.exec(path.toLowerCase());
            const dotExtension = match ? match[0] : extname(path);
            const extension = dotExtension.slice(1);
            const fileName = basename(path);
            const getFileContentsFn = async () => content;

            setFile({ content, path });

            setEmbeddedTextEditorFile({
              path: path,
              getFileContents: getFileContentsFn,
              isReadOnly: props.isReadOnly,
              fileExtension: extension,
              fileName: fileName,
            });

            setEmbeddedDiagramEditorFile({
              path: path,
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
          textEditor?.undo();
          diagramEditor?.undo();
        },
        redo: async () => {
          textEditor?.redo();
          diagramEditor?.redo();
        },
        validate: async (): Promise<Notification[]> => textEditor?.validate() ?? [],
        setTheme: async (theme: EditorTheme) => {
          textEditor?.setTheme(theme);
          diagramEditor?.setTheme(theme);
        },
      };
    },
    [diagramEditor, file, props.isReadOnly, textEditor]
  );

  useStateControlSubscription(
    textEditor,
    useCallback(
      async (_isDirty) => {
        if (!textEditor) {
          return;
        }

        const content = await textEditor.getContent();
        props.onNewEdit(new WorkspaceEdit(content));
        setFile((prevState) => ({
          ...prevState!,
          content,
        }));
      },
      [props, textEditor]
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
        props.onNewEdit(new WorkspaceEdit(content));
        setFile((prevState) => ({
          ...prevState!,
          content,
        }));
      },
      [props, diagramEditor]
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

  const useSwfTextEditorChannelApiArgs = useMemo(
    () => ({
      channelApi:
        editorEnvelopeCtx.channelApi as unknown as MessageBusClientApi<ServerlessWorkflowTextEditorChannelApi>,
      locale: props.locale,
      embeddedEditorFile: embeddedTextEditorFile,
      onEditorReady: onTextEditorReady,
      swfDiagramEditorEnvelopeApi: diagramEditor?.getEnvelopeServer()
        .envelopeApi as unknown as MessageBusClientApi<ServerlessWorkflowDiagramEditorEnvelopeApi>,
    }),
    [editorEnvelopeCtx, onTextEditorReady, diagramEditor, embeddedTextEditorFile, props.locale]
  );

  const { stateControl: diagramEditorStateControl, channelApi: diagramEditorChannelApi } =
    useSwfDiagramEditorChannelApi(useSwfDiagramEditorChannelApiArgs);

  const { stateControl: textEditorStateControl, channelApi: textEditorChannelApi } =
    useSwfTextEditorChannelApi(useSwfTextEditorChannelApiArgs);

  return (
    <div style={{ height: "100%" }}>
      <LoadingScreen loading={!isCombinedEditorReady} />
      <Drawer isExpanded={true} isInline={true}>
        <DrawerContent
          panelContent={
            <DrawerPanelContent isResizable={true} defaultSize={previewOptions?.diagramDefaultWidth ?? "50%"}>
              <DrawerPanelBody style={{ padding: 0 }}>
                {embeddedDiagramEditorFile && (
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
                )}
              </DrawerPanelBody>
            </DrawerPanelContent>
          }
        >
          <DrawerContentBody>
            {embeddedTextEditorFile && (
              <EmbeddedEditor
                ref={textEditorRef}
                file={embeddedTextEditorFile}
                channelType={props.channelType}
                kogitoEditor_ready={onTextEditorReady}
                kogitoEditor_setContentError={onTextEditorSetContentError}
                editorEnvelopeLocator={textEditorEnvelopeLocator}
                locale={props.locale}
                customChannelApiImpl={textEditorChannelApi}
                stateControl={textEditorStateControl}
                isReady={isTextEditorReady}
              />
            )}
          </DrawerContentBody>
        </DrawerContent>
      </Drawer>
    </div>
  );
};

export const ServerlessWorkflowCombinedEditor = forwardRef(RefForwardingServerlessWorkflowCombinedEditor);
