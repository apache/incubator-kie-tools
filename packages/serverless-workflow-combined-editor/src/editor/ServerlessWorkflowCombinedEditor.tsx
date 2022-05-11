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
  StateControlCommand,
} from "@kie-tools-core/editor/dist/api";
import { EmbeddedEditorFile } from "@kie-tools-core/editor/dist/channel";
import { EmbeddedEditor, useEditorRef, useStateControlSubscription } from "@kie-tools-core/editor/dist/embedded";
import { LoadingScreen } from "@kie-tools-core/editor/dist/envelope";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import {
  KogitoEdit,
  ResourceContent,
  ResourceContentRequest,
  ResourceListRequest,
  ResourcesList,
} from "@kie-tools-core/workspace/dist/api";
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

interface Props {
  locale: string;
  isReadOnly: boolean;
  channelType: ChannelType;
  resourcesPathPrefix: string;
  onNewEdit: (edit: KogitoEdit) => void;
  onStateControlCommandUpdate: (command: StateControlCommand) => void;
  setNotifications: (path: string, notifications: Notification[]) => void;
  kogitoWorkspace_resourceContentRequest(request: ResourceContentRequest): Promise<ResourceContent | undefined>;
  kogitoWorkspace_resourceListRequest(request: ResourceListRequest): Promise<ResourcesList>;
}

export type ServerlessWorkflowCombinedEditorRef = {
  setContent(path: string, content: string): Promise<void>;
};

interface File {
  path: string;
  content: string;
}

const ENVELOPE_LOCATOR_TYPE = "sw";
const ENVELOPE_LOCATOR_FILE_PATH_GLOB = "**/*.sw.+(json|yml|yaml)";

const RefForwardingServerlessWorkflowCombinedEditor: ForwardRefRenderFunction<
  ServerlessWorkflowCombinedEditorRef | undefined,
  Props
> = (props, forwardedRef) => {
  const [file, setFile] = useState<File | undefined>(undefined);
  const [embeddedTextEditorFile, setEmbeddedTextEditorFile] = useState<EmbeddedEditorFile>();
  const [embeddedDiagramEditorFile, setEmbeddedDiagramEditorFile] = useState<EmbeddedEditorFile>();
  const { editor: textEditor, editorRef: textEditorRef } = useEditorRef();
  const { editor: diagramEditor, editorRef: diagramEditorRef } = useEditorRef();
  const lastContent = useRef<string>();

  const [isTextEditorReady, setTextEditorReady] = useState(false);
  const [isDiagramEditorReady, setDiagramEditorReady] = useState(false);

  const isCombinedEditorReady = useMemo(
    () => isTextEditorReady && isDiagramEditorReady,
    [isDiagramEditorReady, isTextEditorReady]
  );

  const textEditorEnvelopeLocator = useMemo(
    () =>
      new EditorEnvelopeLocator(window.location.origin, [
        new EnvelopeMapping(
          ENVELOPE_LOCATOR_TYPE,
          ENVELOPE_LOCATOR_FILE_PATH_GLOB,
          props.resourcesPathPrefix,
          "serverless-workflow-text-editor-envelope.html"
        ),
      ]),
    [props.resourcesPathPrefix]
  );

  const diagramEditorEnvelopeLocator = useMemo(
    () =>
      new EditorEnvelopeLocator(window.location.origin, [
        new EnvelopeMapping(
          ENVELOPE_LOCATOR_TYPE,
          ENVELOPE_LOCATOR_FILE_PATH_GLOB,
          `${props.resourcesPathPrefix}gwt-editors/serverless-workflow`,
          "serverless-workflow-diagram-editor-envelope.html"
        ),
      ]),
    [props.resourcesPathPrefix]
  );

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
        getContent: async () => textEditor?.getContent(),
        getPreview: async () => diagramEditor?.getPreview(),
        undo: async () => {
          textEditor?.undo();
          diagramEditor?.undo();
        },
        redo: async () => {
          textEditor?.redo();
          diagramEditor?.redo();
        },
        validate: (): Notification[] => [],
        setTheme: async (theme: EditorTheme) => {
          textEditor?.setTheme(theme);
          diagramEditor?.setTheme(theme);
        },
      };
    },
    [diagramEditor, props.isReadOnly, textEditor]
  );

  useStateControlSubscription(
    textEditor,
    useCallback(
      (isDirty) => {
        if (!isDirty || !textEditor) {
          return;
        }

        textEditor.getContent().then((content) => {
          setFile((prevState) => ({
            ...prevState!,
            content,
          }));
        });
      },
      [textEditor]
    ),
    { throttle: 200 }
  );

  useStateControlSubscription(
    diagramEditor,
    useCallback(
      (isDirty) => {
        if (!isDirty || !diagramEditor) {
          return;
        }

        diagramEditor.getContent().then((content) => {
          setFile((prevState) => ({
            ...prevState!,
            content,
          }));
        });
      },
      [diagramEditor]
    ),
    { throttle: 200 }
  );

  const updateEditors = useCallback(
    async (file: File) => {
      if (!textEditor || !diagramEditor) {
        return;
      }

      // No need to update textEditor as long as diagramEditor is readonly
      // await textEditor.setContent(file.path, file.content);
      await diagramEditor.setContent(file.path, file.content);
    },
    [diagramEditor, textEditor]
  );

  useEffect(() => {
    if (file?.content === undefined || file.content === lastContent.current) {
      return;
    }

    lastContent.current = file.content;
    updateEditors(file);
  }, [file, updateEditors]);

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

  return (
    <div style={{ height: "100%" }}>
      <LoadingScreen loading={!isCombinedEditorReady} />
      <Drawer isExpanded={true} isInline={true}>
        <DrawerContent
          panelContent={
            <DrawerPanelContent isResizable={true} defaultSize={"50%"}>
              <DrawerPanelBody style={{ padding: 0 }}>
                {embeddedDiagramEditorFile && (
                  <EmbeddedEditor
                    ref={diagramEditorRef}
                    file={embeddedDiagramEditorFile}
                    channelType={props.channelType}
                    kogitoEditor_ready={onDiagramEditorReady}
                    kogitoWorkspace_resourceContentRequest={props.kogitoWorkspace_resourceContentRequest}
                    kogitoWorkspace_resourceListRequest={props.kogitoWorkspace_resourceListRequest}
                    kogitoEditor_setContentError={onDiagramEditorSetContentError}
                    editorEnvelopeLocator={diagramEditorEnvelopeLocator}
                    locale={props.locale}
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
                kogitoWorkspace_resourceContentRequest={props.kogitoWorkspace_resourceContentRequest}
                kogitoWorkspace_resourceListRequest={props.kogitoWorkspace_resourceListRequest}
                kogitoEditor_setContentError={onTextEditorSetContentError}
                editorEnvelopeLocator={textEditorEnvelopeLocator}
                locale={props.locale}
              />
            )}
          </DrawerContentBody>
        </DrawerContent>
      </Drawer>
    </div>
  );
};

export const ServerlessWorkflowCombinedEditor = forwardRef(RefForwardingServerlessWorkflowCombinedEditor);
