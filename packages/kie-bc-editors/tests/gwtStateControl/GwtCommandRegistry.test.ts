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

import { DefaultKogitoCommandRegistry } from "@kogito-tooling/kie-bc-editors/dist/common/gwtStateControl";
import { KogitoEditorChannelApi } from "@kie-tooling-core/editor/dist/api";
import { MessageBusClientApi } from "@kie-tooling-core/envelope-bus/dist/api";
import { messageBusClientApiMock } from "@kie-tooling-core/envelope-bus/dist-tests/common";

class Command {
  private id: string;

  constructor(id: string) {
    this.id = id;
  }

  get getId(): string {
    return this.id;
  }
}

let channelApi: MessageBusClientApi<KogitoEditorChannelApi>;
let registry: DefaultKogitoCommandRegistry<Command>;

const COMMAND1 = new Command("1");
const COMMAND2 = new Command("2");
const COMMAND3 = new Command("3");
const COMMAND4 = new Command("4");

describe("DefaultKogitoCommandRegistry", () => {
  beforeEach(() => {
    channelApi = messageBusClientApiMock<KogitoEditorChannelApi>();
    registry = new DefaultKogitoCommandRegistry<Command>(channelApi);
  });

  test("test basic add/remove elements", () => {
    registry.register(COMMAND1.getId, COMMAND1);
    expect(channelApi.notifications.kogitoWorkspace_newEdit).toBeCalledWith(expect.objectContaining({ id: "1" }));

    registry.register(COMMAND2.getId, COMMAND2);
    expect(channelApi.notifications.kogitoWorkspace_newEdit).toBeCalledWith(expect.objectContaining({ id: "2" }));

    registry.register(COMMAND3.getId, COMMAND3);
    expect(channelApi.notifications.kogitoWorkspace_newEdit).toBeCalledWith(expect.objectContaining({ id: "3" }));

    registry.register(COMMAND4.getId, COMMAND4);
    expect(channelApi.notifications.kogitoWorkspace_newEdit).toBeCalledWith(expect.objectContaining({ id: "4" }));

    expect(channelApi.notifications.kogitoWorkspace_newEdit).toBeCalledTimes(4);

    expect(registry.getCommands()).toHaveLength(4);
    expect(registry.getCommands()).toContain(COMMAND1);
    expect(registry.getCommands()).toContain(COMMAND2);
    expect(registry.getCommands()).toContain(COMMAND3);
    expect(registry.getCommands()).toContain(COMMAND4);
    expect(registry.isEmpty()).toBeFalsy();

    expect(registry.peek()).toBe(COMMAND4);
    expect(registry.getCommands()).toHaveLength(4);

    expect(registry.pop()).toBe(COMMAND4);

    expect(registry.getCommands()).toHaveLength(3);
    expect(registry.getCommands()).toContain(COMMAND1);
    expect(registry.getCommands()).toContain(COMMAND2);
    expect(registry.getCommands()).toContain(COMMAND3);
    expect(registry.isEmpty()).toBeFalsy();

    expect(registry.pop()).toBe(COMMAND3);

    expect(registry.getCommands()).toHaveLength(2);
    expect(registry.getCommands()).toContain(COMMAND1);
    expect(registry.getCommands()).toContain(COMMAND2);
    expect(registry.isEmpty()).toBeFalsy();

    registry.clear();
    expect(registry.getCommands()).toHaveLength(0);
    expect(registry.isEmpty()).toBeTruthy();
  });

  test("test adding reaching max elements", () => {
    registry.setMaxSize(2);

    registry.register(COMMAND1.getId, COMMAND1);

    expect(channelApi.notifications.kogitoWorkspace_newEdit).toBeCalledWith(expect.objectContaining({ id: "1" }));

    registry.register(COMMAND2.getId, COMMAND2);
    expect(channelApi.notifications.kogitoWorkspace_newEdit).toBeCalledWith(expect.objectContaining({ id: "2" }));

    expect(channelApi.notifications.kogitoWorkspace_newEdit).toBeCalledTimes(2);

    expect(registry.getCommands()).toHaveLength(2);
    expect(registry.getCommands()).toContain(COMMAND1);
    expect(registry.getCommands()).toContain(COMMAND2);
    expect(registry.isEmpty()).toBeFalsy();

    registry.register(COMMAND3.getId, COMMAND3);

    expect(registry.getCommands()).toHaveLength(2);
    expect(registry.getCommands()).toContain(COMMAND3);
    expect(registry.getCommands()).toContain(COMMAND2);
    expect(registry.isEmpty()).toBeFalsy();

    registry.register(COMMAND4.getId, COMMAND4);

    expect(registry.getCommands()).toHaveLength(2);
    expect(registry.getCommands()).toContain(COMMAND3);
    expect(registry.getCommands()).toContain(COMMAND4);
    expect(registry.isEmpty()).toBeFalsy();
  });

  test("test basic adding elements after remove", () => {
    registry.register(COMMAND1.getId, COMMAND1);
    expect(channelApi.notifications.kogitoWorkspace_newEdit).toBeCalledWith(expect.objectContaining({ id: "1" }));

    registry.register(COMMAND2.getId, COMMAND2);
    expect(channelApi.notifications.kogitoWorkspace_newEdit).toBeCalledWith(expect.objectContaining({ id: "2" }));

    expect(channelApi.notifications.kogitoWorkspace_newEdit).toBeCalledTimes(2);

    expect(registry.getCommands()).toHaveLength(2);
    expect(registry.getCommands()).toContain(COMMAND1);
    expect(registry.getCommands()).toContain(COMMAND2);
    expect(registry.isEmpty()).toBeFalsy();

    expect(registry.pop()).toBe(COMMAND2);
    expect(registry.pop()).toBe(COMMAND1);

    expect(registry.getUndoneCommands()).toHaveLength(2);
    expect(registry.getUndoneCommands()).toContain(COMMAND1.getId);
    expect(registry.getUndoneCommands()).toContain(COMMAND2.getId);
    expect(registry.isEmpty()).toBeTruthy();

    registry.register(COMMAND2.getId, COMMAND2);

    expect(channelApi.notifications.kogitoWorkspace_newEdit).toBeCalledTimes(2);
    expect(registry.getCommands()).toHaveLength(1);
    expect(registry.getCommands()).toContain(COMMAND2);
    expect(registry.getUndoneCommands()).toHaveLength(1);
    expect(registry.getUndoneCommands()).toContain(COMMAND1.getId);
    expect(registry.isEmpty()).toBeFalsy();

    registry.register(COMMAND1.getId, COMMAND1);

    expect(channelApi.notifications.kogitoWorkspace_newEdit).toBeCalledTimes(2);
    expect(registry.getCommands()).toHaveLength(2);
    expect(registry.getCommands()).toContain(COMMAND1);
    expect(registry.getCommands()).toContain(COMMAND2);
    expect(registry.getUndoneCommands()).toHaveLength(0);
    expect(registry.isEmpty()).toBeFalsy();

    registry.register(COMMAND3.getId, COMMAND3);
    expect(channelApi.notifications.kogitoWorkspace_newEdit).toBeCalledWith(expect.objectContaining({ id: "3" }));
    expect(channelApi.notifications.kogitoWorkspace_newEdit).toBeCalledTimes(3);
    expect(registry.getCommands()).toHaveLength(3);
    expect(registry.getCommands()).toContain(COMMAND1);
    expect(registry.getCommands()).toContain(COMMAND2);
    expect(registry.getCommands()).toContain(COMMAND3);
  });
});
