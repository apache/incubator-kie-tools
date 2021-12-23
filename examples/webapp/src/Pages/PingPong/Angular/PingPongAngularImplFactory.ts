import { PingPong } from "@kogito-tooling-examples/ping-pong-view/dist/envelope";
import { MessageBusClientApi } from "@kie-tooling-core/envelope-bus/dist/api";
import { PingPongInitArgs, PingPongChannelApi } from "@kogito-tooling-examples/ping-pong-view/dist/api";
import { PingPongFactory } from "@kogito-tooling-examples/ping-pong-view/dist/envelope";
import * as PingPongViewEnvelope from "@kogito-tooling-examples/ping-pong-view/dist/envelope";
import { ContainerType } from "@kie-tooling-core/envelope/dist/api";

export class PingPongAngularImplFactory implements PingPongFactory {
  create(initArgs: PingPongInitArgs, channelApi: MessageBusClientApi<PingPongChannelApi>): PingPong {
    (window as any).initArgs = initArgs;
    (window as any).channelApi = channelApi;

    return {};
  }
}

PingPongViewEnvelope.init({
  container: document.getElementById("envelope-app")!,
  config: { containerType: ContainerType.IFRAME },
  bus: { postMessage: (message, targetOrigin, transfer) => window.parent.postMessage(message, "*", transfer) },
  pingPongViewFactory: new PingPongAngularImplFactory(),
});
