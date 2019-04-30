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
import { Element } from "../core";
import { Screen } from "./Screen";
import { Perspective } from "./Perspective";
import { Portable } from "../marshalling";

/**
 * AppFormer.js public API.
 *
 */
export class AppFormer {
  /**
   * Starts an AppFormer instance.
   * @param container
   * The DOM element on which AppFormer will start into
   * @param callback
   * Function to be executed after AppFormer init is complete
   */
  public init(container: HTMLElement, callback: () => void): AppFormer {
    return this;
  }

  /**
   * Registers a Screen component.
   * @param screen
   */
  // tslint:disable-next-line
  public registerScreen(screen: Screen): void {}

  /**
   * Registers a Perspective component
   * @param perspective
   */
  // tslint:disable-next-line
  public registerPerspective(perspective: Perspective): void {}

  /**
   * Renders the component with the corresponding id.
   * @param af_componentId
   * The component id
   * @param args
   * Arbitrary arguments to be used by the component
   */
  // tslint:disable-next-line
  public goTo(af_componentId: string, args?: Map<string, any>): void {}

  /**
   * Translates a bundle key
   * @param tkey
   * The bundle key
   * @param args
   * The arguments to this bundle
   */
  // tslint:disable-next-line
  public translate(tkey: string, args: string[]): string {
    throw new Error("Not implemented");
  }

  /**
   * Renders a component.
   * @param element
   * The component to be rendered
   * @param container
   * The DOM element on which the component will be rendered.
   * @param callback
   * Function to be executed after the component is done rendering.
   */
  // tslint:disable-next-line
  public render(element: Element, container: HTMLElement, callback: () => void): void {}

  /**
   * Fires an event using Errai bus.
   * @param obj
   * The event object.
   */
  // tslint:disable-next-line
  public fireEvent<T>(obj: Portable<T>): void {}

  /**
   * Executes an RPC call to an Errai Remote.
   * @param path
   * The Errai bus RPC path
   * @param args
   * The arguments to this RPC
   */
  public rpc(path: string, args: Array<Portable<any>>): Promise<string> {
    throw new Error("Not implemented");
  }

  /**
   * Unrenders a component
   * @param af_componentId
   * The component id.
   */
  // tslint:disable-next-line
  public close(af_componentId: string): void {}
}
