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

import { Rect } from "@kogito-tooling/microeditor-envelope-protocol";

/**
 * Editor component API. Implement this class to create an Editor.
 */
export abstract class Editor {
  public af_isReact: boolean = false;
  public af_componentId: string;
  public af_componentTitle: string;

  protected constructor(componentId: string) {
    this.af_componentId = componentId;
  }

  public abstract setContent(path: string, content: string): Promise<void>;

  public abstract getContent(): Promise<string>;

  public abstract getPreview(): Promise<string | undefined>;

  public abstract getElementPosition(selector: string): Promise<Rect | undefined>;

  public abstract undo(): Promise<void>;

  public abstract redo(): Promise<void>;

  public abstract af_componentRoot(): React.ReactPortal | React.ReactElement | HTMLElement | string;

  public af_onStartup(): void {
    //
  }

  public af_onOpen(): void {
    //
  }
}
