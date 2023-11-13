/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { ComponentMessage, MessageProperty } from "../message";
import { ComponentBus } from "./ComponentBus";

export class BrowserComponentBus implements ComponentBus {
  private listener: (message: ComponentMessage) => void;

  private readonly messageListener = (e: MessageEvent) => {
    this.listener(e.data as ComponentMessage);
  };

  public start() {
    window.addEventListener("message", this.messageListener, false);
  }

  public send(componentId: string, message: ComponentMessage): void {
    console.debug("[BrowserComponentBus] Sending Message");
    console.debug(message);
    message.properties.set(MessageProperty.COMPONENT_ID, componentId);
    window.parent.postMessage(message, window.location.href);
  }

  public setListener(onMessage: (message: ComponentMessage) => void): void {
    this.listener = onMessage;
  }

  public destroy(): void {
    window.removeEventListener("message", this.messageListener, false);
  }
}
