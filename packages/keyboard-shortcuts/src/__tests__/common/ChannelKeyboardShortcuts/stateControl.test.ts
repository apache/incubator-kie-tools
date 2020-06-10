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

import { redoShortcut, undoShortcut } from "../../../common/ChannelKeyboardShortcuts";

describe("redoShortcut", () => {
  test("should return values correctly", () => {
    const redo = redoShortcut();
    expect(redo.combination).toEqual("shift+ctrl+z");
    expect(redo.label).toEqual("Edit | Redo last edit");
  });
});

describe("undoShortcut", () => {
  test("should return values correctly", () => {
    const undo = undoShortcut();
    expect(undo.combination).toEqual("ctrl+z");
    expect(undo.label).toEqual("Edit | Undo last edit");
  });
});
