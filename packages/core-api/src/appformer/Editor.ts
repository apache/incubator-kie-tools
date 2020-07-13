/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import { Component } from "./Component";
import { ComponentTypes } from "./ComponentTypes";

/**
 * Editor component API. Implement this class to create an Editor.
 */
export abstract class Editor extends Component {
  public af_componentTitle?: string = undefined;
  public af_subscriptions: Map<string, (event: any) => void> = new Map();

  protected constructor(componentId: string) {
    super({ type: ComponentTypes.EDITOR, af_componentId: componentId });
  }

  public abstract setContent(path: string, content: string): Promise<void>;

  public abstract getContent(): Promise<string>;

  public abstract getPreview(): Promise<string | undefined>;

  public abstract undo(): Promise<void>;

  public abstract redo(): Promise<void>;
}
