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

import * as React from "react";
import { KeyBindingsHelpOverlay } from "../../KeyBindingsHelpOverlay";
import { DefaultKeyboardShortcutsService } from "@kogito-tooling/keyboard-shortcuts";
import { ChannelType, OperatingSystem } from "@kogito-tooling/microeditor-envelope-protocol";
import { fireEvent, render } from "@testing-library/react";
import { DEFAULT_TESTING_ENVELOPE_CONTEXT, usingEnvelopeApi } from "../envelopeApiUtils";

describe("KeyBindingsHelpOverlay", () => {
  test("minimal setup", async () => {
    const context = { operatingSystem: OperatingSystem.WINDOWS, channel: ChannelType.DESKTOP };
    const keyboardShortcutsService = new DefaultKeyboardShortcutsService({ editorContext: context });
    keyboardShortcutsService.registerKeyPress("ctrl+c", "Copy", () => Promise.resolve(), {});

    const component = render(
      usingEnvelopeApi(<KeyBindingsHelpOverlay />, {
        context: context,
        services: {
          ...DEFAULT_TESTING_ENVELOPE_CONTEXT.services,
          keyboardShortcuts: keyboardShortcutsService,
        }
      }).wrapper
    );

    fireEvent.click(component.getByTestId("keyboard-shortcuts-help-overlay-icon"));

    await component.findByTestId("keyboard-shortcuts-help-overlay");
    await component.findByText("Copy");

    expect(component.baseElement).toMatchSnapshot();
  });
});
