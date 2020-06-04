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

import { EditorStateControl } from "../../stateControl/EditorStateControl";

describe("EditorStateControl", () => {
  let editorStateControl: EditorStateControl;

  beforeEach(() => {
    editorStateControl = new EditorStateControl();
  });

  describe("eventStack", () => {
    test("should be empty", () => {
      expect(editorStateControl.getEventStack()).toEqual([]);
    });

    test("should set new events on the stack", () => {
      const events = ["1", "2", "3", "4"];
      editorStateControl.setEventStack(events);
      expect(editorStateControl.getEventStack()).toEqual(events);
    });

    test("should set new events on the stack", () => {
      ["1", "2", "3", "4"].forEach((event, index, events) => {
        editorStateControl.updateEventStack(event);
        expect(editorStateControl.getEventStack()).toEqual(events.slice(0, index + 1));
      });
    });
  });

  describe("currentEvent", () => {
    test("should be undefined", () => {
      expect(editorStateControl.getCurrentEvent()).toBeUndefined();
    });

    test("should be the last added event on the stack", () => {
      ["1", "2", "3", "4"].forEach(event => {
        editorStateControl.updateEventStack(event);
        expect(editorStateControl.getCurrentEvent()).toEqual(event);
      });
    });
  });

  describe("savedEvent", () => {
    test("should be undefined", () => {
      expect(editorStateControl.getSavedEvent()).toBeUndefined();
      editorStateControl.setSavedEvent();
      expect(editorStateControl.getSavedEvent()).toBeUndefined();

      ["1", "2", "3", "4"].forEach(event => {
        editorStateControl.updateEventStack(event);
        expect(editorStateControl.getSavedEvent()).toBeUndefined();
      });
    });

    test("should be the last event added", () => {
      ["1", "2", "3", "4"].forEach(event => {
        editorStateControl.updateEventStack(event);
        editorStateControl.setSavedEvent();
        expect(editorStateControl.getSavedEvent()).toEqual(event);
      });
    });

    test("should be the saved event", () => {
      const specialEvent = "special";
      editorStateControl.updateEventStack(specialEvent);
      editorStateControl.setSavedEvent();

      ["1", "2", "3", "4"].forEach(event => {
        editorStateControl.updateEventStack(event);
        expect(editorStateControl.getSavedEvent()).toEqual(specialEvent);
      });
    });
  });

  describe("isDirty::EditorStateControl", () => {
    test("should be false", () => {
      expect(editorStateControl.isDirty()).toBeFalsy();

      ["1", "2", "3", "4"].forEach(event => {
        editorStateControl.updateEventStack(event);
        editorStateControl.setSavedEvent();
        expect(editorStateControl.isDirty()).toBeFalsy();
      });
    });

    test("should be true", () => {
      ["1", "2", "3", "4"].forEach(event => {
        editorStateControl.updateEventStack(event);
        expect(editorStateControl.isDirty()).toBeTruthy();
      });
    });

    test("should be true", () => {
      const specialEvent = "special";
      editorStateControl.updateEventStack(specialEvent);
      editorStateControl.setSavedEvent();
      expect(editorStateControl.isDirty()).toBeFalsy();

      ["1", "2", "3", "4"].forEach(event => {
        editorStateControl.updateEventStack(event);
        expect(editorStateControl.isDirty()).toBeTruthy();
      });
      editorStateControl.setSavedEvent();
      expect(editorStateControl.isDirty()).toBeFalsy();
    });
  });

  describe("updateEventStack::EditorStateControl", () => {
    test("shouldn't erase events", () => {
      editorStateControl.updateEventStack("1");
      expect(editorStateControl.getEventStack()).toEqual(["1"]);

      const events = ["1", "2", "3", "4"];
      events.forEach(event => editorStateControl.updateEventStack(event));

      expect(editorStateControl.getEventStack()).toEqual(events);
    });

    test("should erase", () => {
      const events = ["1", "2", "3", "4"];
      events.forEach(event => editorStateControl.updateEventStack(event));
      editorStateControl.setCurrentEvent("2");
      editorStateControl.updateEventStack("5");

      expect(editorStateControl.getEventStack()).toEqual(["1", "2", "5"]);
    });
  });

  describe("undoEvent::EditorStateControl", () => {
    test("shouldn't undo an empty event stack", () => {
      editorStateControl.undoEvent();
      expect(editorStateControl.getCurrentEvent()).toBeUndefined();
    });

    test("should undo to previous event and maintain event stack", () => {
      const events = ["1", "2", "3", "4"];
      events.forEach(event => editorStateControl.updateEventStack(event));

      ["4", "3", "2", "1"].forEach(event => {
        expect(editorStateControl.getCurrentEvent()).toEqual(event);
        editorStateControl.undoEvent();
      });
      expect(editorStateControl.getCurrentEvent()).toBeUndefined();
      expect(editorStateControl.getEventStack()).toEqual(events);
    });
  });

  describe("redoEvent::EditorStateControl", () => {
    test("shouldn't redo an empty event stack", () => {
      editorStateControl.redoEvent();
      expect(editorStateControl.getCurrentEvent()).toBeUndefined();
    });

    test("should redo to the next possible event on the event stack", () => {
      const events = ["1", "2", "3", "4"];
      events.forEach(event => editorStateControl.updateEventStack(event));

      events.forEach(event => editorStateControl.undoEvent());
      expect(editorStateControl.getCurrentEvent()).toBeUndefined();

      events.forEach(event => {
        editorStateControl.redoEvent();
        expect(editorStateControl.getCurrentEvent()).toEqual(event);
      });

      editorStateControl.redoEvent();
      expect(editorStateControl.getCurrentEvent()).toEqual(events.pop());
    });
  });

  describe("subscribe::EditorStateControl", () => {
    let isDirty = false;
    const setIsDirty = (newState: boolean) => {
      isDirty = newState;
    };

    test("should update the state when a change occurs", () => {
      editorStateControl.subscribe(setIsDirty);
      expect(editorStateControl.isDirty()).toBeFalsy();

      editorStateControl.updateEventStack("1");
      expect(editorStateControl.isDirty()).toBeTruthy();
      expect(isDirty).toBeTruthy();

      editorStateControl.setSavedEvent();
      expect(editorStateControl.isDirty()).toBeFalsy();
      expect(isDirty).toBeFalsy();
    });
  });

  describe("unsubscribe::EditorStateControl", () => {
    let isDirty = false;
    const setIsDirty = (newState: boolean) => {
      isDirty = newState;
    };

    test("should unsubscribe and make incosistent data", () => {
      editorStateControl.subscribe(setIsDirty);
      editorStateControl.updateEventStack("1");
      editorStateControl.unsubscribe(setIsDirty);
      editorStateControl.setSavedEvent();

      expect(editorStateControl.isDirty()).toBeFalsy();
      expect(isDirty).toBeTruthy();
    });
  });

  describe("complete workflow", () => {
    test("shouldn't redo an empty event stack", () => {
      editorStateControl.updateEventStack("1");
      editorStateControl.updateEventStack("2");
      expect(editorStateControl.isDirty()).toBeTruthy();
      editorStateControl.setSavedEvent();
      expect(editorStateControl.isDirty()).toBeFalsy();

      editorStateControl.undoEvent();
      expect(editorStateControl.isDirty()).toBeTruthy();
      editorStateControl.redoEvent();
      expect(editorStateControl.isDirty()).toBeFalsy();

      expect(editorStateControl.getEventStack()).toEqual(["1", "2"]);
      editorStateControl.undoEvent();
      expect(editorStateControl.getCurrentEvent()).toEqual("1");
      editorStateControl.updateEventStack("3");
      expect(editorStateControl.getEventStack()).toEqual(["1", "3"]);
      editorStateControl.redoEvent();
      expect(editorStateControl.getCurrentEvent()).toEqual("3");
    });
  });
});
