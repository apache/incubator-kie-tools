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

import { KogitoGuidedTour, Tutorial, UserInteraction } from "..";

import * as React from "react";
import * as ReactDOM from "react-dom";
import { GuidedTour } from "../components";
import { GuidedTourCookie, GuidedTourDomUtils, GuidedTourEventBus } from "../core";

describe("KogitoGuidedTour", () => {
  beforeEach(() => jest.clearAllMocks());

  describe("setup", () => {
    it("renders the dialog when guided tour is enabled", () => {
      mockStatic(GuidedTourCookie.isDisabled, () => false);
      mockInstance(GuidedTourDomUtils, {
        getGuidedTourHTMLElement: () => document.createElement("div")
      });

      KogitoGuidedTour.setup();

      expect(ReactDOM.render).toBeCalledWith(<GuidedTour />, expect.any(HTMLElement), expect.any(Function));
    });

    it("does not render anything when it's not enabled", () => {
      mockStatic(GuidedTourCookie.isDisabled, () => true);

      KogitoGuidedTour.setup();

      expect(ReactDOM.render).not.toBeCalled();
    });
  });

  describe("teardown", () => {
    it("removes the dialog when guided tour is enabled", () => {
      const removeGuidedTourHTMLElement = jest.fn();

      mockStatic(GuidedTourCookie.isDisabled, () => false);
      mockInstance(GuidedTourDomUtils, {
        removeGuidedTourHTMLElement: removeGuidedTourHTMLElement
      });

      KogitoGuidedTour.teardown();

      expect(removeGuidedTourHTMLElement).toBeCalled();
      expect(GuidedTourCookie.markAsDisabled).toBeCalled();
    });

    it("does not do anything when it's not enabled", () => {
      const removeGuidedTourHTMLElement = jest.fn();

      mockStatic(GuidedTourCookie.isDisabled, () => true);
      mockInstance(GuidedTourDomUtils, {
        removeGuidedTourHTMLElement: removeGuidedTourHTMLElement
      });

      KogitoGuidedTour.teardown();

      expect(removeGuidedTourHTMLElement).not.toBeCalled();
      expect(GuidedTourCookie.markAsDisabled).not.toBeCalled();
    });
  });

  describe("isEnabled", () => {
    it("returns 'true' when 'GuidedTourCookie.isDisabled' retuns 'false'", () => {
      mockStatic(GuidedTourCookie.isDisabled, () => false);
      expect(KogitoGuidedTour.isEnabled()).toBeTruthy();
    });

    it("returns 'false' when 'GuidedTourCookie.isDisabled' retuns 'true'", () => {
      mockStatic(GuidedTourCookie.isDisabled, () => true);
      expect(KogitoGuidedTour.isEnabled()).toBeFalsy();
    });
  });

  describe("start", () => {
    it("triggers the bus", () => {
      const startTutorial = jest.fn();
      const tutorialLabel = "Tutorial 1";

      mockInstance(GuidedTourEventBus, { startTutorial: startTutorial });

      KogitoGuidedTour.start(tutorialLabel);

      expect(startTutorial).toBeCalledWith(tutorialLabel);
    });
  });

  describe("registerTutorial", () => {
    it("registers a tutorial", () => {
      const tutorial1 = new Tutorial("Tutorial 1", []);
      const tutorial2 = new Tutorial("Tutorial 2", []);

      KogitoGuidedTour.registerTutorial(tutorial1);
      KogitoGuidedTour.registerTutorial(tutorial2);

      expect(KogitoGuidedTour.getRegisteredTutorials()).toEqual([tutorial2, tutorial1]);
    });
  });

  describe("onUserInteraction", () => {
    it("triggers the bus", () => {
      const onUserInteraction = jest.fn();
      const userInteraction = new UserInteraction("CLICK", "Node");

      mockInstance(GuidedTourEventBus, { onUserInteraction: onUserInteraction });

      KogitoGuidedTour.onUserInteraction(userInteraction);

      expect(onUserInteraction).toBeCalledWith(userInteraction);
    });
  });

  describe("onPositionReceived", () => {
    it("triggers the bus", () => {
      const onPositionReceived = jest.fn();
      const rect = { bottom: 1, height: 1, left: 1, right: 1, top: 1, width: 1, x: 1, y: 1 };
      const parent = { bottom: 2, height: 2, left: 2, right: 2, top: 2, width: 2, x: 2, y: 2 };

      mockInstance(GuidedTourEventBus, { onPositionReceived: onPositionReceived });

      KogitoGuidedTour.onPositionReceived(rect, parent);

      expect(onPositionReceived).toBeCalledWith({
        bottom: 1,
        height: 1,
        left: 3,
        right: 1,
        top: 3,
        width: 1,
        x: 1,
        y: 1
      });
    });
  });

  describe("positionProvider", () => {
    it("registers position provider", () => {
      const positionProvider = jest.fn();
      const selector = "Node";

      KogitoGuidedTour.registerPositionProvider(positionProvider);
      KogitoGuidedTour.triggerPositionProvider(selector);

      expect(positionProvider).toBeCalledWith(selector);
    });
  });
});

jest.mock("../components");
jest.mock("../core");
jest.mock("react-dom", () => ({
  render: jest.fn()
}));

function mockStatic(obj: any, fn: any) {
  obj.mockImplementation(fn);
}

function mockInstance(obj: any, method: any) {
  const methodName = Object.keys(method)[0];
  const methodImpl = Object.values(method)[0];
  obj.prototype[methodName].mockImplementation(methodImpl);
}
