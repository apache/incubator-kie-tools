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

import { KogitoGuidedTour } from "..";
import { Tutorial, UserInteraction } from "../api";

import * as React from "react";
import * as ReactDOM from "react-dom";
import { GuidedTour } from "../components";
import { GuidedTourCookie, GuidedTourDomUtils, GuidedTourEventBus } from "../core";

describe("KogitoGuidedTour", () => {
  beforeEach(() => jest.clearAllMocks());

  describe("setup", () => {
    it("renders the dialog when guided tour is enabled", () => {
      mockInstance(GuidedTourCookie, { isDisabled: () => false });
      mockInstance(GuidedTourDomUtils, { getGuidedTourHTMLElement: () => document.createElement("div") });

      KogitoGuidedTour.getInstance().setup();

      expect(ReactDOM.render).toBeCalledWith(<GuidedTour />, expect.any(HTMLElement), expect.any(Function));
    });

    it("does not render anything when it's not enabled", () => {
      mockInstance(GuidedTourCookie, { isDisabled: () => true });

      KogitoGuidedTour.getInstance().setup();

      expect(ReactDOM.render).not.toBeCalled();
    });
  });

  describe("teardown", () => {
    it("removes the dialog when guided tour is enabled", () => {
      const removeGuidedTourHTMLElement = jest.fn();
      const markAsDisabled = jest.fn();

      mockInstance(GuidedTourCookie, {
        isDisabled: () => false,
        markAsDisabled: markAsDisabled
      });
      mockInstance(GuidedTourDomUtils, {
        removeGuidedTourHTMLElement: removeGuidedTourHTMLElement
      });

      KogitoGuidedTour.getInstance().teardown();

      expect(removeGuidedTourHTMLElement).toBeCalled();
      expect(markAsDisabled).toBeCalled();
    });

    it("does not do anything when it's not enabled", () => {
      const removeGuidedTourHTMLElement = jest.fn();
      const markAsDisabled = jest.fn();

      mockInstance(GuidedTourCookie, {
        isDisabled: () => true,
        markAsDisabled: markAsDisabled
      });
      mockInstance(GuidedTourDomUtils, {
        removeGuidedTourHTMLElement: removeGuidedTourHTMLElement
      });

      KogitoGuidedTour.getInstance().teardown();

      expect(removeGuidedTourHTMLElement).not.toBeCalled();
      expect(markAsDisabled).not.toBeCalled();
    });
  });

  describe("isEnabled", () => {
    it("returns 'true' when 'GuidedTourCookie.isDisabled' retuns 'false'", () => {
      mockInstance(GuidedTourCookie, { isDisabled: () => false });
      expect(KogitoGuidedTour.getInstance().isEnabled()).toBeTruthy();
    });

    it("returns 'false' when 'GuidedTourCookie.isDisabled' retuns 'true'", () => {
      mockInstance(GuidedTourCookie, { isDisabled: () => true });
      expect(KogitoGuidedTour.getInstance().isEnabled()).toBeFalsy();
    });
  });

  describe("start", () => {
    it("triggers the bus", () => {
      const startTutorial = jest.fn();
      const tutorialLabel = "Tutorial 1";

      mockInstance(GuidedTourEventBus, { startTutorial: startTutorial });

      KogitoGuidedTour.getInstance().start(tutorialLabel);

      expect(startTutorial).toBeCalledWith(tutorialLabel);
    });
  });

  describe("registerTutorial", () => {
    it("registers a tutorial", () => {
      const guidedTour = KogitoGuidedTour.getInstance();
      const tutorial1 = new Tutorial("Tutorial 1", []);
      const tutorial2 = new Tutorial("Tutorial 2", []);

      guidedTour.registerTutorial(tutorial1);
      guidedTour.registerTutorial(tutorial2);

      expect(guidedTour.getRegisteredTutorials()).toEqual([tutorial2, tutorial1]);
    });
  });

  describe("onUserInteraction", () => {
    it("triggers the bus", () => {
      const onUserInteraction = jest.fn();
      const userInteraction = new UserInteraction("CLICK", "Node");

      mockInstance(GuidedTourEventBus, { onUserInteraction: onUserInteraction });

      KogitoGuidedTour.getInstance().onUserInteraction(userInteraction);

      expect(onUserInteraction).toBeCalledWith(userInteraction);
    });
  });

  describe("onPositionReceived", () => {
    it("triggers the bus", () => {
      const onPositionReceived = jest.fn();
      const rect = { bottom: 1, height: 1, left: 1, right: 1, top: 1, width: 1, x: 1, y: 1 };
      const parent = { bottom: 2, height: 2, left: 2, right: 2, top: 2, width: 2, x: 2, y: 2 };

      mockInstance(GuidedTourEventBus, { onPositionReceived: onPositionReceived });

      KogitoGuidedTour.getInstance().onPositionReceived(rect, parent);

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
      const guidedTour = KogitoGuidedTour.getInstance();
      const positionProvider = jest.fn();
      const selector = "Node";

      guidedTour.registerPositionProvider(positionProvider);
      guidedTour.triggerPositionProvider(selector);

      expect(positionProvider).toBeCalledWith(selector);
    });
  });
});

jest.mock("../components");
jest.mock("../core");
jest.mock("react-dom", () => ({
  render: jest.fn()
}));

function mockInstance(obj: any, methods: any) {
  const methodNames = Object.keys(methods);
  methodNames.forEach(methodName => {
    const methodImpl = methods[methodName];
    obj.prototype[methodName].mockImplementation(methodImpl);
  });
}
