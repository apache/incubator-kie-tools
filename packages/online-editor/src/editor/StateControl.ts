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

export function useStateControl(stateControl: StateControl) {
  const [isDirty, setIsDirty] = useState(false);

  useEffect(() => {
    stateControl.subscribe(setIsDirty);
    return () => {
      stateControl.unsubscribe();
    };
  }, []);

  return isDirty;
}

export class StateControl {
  private events: string[];
  private currentEvent: Event;
  private savedEvent: Event;
  public registeredCallbacks: undefined | ((isDirty: boolean) => void);

  constructor() {
    this.events = [];
  }

  public setSavedEvent(event: Event) {
    this.savedEvent = event;
    if (this.registeredCallbacks) {
      this.registeredCallbacks(this.isDirty());
    }
  }

  public getCurrentEvent() {
    return this.currentEvent;
  }

  public setCurrentEvent(event: Event) {
    this.currentEvent = event;
    if (this.registeredCallbacks) {
      this.registeredCallbacks(this.isDirty());
    }
  }

  public subscribe(callback: (isDirty: boolean) => void) {
    this.registeredCallbacks = callback;
  }

  public unsubscribe() {
    this.registeredCallbacks = undefined;
  }

  public getEvents() {
    return this.events;
  }

  public setEvents(events: string[]) {
    this.events = events;
  }

  public eraseRedoEvents() {
    return this.events.slice(0, this.events.indexOf(this.currentEvent!) + 1);
  }

  public isDirty() {
    return this.currentEvent !== this.savedEvent;
  }

  public undoEdit() {
    const indexOfCurrentEvent = this.events.indexOf(this.currentEvent!);

    let eventUndone: Event;
    if (this.events[indexOfCurrentEvent - 1]) {
      eventUndone = this.events[indexOfCurrentEvent - 1];
    }
    this.setCurrentEvent(eventUndone);
  }

  public redoEdit() {
    const indexOfCurrentEvent = this.events.indexOf(this.currentEvent!);
    if (this.events[indexOfCurrentEvent + 1]) {
      const eventRedone = this.events[indexOfCurrentEvent + 1];
      this.setCurrentEvent(eventRedone);
    }
  }
}
