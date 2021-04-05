/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { Editor } from "../api";
import { LoadingScreen } from "./LoadingScreen";
import { KeyBindingsHelpOverlay } from "./KeyBindingsHelpOverlay";
import { useCallback, useImperativeHandle, useState } from "react";

interface Props {
  setLocale: React.Dispatch<string>;
}

export interface EditorEnvelopeViewApi<E extends Editor> {
  getEditor: () => E | undefined;
  setEditor: (editor: E) => void;
  setLoading: () => void;
  setLoadingFinished: () => void;
  setLocale: (locale: string) => void;
}

export const EditorEnvelopeViewRef: React.RefForwardingComponent<EditorEnvelopeViewApi<Editor>, Props> = (
  props: Props,
  forwardingRef
) => {
  const [editor, setEditor] = useState<Editor | undefined>(undefined);
  const [loading, setLoading] = useState(true);

  const getEditor = useCallback(() => {
    return editor;
  }, [editor]);

  const setNewEditor = useCallback((newEditor: Editor) => {
    setEditor(newEditor);
  }, []);

  const setLoadingInit = useCallback(() => {
    setLoading(true);
  }, []);

  const setLoadingFinished = useCallback(() => {
    setLoading(false);
  }, []);

  const setLocale = useCallback((locale: string) => {
    props.setLocale(locale);
  }, []);

  useImperativeHandle(
    forwardingRef,
    () => {
      return {
        getEditor: () => getEditor(),
        setEditor: newEditor => setNewEditor(newEditor),
        setLoading: () => setLoadingInit(),
        setLoadingFinished: () => setLoadingFinished(),
        setLocale: locale => setLocale(locale)
      };
    },
    []
  );

  return (
    <>
      {!loading && <KeyBindingsHelpOverlay />}
      <LoadingScreen loading={loading} />
      <div style={{ position: "absolute", width: "100vw", height: "100vh", top: "0", left: "0" }}>
        {editor && editor.af_isReact && editor.af_componentRoot()}
      </div>
    </>
  );
};

export const EditorEnvelopeView = React.forwardRef(EditorEnvelopeViewRef);
