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

import * as React from "react";

/**
 * Core component. Provides basic low-level lifecycle methods for AppFormer components.
 */
export abstract class Component {
  public readonly core_componentId: string;
  public readonly type: string;
  public readonly _container: HTMLElement;
  public readonly _components: string[] = [];

  public af_isReact: boolean = false;
  public af_hasContext: boolean = false;

  protected constructor(args: { type: string; core_componentId: string }) {
    this.core_componentId = args.core_componentId;
    this.type = args.type;
  }

  public abstract core_componentRoot(): Element;

  public core_onReady() {
    console.info(`core: ${this.core_componentId} is ready.`);
  }

  public core_onVanished() {
    console.info(`core: ${this.core_componentId} was removed.`);
  }
}

export type Element = React.ReactPortal | React.ReactElement<any> | HTMLElement | string;
