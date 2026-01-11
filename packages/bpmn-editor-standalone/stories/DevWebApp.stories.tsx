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
import type { Meta, StoryObj } from "@storybook/react";
import { useArgs } from "@storybook/preview-api";
import { useRef, useState, useEffect, useCallback, useMemo } from "react";
import "@patternfly/react-core/dist/styles/base.css";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import * as BpmnEditor from "../dist/index";
import { BpmnEditorStandaloneApi, BpmnEditorStandaloneResource } from "../dist/index";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex/Flex";
import { FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex/FlexItem";
import { Text, TextVariants } from "@patternfly/react-core/dist/js/components/Text";

const droppingFileStyle = {
  position: "absolute",
  top: 0,
  left: 0,
  display: "flex",
  alignItems: "center",
  flex: "1 0 100%",
  justifyContent: "center",
  margin: "8px",
  width: "calc(100% - 16px)",
  height: "calc(100% - 16px)",
  backdropFilter: "blur(2px)",
  backgroundColor: "rgba(255, 255, 255, 0.9)",
  border: "5px  dashed lightgray",
  borderRadius: "16px",
  pointerEvents: "none",
  zIndex: 999,
} as React.CSSProperties;

export type DevWebAppProps = {
  initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot: string;
  initialContent: string;
  readOnly: boolean;
  resources: Array<[string, BpmnEditorStandaloneResource]>;
  origin: string;
};

function DevWebApp(props?: Partial<DevWebAppProps>) {
  const [editCount, setEditCount] = useState(0);
  const [args, updateArgs] = useArgs<DevWebAppProps>();
  const editorRef = useRef<BpmnEditorStandaloneApi>(null);
  const editorContainerRef = useRef<HTMLDivElement>(null);
  const downloadRef = useRef<HTMLAnchorElement>(null);
  const [isDroppingFile, setIsDroppingFile] = useState(false);

  const resources = useMemo(() => {
    const inputResources = props?.resources ?? args.resources;

    return inputResources
      ? new Map(
          inputResources.map(([key, value]) => [
            key,
            { contentType: value.contentType, content: Promise.resolve(value.content) },
          ])
        )
      : undefined;
  }, [args.resources, props?.resources]);

  useEffect(() => {
    const editor = BpmnEditor.open({
      container: editorContainerRef.current!,
      initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot:
        props?.initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot ??
        args.initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot,
      initialContent: Promise.resolve(props?.initialContent ?? args.initialContent),
      readOnly: props?.readOnly ?? args.readOnly,
      resources: resources,
      origin: props?.origin ?? args.origin ?? "*",
    });

    editor.subscribeToContentChanges(() => setEditCount((currentCount) => currentCount + 1));

    (editorRef as any).current = editor;

    console.info(
      "Access the 'editor' variable by right clicking the following object and selecting 'Store as temp object'. With this you will be able to interact with the editor API."
    );
    console.info(
      "Remember to select the 'storybook-preview-iframe' in the context selector above, to left of the filter input."
    );
    console.log(editor);

    return () => {
      editor.close();
      setEditCount(0);
    };
  }, [args, props, resources]);

  const onUndo = useCallback(() => {
    setEditCount((currentCount) => {
      if (currentCount > 0) {
        editorRef.current?.undo();
        // -2 because undoing will generate a contentChange notification.
        return currentCount - 2;
      }
      return currentCount;
    });
  }, []);

  const onRedo = useCallback(() => {
    editorRef.current?.redo();
  }, []);

  const onReset = useCallback(() => {
    editorRef.current?.setContent("empty.bpmn", "");
    setEditCount(0);
  }, []);

  const onDownloadBpmn = useCallback(() => {
    if (editorRef.current) {
      editorRef.current.getContent().then((content) => {
        if (downloadRef.current) {
          downloadRef.current.href = "data:text/plain;charset=utf-8," + encodeURIComponent(content);
          downloadRef.current.download = "model.bpmn";
          downloadRef.current.click();
        }
        editorRef.current?.markAsSaved();
      });
    }
  }, []);

  const onDownloadSvg = useCallback(() => {
    if (editorRef.current) {
      editorRef.current.getPreview().then((svgContent) => {
        if (downloadRef.current && svgContent) {
          downloadRef.current.href = "data:text/plain;charset=utf-8," + encodeURIComponent(svgContent);
          downloadRef.current.download = "model.svg";
          downloadRef.current.click();
        }
        editorRef.current?.markAsSaved();
      });
    }
  }, []);

  const onDrop = useCallback((e: React.DragEvent) => {
    console.log("BPMN Editor :: Dev webapp :: File(s) dropped! Opening it.");

    e.preventDefault(); // Necessary to disable the browser's default 'onDrop' handling.

    if (e.dataTransfer.files) {
      // Use DataTransferItemList interface to access the file(s)
      [...e.dataTransfer.files].forEach((file) => {
        const reader = new FileReader();
        reader.addEventListener("load", ({ target }) => {
          if (target) {
            editorRef.current?.setContent(file.name, target.result as string);
          }
        });
        reader.readAsText(file);
      });
    }

    setIsDroppingFile(false);
  }, []);

  const onDragOver = useCallback((e: React.DragEvent) => {
    e.preventDefault(); // Necessary to disable the browser's default 'onDrop' handling.
  }, []);

  const onDragEnter = useCallback((e: React.DragEvent) => {
    setIsDroppingFile(true);
  }, []);

  const onDragLeave = useCallback((e: React.DragEvent) => {
    setIsDroppingFile(false);
  }, []);

  return (
    <>
      <div style={{ width: "100vw", height: "100vh" }}>
        <Page>
          <PageSection
            variant={"light"}
            isFilled={false}
            padding={{ default: "padding" }}
            style={{ position: "relative" }}
            onDragOver={onDragOver}
            onDrop={onDrop}
            onDragEnter={onDragEnter}
            onDragLeave={onDragLeave}
          >
            <Flex
              justifyContent={{ default: "justifyContentSpaceBetween" }}
              style={isDroppingFile ? { pointerEvents: "none" } : {}}
            >
              {isDroppingFile && (
                <FlexItem style={droppingFileStyle}>
                  <Text component={TextVariants.h3}></Text>Upload file
                </FlexItem>
              )}
              <FlexItem shrink={{ default: "shrink" }}>
                <h3>BPMN Standalone Editor :: Dev webapp </h3>
              </FlexItem>
              <FlexItem>
                <h5>(Drag & drop a file here to open it)</h5>
              </FlexItem>
              <FlexItem shrink={{ default: "shrink" }}>
                <button onClick={onReset}>Reset</button>
                &nbsp; &nbsp; | &nbsp; &nbsp; Edit count: {editCount}
                &nbsp; &nbsp;
                <button onClick={onUndo}>Undo</button>
                &nbsp; &nbsp;
                <button onClick={onRedo}>Redo</button>
                &nbsp; &nbsp; | &nbsp; &nbsp;
                <button onClick={onDownloadBpmn}>Download</button>
                &nbsp; &nbsp;
                <button onClick={onDownloadSvg}>Download SVG</button>
              </FlexItem>
            </Flex>
            <a ref={downloadRef} />
          </PageSection>
          <hr />
          <PageSection
            variant={"light"}
            isFilled={true}
            hasOverflowScroll={true}
            aria-label={"editor"}
            padding={{ default: "noPadding" }}
          >
            <div ref={editorContainerRef} id="bpmn-editor-container" style={{ height: "100%" }} />
          </PageSection>
        </Page>
      </div>
    </>
  );
}

// More on how to set up stories at: https://storybook.js.org/docs/writing-stories#default-export
const meta: Meta<typeof DevWebApp> = {
  title: "Dev/Web App",
  component: DevWebApp,
};

export default meta;
type Story = StoryObj<typeof DevWebApp>;

// More on writing stories with args: https://storybook.js.org/docs/writing-stories/args
export const WebApp: Story = {
  render: (args) => DevWebApp(),
  args: {
    initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot: "path1/subpath/newModel1.bpmn",
    initialContent: "",
    readOnly: false,
    resources: [],
    origin: "*",
  },
};
