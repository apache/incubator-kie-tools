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

import { GuidedTourEventBus } from "../../core";
import { act } from "react-dom/test-utils";
import { Rect, UserInteraction } from "@kogito-tooling/editor-envelope-protocol";

describe("GuidedTourEventBus", () => {
  beforeEach(() => {
    dispatchedEvents = [];
    document.dispatchEvent = mockedDispatchEvent;
  });

  afterEach(() => {
    document.dispatchEvent = realDispatchEvent;
  });

  describe("startTutorial", () => {
    it("dispatches events when bus is enabled", () => {
      const eventBus = new GuidedTourEventBus();

      act(() => {
        eventBus.startTutorial("Tutorial 1");
        eventBus.startTutorial("Tutorial 2");
        eventBus.enableBus();
      });

      expect(dispatchedEvents.length).toBe(2);
      expect(dispatchedEvents[0].detail).toBe("Tutorial 1");
      expect(dispatchedEvents[1].detail).toBe("Tutorial 2");
    });

    it("does not dispatch any event when bus is not enabled", () => {
      const eventBus = new GuidedTourEventBus();

      act(() => {
        eventBus.startTutorial("Tutorial 1");
        eventBus.startTutorial("Tutorial 2");
      });

      expect(dispatchedEvents.length).toBe(0);
    });
  });

  describe("onPositionReceived", () => {
    const rect1: Rect = { bottom: 1, height: 1, left: 1, right: 1, top: 1, width: 1, x: 1, y: 1 };
    const rect2: Rect = { bottom: 2, height: 2, left: 2, right: 2, top: 2, width: 2, x: 2, y: 2 };
    const rect3: Rect = { bottom: 3, height: 3, left: 3, right: 3, top: 3, width: 3, x: 3, y: 3 };

    it("dispatches events when bus is enabled", () => {
      const eventBus = new GuidedTourEventBus();

      act(() => {
        eventBus.onPositionReceived(rect1);
        eventBus.onPositionReceived(rect2);
        eventBus.onPositionReceived(rect3);
        eventBus.enableBus();
      });

      expect(dispatchedEvents.length).toBe(3);
      expect(dispatchedEvents[0].detail).toBe(rect1);
      expect(dispatchedEvents[1].detail).toBe(rect2);
      expect(dispatchedEvents[2].detail).toBe(rect3);
    });

    it("does not dispatch any event when bus is not enabled", () => {
      const eventBus = new GuidedTourEventBus();

      act(() => {
        eventBus.onPositionReceived(rect1);
        eventBus.onPositionReceived(rect2);
        eventBus.onPositionReceived(rect3);
      });

      expect(dispatchedEvents.length).toBe(0);
    });
  });

  describe("onUserInteraction", () => {
    const userInteraction1: UserInteraction = { action: "CREATED", target: "Node1" };
    const userInteraction2: UserInteraction = { action: "UPDATED", target: "Node2" };
    const userInteraction3: UserInteraction = { action: "REMOVED", target: "Node3" };
    const userInteraction4: UserInteraction = { action: "CLICKED", target: "Node4" };

    it("dispatches events when bus is enabled", () => {
      const eventBus = new GuidedTourEventBus();

      act(() => {
        eventBus.onUserInteraction(userInteraction1);
        eventBus.onUserInteraction(userInteraction2);
        eventBus.onUserInteraction(userInteraction3);
        eventBus.onUserInteraction(userInteraction4);
        eventBus.enableBus();
      });

      expect(dispatchedEvents.length).toBe(4);
      expect(dispatchedEvents[0].detail).toBe(userInteraction1);
      expect(dispatchedEvents[1].detail).toBe(userInteraction2);
      expect(dispatchedEvents[2].detail).toBe(userInteraction3);
      expect(dispatchedEvents[3].detail).toBe(userInteraction4);
    });

    it("does not dispatch any event when bus is not enabled", () => {
      const eventBus = new GuidedTourEventBus();

      act(() => {
        eventBus.onUserInteraction(userInteraction1);
        eventBus.onUserInteraction(userInteraction2);
        eventBus.onUserInteraction(userInteraction3);
        eventBus.onUserInteraction(userInteraction4);
      });

      expect(dispatchedEvents.length).toBe(0);
    });
  });
});

let dispatchedEvents: CustomEvent[] = [];

const realDispatchEvent = document.dispatchEvent;
const mockedDispatchEvent = jest.fn(event => {
  dispatchedEvents = [...dispatchedEvents, event];
  return true;
});
