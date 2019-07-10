import { EnvelopeBusMessage } from "./EnvelopeBusMessage";

export interface EnvelopeBusApi {
  postMessage<T>(message: EnvelopeBusMessage<T>, targetOrigin?: string, _?: any): void;
}
