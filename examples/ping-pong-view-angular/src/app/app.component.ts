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
      viewReady: function (): Promise<() => HTMLElement> {
        return Promise.resolve(() => document.getElementById("envelope-app")!);
      },
    });

    // Create an observable variable with the 10 latest values of the log.
    this.log = this.apiService.log.asObservable().pipe(scan((acc, curr) => [...acc.slice(-9), curr], []));
  }
}
