import { EnvelopeBusOuterMessageHandler } from "../EnvelopeBusOuterMessageHandler";
import {
  EditorContent,
  KogitoEdit,
  ResourceContentRequest,
  ResourceListRequest,
  StateControlCommand
} from "@kogito-tooling/core-api";
import { Rect, Tutorial, UserInteraction } from "@kogito-tooling/guided-tour";
import {EnvelopeBusMessage} from "../EnvelopeBusMessage";

export interface TestEnvelopeBusOuterMessageHandler {
  sentMessages: Array<EnvelopeBusMessage<any>>,
  receivedMessages: string[],
  initPollCount: number,
  handler: EnvelopeBusOuterMessageHandler,
}

export function getTestEnvelopeBusOuterMessageHandler() {
  const testEnvelopeBusOuterMessageHandler: TestEnvelopeBusOuterMessageHandler = {
    sentMessages: [],
    receivedMessages: [],
    initPollCount: 0,
    handler: new EnvelopeBusOuterMessageHandler(
      {
        postMessage: msg => testEnvelopeBusOuterMessageHandler.sentMessages.push(msg)
      },
      self => ({
        pollInit: () => {
          testEnvelopeBusOuterMessageHandler.initPollCount++;
        },
        receive_languageRequest() {
          testEnvelopeBusOuterMessageHandler.receivedMessages.push("languageRequest");
        },
        receive_contentRequest() {
          testEnvelopeBusOuterMessageHandler.receivedMessages.push("contentRequest");
        },
        receive_contentResponse(content: EditorContent) {
          testEnvelopeBusOuterMessageHandler.receivedMessages.push("contentResponse_" + content.content);
        },
        receive_setContentError: (errorMessage: string) => {
          testEnvelopeBusOuterMessageHandler.receivedMessages.push("setContentError_" + errorMessage);
        },
        receive_dirtyIndicatorChange(isDirty: boolean): void {
          testEnvelopeBusOuterMessageHandler.receivedMessages.push("dirtyIndicatorChange_" + isDirty);
        },
        receive_resourceContentRequest(resourceContentRequest: ResourceContentRequest): void {
          testEnvelopeBusOuterMessageHandler.receivedMessages.push(
            "resourceContentRequest_" + resourceContentRequest.path
          );
        },
        receive_readResourceContentError(errorMessage: string): void {
          testEnvelopeBusOuterMessageHandler.receivedMessages.push("readResourceContentError_" + errorMessage);
        },
        receive_resourceListRequest(resourceListRequest: ResourceListRequest): void {
          testEnvelopeBusOuterMessageHandler.receivedMessages.push("resourceListRequest_" + resourceListRequest);
        },
        receive_ready() {
          testEnvelopeBusOuterMessageHandler.receivedMessages.push("ready");
        },
        notify_editorUndo() {
          testEnvelopeBusOuterMessageHandler.receivedMessages.push("undo");
        },
        notify_editorRedo() {
          testEnvelopeBusOuterMessageHandler.receivedMessages.push("redo");
        },
        receive_newEdit(edit: KogitoEdit) {
          testEnvelopeBusOuterMessageHandler.receivedMessages.push("receiveNewEdit_" + edit.id);
        },
        receive_openFile(path: string): void {
          testEnvelopeBusOuterMessageHandler.receivedMessages.push("receiveOpenFile_" + path);
        },
        receive_previewRequest(previewSvg: string) {
          testEnvelopeBusOuterMessageHandler.receivedMessages.push("preview");
        },
        receive_stateControlCommandUpdate(command: StateControlCommand) {
          testEnvelopeBusOuterMessageHandler.receivedMessages.push("receiveStateControlEvent_" + command);
        },
        receive_guidedTourUserInteraction(userInteraction: UserInteraction) {
          testEnvelopeBusOuterMessageHandler.receivedMessages.push("guidedTour_UserInteraction");
        },
        receive_guidedTourRegisterTutorial(tutorial: Tutorial) {
          testEnvelopeBusOuterMessageHandler.receivedMessages.push("guidedTour_RegisterTutorial");
        },
        receive_guidedTourElementPositionResponse(position: Rect) {
          testEnvelopeBusOuterMessageHandler.receivedMessages.push("guidedTour_ElementPositionRequest");
        }
      })
    )
  };

  return testEnvelopeBusOuterMessageHandler;
}
