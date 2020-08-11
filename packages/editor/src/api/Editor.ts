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

import { Rect } from "@kogito-tooling/guided-tour/dist/api";

/**
 * Editor component API. Implement this class to create an Editor.
 */
export interface Editor extends EditorApi {
  af_isReact: boolean;
  af_componentId: string;
  af_componentTitle: string;

  af_componentRoot(): React.ReactPortal | React.ReactElement | HTMLElement | string;
  af_onStartup?(): void;
  af_onOpen?(): void;
}

/**
 * Editor component API. Basic Editor feature definitions.
 */
export interface EditorApi {
  setContent(path: string, content: string): Promise<void>;
  getContent(): Promise<string>;
  getPreview(): Promise<string | undefined>;
  getElementPosition(selector: string): Promise<Rect | undefined>;
  undo(): Promise<void>;
  redo(): Promise<void>;
}
