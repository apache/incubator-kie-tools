import { PingPongAngularImplFactory } from "./PingPongAngularImplFactory";
import { Component, OnInit } from "@angular/core";
import { PingPongInitArgs, PingPongChannelApi } from "@kogito-tooling-examples/ping-pong-lib/dist/api";
import * as PingPongViewEnvelope from "@kogito-tooling-examples/ping-pong-lib/dist/envelope";
import { ContainerType } from "@kie-tooling-core/envelope/dist/api";

declare global {
  interface Window {
    initArgs: PingPongInitArgs;
    channelApi: PingPongChannelApi;
  }
}

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.css"],
})
export class AppComponent implements OnInit {
  title = "ping-pong-view-angular";

  ngOnInit() {
    console.log({ initArgs: window.initArgs, channelApi: window.channelApi });
    PingPongViewEnvelope.init({
      container: document.getElementById("envelope-app")!,
      config: { containerType: ContainerType.IFRAME },
      bus: { postMessage: (message, _targetOrigin, transfer) => window.parent.postMessage(message, "*", transfer) },
      pingPongViewFactory: new PingPongAngularImplFactory(),
      viewReady: function (): Promise<() => HTMLElement> {
        return Promise.resolve(() => document.getElementById("envelope-app")!);
      },
    });
  }
}
