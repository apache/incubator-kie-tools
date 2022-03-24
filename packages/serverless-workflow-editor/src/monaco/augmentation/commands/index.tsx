import { editor, Position } from "monaco-editor";
import * as React from "react";
import { openWidget } from "../widgets";
import { ServerlessWorkflowEditorChannelApi } from "../../../editor";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { SwfServiceCatalogService } from "@kie-tools/serverless-workflow-service-catalog/dist/api";

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
  | "SetupServiceRegistryUrl"
  | "RefreshServiceCatalogFromRhhcc"
  | "ImportFunctionFromCompletionItem"
  | "OpenFunctionsWidget"
  | "OpenStatesWidget"
  | "OpenFunctionsCompletionItems";

export type SwfMonacoEditorCommandArgs = {
  LogInToRhhcc: {};
  SetupServiceRegistryUrl: {};
  RefreshServiceCatalogFromRhhcc: {};
  ImportFunctionFromCompletionItem: { containingService: SwfServiceCatalogService };
  OpenFunctionsWidget: { position: Position };
  OpenStatesWidget: { position: Position };
  OpenFunctionsCompletionItems: { newCursorPosition: Position };
};

export type SwfMonacoEditorCommandIds = Record<SwfMonacoEditorCommandTypes, string>;

export function initAugmentationCommands(
  editorInstance: editor.IStandaloneCodeEditor,
  channelApi: MessageBusClientApi<ServerlessWorkflowEditorChannelApi>
): SwfMonacoEditorCommandIds {
  return {
    SetupServiceRegistryUrl: editorInstance.addCommand(
      0,
      async (ctx, args: SwfMonacoEditorCommandArgs["SetupServiceRegistryUrl"]) => {
        channelApi.notifications.kogitoSwfServiceCatalog_setupServiceRegistryUrl.send();
      }
    )!,
    ImportFunctionFromCompletionItem: editorInstance.addCommand(
      0,
      async (ctx, args: SwfMonacoEditorCommandArgs["ImportFunctionFromCompletionItem"]) => {
        channelApi.notifications.kogitoSwfServiceCatalog_importFunctionFromCompletionItem.send(args.containingService);
      }
    )!,
    LogInToRhhcc: editorInstance.addCommand(0, async (ctx, args: SwfMonacoEditorCommandArgs["LogInToRhhcc"]) => {
      channelApi.notifications.kogitoSwfServiceCatalog_logInToRhhcc.send();
    })!,
    RefreshServiceCatalogFromRhhcc: editorInstance.addCommand(
      0,
      async (ctx, args: SwfMonacoEditorCommandArgs["RefreshServiceCatalogFromRhhcc"]) => {
        channelApi.notifications.kogitoSwfServiceCatalog_refresh.send();
      }
    )!,
    OpenFunctionsCompletionItems: editorInstance.addCommand(
      0,
      async (ctx, args: SwfMonacoEditorCommandArgs["OpenFunctionsCompletionItems"]) => {
        editorInstance.setPosition(args.newCursorPosition);
        editorInstance.trigger("OpenFunctionsCompletionItemsAtTheBottom", "editor.action.triggerSuggest", {});
      }
    )!,
    OpenFunctionsWidget: editorInstance.addCommand(
      0,
      async (ctx, args: SwfMonacoEditorCommandArgs["OpenFunctionsWidget"]) => {
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
      }
    )!,
    OpenStatesWidget: editorInstance.addCommand(
      0,
      async (ctx, args: SwfMonacoEditorCommandArgs["OpenStatesWidget"]) => {
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
      }
    )!,
  };
}
