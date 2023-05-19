/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import { WebView } from "vscode-extension-tester";
import { switchWebviewToFrame, switchBack } from "./VSCodeTestHelper";

/**
 * Helper class to support work with kie editor webviews.
 *
 * Extend the class to add functionality for the specific kie editor.
 * Make sure you switch to the webview's frame before creating an instance of extended class.
 */
export abstract class EditorTestHelper {
  constructor(protected readonly webview: WebView) {}

  public async switchToEditorFrame(): Promise<void> {
    await switchWebviewToFrame(this.webview);
  }

  public async switchBack(): Promise<void> {
    await switchBack(this.webview);
  }
}
