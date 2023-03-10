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
import { useEffect, useMemo, useRef } from "react";
import { bootstrapMonaco } from "./PredicateEditorSetup";
import { PredicateEditorMonacoController } from "./PredicateEditorMonacoController";

interface PredicateEditorProps {
  text: string | undefined;
  setText: (_text: string | undefined) => void;
}

bootstrapMonaco();

export const PredicateEditor: React.FC<PredicateEditorProps> = ({ text, setText }) => {
  const monacoContainerRef = useRef<HTMLDivElement>(null);

  const monacoController = useMemo(() => new PredicateEditorMonacoController(), []);

  useEffect(() => {
    if (monacoContainerRef.current) {
      monacoController.createEditor(monacoContainerRef.current, setText);
    }
    return () => {
      monacoController.dispose();
    };
  }, [monacoContainerRef]);

  useEffect(() => {
    // Setting the
    monacoController.setValue(text);
  }, [text]);

  return <div style={{ height: "300px" }} ref={monacoContainerRef} />;
};
