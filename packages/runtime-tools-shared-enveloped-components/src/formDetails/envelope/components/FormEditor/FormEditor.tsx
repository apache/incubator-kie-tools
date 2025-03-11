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

import { EditorTheme } from "@kie-tools-core/editor/dist/api";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { UndoIcon } from "@patternfly/react-icons/dist/js/icons/undo-icon";
import { SaveIcon } from "@patternfly/react-icons/dist/js/icons/save-icon";
import { RedoIcon } from "@patternfly/react-icons/dist/js/icons/redo-icon";
import React, { useEffect, useImperativeHandle, useMemo, useRef, useState, useCallback } from "react";
import { ResizableContent } from "../FormDetails/FormDetails";
import "../styles.css";
import { FormEditorEditorApi, FormEditorEditorController } from "./FormEditorController";

function FormEditorControl(props: {
  toolTipText: string;
  onClick: () => void;
  ariaLabel: string;
  icon: React.ReactNode;
}) {
  return (
    <Tooltip content={<div>{props.toolTipText}</div>}>
      <Button onClick={props.onClick} variant="control" aria-label={props.ariaLabel}>
        {props.icon}
      </Button>
    </Tooltip>
  );
}

export interface FormEditorProps {
  formLanguage: string;
  textContent: string;
  saveContent: (content: string) => void;
}

export const FormEditor = React.forwardRef<ResizableContent, FormEditorProps>(
  ({ textContent, formLanguage, saveContent }, forwardedRef) => {
    const [content, setContent] = useState<string>(textContent);
    const container = useRef<HTMLDivElement>(null);

    const controller: FormEditorEditorApi = useMemo<FormEditorEditorApi>(() => {
      return new FormEditorEditorController(textContent, (args) => setContent(args.content), formLanguage, false);
    }, [textContent, formLanguage]);

    useEffect(() => {
      if (container.current) {
        controller.show(container.current, EditorTheme.LIGHT);
      }

      return () => {
        controller.dispose();
      };
    }, [controller]);

    useImperativeHandle(forwardedRef, () => controller, [controller]);

    const onSaveFormContent = useCallback(() => {
      saveContent(content);
    }, [content, saveContent]);

    const onUndo = useCallback(() => {
      controller.undo();
    }, [controller]);

    const onRedo = useCallback(() => {
      controller.redo();
    }, [controller]);

    return (
      <>
        <div>
          <FormEditorControl
            icon={<SaveIcon />}
            ariaLabel="Save form"
            toolTipText="Save form"
            onClick={() => onSaveFormContent()}
          />
          <FormEditorControl icon={<UndoIcon />} ariaLabel="Undo changes" toolTipText="Undo changes" onClick={onUndo} />
          <FormEditorControl icon={<RedoIcon />} ariaLabel="Redo changes" toolTipText="Redo changes" onClick={onRedo} />
        </div>
        <div className={"kogito-form-editor-container"} ref={container} style={{ height: "700px" }} />
      </>
    );
  }
);

export default FormEditor;
