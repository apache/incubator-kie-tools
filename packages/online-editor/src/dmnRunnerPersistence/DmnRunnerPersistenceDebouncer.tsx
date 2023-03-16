/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import { CompanionFsService } from "../companionFs/CompanionFsService";

// intentention:
// Multiple operations in a short period of time that shares the same type should be one event;
// For this case, the last operation should override the first;

type Parameter<Method> = Method extends (...args: infer Args) => any ? Args : never;

export interface DmnRunnerPersistenceDebouncerEvent<
  FS extends CompanionFsService = CompanionFsService,
  Methods extends keyof FS = keyof FS,
  Method = FS[Methods]
> {
  method: Method;
  args: Parameter<Method>;
}

export class DmnRunnerPersistenceDebouncer {
  private readonly queue: DmnRunnerPersistenceDebouncerEvent[] = [];
  private timeout: number | undefined = undefined;

  constructor(public readonly companionFsService: CompanionFsService) {}

  public debounce(event: DmnRunnerPersistenceDebouncerEvent) {
    this.queue.push(event);

    if (this.timeout) {
      window.clearTimeout(this.timeout);
    }

    // saves the curernt length before schedule the timeout;
    // if an event appears after the dispatch, it will not be lost;
    const length = this.queue.length;
    this.timeout = window.setTimeout(() => {
      this.dispatch(length);
    }, 400); // FIXME: performing a update in the timeout time will override changes;
  }

  private dispatch(length: number) {
    // erase the first elements;
    this.queue.splice(0, length - 1);
    const event = this.queue.shift();
    if (!event) {
      return;
    }

    const { method, args } = event;
    method.call(this.companionFsService, ...args);
  }
}
