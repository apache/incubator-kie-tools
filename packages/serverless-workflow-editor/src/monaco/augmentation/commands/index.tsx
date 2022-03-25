import { editor, Position } from "monaco-editor";
import * as React from "react";
import { openWidget } from "../widgets";
import { ServerlessWorkflowEditorChannelApi } from "../../../editor";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import {
  SwfLanguageServiceCommandArgs,
  SwfLanguageServiceCommandIds,
} from "@kie-tools/serverless-workflow-language-service";

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

export function initAugmentationCommands(
  editorInstance: editor.IStandaloneCodeEditor,
  channelApi: MessageBusClientApi<ServerlessWorkflowEditorChannelApi>
): SwfLanguageServiceCommandIds {
  return {
    SetupServiceRegistryUrl: editorInstance.addCommand(
      0,
      async (ctx, args: SwfLanguageServiceCommandArgs["SetupServiceRegistryUrl"]) => {
        channelApi.notifications.kogitoSwfServiceCatalog_setupServiceRegistryUrl.send();
      }
    )!,
    ImportFunctionFromCompletionItem: editorInstance.addCommand(
      0,
      async (ctx, args: SwfLanguageServiceCommandArgs["ImportFunctionFromCompletionItem"]) => {
        channelApi.notifications.kogitoSwfServiceCatalog_importFunctionFromCompletionItem.send(args.containingService);
      }
    )!,
    LogInToRhhcc: editorInstance.addCommand(0, async (ctx, args: SwfLanguageServiceCommandArgs["LogInToRhhcc"]) => {
      channelApi.notifications.kogitoSwfServiceCatalog_logInToRhhcc.send();
    })!,
    RefreshServiceCatalogFromRhhcc: editorInstance.addCommand(
      0,
      async (ctx, args: SwfLanguageServiceCommandArgs["RefreshServiceCatalogFromRhhcc"]) => {
        channelApi.notifications.kogitoSwfServiceCatalog_refresh.send();
      }
    )!,
    OpenFunctionsCompletionItems: editorInstance.addCommand(
      0,
      async (ctx, args: SwfLanguageServiceCommandArgs["OpenFunctionsCompletionItems"]) => {
        editorInstance.setPosition({
          lineNumber: args.newCursorPosition.line + 1,
          column: args.newCursorPosition.character + 1,
        });
        editorInstance.trigger("OpenFunctionsCompletionItemsAtTheBottom", "editor.action.triggerSuggest", {});
      }
    )!,
    OpenFunctionsWidget: editorInstance.addCommand(
      0,
      async (ctx, args: SwfLanguageServiceCommandArgs["OpenFunctionsWidget"]) => {
        openWidget(editorInstance, {
          position: new Position(args.position.line, args.position.character),
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
      async (ctx, args: SwfLanguageServiceCommandArgs["OpenStatesWidget"]) => {
        openWidget(editorInstance, {
          position: new Position(args.position.line, args.position.character),
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
