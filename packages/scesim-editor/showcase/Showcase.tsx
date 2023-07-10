/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
import { ChangeEvent, useRef, useState } from "react";
import type { Property } from "csstype";
import { TestScenarioEditor } from "../src";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateSecondaryActions,
  EmptyStateVariant,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import "./Showcase.scss";

let editor: TestScenarioEditor;

type State = string | undefined;

export const Showcase = () => {
  const [content, setContent] = useState<State>(undefined);

  const displayEditor = (): Property.Display => {
    return content === undefined ? "none" : "block";
  };

  const undo = (): void => {
    editor.undo().finally();
  };

  const redo = (): void => {
    editor.redo().finally();
  };

  const validate = () => {
    /** Check if validation makes sense */
    const notifications: Notification[] = editor.validate();
    window.alert(JSON.stringify(notifications, undefined, 2));
  };

  const container = useRef<HTMLDivElement | null>(null);

  return (
    <div>
      {content === undefined && (
        <EmptyState variant={EmptyStateVariant.small}>
          <EmptyStateIcon icon={CubesIcon} />
          <Title headingLevel="h4" size="lg">
            Test Scenario Showcase
          </Title>
          <EmptyStateBody>Please either upload an existing Test Scenario file or create a new one.</EmptyStateBody>
          <FileChooser
            loadScesim={(path: string, xml: string) => {
              setContent(xml);
              editor.setContent(path, xml).finally();
              console.log(xml);
            }}
          />
          <EmptyStateSecondaryActions>
            <Button
              variant="link"
              onClick={(e) => {
                setContent("");
                editor.setContent("Untitled.scesim", "").finally();
              }}
              ouiaId="new-button"
            >
              Create a new Test Scenario
            </Button>
          </EmptyStateSecondaryActions>
        </EmptyState>
      )}
      <div style={{ display: displayEditor() }}>
        <div className="editor-footer ignore-onclickoutside">
          <Split hasGutter={true}>
            <SplitItem>
              <Button variant="primary" onClick={() => setContent(undefined)} ouiaId="undo-button">
                Back
              </Button>
            </SplitItem>
            <SplitItem>
              <Button variant="primary" onClick={undo} ouiaId="undo-button">
                Undo
              </Button>
            </SplitItem>
            <SplitItem>
              <Button variant="primary" onClick={redo} ouiaId="redo-button">
                Redo
              </Button>
            </SplitItem>
            <SplitItem>
              <Button variant="secondary" onClick={validate} ouiaId="validate-button">
                Validate
              </Button>
            </SplitItem>
          </Split>
          <hr className="editor-footer__divider" />
        </div>
        <div ref={container} className="editor-container">
          <TestScenarioEditor
            exposing={(self: TestScenarioEditor) => (editor = self)}
            ready={() => {
              /*NOP*/
            }}
            newEdit={() => {
              /*NOP*/
            }}
            setNotifications={() => {
              /*NOP*/
            }}
          />
        </div>
      </div>
    </div>
  );
};

const FileChooser = ({ loadScesim }: { loadScesim: (path: string, xml: string) => void }) => {
  const showFile = async (e1: ChangeEvent<HTMLInputElement>) => {
    e1.preventDefault();
    const reader = new FileReader();
    const files: FileList | null = e1.target.files;
    if (files !== null) {
      const file: File = files[0];

      reader.onload = async (e2: ProgressEvent<FileReader>) => {
        const text: string | ArrayBuffer | null | undefined = e2.target?.result;
        if (text) {
          loadScesim(file.name, text?.toString() as string);
        }
      };

      reader.readAsText(file);
    }
  };

  return (
    <div style={{ marginTop: "var(--pf-c-empty-state__primary--MarginTop)" }}>
      <label htmlFor="file-upload" className="pf-c-button pf-m-primary" data-ouia-component-id="upload-button">
        <i className="fa fa-cloud-upload" />
        Load an existing Test Scenario (*.scesim)
      </label>
      <input
        id="file-upload"
        style={{ display: "none" }}
        type="file"
        accept={".scesim"}
        onChange={(e) => showFile(e)}
      />
    </div>
  );
};
