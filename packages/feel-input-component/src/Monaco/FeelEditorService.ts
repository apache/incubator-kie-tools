/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import * as Monaco from "monaco-editor";
import {
  initializeFeelCompletionItemProvider,
  initializeMonacoTheme,
  initializeFeelLanguage,
  initializeFeelTokensProvider,
  feelDefaultConfig,
  SuggestionProvider,
  MONACO_FEEL_LANGUAGE,
} from ".";

interface FeelExtendedWindow extends Window {
  __KIE__MONACO__EDITOR__?: FeelEditorService;
}

declare let window: FeelExtendedWindow;

export class FeelEditorService {
  private standaloneEditor?: Monaco.editor.IStandaloneCodeEditor;

  private domElement?: HTMLElement;

  private onChange?: (event: Monaco.editor.IModelContentChangedEvent, value: string, preview: string) => void;

  private onKeyDown?: (event: Monaco.IKeyboardEvent, value: string) => void;

  private options?: Monaco.editor.IStandaloneEditorConstructionOptions;

  private onBlur?: (value: string) => void;

  static getEditorBuilder(suggestionProvider?: SuggestionProvider) {
    if (this.getServiceInstance() === undefined) {
      initializeFeelLanguage();
      initializeMonacoTheme();
      initializeFeelTokensProvider();
      initializeFeelCompletionItemProvider(suggestionProvider);
      this.setServiceInstance(new FeelEditorService());
    }
    return this.getServiceInstance()!;
  }

  static setServiceInstance(editor: FeelEditorService | undefined) {
    window.__KIE__MONACO__EDITOR__ = editor;
  }

  static getServiceInstance() {
    return window.__KIE__MONACO__EDITOR__;
  }

  static getStandaloneEditor() {
    return this.getEditorBuilder().standaloneEditor;
  }

  static dispose() {
    this.getEditorBuilder().standaloneEditor?.dispose();
    this.getEditorBuilder().standaloneEditor = undefined;
  }

  static isInitialized() {
    return this.getEditorBuilder().standaloneEditor !== undefined;
  }

  withDomElement(domElement: HTMLElement) {
    this.domElement = domElement;
    return this;
  }

  withOnChange(onChange?: (event: Monaco.editor.IModelContentChangedEvent, content: string, preview: string) => void) {
    this.onChange = onChange;
    return this;
  }

  withOnKeyDown(onKeyDown?: (event: Monaco.IKeyboardEvent, value: string) => void) {
    this.onKeyDown = onKeyDown;
    return this;
  }

  withOnBlur(onBlur?: (value: string) => void) {
    this.onBlur = onBlur;
    return this;
  }

  withOptions(options?: Monaco.editor.IStandaloneEditorConstructionOptions) {
    this.options = options;
    return this;
  }

  createEditor() {
    this.dispose();
    return this.createStandaloneEditor();
  }

  colorize(value: string) {
    this.firstSetup();
    return Monaco.editor.colorize(value, MONACO_FEEL_LANGUAGE, {});
  }

  private firstSetup() {
    // Monaco cannot colorize elements with a custom language
    // before its first setup.
    if (!FeelEditorService.isInitialized()) {
      this.createEditor();
      this.dispose();
    }
  }

  dispose() {
    this.standaloneEditor?.dispose();
  }

  private getValue() {
    return this.standaloneEditor?.getValue() || "";
  }

  private createStandaloneEditor() {
    if (!this.domElement) {
      throw new Error("FEEL editor cannot be created without a DOM element.");
    }

    const element = this.domElement;
    const config = feelDefaultConfig(this.options);

    this.standaloneEditor = Monaco.editor.create(element, config);

    this.setupOnChange();
    this.setupOnBlur();
    this.setupOnKeyDown();

    return this.standaloneEditor!;
  }

  private setupOnChange() {
    if (!this.onChange) {
      return;
    }
    this.standaloneEditor?.onDidChangeModelContent((event) => {
      const value = this.getValue();
      const colorize = this.colorize(value);

      colorize.then((colorizedValue) => {
        this.onChange!(event, value, colorizedValue);
      });
    });
  }

  private setupOnBlur() {
    if (!this.onBlur) {
      return;
    }
    this.standaloneEditor?.onDidBlurEditorText(() => {
      this.onBlur!(this.getValue());
    });
  }

  private setupOnKeyDown() {
    if (!this.onKeyDown) {
      return;
    }
    this.standaloneEditor?.onKeyDown((e) => {
      this.onKeyDown!(e, this.getValue());
    });
  }
}
