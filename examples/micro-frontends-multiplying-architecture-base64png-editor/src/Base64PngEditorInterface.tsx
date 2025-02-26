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

import * as React from "react";
import {
  Editor,
  EditorApi,
  EditorInitArgs,
  EditorTheme,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeApi,
  KogitoEditorEnvelopeContextType,
} from "@kie-tools-core/editor/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { Base64PngEditor } from "./Base64PngEditor";

/**
 * This class implements the Editor interface, a contract made by the Apache KIE Tools that determines what methods an Editor needs to implement and its properties.
 * The implemented methods in this class are used to communicate with the Channel.
 *
 * @constructor envelopeContext All the features and information provided by the Apache KIE Tools Envelope.
 * @constructor envelopeContext.services The services available to be used by the Editor.
 * @constructor envelopeContext.services.keyboardShortcuts Give the possibility to register keyboard shortcuts on your Editor
 * @constructor envelopeContext.services.i18n Give the possibility to subscribe to locale changes that happened on the Channel.
 * @constructor envelopeContext.services.guidedTour
 * @constructor envelopeContext.channelApi The object which allows this Editor to communicate with its containing Channel.
 * @constructor envelopeContext.channelApi.request Make requests to the Channel that returns a Promise with a response.
 * @constructor envelopeContext.channelApi.notify Send a notification to the Channel that doesn't return a response.
 * @constructor envelopeContext.channelApi.subscribe Subscribe to other envelopes.
 * @constructor envelopeContext.channelApi.unsubscribe Unsubscribe to other envelopes.
 * @constructor envelopeContext.context The object that contains additional information about where the Editor is running.
 * @constructor envelopeContext.context.channel The Channel which the Editor is running.
 * @constructor envelopeContext.context.operatingSystem The OS which the Editor is running.
 *
 * @constructor initArgs Initial arguments sent by the Channel that enable this Editor to start properly.
 * @constructor initArgs.resourcesPathPrefix The prefix which must be prepended by static resources (e.g. JS/CSS files) to be loaded properly.
 * @constructor initArgs.fileExtension The file extension of the file that's being opened. Used when the same Editor can handle multiple file extensions.
 * @constructor initArgs.initialLocale The initial locale of the application. Useful in case the Editor implements i18n.
 */
export class Base64PngEditorInterface implements Editor {
  private editorRef: React.RefObject<EditorApi>;
  public af_isReact = true;
  public af_componentId: "base64png-editor";
  public af_componentTitle: "Base64 PNG Editor";

  constructor(
    private readonly envelopeContext: KogitoEditorEnvelopeContextType<KogitoEditorEnvelopeApi, KogitoEditorChannelApi>,
    private readonly initArgs: EditorInitArgs
  ) {
    this.editorRef = React.createRef<EditorApi>();
  }

  /**
   * Retrieve the Editor content.
   * The Editor must return a Promise of its content.
   */
  public getContent(): Promise<string> {
    return this.editorRef.current!.getContent()!;
  }

  /**
   * Set the Editor content
   * @param normalizedPosixPathRelativeToTheWorkspaceRoot The file normalizedPosixPathRelativeToTheWorkspaceRoot that is being open.
   * @param content The file content in a string format.
   */
  public setContent(normalizedPosixPathRelativeToTheWorkspaceRoot: string, content: string): Promise<void> {
    return this.editorRef.current!.setContent(normalizedPosixPathRelativeToTheWorkspaceRoot, content)!;
  }

  /**
   * The Editor must return a valid SVG file, which represents the Editor content.
   */
  public getPreview(): Promise<string | undefined> {
    return this.editorRef.current!.getPreview()!;
  }

  /**
   * Calls the Editor undo method.
   */
  public undo(): Promise<void> {
    return this.editorRef.current!.undo()!;
  }

  /**
   * Calls the Editor redo method.
   */
  public redo(): Promise<void> {
    return this.editorRef.current!.redo()!;
  }

  /**
   * Calls the Editor validate method.
   */
  public validate(): Promise<Notification[]> {
    return this.editorRef.current!.validate()!;
  }

  /**
   * Not supported.
   */
  public setTheme(theme: EditorTheme): Promise<void> {
    return Promise.resolve();
  }

  /**
   * Retrieve the root component of the Editor. Here the Editor is going to be initialized with its props.
   */
  public af_componentRoot() {
    return <Base64PngEditor ref={this.editorRef} envelopeContext={this.envelopeContext} />;
  }
}
