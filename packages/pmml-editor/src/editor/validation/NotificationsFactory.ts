/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { ValidationLevel } from "./ValidationLevel";
import { Notification, NotificationSeverity } from "@kie-tooling-core/notifications/dist/api";
import { ValidationEntry } from "./ValidationRegistry";

export const toNotifications = (path: string, validationEntries: ValidationEntry[]): Notification[] => {
  const mapValidationLevel = (level: ValidationLevel): NotificationSeverity => {
    switch (level) {
      case ValidationLevel.ERROR:
        return "ERROR";
      case ValidationLevel.WARNING:
        return "WARNING";
    }
  };

  return validationEntries.map<Notification>((validationEntry) => {
    return {
      path: path,
      message: validationEntry.message ?? "",
      type: "PROBLEM",
      severity: mapValidationLevel(validationEntry.level),
    };
  });
};
