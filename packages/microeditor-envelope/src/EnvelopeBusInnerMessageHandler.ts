import { EnvelopeBusMessage, EnvelopeBusMessageType } from "appformer-js-microeditor-envelope-protocol";
import { LanguageData } from "appformer-js-microeditor-router";
import { EnvelopeBusApi } from "appformer-js-microeditor-envelope-protocol";

export interface Impl {
  receive_contentResponse(content: string): void;
  receive_languageResponse(languageData: LanguageData): void;
  receive_contentRequest(): void;
}

export class EnvelopeBusInnerMessageHandler {
  private readonly envelopeBusApi: EnvelopeBusApi;
  private readonly impl: Impl;

  public capturedInitRequestYet = false;
  public targetOrigin: string;
  public eventListener?: any;

  constructor(busApi: EnvelopeBusApi, impl: (_this: EnvelopeBusInnerMessageHandler) => Impl) {
    this.envelopeBusApi = busApi;
    this.impl = impl(this);
  }

  public startListening() {
    if (this.eventListener) {
      return;
    }

    this.eventListener = (event: any) => this.receive(event.data);
    window.addEventListener("message", this.eventListener);
  }

  public stopListening() {
    window.removeEventListener("message", this.eventListener);
  }

  public send<T>(message: EnvelopeBusMessage<T>) {
    if (!this.targetOrigin) {
      throw new Error("Tried to send message without targetOrigin set");
    }
    this.envelopeBusApi.postMessage(message, this.targetOrigin);
  }

  public respond_initRequest() {
    return this.send({ type: EnvelopeBusMessageType.RETURN_INIT, data: undefined });
  }

  public respond_contentRequest(content: string) {
    return this.send({ type: EnvelopeBusMessageType.RETURN_CONTENT, data: content });
  }

  public request_languageResponse() {
    return this.send({ type: EnvelopeBusMessageType.REQUEST_LANGUAGE, data: undefined });
  }

  public request_contentResponse() {
    return this.send({ type: EnvelopeBusMessageType.REQUEST_CONTENT, data: undefined });
  }

  private receive_initRequest(targetOrigin: string) {
    if (this.capturedInitRequestYet) {
      return;
    }

    this.capturedInitRequestYet = true;
    this.targetOrigin = targetOrigin;

    this.respond_initRequest();
    this.request_languageResponse();
  }

  public receive(message: EnvelopeBusMessage<any>) {
    switch (message.type) {
      case EnvelopeBusMessageType.REQUEST_INIT:
        this.receive_initRequest(message.data as string);
        break;
      case EnvelopeBusMessageType.RETURN_LANGUAGE:
        this.impl.receive_languageResponse(message.data as LanguageData);
        break;
      case EnvelopeBusMessageType.RETURN_CONTENT:
        this.impl.receive_contentResponse(message.data as string);
        break;
      case EnvelopeBusMessageType.REQUEST_CONTENT:
        this.impl.receive_contentRequest();
        break;
      default:
        console.info(`Unknown message type received: ${message.type}"`);
        break;
    }
  }
}
