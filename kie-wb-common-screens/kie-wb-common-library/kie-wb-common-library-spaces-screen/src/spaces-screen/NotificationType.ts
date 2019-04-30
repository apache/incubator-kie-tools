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

import { JavaEnum } from "appformer-js";

export class NotificationType extends JavaEnum<NotificationType> {
  public static readonly DEFAULT: NotificationType = new NotificationType("DEFAULT");
  public static readonly ERROR: NotificationType = new NotificationType("ERROR");
  public static readonly SUCCESS: NotificationType = new NotificationType("SUCCESS");
  public static readonly INFO: NotificationType = new NotificationType("INFO");
  public static readonly WARNING: NotificationType = new NotificationType("WARNING");

  protected readonly _fqcn: string = NotificationType.__fqcn();

  public static __fqcn(): string {
    return "org.uberfire.workbench.events.NotificationEvent$NotificationType";
  }

  public static values() {
    return [
      NotificationType.DEFAULT,
      NotificationType.ERROR,
      NotificationType.SUCCESS,
      NotificationType.INFO,
      NotificationType.WARNING
    ];
  }
}
