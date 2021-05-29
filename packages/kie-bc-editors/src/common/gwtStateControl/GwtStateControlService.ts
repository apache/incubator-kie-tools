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

import { DefaultKogitoCommandRegistry } from "./KogitoCommandRegistry";
import { KogitoEditorChannelApi } from "@kie-tooling-core/editor/dist/api";
import { StateControlApi } from "../api/StateControlApi";
import { MessageBusClientApi } from "@kie-tooling-core/envelope-bus/dist/api";

export class GwtStateControlService {
  private undoCommand: () => void;
  private redoCommand: () => void;

  public undo(): void {
    if (this.undoCommand) {
      this.undoCommand();
    }
  }

  public redo(): void {
    if (this.redoCommand) {
      this.redoCommand();
    }
  }

  public exposeApi(channelApi: MessageBusClientApi<KogitoEditorChannelApi>): StateControlApi {
    return {
      registry: new DefaultKogitoCommandRegistry<unknown>(channelApi),
      setUndoCommand: (undoCommand) => (this.undoCommand = undoCommand),
      setRedoCommand: (redoCommand) => (this.redoCommand = redoCommand),
    };
  }
}
