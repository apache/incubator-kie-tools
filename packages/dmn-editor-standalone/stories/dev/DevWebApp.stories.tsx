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
import { useRef, useState, useEffect, useCallback } from "react";
import "@patternfly/react-core/dist/styles/base.css";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import * as DmnEditor from "../../dist/index";
import { StandaloneDmnEditorApi } from "../../dist/StandaloneDmnEditorApi";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex/Flex";
import { FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex/FlexItem";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { emptyDmn, emptyDrd, loanPreQualificationDmn } from "./externalModels";

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

function DevWebApp() {
  const [isDirty, setDirty] = useState(false);
  const editorRef = useRef<StandaloneDmnEditorApi>(null);
  const editorContainerRef = useRef<HTMLDivElement>(null);
  const downloadRef = useRef<HTMLAnchorElement>(null);
  const [isDroppingFile, setIsDroppingFile] = useState(false);

  useEffect(() => {
    const editor = DmnEditor.open({
      container: editorContainerRef.current!,
      initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot: "path1/subpath/newModel1.dmn",
      initialContent: Promise.resolve(""),
      readOnly: false,
      resources: new Map(),
      origin: "*",
    });

    editor.subscribeToContentChanges(setDirty);

    (editorRef as any).current = editor;

    return () => {
      editor.close();
    };
  }, []);

  const onDownloadDmn = useCallback(() => {
    if (editorRef.current) {
      editorRef.current.getContent().then((content) => {
        if (downloadRef.current) {
          downloadRef.current.href = "data:text/plain;charset=utf-8," + encodeURIComponent(content);
          downloadRef.current.download = "model.dmn";
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
    console.log("DMN Editor :: Dev webapp :: File(s) dropped! Opening it.");

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
                <h3>DMN Standalone Editor :: Dev webapp </h3>
              </FlexItem>
              <FlexItem>
                <h5>(Drag & drop a file here to open it)</h5>
              </FlexItem>
              <FlexItem shrink={{ default: "shrink" }}>
                <button onClick={() => editorRef.current?.setContent("empty.dmn", emptyDmn)}>Reset</button>
                &nbsp; &nbsp; | &nbsp; &nbsp;
                {/* <button onClick={() => editorRef.current?.setContent("loan_pre_qualification.dmn", loanPreQualificationDmn)}>Loan Pre Qualification</button>
                &nbsp; &nbsp;
                <button onClick={() => editorRef.current?.setContent("empty.drd", emptyDrd)}>Empty DRD</button>
                &nbsp; &nbsp; | &nbsp; &nbsp; */}
                <button onClick={() => editorRef.current?.undo()}>Undo</button>
                &nbsp; &nbsp;
                <button onClick={() => editorRef.current?.redo()}>Redo</button>
                &nbsp; &nbsp; | &nbsp; &nbsp;
                <button onClick={onDownloadDmn}>Download</button>
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
            <div ref={editorContainerRef} id="dmn-editor-container" style={{ height: "100%" }} />
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
  args: {},
};
