import { EnvelopeBusMessageType } from "./EnvelopeBusMessageType";

export interface EnvelopeBusMessage<T> {
  type: EnvelopeBusMessageType;
  data: T;
}
