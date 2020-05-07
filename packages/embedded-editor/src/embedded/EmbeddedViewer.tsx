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

import { ChannelType, KogitoEdit, ResourceContent, ResourceContentRequest, ResourceListRequest, ResourcesList } from "@kogito-tooling/core-api";
import * as React from "react";
import { useCallback } from "react";
import { File } from "../common/File";
import { EmbeddedEditor } from "./EmbeddedEditor";
import { EmbeddedEditorRouter } from "./EmbeddedEditorRouter";

interface Props {
  file: File;
  router: EmbeddedEditorRouter;
  channelType: ChannelType;
  onResourceContentRequest?: (request: ResourceContentRequest) => ResourceContent;
  onResourceListRequest?: (request: ResourceListRequest) => ResourcesList;
  envelopeUri?: string;
}

export const EmbeddedViewer = (props: Props) => {

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
    <EmbeddedEditor
      file={props.file}
      router={props.router}
      channelType={props.channelType}
      onContentResponse={() => {/*NOP*/ }}
      onSetContentError={() => {/*NOP*/ }}
      onDirtyIndicatorChange={(isDirty: boolean) => {/*NOP*/ }}
      onReady={() => {/*NOP*/ }}
      onResourceContentRequest={onResourceContentRequest}
      onResourceListRequest={onResourceListRequest}
      onEditorUndo={(edits: ReadonlyArray<KogitoEdit>) => {/*NOP*/ }}
      onEditorRedo={(edits: ReadonlyArray<KogitoEdit>) => {/*NOP*/ }}
      onNewEdit={(edit: KogitoEdit) => {/*NOP*/ }}
      onPreviewResponse={(previewSvg: string) => {/*NOP*/ }}
      envelopeUri={props.envelopeUri}
    />
  );
};
