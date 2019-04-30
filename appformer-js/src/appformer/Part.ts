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

import { DisplayInfo } from "./DisplayInfo";

/**
 * Part of a CompassLayoutPerspective
 */
export class Part {
  private _placeName: string;
  private _displayInfo: DisplayInfo = new DisplayInfo();
  private _parameters: {} = {};

  constructor(placeName: string) {
    this._placeName = placeName;
  }

  get placeName(): string {
    return this._placeName;
  }

  set placeName(value: string) {
    this._placeName = value;
  }

  get displayInfo(): DisplayInfo {
    return this._displayInfo;
  }

  set displayInfo(value: DisplayInfo) {
    this._displayInfo = value;
  }

  get parameters(): {} {
    return this._parameters;
  }

  set parameters(value: {}) {
    this._parameters = value;
  }
}
