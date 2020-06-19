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

import { renderHook } from "@testing-library/react-hooks";
import { useDirtyState } from "../../stateControl/Hooks";
import { StateControl } from "../../stateControl/StateControl";
import { act } from "react-test-renderer";

describe("useEditorDirtyState", () => {
  let editorStateControl: StateControl;
  beforeEach(() => (editorStateControl = new StateControl()));

  describe("false", () => {
    test("after initialization", () => {
      const { result } = renderHook(() => useDirtyState(editorStateControl));

      expect(result.current).toBeFalsy();
    });

    test("redo without any command to be redone", () => {
      const { result } = renderHook(() => useDirtyState(editorStateControl));

      act(() => {
        editorStateControl.redo();
      });

      expect(result.current).toBeFalsy();
    });

    test("add command and save it", () => {
      const { result } = renderHook(() => useDirtyState(editorStateControl));

      act(() => {
        editorStateControl.updateCommandStack("1");
        editorStateControl.setSavedCommand();
      });

      expect(result.current).toBeFalsy();
    });

    test("add command and undo it", () => {
      const { result } = renderHook(() => useDirtyState(editorStateControl));

      act(() => {
        editorStateControl.updateCommandStack("1");
        editorStateControl.undo();
      });

      expect(result.current).toBeFalsy();
    });

    test("add command, save it, undo and redo", () => {
      const { result } = renderHook(() => useDirtyState(editorStateControl));

      act(() => {
        editorStateControl.updateCommandStack("1");
        editorStateControl.setSavedCommand();
        editorStateControl.undo();
        editorStateControl.redo();
      });

      expect(result.current).toBeFalsy();
    });
  });

  describe("true", () => {
    test("add command without saving", () => {
      const { result } = renderHook(() => useDirtyState(editorStateControl));

      act(() => {
        editorStateControl.updateCommandStack("1");
      });

      expect(result.current).toBeTruthy();
    });

    test("add command, undo and redo", () => {
      const { result } = renderHook(() => useDirtyState(editorStateControl));

      act(() => {
        editorStateControl.updateCommandStack("1");
        editorStateControl.undo();
        editorStateControl.redo();
      });

      expect(result.current).toBeTruthy();
    });

    test("add command, save it, undo and new command", () => {
      const { result } = renderHook(() => useDirtyState(editorStateControl));

      act(() => {
        editorStateControl.updateCommandStack("1");
        editorStateControl.setSavedCommand();
        editorStateControl.undo();
        editorStateControl.updateCommandStack("2");
      });

      expect(result.current).toBeTruthy();
    });
  });
});
