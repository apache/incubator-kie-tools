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
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { WorkspaceEdit } from "@kie-tools-core/workspace/dist/api";
import { TextArea, TextInput } from "@patternfly/react-core/dist/js";

interface Props {
  /**
   * Callback to the container so that it may bind to the TestScenarioEditor.
   *
   * @returns Instance of the TestScenarioEditor.
   */
  exposing: (s: TestScenarioEditor) => void;

  /**
   * Delegation for KogitoEditorChannelApi.kogitoEditor_ready() to signal to the Channel that the editor is ready.
   */
  ready: () => void;

  /**
   * Delegation for WorkspaceChannelApi.kogitoWorkspace_newEdit(edit) to signal to the Channel
   * @param edit An object representing the unique change.
   */
  newEdit: (edit: WorkspaceEdit) => void;

  /**
   * Delegation for NotificationsChannelApi.kogitoNotifications_setNotifications(path, notifications) to report all validation
   * notifications to the Channel that will replace existing notification for the path.
   * @param path The path that references the Notification
   * @param notifications List of Notifications
   */
  setNotifications: (path: string, notifications: Notification[]) => void;
}

export interface State {
  path: string;
  content: string;
}

export class TestScenarioEditor extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    props.exposing(this);
    this.state = {
      path: "",
      content: "",
    };
  }

  public setContent(path: string, content: string): Promise<void> {
    try {
      //TODO: XML Deserialization to JSON here (Scesim File)

      //TODO: XML Deserialization to JSON here (DMN File) If DMN type scesim
      this.setState({ path: path, content: content });
      return Promise.resolve();
    } catch (e) {
      console.error(e);
      return Promise.reject();
    }
  }

  public getContent(): Promise<string> {
    //TODO: JSON Serialization to XML here (SCESIM file)

    return Promise.resolve("");
  }

  public async undo(): Promise<void> {
    return Promise.resolve(undefined);
  }

  public async redo(): Promise<void> {
    return Promise.resolve(undefined);
  }

  public validate(): Notification[] {
    return [];
  }

  public render() {
    return (
      <>
        <TextInput value={this.state.path} />
        <TextArea autoResize={true} value={this.state.content} />
      </>
    );
  }
}
