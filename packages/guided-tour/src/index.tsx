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
import * as ReactDOM from "react-dom";

import {
  AutoMode,
  BlockMode,
  DemoMode,
  Mode,
  Rect,
  Step,
  SubTutorialMode,
  Tutorial,
  UserInteraction,
  DEFAULT_RECT
} from "./api";
import { GuidedTour } from "./components";
import { GuidedTourDomUtils, GuidedTourEventBus, GuidedTourCookie } from "./core";

class KogitoGuidedTour {
  private static domUtils = new GuidedTourDomUtils();

  private static eventBus = new GuidedTourEventBus();

  private static registeredTutorials: Tutorial[] = [];

  private static positionProvider: (selector: string) => void;

  /**
   * Setup the Guided Tour component on a `HTMLElement` at the `document` level.
   * ---
   * Notice: When this method is called from a `React` app, is must be called
   * into a `React.useEffect` function or a `React.Component#componentDidMount`
   * function , e.g.:
   * ---
   * ```typescript
   * useEffect(() => {
   *    KogitoGuidedTour.setup();
   *    return () => KogitoGuidedTour.teardown();
   * }, []);
   * ```
   */
  public static setup() {
    if (GuidedTourCookie.isDisabled()) {
      return;
    }
    ReactDOM.render(<GuidedTour />, this.domUtils.getGuidedTourHTMLElement(), () => {
      this.eventBus.enableBus();
    });
  }

  /**
   * Teardown the Guided Decision tour component, by removing the `HTMLElement`
   * created by the `setup` method.
   */
  public static teardown() {
    if (GuidedTourCookie.isDisabled()) {
      return;
    }
    this.domUtils.removeGuidedTourHTMLElement();
    GuidedTourCookie.markAsDisabled();
  }

  /**
   * Check if Guided Tour is enabled. When users already dismissed the Guided
   * Tour, it returns false.
   */
  public static isEnabled() {
    return !GuidedTourCookie.isDisabled();
  }

  /**
   * Start a tutorial.
   */
  public static start(tutorialLabel: string) {
    this.eventBus.startTutorial(tutorialLabel);
  }

  /**
   * Register a tutorial.
   */
  public static registerTutorial(tutorial: Tutorial) {
    this.registeredTutorials = [tutorial, ...this.registeredTutorials];
  }

  /**
   * Get registered tutorials.
   */
  public static getRegisteredTutorials() {
    return this.registeredTutorials;
  }

  /**
   * Notifies a new user interaction happens.
   */
  public static onUserInteraction(userInteraction: UserInteraction) {
    this.eventBus.onUserInteraction(userInteraction);
  }

  /**
   * Receives a position.
   */
  public static onPositionReceived(rect: Rect, parentRect?: Rect) {
    if (parentRect) {
      rect.left += parentRect.left;
      rect.top += parentRect.y;
    }
    this.eventBus.onPositionReceived(rect);
  }

  /**
   * Triggers a request for a position with a custom selector.
   */
  public static triggerPositionProvider(selector: string) {
    this.positionProvider(selector);
  }

  /**
   * Register a custom position provider, which is used when a custom selector
   * is passed to a step.
   */
  public static registerPositionProvider(positionProvider: (selector: string) => void): void {
    this.positionProvider = positionProvider;
  }
}

export {
  KogitoGuidedTour,
  UserInteraction,
  Mode,
  BlockMode,
  AutoMode,
  DemoMode,
  SubTutorialMode,
  Rect,
  Step,
  Tutorial,
  DEFAULT_RECT
};
