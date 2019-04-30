/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import { Component } from "./Component";

/**
 * Perspective component API. Implement this class to create a Perspective.
 */
export abstract class Perspective extends Component {
  public af_name: string;
  public af_perspectiveScreens: string[] = [];
  public af_isDefault: boolean = false;
  public af_isTransient: boolean = true;
  public af_isTemplated: boolean = true;

  protected constructor(componentId: string) {
    super({ type: "perspective", af_componentId: componentId });
  }

  public af_onStartup(): void {
    //
  }

  public af_onOpen(): void {
    //
  }

  public af_onClose(): void {
    //
  }

  public af_onShutdown(): void {
    //
  }
}
