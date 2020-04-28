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

import * as React from "react";
import * as ReactDOM from "react-dom";
import { EMPTY_FILE_DMN, EMPTY_FILE_BPMN } from "./common/File";
import { KogitoEditorWrapper, KogitoEditorWrapperRef } from "./KogitoEditorWrapper";

const wrapperRef = React.createRef<KogitoEditorWrapperRef>();

const requestContent = () => {
  wrapperRef.current?.requestContent;
};

const onContentReceived = (content: string) => {
  alert("Reporting externally: " + content);
};

ReactDOM.render(
  <>
    <p>Hello from an embedded world</p>
    <button onClick={requestContent}>Get content (external)</button>
    <KogitoEditorWrapper
      ref={wrapperRef}
      file={EMPTY_FILE_DMN}
      readonly={false}
      external={false}
      onContentReceived={onContentReceived}
    />
  </>,
  document.getElementById("app")!
);
