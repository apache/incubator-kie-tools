/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React, { useContext } from "react";
import { CustomLabels } from "../../api/CustomLabels";
import { User } from "@kie-tools/runtime-tools-process-enveloped-components/dist/taskForm";
import { DiagramPreviewSize } from "@kie-tools/runtime-tools-process-enveloped-components/dist/processDetails";

export interface DevUIAppContext {
  isProcessEnabled: boolean;
  getCurrentUser(): User;
  getAllUsers(): User[];
  switchUser(userId: string): void;
  onUserChange(listener: UserChangeListener): UnSubscribeHandler;
  getDevUIUrl(): string;
  getOpenApiPath(): string;
  getRemoteKogitoAppUrl(): string;
  availablePages?: string[];
  customLabels: CustomLabels;
  omittedProcessTimelineEvents: string[];
  diagramPreviewSize?: DiagramPreviewSize;
}

export interface UserChangeListener {
  onUserChange: (user: User) => void;
}

export interface UnSubscribeHandler {
  unSubscribe: () => void;
}

export type DevUIAppContextArgs = {
  users?: User[];
  devUIUrl: string;
  openApiPath: string;
  remoteKogitoAppUrl: string;
  isProcessEnabled: boolean;
  availablePages?: string[];
  customLabels?: CustomLabels;
  omittedProcessTimelineEvents: string[];
  diagramPreviewSize?: DiagramPreviewSize;
};

export class DevUIAppContextImpl implements DevUIAppContext {
  private currentUser: User;
  private readonly userListeners: UserChangeListener[] = [];

  constructor(private readonly args: DevUIAppContextArgs) {
    if (args.users?.length > 0) {
      this.currentUser = args.users[0];
    }
  }

  getDevUIUrl(): string {
    return this.args.devUIUrl;
  }

  getOpenApiPath(): string {
    return this.args.openApiPath;
  }

  getRemoteKogitoAppUrl(): string {
    return this.args.remoteKogitoAppUrl;
  }

  getCurrentUser(): User {
    return this.currentUser;
  }

  getAllUsers(): User[] {
    return this.args.users;
  }

  switchUser(userId: string): void {
    const switchedUser = this.args.users.find((user) => user.id === userId);
    if (switchedUser) {
      this.currentUser = switchedUser;
      this.userListeners.forEach((listener) => listener.onUserChange(switchedUser));
    }
  }

  onUserChange(listener: UserChangeListener): UnSubscribeHandler {
    this.userListeners.push(listener);

    return {
      unSubscribe: () => {
        const index = this.userListeners.indexOf(listener);
        if (index > -1) {
          this.userListeners.splice(index, 1);
        }
      },
    };
  }

  get isProcessEnabled(): boolean {
    return this.args.isProcessEnabled;
  }

  get availablePages(): string[] {
    return this.args.availablePages;
  }

  get customLabels(): CustomLabels {
    return this.args.customLabels;
  }

  get omittedProcessTimelineEvents(): string[] {
    return this.args.omittedProcessTimelineEvents;
  }

  get diagramPreviewSize(): DiagramPreviewSize {
    return this.args.diagramPreviewSize;
  }
}

const RuntimeToolsDevUIAppContext = React.createContext<DevUIAppContext>(null);

export default RuntimeToolsDevUIAppContext;

export const useDevUIAppContext = () => useContext<DevUIAppContext>(RuntimeToolsDevUIAppContext);
