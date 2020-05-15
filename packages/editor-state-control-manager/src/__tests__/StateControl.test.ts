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

import { StateControl } from "../StateControl";

describe("StateControl", () => {
  let stateControl: StateControl;

  beforeEach(() => {
    stateControl = new StateControl();
  });

  describe("eventStack", () => {
    test("should be empty", () => {
      expect(stateControl.getEventStack()).toEqual([]);
    });

    test("should set new events on the stack", () => {
      const events = ["1", "2", "3", "4"];
      stateControl.setEventStack(events);
      expect(stateControl.getEventStack()).toEqual(events);
    });

    test("should set new events on the stack", () => {
      ["1", "2", "3", "4"].forEach((event, index, events) => {
        stateControl.updateEventStack(event);
        expect(stateControl.getEventStack()).toEqual(events.slice(0, index + 1));
      });
    });
  });

  describe("currentEvent", () => {
    test("should be undefined", () => {
      expect(stateControl.getCurrentEvent()).toBeUndefined();
    });

    test("should be the last added event on the stack", () => {
      ["1", "2", "3", "4"].forEach(event => {
        stateControl.updateEventStack(event);
        expect(stateControl.getCurrentEvent()).toEqual(event);
      });
    });
  });

  describe("savedEvent", () => {
    test("should be undefined", () => {
      expect(stateControl.getSavedEvent()).toBeUndefined();
      stateControl.setSavedEvent();
      expect(stateControl.getSavedEvent()).toBeUndefined();

      ["1", "2", "3", "4"].forEach(event => {
        stateControl.updateEventStack(event);
        expect(stateControl.getSavedEvent()).toBeUndefined();
      });
    });

    test("should be the last event added", () => {
      ["1", "2", "3", "4"].forEach(event => {
        stateControl.updateEventStack(event);
        stateControl.setSavedEvent();
        expect(stateControl.getSavedEvent()).toEqual(event);
      });
    });

    test("should be the saved event", () => {
      const specialEvent = "special";
      stateControl.updateEventStack(specialEvent);
      stateControl.setSavedEvent();

      ["1", "2", "3", "4"].forEach(event => {
        stateControl.updateEventStack(event);
        expect(stateControl.getSavedEvent()).toEqual(specialEvent);
      });
    });
  });

  describe("isDirty::StateControl", () => {
    test("should be false", () => {
      expect(stateControl.isDirty()).toBeFalsy();

      ["1", "2", "3", "4"].forEach(event => {
        stateControl.updateEventStack(event);
        stateControl.setSavedEvent();
        expect(stateControl.isDirty()).toBeFalsy();
      });
    });

    test("should be true", () => {
      ["1", "2", "3", "4"].forEach(event => {
        stateControl.updateEventStack(event);
        expect(stateControl.isDirty()).toBeTruthy();
      });
    });

    test("should be true", () => {
      const specialEvent = "special";
      stateControl.updateEventStack(specialEvent);
      stateControl.setSavedEvent();
      expect(stateControl.isDirty()).toBeFalsy();

      ["1", "2", "3", "4"].forEach(event => {
        stateControl.updateEventStack(event);
        expect(stateControl.isDirty()).toBeTruthy();
      });
      stateControl.setSavedEvent();
      expect(stateControl.isDirty()).toBeFalsy();
    });
  });

  describe("eraseRedoEvent::StateControl", () => {
    test("shouldn't erase", () => {
      stateControl.eraseRedoEvents();
      expect(stateControl.getEventStack()).toEqual([]);

      const events = ["1", "2", "3", "4"];
      events.forEach(event => stateControl.updateEventStack(event));

      stateControl.eraseRedoEvents();
      expect(stateControl.getEventStack()).toEqual(events);
    });

    test("should erase", () => {
      const events = ["1", "2", "3", "4"];
      events.forEach(event => stateControl.updateEventStack(event));
      stateControl.setCurrentEvent("2");

      stateControl.setEventStack(stateControl.eraseRedoEvents());
      expect(stateControl.getEventStack()).toEqual(["1", "2"]);
    });
  });

  describe("undoEvent::StateControl", () => {
    test("shouldn't undo an empty event stack", () => {
      stateControl.undoEvent();
      expect(stateControl.getCurrentEvent()).toBeUndefined();
    });

    test("should undo to previous event and mantain event stack", () => {
      const events = ["1", "2", "3", "4"];
      events.forEach(event => stateControl.updateEventStack(event));

      ["4", "3", "2", "1"].forEach(event => {
        expect(stateControl.getCurrentEvent()).toEqual(event);
        stateControl.undoEvent();
      });
      expect(stateControl.getCurrentEvent()).toBeUndefined();
      expect(stateControl.getEventStack()).toEqual(events);
    });
  });

  describe("redoEvent::StateControl", () => {
    test("shouldn't redo an empty event stack", () => {
      stateControl.redoEvent();
      expect(stateControl.getCurrentEvent()).toBeUndefined();
    });

    test("should redo to the next possible event on the event stack", () => {
      const events = ["1", "2", "3", "4"];
      events.forEach(event => stateControl.updateEventStack(event));

      events.forEach(event => stateControl.undoEvent());
      expect(stateControl.getCurrentEvent()).toBeUndefined();

      events.forEach(event => {
        stateControl.redoEvent();
        expect(stateControl.getCurrentEvent()).toEqual(event);
      });

      stateControl.redoEvent();
      expect(stateControl.getCurrentEvent()).toEqual(events.pop());
    });
  });

  describe("subscribe::StateControl", () => {
    let isDirty = false;
    const setIsDirty = (newState: boolean) => {
      isDirty = newState;
    };

    test("should update the state when a change occurs", () => {
      stateControl.subscribe(setIsDirty);
      expect(stateControl.isDirty()).toBeFalsy();

      stateControl.updateEventStack("1");
      expect(stateControl.isDirty()).toBeTruthy();
      expect(isDirty).toBeTruthy();

      stateControl.setSavedEvent();
      expect(stateControl.isDirty()).toBeFalsy();
      expect(isDirty).toBeFalsy();
    });
  });

  describe("unsubscribe::StateControl", () => {
    let isDirty = false;
    const setIsDirty = (newState: boolean) => {
      isDirty = newState;
    };

    test("should unsubscribe and make incosistent data", () => {
      stateControl.subscribe(setIsDirty);
      stateControl.updateEventStack("1");
      stateControl.unsubscribe(setIsDirty);
      stateControl.setSavedEvent();

      expect(stateControl.isDirty()).toBeFalsy();
      expect(isDirty).toBeTruthy();
    });
  });

  describe("complete workflow", () => {
    test("shouldn't redo an empty event stack", () => {
      stateControl.updateEventStack("1");
      stateControl.updateEventStack("2");
      expect(stateControl.isDirty()).toBeTruthy();
      stateControl.setSavedEvent();
      expect(stateControl.isDirty()).toBeFalsy();

      stateControl.undoEvent();
      expect(stateControl.isDirty()).toBeTruthy();
      stateControl.redoEvent();
      expect(stateControl.isDirty()).toBeFalsy();

      expect(stateControl.getEventStack()).toEqual(["1", "2"]);
      stateControl.undoEvent();
      expect(stateControl.getCurrentEvent()).toEqual("1");
      stateControl.updateEventStack("3");
      expect(stateControl.getEventStack()).toEqual(["1", "3"]);
      stateControl.redoEvent();
      expect(stateControl.getCurrentEvent()).toEqual("3");
    });
  });
});
