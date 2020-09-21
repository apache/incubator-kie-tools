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

import { KogitoEditorChannelApi } from "@kogito-tooling/editor/dist/api";
import { PMMLEditor } from "../editor";
import * as React from "react";
import { useState } from "react";
import { EnvelopeBusMessageManager } from "@kogito-tooling/envelope-bus/dist/common";
import { PMMLEmptyState } from "./EmptyState";
import { DisplayProperty } from "csstype";

const EMPTY_PMML: string = `<PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"><Header /><DataDictionary/></PMML>`;

const manager: EnvelopeBusMessageManager<
  KogitoEditorChannelApi,
  KogitoEditorChannelApi
> = new EnvelopeBusMessageManager((msg: any) => console.log(msg));

let editor: PMMLEditor;

type State = string | undefined;

export const App = () => {
  const [content, setContent] = useState<State>(undefined);

  const displayPMMLEditor = (): DisplayProperty => {
    return content === undefined ? "none" : "block";
  };

  return (
    <div>
      {content === undefined && (
        <PMMLEmptyState
          newContent={() => {
            setContent(EMPTY_PMML);
            editor.setContent("", EMPTY_PMML).finally();
          }}
          setContent={(xml: string) => {
            setContent(xml);
            editor.setContent("", xml).finally();
          }}
        />
      )}
      <div style={{ display: displayPMMLEditor() }}>
        <PMMLEditor exposing={(self: PMMLEditor) => (editor = self)} channelApi={manager.clientApi} />
      </div>
    </div>
  );
};
