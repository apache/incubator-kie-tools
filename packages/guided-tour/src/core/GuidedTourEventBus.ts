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

import { Rect, UserInteraction } from "@kogito-tooling/microeditor-envelope-protocol";
import { GuidedTourCookie } from "../core";

export type EventLabel = "GuidedTour.startTutorial" | "GuidedTour.userInteraction" | "GuidedTour.newPosition";

export class GuidedTourEventBus {
  private isEnabled = false;
  private queuedEvents: CustomEvent[] = [];
  private cookie = new GuidedTourCookie();

  public enableBus() {
    this.isEnabled = true;
    this.triggerQueuedEvents();
  }

  public onUserInteraction(userInteraction: UserInteraction) {
    const event = this.createEvent("GuidedTour.userInteraction", userInteraction);
    this.dispatchEvent(event);
  }

  public onPositionReceived(rect: Rect) {
    const event = this.createEvent("GuidedTour.newPosition", rect);
    this.dispatchEvent(event);
  }

  public startTutorial(tutorialLabel: string) {
    const event = this.createEvent("GuidedTour.startTutorial", tutorialLabel);
    this.dispatchEvent(event);
  }

  private triggerQueuedEvents() {
    while (this.queuedEvents.length > 0 && this.isBusEnabled()) {
      this.dispatchEvent(this.queuedEvents.shift()!);
    }
  }

  private createEvent<T>(eventLabel: EventLabel, detail: T) {
    return new CustomEvent(eventLabel, { detail });
  }

  private dispatchEvent(event: CustomEvent) {
    if (this.isBusEnabled()) {
      document.dispatchEvent(event);
    } else {
      this.queuedEvents.push(event);
    }
  }

  private isBusEnabled() {
    return this.isEnabled && !this.cookie.isDisabled();
  }
}
