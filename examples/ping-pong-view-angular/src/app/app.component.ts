import { Component, OnInit } from "@angular/core";
import { PingPongInitArgs, PingPongChannelApi } from "@kogito-tooling-examples/ping-pong-view/dist/api";

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
  }
}
