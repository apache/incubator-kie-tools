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

import { DataSet } from "./dataset";
import { BrowserComponentBus } from "./controller/BrowserComponentBus";
import { DashbuilderComponentController } from "./controller/DashbuilderComponentController";
import { DashbuilderComponentDispatcher } from "./controller/DashbuilderComponentDispatcher";
import { ComponentBus, ComponentController } from "./controller";

export class ComponentApi {
  private bus: ComponentBus;
  private controller: DashbuilderComponentController;
  private listener: DashbuilderComponentDispatcher;
  constructor() {
    this.bus = new BrowserComponentBus();
    this.controller = new DashbuilderComponentController(this.bus);
    this.listener = new DashbuilderComponentDispatcher(this.bus, this.controller);
    this.listener.init();
  }
  public getComponentController(
    onInit?: (params: Map<string, any>) => void,
    onDataSet?: (dataSet: DataSet, params?: Map<string, any>) => void
  ): ComponentController {
    if (onInit) {
      this.controller.setOnInit(onInit);
    }
    if (onDataSet) {
      this.controller.setOnDataSet(onDataSet);
    }
    return this.controller;
  }

  public restart() {
    this.destroy();
    this.listener.init();
  }

  public destroy() {
    this.listener.stop();
  }
}
