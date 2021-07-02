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

import { Rect, Tutorial, UserInteraction } from "./api";
import { GuidedTour } from "./components";
import { GuidedTourCookie, GuidedTourDomUtils, GuidedTourEventBus } from "./core";

class KogitoGuidedTour {
  private static instance?: KogitoGuidedTour;

  private domUtils = new GuidedTourDomUtils();

  private eventBus = new GuidedTourEventBus();

  private cookie = new GuidedTourCookie();

  private registeredTutorials: Tutorial[] = [];

  private positionProvider: (selector: string) => void;

  private constructor() {}

  /**
   * Setup the Guided Tour component on a `HTMLElement` at the `document` level.
   * ---
   * Notice: When this method is called from a `React` app, is must be called
   * into a `React.useEffect` function or a `React.Component#componentDidMount`
   * function , e.g.:
   * ---
   * ```typescript
   * useEffect(() => {
   *    KogitoGuidedTour.getInstance().setup();
   *    return () => KogitoGuidedTour.getInstance().teardown();
   * }, []);
   * ```
   */
  public setup() {
    if (this.cookie.isDisabled()) {
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
  public teardown() {
    if (this.cookie.isDisabled()) {
      return;
    }
    this.domUtils.removeGuidedTourHTMLElement();
    this.cookie.markAsDisabled();
  }

  /**
   * Check if Guided Tour is enabled. When users already dismissed the Guided
   * Tour, it returns false.
   */
  public isEnabled() {
    return !this.cookie.isDisabled();
  }

  /**
   * Start a tutorial.
   */
  public start(tutorialLabel: string) {
    this.eventBus.startTutorial(tutorialLabel);
  }

  /**
   * Register a tutorial.
   */
  public registerTutorial(tutorial: Tutorial) {
    this.registeredTutorials = [tutorial, ...this.registeredTutorials];
  }

  /**
   * Get registered tutorials.
   */
  public getRegisteredTutorials() {
    return this.registeredTutorials;
  }

  /**
   * Notifies a new user interaction happens.
   */
  public onUserInteraction(userInteraction: UserInteraction) {
    this.eventBus.onUserInteraction(userInteraction);
  }

  /**
   * Receives a position.
   */
  public onPositionReceived(rect: Rect, parentRect?: Rect) {
    if (parentRect) {
      rect.left += parentRect.left;
      rect.top += parentRect.y;
    }
    this.eventBus.onPositionReceived(rect);
  }

  /**
   * Triggers a request for a position with a custom selector.
   */
  public triggerPositionProvider(selector: string) {
    this.positionProvider(selector);
  }

  /**
   * Register a custom position provider, which is used when a custom selector
   * is passed to a step.
   */
  public registerPositionProvider(positionProvider: (selector: string) => void): void {
    this.positionProvider = positionProvider;
  }
  /**
   * Returns the 'KogitoGuidedTour' instance.
   */
  public static getInstance() {
    if (!this.instance) {
      this.instance = new KogitoGuidedTour();
    }
    return this.instance;
  }
}

export { KogitoGuidedTour };
