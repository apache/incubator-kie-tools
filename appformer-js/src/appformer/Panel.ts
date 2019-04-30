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

import { Part } from "./Part";
import { DisplayInfo } from "./DisplayInfo";

export enum PanelType {
  MULTI_LIST = "org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter",
  STATIC = "org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter"
}

export enum CompassPosition {
  NONE = "NONE",
  NORTH = "NORTH",
  SOUTH = "SOUTH",
  EAST = "EAST",
  WEST = "WEST",
  SELF = "SELF",
  ROOT = "ROOT",
  CENTER = "CENTER"
}

/**
 * Panel of a CompassLayoutPerspective
 */
export class Panel {
  private _position: CompassPosition;
  private _width: number = -1;
  private _minWidth: number = -1;
  private _height: number = -1;
  private _minHeight: number = -1;

  private _children: Panel[] = [];
  private _parts: Part[] = [];

  private _panelType: PanelType = PanelType.MULTI_LIST;
  private _displayInfo: DisplayInfo = new DisplayInfo();

  constructor(position: CompassPosition) {
    this._position = position;
  }

  get position(): CompassPosition {
    return this._position;
  }

  set position(value: CompassPosition) {
    this._position = value;
  }

  get width(): number {
    return this._width;
  }

  set width(value: number) {
    this._width = value;
  }

  get minWidth(): number {
    return this._minWidth;
  }

  set minWidth(value: number) {
    this._minWidth = value;
  }

  get height(): number {
    return this._height;
  }

  set height(value: number) {
    this._height = value;
  }

  get minHeight(): number {
    return this._minHeight;
  }

  set minHeight(value: number) {
    this._minHeight = value;
  }

  get children(): Panel[] {
    return this._children;
  }

  set children(value: Panel[]) {
    this._children = value;
  }

  get parts(): Part[] {
    return this._parts;
  }

  set parts(value: Part[]) {
    this._parts = value;
  }

  get panelType(): PanelType {
    return this._panelType;
  }

  set panelType(value: PanelType) {
    this._panelType = value;
  }

  get displayInfo(): DisplayInfo {
    return this._displayInfo;
  }

  set displayInfo(value: DisplayInfo) {
    this._displayInfo = value;
  }
}
