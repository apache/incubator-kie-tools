/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import { useEffect, useState } from "react";

type Event = undefined | string;

export function useEditorDirtyState(stateControl: StateControl) {
  const [isDirty, setIsDirty] = useState(false);

  useEffect(() => {
    const callback = stateControl.subscribe(setIsDirty);
    return () => {
      stateControl.unsubscribe(callback);
    };
  }, []);

  return isDirty;
}

export class StateControl {
  private eventStack: string[];
  private currentEvent: Event;
  private savedEvent: Event;
  public registeredCallbacks: Array<(isDirty: boolean) => void>;

  constructor() {
    this.eventStack = [];
    this.registeredCallbacks = [];
  }

  public subscribe(callback: (isDirty: boolean) => void) {
    this.registeredCallbacks.push(callback);
    return callback;
  }

  public unsubscribe(callback: (isDirty: boolean) => void) {
    const index = this.registeredCallbacks.indexOf(callback);
    if (index > -1) {
      this.registeredCallbacks.splice(index, 1);
    }
  }

  public setSavedEvent() {
    this.savedEvent = this.currentEvent;
    this.registeredCallbacks.forEach(setIsDirty => setIsDirty(this.isDirty()));
  }

  public getCurrentEvent() {
    return this.currentEvent;
  }

  public setCurrentEvent(event: Event) {
    this.currentEvent = event;
    this.registeredCallbacks.forEach(setIsDirty => setIsDirty(this.isDirty()));
  }

  public getEventStack() {
    return this.eventStack;
  }

  public setEventStack(events: string[]) {
    this.eventStack = events;
  }

  public isDirty() {
    return this.currentEvent !== this.savedEvent;
  }

  public undoEdit() {
    const indexOfCurrentEvent = this.eventStack.indexOf(this.currentEvent!);

    let eventUndone: Event;
    if (this.eventStack[indexOfCurrentEvent - 1]) {
      eventUndone = this.eventStack[indexOfCurrentEvent - 1];
    }
    this.setCurrentEvent(eventUndone);
  }

  public redoEdit() {
    const indexOfCurrentEvent = this.eventStack.indexOf(this.currentEvent!);
    if (this.eventStack[indexOfCurrentEvent + 1]) {
      const eventRedone = this.eventStack[indexOfCurrentEvent + 1];
      this.setCurrentEvent(eventRedone);
    }
  }

  public eraseRedoEvents() {
    return this.eventStack.slice(0, this.eventStack.indexOf(this.currentEvent!) + 1);
  }

  public updateEventStack(event: string) {
    console.log(event)
    const updatedEvents = this.eraseRedoEvents().concat(event);
    this.setEventStack(updatedEvents);
    this.setCurrentEvent(event);
  }
}
