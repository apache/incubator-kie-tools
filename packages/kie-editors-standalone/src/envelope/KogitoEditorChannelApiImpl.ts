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
  ResourcesList,
} from "@kie-tooling-core/workspace/dist/api";
import { EditorContent, KogitoEditorChannelApi, StateControlCommand } from "@kie-tooling-core/editor/dist/api";
import { Tutorial, UserInteraction } from "@kie-tooling-core/guided-tour/dist/api";
import { File, StateControl } from "@kie-tooling-core/editor/dist/channel";
import { Minimatch } from "minimatch";
import { Notification } from "@kie-tooling-core/notifications/dist/api";

export class KogitoEditorChannelApiImpl implements KogitoEditorChannelApi {
  constructor(
    private readonly stateControl: StateControl,
    private readonly file: File,
    private readonly locale: string,
    private readonly overrides: Partial<KogitoEditorChannelApi>,
    private readonly resources?: Map<string, { contentType: ContentType; content: Promise<string> }>
  ) {}

  public kogitoWorkspace_newEdit(edit: KogitoEdit) {
    this.stateControl.updateCommandStack({ id: edit.id });
    this.overrides.kogitoWorkspace_newEdit?.(edit);
  }

  public kogitoEditor_stateControlCommandUpdate(command: StateControlCommand) {
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
    this.overrides.kogitoEditor_stateControlCommandUpdate?.(command);
  }

  public kogitoGuidedTour_guidedTourUserInteraction(userInteraction: UserInteraction) {
    /* unsupported */
  }

  public kogitoGuidedTour_guidedTourRegisterTutorial(tutorial: Tutorial) {
    /* unsupported */
  }

  public async kogitoEditor_contentRequest() {
    const content = await this.file.getFileContents();
    return { content: content ?? "", path: this.file.fileName };
  }

  public async kogitoWorkspace_resourceContentRequest(request: ResourceContentRequest) {
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

  public async kogitoWorkspace_resourceListRequest(request: ResourceListRequest) {
    if (!this.resources) {
      return new ResourcesList(request.pattern, []);
    }

    const matcher = new Minimatch(request.pattern);
    const matches = Array.from(this.resources.keys()).filter((path) => matcher.match(path));
    return new ResourcesList(request.pattern, matches);
  }

  public kogitoWorkspace_openFile(path: string): void {
    this.overrides.kogitoWorkspace_openFile?.(path);
  }

  public kogitoEditor_ready(): void {
    this.overrides.kogitoEditor_ready?.();
  }

  public kogitoEditor_setContentError(editorContent: EditorContent): void {
    this.overrides.kogitoEditor_setContentError?.(editorContent);
  }

  public kogitoI18n_getLocale(): Promise<string> {
    return Promise.resolve(this.locale);
  }

  public kogitoNotifications_createNotification(notification: Notification): void {
    this.overrides.kogitoNotifications_createNotification?.(notification);
  }

  public kogitoNotifications_setNotifications(path: string, notifications: Notification[]): void {
    this.overrides.kogitoNotifications_setNotifications?.(path, notifications);
  }

  public kogitoNotifications_removeNotifications(path: string): void {
    this.overrides.kogitoNotifications_removeNotifications?.(path);
  }
}
