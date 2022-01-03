import { PingPong } from "@kogito-tooling-examples/ping-pong-lib/dist/envelope";
import { MessageBusClientApi } from "@kie-tooling-core/envelope-bus/dist/api";
import { PingPongInitArgs, PingPongChannelApi } from "@kogito-tooling-examples/ping-pong-lib/dist/api";
import { PingPongFactory } from "@kogito-tooling-examples/ping-pong-lib/dist/envelope";

export class PingPongAngularImplFactory implements PingPongFactory {
  create(initArgs: PingPongInitArgs, channelApi: MessageBusClientApi<PingPongChannelApi>): PingPong {
    console.log({ initArgs, channelApi });
    (window as any).initArgs = initArgs;
    (window as any).channelApi = channelApi;

    return {};
  }
}
