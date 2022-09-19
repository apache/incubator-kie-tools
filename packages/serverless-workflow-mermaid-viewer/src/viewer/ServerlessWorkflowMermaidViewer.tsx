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
import { ChannelType, EditorTheme } from "@kie-tools-core/editor/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { MermaidDiagram, Specification } from "@severlessworkflow/sdk-typescript";
import mermaid from "mermaid";
import * as React from "react";
import { useCallback, useEffect, useImperativeHandle, useRef, useState } from "react";
import svgPanZoom from "svg-pan-zoom";
import "./ServerlessWorkflowMermaidViewer.css";

interface Props {
  channelType: ChannelType;
}

export type ServerlessWorkflowMermaidViewerRef = {
  setContent(path: string, content: string): Promise<void>;
};

type ServerlessWorkflowContent = {
  originalContent: string;
  path: string;
};

const RefForwardingServerlessWorkflowMermaidViewer: React.ForwardRefRenderFunction<
  ServerlessWorkflowMermaidViewerRef | undefined,
  Props
> = (props, forwardedRef) => {
  const [initialContent, setInitialContent] = useState<ServerlessWorkflowContent | undefined>(undefined);
  const [isDiagramOutOfSync, setDiagramOutOfSync] = useState<boolean>(false);
  const diagramContainerRef = useRef<HTMLDivElement>(null);
  const diagramHiddenContainerRef = useRef<HTMLDivElement>(null);

  useImperativeHandle(
    forwardedRef,
    () => {
      return {
        setContent: (path: string, newContent: string): Promise<void> => {
          try {
            setInitialContent({
              originalContent: newContent,
              path: path,
            });
            return Promise.resolve();
          } catch (e) {
            console.error(e);
            return Promise.reject();
          }
        },
        getContent: async (): Promise<string> => initialContent?.originalContent ?? "",
        getPreview: async (): Promise<string> => {
          diagramHiddenContainerRef.current!.innerHTML = diagramContainerRef.current!.innerHTML;
          diagramHiddenContainerRef.current!.getElementsByTagName("svg")[0].removeAttribute("style");

          // Remove zoom controls from SVG
          diagramHiddenContainerRef.current!.getElementsByTagName("svg")[0].lastChild?.remove();

          // Line breaks replaced due to https://github.com/mermaid-js/mermaid/issues/1766
          return diagramHiddenContainerRef.current!.innerHTML.replaceAll("<br>", "<br/>");
        },
        undo: async (): Promise<void> => {
          return;
        },
        redo: async (): Promise<void> => {
          return;
        },
        validate: async (): Promise<Notification[]> => [],
        setTheme: async (theme: EditorTheme): Promise<void> => {
          return;
        },
      };
    },
    [initialContent]
  );

  const updateDiagram = useCallback((content: string) => {
    if (!diagramContainerRef.current) {
      return;
    }

    try {
      const workflow: Specification.Workflow = Specification.Workflow.fromSource(content);
      const mermaidSourceCode = workflow.states ? new MermaidDiagram(workflow).sourceCode() : "";

      if (mermaidSourceCode?.length > 0) {
        diagramContainerRef.current.innerHTML = mermaidSourceCode;
        diagramContainerRef.current.removeAttribute("data-processed");
        mermaid.init(diagramContainerRef.current);
        svgPanZoom(diagramContainerRef.current.getElementsByTagName("svg")[0], {
          controlIconsEnabled: true,
        });
        diagramContainerRef.current.getElementsByTagName("svg")[0].style.maxWidth = "";
        diagramContainerRef.current.getElementsByTagName("svg")[0].style.height = "100%";
        setDiagramOutOfSync(false);
      } else {
        diagramContainerRef.current.innerHTML = "Create a workflow to see its preview here.";
        setDiagramOutOfSync(true);
      }
    } catch (e) {
      console.error(e);
      setDiagramOutOfSync(true);
    }
  }, []);

  useEffect(() => {
    if (initialContent?.originalContent === undefined) {
      return;
    }

    updateDiagram(initialContent.originalContent);
  }, [initialContent, updateDiagram]);

  return (
    <>
      {initialContent && (
        <>
          <div
            style={{ height: "100%", textAlign: "center", opacity: isDiagramOutOfSync ? 0.5 : 1 }}
            ref={diagramContainerRef}
            className={"mermaid"}
          />
          <div ref={diagramHiddenContainerRef} className={"hidden"} />
        </>
      )}
    </>
  );
};

export const ServerlessWorkflowMermaidViewer = React.forwardRef(RefForwardingServerlessWorkflowMermaidViewer);
