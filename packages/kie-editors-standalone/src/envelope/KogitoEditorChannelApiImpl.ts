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

import {
  ContentType,
  KogitoEdit,
  ResourceContent,
  ResourceContentRequest,
  ResourceListRequest,
  ResourcesList
} from "@kogito-tooling/channel-common-api";
import { EditorContent, KogitoEditorChannelApi, StateControlCommand } from "@kogito-tooling/editor/dist/api";
import { Tutorial, UserInteraction } from "@kogito-tooling/guided-tour/dist/api";
import { File, StateControl } from "@kogito-tooling/editor/dist/channel";
import { Minimatch } from "minimatch";
import { Notification } from "@kogito-tooling/notifications/dist/api";

export class KogitoEditorChannelApiImpl implements KogitoEditorChannelApi {
  constructor(
    private readonly stateControl: StateControl,
    private readonly file: File,
    private readonly locale: string,
    private readonly overrides: Partial<KogitoEditorChannelApi>,
    private readonly resources?: Map<string, { contentType: ContentType; content: Promise<string> }>
  ) {}

  public receive_newEdit(edit: KogitoEdit) {
    this.stateControl.updateCommandStack({ id: edit.id });
    this.overrides.receive_newEdit?.(edit);
  }

  public receive_stateControlCommandUpdate(command: StateControlCommand) {
    switch (command) {
      case StateControlCommand.REDO:
        this.stateControl.redo();
        break;
      case StateControlCommand.UNDO:
        this.stateControl.undo();
        break;
      default:
        console.info(`Unknown message type received: ${command}`);
        break;
    }
    this.overrides.receive_stateControlCommandUpdate?.(command);
  }

  public receive_guidedTourUserInteraction(userInteraction: UserInteraction) {
    /* unsupported */
  }

  public receive_guidedTourRegisterTutorial(tutorial: Tutorial) {
    /* unsupported */
  }

  public async receive_contentRequest() {
    const content = await this.file.getFileContents();
    return { content: content ?? "", path: this.file.fileName };
  }

  public async receive_resourceContentRequest(request: ResourceContentRequest) {
    const resource = this.resources?.get(request.path);

    if (!resource) {
      console.warn("The editor requested an unspecified resource: " + request.path);
      return new ResourceContent(request.path, undefined);
    }

    const requestedContentType = request.opts?.type ?? resource.contentType;
    if (requestedContentType !== resource.contentType) {
      console.warn(
        "The editor requested a resource with a different content type from the one specified: " +
          request.path +
          ". Content type requested: " +
          requestedContentType
      );
      return new ResourceContent(request.path, undefined);
    }

    return new ResourceContent(request.path, await resource.content, resource.contentType);
  }

  public async receive_resourceListRequest(request: ResourceListRequest) {
    if (!this.resources) {
      return new ResourcesList(request.pattern, []);
    }

    const matcher = new Minimatch(request.pattern);
    const matches = Array.from(this.resources.keys()).filter(path => matcher.match(path));
    return new ResourcesList(request.pattern, matches);
  }

  public receive_openFile(path: string): void {
    this.overrides.receive_openFile?.(path);
  }

  public receive_ready(): void {
    this.overrides.receive_ready?.();
  }

  public receive_setContentError(editorContent: EditorContent): void {
    this.overrides.receive_setContentError?.(editorContent);
  }

  public receive_getLocale(): Promise<string> {
    return Promise.resolve(this.locale);
  }

  public createNotification(notification: Notification): void {
    this.overrides.createNotification?.(notification);
  }

  public setNotifications(path: string, notifications: Notification[]): void {
    this.overrides.setNotifications?.(path, notifications);
  }

  public removeNotifications(path: string): void {
    this.overrides.removeNotifications?.(path);
  }
}
