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

import { Component as CoreComponent } from "../core";
import { Element } from "../core";

/**
 * Base AppFormer.js component. It contains the default life-cycle methods that are common to all components.
 *
 */
export abstract class Component extends CoreComponent {
  public readonly af_componentId: string;
  public af_subscriptions: Map<string, ((event: any) => void)> = new Map();

  protected constructor(args: { type: string; af_componentId: string }) {
    super({ type: args.type, core_componentId: args.af_componentId });
    this.af_componentId = args.af_componentId;
  }

  public af_onStartup(): void {
    //
  }

  public af_onOpen(): void {
    //
  }

  public af_onFocus(): void {
    //
  }

  public af_onLostFocus(): void {
    //
  }

  public af_onMayClose(): boolean {
    return true;
  }

  public af_onClose(): void {
    //
  }

  public af_onShutdown(): void {
    //
  }

  public core_componentRoot(children?: any): Element {
    return this.af_componentRoot(children);
  }

  public abstract af_componentRoot(children?: any): Element;
}
