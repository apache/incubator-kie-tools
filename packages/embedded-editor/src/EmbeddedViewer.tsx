/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import { KogitoEdit, ResourceContent, ResourceContentRequest, ResourceListRequest, ResourcesList } from "@kogito-tooling/core-api";
import { GwtEditorRoutes } from "@kogito-tooling/kie-bc-editors";
import "@patternfly/patternfly/patternfly-addons.css";
import "@patternfly/patternfly/patternfly-variables.css";
import "@patternfly/patternfly/patternfly.css";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { EmbeddedEditorContext } from "./common/EmbeddedEditorContext";
import { EmbeddedEditorRouter } from "./common/EmbeddedEditorRouter";
import { File } from "./common/File";
import { BaseEditor } from "./editor/BaseEditor";
import { EnvelopeBusOuterMessageHandlerFactory } from "./editor/EnvelopeBusOuterMessageHandlerFactory";

interface Props {
  file: File;
  onResourceContentRequest?: (request: ResourceContentRequest) => ResourceContent;
  onResourceListRequest?: (request: ResourceListRequest) => ResourcesList;
}

const iframeTemplateRelativePath: string = "envelope/index.html";

export const EmbeddedViewer = (props: Props) => {

  const envelopeBusOuterMessageHandlerFactory = useMemo(() => new EnvelopeBusOuterMessageHandlerFactory(), []);
  const onlineEditorRouter = useMemo(
    () =>
      new EmbeddedEditorRouter(
        new GwtEditorRoutes({
          bpmnPath: "gwt-editors/bpmn",
          dmnPath: "gwt-editors/dmn"
        })
      ),
    []
  );

  const onResourceContentRequest = useCallback((request: ResourceContentRequest) => {
    if (props.onResourceContentRequest) {
      return props.onResourceContentRequest(request);
    }
    return new ResourceContent(request.path, undefined);
  }, [props.onResourceContentRequest]);

  const onResourceListRequest = useCallback((request: ResourceListRequest) => {
    if (props.onResourceListRequest) {
      return props.onResourceListRequest(request);
    }
    return new ResourcesList(request.pattern, []);
  }, [props.onResourceListRequest]);

  return (
    <EmbeddedEditorContext.Provider
      value={{
        router: onlineEditorRouter,
        envelopeBusOuterMessageHandlerFactory: envelopeBusOuterMessageHandlerFactory,
        iframeTemplateRelativePath: iframeTemplateRelativePath
      }}
    >
      <BaseEditor
        file={props.file}
        onContentResponse={() => {/*NOP*/ }}
        onSetContentError={() => {/*NOP*/ }}
        onDirtyIndicatorChange={(isDirty: boolean) => {/*NOP*/ }}
        onReady={() => {/*NOP*/ }}
        onResourceContentRequest={onResourceContentRequest}
        onResourceListRequest={onResourceListRequest}
        onEditorUndo={(edits: ReadonlyArray<KogitoEdit>) => {/*NOP*/ }}
        onEditorRedo={(edits: ReadonlyArray<KogitoEdit>) => {/*NOP*/ }}
        onNewEdit={(edit: KogitoEdit) => {/*NOP*/ }}
        onPreviewRequest={(previewSvg: string) => {/*NOP*/ }}
      />
    </EmbeddedEditorContext.Provider>
  );
};
