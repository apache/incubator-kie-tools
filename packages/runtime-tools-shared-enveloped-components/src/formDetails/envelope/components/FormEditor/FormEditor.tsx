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
import { PlayIcon } from "@patternfly/react-icons/dist/js/icons/play-icon";
import cloneDeep from "lodash/cloneDeep";
import React, { useEffect, useImperativeHandle, useMemo, useRef, useState } from "react";
import { Form } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { useFormDetailsContext } from "../contexts/FormDetailsContext";
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
  formType?: string;
  isSource?: boolean;
  isConfig?: boolean;
  formContent: Form;
  code: string;
  setFormContent: (formContent: Form) => void;
  saveFormContent: (formContent: Form) => void;
}

export const FormEditor = React.forwardRef<ResizableContent, FormEditorProps>(
  (
    { code, formType, formContent, setFormContent, saveFormContent, isSource = false, isConfig = false },
    forwardedRef
  ) => {
    const [content, setContent] = useState("");
    const appContext = useFormDetailsContext();
    const container = useRef<HTMLDivElement>(null);

    const formLanguage = useMemo<string | undefined>(() => {
      if (isSource && formType) {
        switch (formType.toLowerCase()) {
          case "tsx":
            return "typescript";
          case "html":
            return "html";
        }
      } else if (isConfig) {
        return "json";
      }
    }, [formType, isSource, isConfig]);

    const controller: FormEditorEditorApi = useMemo<FormEditorEditorApi>(() => {
      return new FormEditorEditorController(code, (args) => setContent(args.content), formLanguage, false);
    }, [code, formLanguage]);

    useEffect(() => {
      if (container.current) {
        controller.show(container.current, EditorTheme.LIGHT);
      }

      return () => {
        controller.dispose();
      };
    }, [controller]);

    useImperativeHandle(forwardedRef, () => controller, [controller]);

    const onExecuteCode = (): void => {
      const tempContent: Form = cloneDeep(formContent);
      const value = content;
      if (Object.keys(formContent)[0].length > 0 && isSource) {
        tempContent.source = value;
      } else {
        tempContent.configuration["resources"] = JSON.parse(value);
      }
      const newFormContent = { ...formContent, ...tempContent };
      appContext.updateContent(newFormContent);
      setFormContent(newFormContent);
    };

    const onSaveForm = (): void => {
      saveFormContent(formContent);
    };

    const onUndoChanges = (): void => {
      controller.undo();
    };

    const onRedoChanges = (): void => {
      controller.redo();
    };

    const customControl = (
      <>
        <FormEditorControl
          icon={<PlayIcon />}
          ariaLabel="Execute form"
          toolTipText="Execute form"
          onClick={onExecuteCode}
        />
        <FormEditorControl
          icon={<UndoIcon />}
          ariaLabel="Undo changes"
          toolTipText="Undo changes"
          onClick={onUndoChanges}
        />
        <FormEditorControl
          icon={<RedoIcon />}
          ariaLabel="Redo changes"
          toolTipText="Redo changes"
          onClick={onRedoChanges}
        />
        <FormEditorControl
          icon={<SaveIcon />}
          ariaLabel="Save form"
          toolTipText="Save form"
          onClick={() => onSaveForm()}
        />
      </>
    );

    return (
      <>
        <div>{customControl}</div>
        <div className={"kogito-form-editor-container"} ref={container} style={{ height: "700px" }} />
      </>
    );
  }
);

export default FormEditor;
