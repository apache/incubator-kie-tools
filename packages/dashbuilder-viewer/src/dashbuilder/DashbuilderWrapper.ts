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

interface CustomWindow extends Window {
  componentServerUrl: string;

  setDashbuilderContent: (content: string) => void;
  dashbuilderReady: () => void;
}

declare let window: CustomWindow;

export class DashbuilderWrapper {
  content: string;
  constructor(ready: () => void) {
    window.setDashbuilderContent = () => console.log("Dashbuilder not ready to receive content!");
    window.dashbuilderReady = () => {
      console.log("Dashbuilder is ready!");
      ready();
      this.setContent(this.content);
    };
  }

  public setContent(content: string): void {
    this.content = content;
    window.setDashbuilderContent(content);
  }

  public getContent(): string {
    return this.content;
  }

  public setComponentServerUrl(componentServerUrl: string) {
    window.componentServerUrl = componentServerUrl;
  }
}
