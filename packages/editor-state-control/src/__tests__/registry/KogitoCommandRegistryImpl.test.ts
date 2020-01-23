/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { KogitoCommandRegistryImpl } from "../..";

class Command {
  private id: number;

  constructor(id: any) {
    this.id = id;
  }

  get getId(): number {
    return this.id;
  }
}

const OnNewCommandCallback = jest.fn(() => ({
  notifyNewCommand: jest.fn()
}));

let registryChangeListener: () => void;
let onNewCommandCallback: any;

let registry:KogitoCommandRegistryImpl<Command>;

const COMMAND1 = new Command(1);
const COMMAND2 = new Command(2);
const COMMAND3 = new Command(3);
const COMMAND4 = new Command(4);

describe("KogitoCommandRegistryImpl", () => {

  beforeEach(() => {
    onNewCommandCallback = new OnNewCommandCallback();
    registryChangeListener = jest.fn();

    registry = new KogitoCommandRegistryImpl<Command>();

    registry.setOnNewCommand(onNewCommandCallback);
    registry.setRegistryChangeListener(registryChangeListener);
  });

  test("test basic add/remove elements", () => {

    registry.register("1", COMMAND1);

    expect(onNewCommandCallback.notifyNewCommand).toBeCalledWith(expect.objectContaining({
      id: "1",
      executable: COMMAND1
    }));

    registry.register("2", COMMAND2);
    expect(onNewCommandCallback.notifyNewCommand).toBeCalledWith(expect.objectContaining({
      id: "2",
      executable: COMMAND2
    }));

    registry.register("3", COMMAND3);
    expect(onNewCommandCallback.notifyNewCommand).toBeCalledWith(expect.objectContaining({
      id: "3",
      executable: COMMAND3
    }));

    registry.register("4", COMMAND4);
    expect(onNewCommandCallback.notifyNewCommand).toBeCalledWith(expect.objectContaining({
      id: "4",
      executable: COMMAND4
    }));

    expect(onNewCommandCallback.notifyNewCommand).toBeCalledTimes(4);
    expect(registryChangeListener).toBeCalledTimes(4);

    expect(registry.getCommands()).toHaveLength(4);
    expect(registry.getCommands()).toContain(COMMAND1);
    expect(registry.getCommands()).toContain(COMMAND2);
    expect(registry.getCommands()).toContain(COMMAND3);
    expect(registry.getCommands()).toContain(COMMAND4);

    expect(registry.isEmpty()).toBeFalsy();

    expect(registry.peek()).toBe(COMMAND4);
    expect(registry.getCommands()).toHaveLength(4);

    expect(registry.pop()).toBe(COMMAND4);
    expect(registryChangeListener).toBeCalledTimes(5);

    expect(registry.getCommands()).toHaveLength(3);
    expect(registry.getCommands()).toContain(COMMAND1);
    expect(registry.getCommands()).toContain(COMMAND2);
    expect(registry.getCommands()).toContain(COMMAND3);

    expect(registry.isEmpty()).toBeFalsy();

    expect(registry.pop()).toBe(COMMAND3);
    expect(registryChangeListener).toBeCalledTimes(6);

    expect(registry.getCommands()).toHaveLength(2);
    expect(registry.getCommands()).toContain(COMMAND1);
    expect(registry.getCommands()).toContain(COMMAND2);

    expect(registry.isEmpty()).toBeFalsy();

    registry.clear();
    expect(registryChangeListener).toBeCalledTimes(7);
    expect(registry.getCommands()).toHaveLength(0);
    expect(registry.isEmpty()).toBeTruthy();
  });

  test("test adding reaching max elements", () => {

    registry.setMaxSize(2);

    registry.register("1", COMMAND1);

    expect(onNewCommandCallback.notifyNewCommand).toBeCalledWith(expect.objectContaining({
      id: "1",
      executable: COMMAND1
    }));

    registry.register("2", COMMAND2);
    expect(onNewCommandCallback.notifyNewCommand).toBeCalledWith(expect.objectContaining({
      id: "2",
      executable: COMMAND2
    }));

    expect(onNewCommandCallback.notifyNewCommand).toBeCalledTimes(2);
    expect(registryChangeListener).toBeCalledTimes(2);

    expect(registry.getCommands()).toHaveLength(2);
    expect(registry.getCommands()).toContain(COMMAND1);
    expect(registry.getCommands()).toContain(COMMAND2);
    expect(registry.isEmpty()).toBeFalsy();

    registry.register("3", COMMAND3);

    expect(registryChangeListener).toBeCalledTimes(3);

    expect(registry.getCommands()).toHaveLength(2);
    expect(registry.getCommands()).toContain(COMMAND3);
    expect(registry.getCommands()).toContain(COMMAND2);
    expect(registry.isEmpty()).toBeFalsy();

    registry.register("4", COMMAND4);

    expect(registryChangeListener).toBeCalledTimes(4);

    expect(registry.getCommands()).toHaveLength(2);
    expect(registry.getCommands()).toContain(COMMAND3);
    expect(registry.getCommands()).toContain(COMMAND4);
    expect(registry.isEmpty()).toBeFalsy();


    /*
    * registry.setMaxSize(2);

        registry.register(COMMAND1);
        registry.register(COMMAND2);

        verify(changeListener, times(2)).notifyRegistryChange();
        assertFalse(registry.isEmpty());

        Assertions.assertThat(registry.getCommandHistory())
                .hasSize(2)
                .containsExactly(COMMAND2, COMMAND1);

        registry.register(COMMAND3);

        verify(changeListener, times(3)).notifyRegistryChange();
        assertFalse(registry.isEmpty());

        Assertions.assertThat(registry.getCommandHistory())
                .hasSize(2)
                .containsExactly(COMMAND3, COMMAND2);

        registry.register(COMMAND4);

        verify(changeListener, times(4)).notifyRegistryChange();
        assertFalse(registry.isEmpty());

        Assertions.assertThat(registry.getCommandHistory())
                .hasSize(2)
                .containsExactly(COMMAND4, COMMAND3);*/
  })
});

