import { editor } from "monaco-editor";
import * as React from "react";
import { openWidget } from "../widgets";
import { ServerlessWorkflowEditorChannelApi } from "../../../editor";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/src/api";

// Part of an example
//
// const pingPongChannelApiImpl = {
//   pingPongView__ping(source: string) {
//     console.info(`Received PING from '${source}'`);
//   },
//   pingPongView__pong(source: string, replyingTo: string) {
//     console.info(`Received PONG from '${source}' in reply to '${replyingTo}'`);
//   },
// };

export type SwfMonacoEditorCommandTypes =
  | "LogInToRhhcc"
  | "RefreshServiceCatalogFromRhhcc"
  | "ImportFunctionFromCompletionItem"
  | "OpenFunctionsWidget"
  | "OpenStatesWidget"
  | "OpenFunctionsCompletionItemsAtTheBottom";

export type SwfMonacoEditorCommandIds = Record<SwfMonacoEditorCommandTypes, string>;

export function initAugmentationCommands(
  editorInstance: editor.IStandaloneCodeEditor,
  channelApi: MessageBusClientApi<ServerlessWorkflowEditorChannelApi>
): SwfMonacoEditorCommandIds {
  return {
    ImportFunctionFromCompletionItem: editorInstance.addCommand(0, async (ctx, args) => {
      channelApi.notifications.kogitoSwfServiceCatalog_importFunctionFromCompletionItem.send(
        args.service,
        args.importedFunction
      );
    })!,
    LogInToRhhcc: editorInstance.addCommand(0, async (ctx, args) => {
      channelApi.notifications.kogitoSwfServiceCatalog_logInToRhhcc.send();
    })!,
    RefreshServiceCatalogFromRhhcc: editorInstance.addCommand(0, async (ctx, args) => {
      channelApi.notifications.kogitoSwfServiceCatalog_refresh.send();
    })!,
    OpenFunctionsCompletionItemsAtTheBottom: editorInstance.addCommand(0, async (ctx, args) => {
      editorInstance.setPosition(args.newCursorPosition);
      editorInstance.trigger("OpenFunctionsCompletionItemsAtTheBottom", "editor.action.triggerSuggest", {});
    })!,
    OpenFunctionsWidget: editorInstance.addCommand(0, async (ctx, args) => {
      openWidget(editorInstance, {
        position: args.position,
        widgetId: "swf.functions.widget",
        backgroundColor: "lightgreen",
        domNodeHolder: {},
        onReady: ({ container }) => {
          console.info("Opening functions widget..");
          // Part of an example
          //
          // ReactDOM.render(
          //   <EmbeddedDivPingPong
          //     apiImpl={pingPongChannelApiImpl}
          //     name={"React " + Math.random()}
          //     targetOrigin={window.location.origin}
          //     renderView={renderPingPongReact}
          //   />,
          //   container
          // );
        },
        onClose: ({ container }) => {
          // Part of an example
          //
          // return ReactDOM.unmountComponentAtNode(container);
        },
      });
    })!,
    OpenStatesWidget: editorInstance.addCommand(0, async (ctx, args) => {
      openWidget(editorInstance, {
        position: args.position,
        widgetId: "swf.states.widget",
        backgroundColor: "lightblue",
        domNodeHolder: {},
        onReady: ({ container }) => {
          console.info("Opening states widget..");
          // Part of an example
          //
          // ReactDOM.render(
          //   <EmbeddedDivPingPong
          //     apiImpl={pingPongChannelApiImpl}
          //     name={"React " + Math.random()}
          //     targetOrigin={window.location.origin}
          //     renderView={renderPingPongReact}
          //   />,
          //   container
          // );
        },
        onClose: ({ container }) => {
          // Part of an example
          //
          // return ReactDOM.unmountComponentAtNode(container);
        },
      });
    })!,
  };
}
