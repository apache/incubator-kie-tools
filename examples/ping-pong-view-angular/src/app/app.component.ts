/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { ApiService, LogEntry } from "./api.service";
import { Component, OnInit } from "@angular/core";
import * as PingPongViewEnvelope from "@kogito-tooling-examples/ping-pong-view/dist/envelope";
import { ContainerType } from "@kie-tooling-core/envelope/dist/api";
import { Observable, scan } from "rxjs";

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.css"],
})
export class AppComponent implements OnInit {
  constructor(public apiService: ApiService) {}

  log: Observable<LogEntry[]>;

  ngOnInit() {
    // Initialize log with a starting message.
    this.apiService.log.next({ line: "Logs will show up here", time: 0 });

    // Initialize envelope with config (stating that we are in an iframe),
    // the bus, ou factory (in this case, a service that implements the "create" method),
    // and the "viewReady" that signals that our app is ready (in this case, immediately,
    // since the app is already loaded).
    PingPongViewEnvelope.init({
      config: { containerType: ContainerType.IFRAME },
      bus: { postMessage: (message, _targetOrigin, transfer) => window.parent.postMessage(message, "*", transfer) },
      pingPongViewFactory: this.apiService,
      viewReady: () => Promise.resolve(() => {}),
    });

    // Create an observable variable with the 10 latest values of the log.
    this.log = this.apiService.log.asObservable().pipe(scan((acc, curr) => [...acc.slice(-9), curr], []));
  }
}
