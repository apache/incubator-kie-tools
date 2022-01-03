import { ApiService, LogEntry } from "./api.service";
import { Component, OnInit } from "@angular/core";
import * as PingPongViewEnvelope from "@kogito-tooling-examples/ping-pong-lib/dist/envelope";
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
    this.apiService.log.next({ line: "Logs will show up here", time: 0 });
    PingPongViewEnvelope.init({
      container: document.getElementById("envelope-app")!,
      config: { containerType: ContainerType.IFRAME },
      bus: { postMessage: (message, _targetOrigin, transfer) => window.parent.postMessage(message, "*", transfer) },
      pingPongViewFactory: this.apiService,
      viewReady: function (): Promise<() => HTMLElement> {
        return Promise.resolve(() => document.getElementById("envelope-app")!);
      },
    });

    this.log = this.apiService.log.asObservable().pipe(scan((acc, curr) => [...acc.slice(-9), curr], []));
  }
}
