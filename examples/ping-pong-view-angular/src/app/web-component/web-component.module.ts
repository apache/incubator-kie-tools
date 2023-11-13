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

import { NgModule, Injector, DoBootstrap } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { createCustomElement } from "@angular/elements";
import { PingPongModule } from "../ping-pong/ping-pong.module";
import { PingPongWcComponent } from "./web-component.component";

@NgModule({
  declarations: [PingPongWcComponent],
  imports: [BrowserModule, PingPongModule],
  entryComponents: [PingPongWcComponent],
  providers: [],
})
export class WebComponentModule implements DoBootstrap {
  constructor(private injector: Injector) {}

  ngDoBootstrap() {
    const element = createCustomElement(PingPongWcComponent, { injector: this.injector });
    customElements.define("ping-pong-angular", element);
  }
}
