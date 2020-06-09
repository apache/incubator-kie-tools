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
import { useDirtyState } from "../../stateControl/DirtyState";
import { StateControl } from "../../stateControl/StateControl";
import { act } from "react-test-renderer";

describe("useEditorDirtyState", () => {
  let editorStateControl: StateControl;
  beforeEach(() => (editorStateControl = new StateControl()));

  describe("false", () => {
    it("after initialization", () => {
      const { result } = renderHook(() => useDirtyState(editorStateControl));

      expect(result.current).toBeFalsy();
    });

    it("redo without any event to be redone", () => {
      const { result } = renderHook(() => useDirtyState(editorStateControl));

      act(() => {
        editorStateControl.redoEvent();
      });

      expect(result.current).toBeFalsy();
    });

    it("add event and save it", () => {
      const { result } = renderHook(() => useDirtyState(editorStateControl));

      act(() => {
        editorStateControl.updateEventStack("1");
        editorStateControl.setSavedEvent();
      });

      expect(result.current).toBeFalsy();
    });

    it("add event and undo it", () => {
      const { result } = renderHook(() => useDirtyState(editorStateControl));

      act(() => {
        editorStateControl.updateEventStack("1");
        editorStateControl.undoEvent();
      });

      expect(result.current).toBeFalsy();
    });

    it("add event, save it, undo and redo", () => {
      const { result } = renderHook(() => useDirtyState(editorStateControl));

      act(() => {
        editorStateControl.updateEventStack("1");
        editorStateControl.setSavedEvent();
        editorStateControl.undoEvent();
        editorStateControl.redoEvent();
      });

      expect(result.current).toBeFalsy();
    });
  });

  describe("true", () => {
    it("add event without saving", () => {
      const { result } = renderHook(() => useDirtyState(editorStateControl));

      act(() => {
        editorStateControl.updateEventStack("1");
      });

      expect(result.current).toBeTruthy();
    });

    it("add event, undo and redo", () => {
      const { result } = renderHook(() => useDirtyState(editorStateControl));

      act(() => {
        editorStateControl.updateEventStack("1");
        editorStateControl.undoEvent();
        editorStateControl.redoEvent();
      });

      expect(result.current).toBeTruthy();
    });

    it("add event, save it, undo and new event", () => {
      const { result } = renderHook(() => useDirtyState(editorStateControl));

      act(() => {
        editorStateControl.updateEventStack("1");
        editorStateControl.setSavedEvent();
        editorStateControl.undoEvent();
        editorStateControl.updateEventStack("2");
      });

      expect(result.current).toBeTruthy();
    });
  });
});
