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

import { PingPongApiService, LogEntry } from "./ping-pong-api.service";
import { Component, Input, OnInit } from "@angular/core";
import * as PingPongViewEnvelope from "@kie-tools-examples/micro-frontends-multiplying-architecture-ping-pong-view/dist/envelope";
import { ContainerType } from "@kie-tools-core/envelope/dist/api";
import { Observable, scan } from "rxjs";
import { CommonModule } from "@angular/common";

@Component({
  standalone: true,
  selector: "app-ping-pong",
  templateUrl: "./ping-pong.component.html",
  styleUrls: ["./ping-pong.component.css"],
  providers: [PingPongApiService],
  imports: [CommonModule],
})
export class PingPongComponent implements OnInit {
  @Input() containerType: ContainerType;
  @Input() envelopeId?: string;
  constructor(public pingPongApiService: PingPongApiService) {}

  log: Observable<LogEntry[]>;

  subscribeToLogUpdates() {
    this.log = this.pingPongApiService.log.asObservable().pipe(scan((acc, curr) => [...acc.slice(-9), curr], []));
  }

  ngOnInit() {
    // Initialize log with a starting message.
    this.pingPongApiService.log.next({ line: "Logs will show up here", time: 0 });

    // Initialize envelope with the container config, the bus,
    // and factory (in this case, a service that implements the "create" method).
    PingPongViewEnvelope.init({
      config: { containerType: this.containerType, envelopeId: this.envelopeId! },
      bus: { postMessage: (message, _targetOrigin, transfer) => window.parent.postMessage(message, "*", transfer) },
      pingPongViewFactory: this.pingPongApiService,
    }).then(() => {
      // Create an observable variable with the 10 latest values of the log.
      this.subscribeToLogUpdates();
      this.pingPongApiService.logCleared.subscribe(() => this.subscribeToLogUpdates());
    });
  }
}
