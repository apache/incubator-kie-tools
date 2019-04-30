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

import { Portable } from "appformer-js";
import { JavaInteger } from "appformer-js";
import { NotificationType } from "./NotificationType";

export class NotificationEvent implements Portable<NotificationEvent> {
  protected readonly _fqcn: string = NotificationEvent.__fqcn();

  public readonly notification?: string = undefined;
  public readonly type?: NotificationType = undefined;
  public readonly isSingleton?: boolean = undefined;
  public readonly initialTopOffset?: JavaInteger = undefined;

  constructor(self: {
    notification?: string;
    type?: NotificationType;
    isSingleton?: boolean;
    initialTopOffset?: JavaInteger;
  }) {
    Object.assign(this, self);
  }

  public static __fqcn(): string {
    return "org.uberfire.workbench.events.NotificationEvent";
  }
}
