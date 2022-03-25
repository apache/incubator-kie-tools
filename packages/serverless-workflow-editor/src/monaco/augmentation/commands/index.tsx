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
    "swf.ls.commands.SetupServiceRegistryUrl": editorInstance.addCommand(
      0,
      async (ctx, args: SwfLanguageServiceCommandArgs["swf.ls.commands.SetupServiceRegistryUrl"]) => {
        channelApi.notifications.kogitoSwfServiceCatalog_setupServiceRegistryUrl.send();
      }
    )!,
    "swf.ls.commands.ImportFunctionFromCompletionItem": editorInstance.addCommand(
      0,
      async (ctx, args: SwfLanguageServiceCommandArgs["swf.ls.commands.ImportFunctionFromCompletionItem"]) => {
        channelApi.notifications.kogitoSwfServiceCatalog_importFunctionFromCompletionItem.send(args.containingService);
      }
    )!,
    "swf.ls.commands.LogInToRhhcc": editorInstance.addCommand(
      0,
      async (ctx, args: SwfLanguageServiceCommandArgs["swf.ls.commands.LogInToRhhcc"]) => {
        channelApi.notifications.kogitoSwfServiceCatalog_logInToRhhcc.send();
      }
    )!,
    "swf.ls.commands.RefreshServiceCatalogFromRhhcc": editorInstance.addCommand(
      0,
      async (ctx, args: SwfLanguageServiceCommandArgs["swf.ls.commands.RefreshServiceCatalogFromRhhcc"]) => {
        channelApi.notifications.kogitoSwfServiceCatalog_refresh.send();
      }
    )!,
    "swf.ls.commands.OpenFunctionsCompletionItems": editorInstance.addCommand(
      0,
      async (ctx, args: SwfLanguageServiceCommandArgs["swf.ls.commands.OpenFunctionsCompletionItems"]) => {
        editorInstance.setPosition({
          lineNumber: args.newCursorPosition.line + 1,
          column: args.newCursorPosition.character + 1,
        });
        editorInstance.trigger("OpenFunctionsCompletionItemsAtTheBottom", "editor.action.triggerSuggest", {});
      }
    )!,
    "swf.ls.commands.OpenFunctionsWidget": editorInstance.addCommand(
      0,
      async (ctx, args: SwfLanguageServiceCommandArgs["swf.ls.commands.OpenFunctionsWidget"]) => {
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
    "swf.ls.commands.OpenStatesWidget": editorInstance.addCommand(
      0,
      async (ctx, args: SwfLanguageServiceCommandArgs["swf.ls.commands.OpenStatesWidget"]) => {
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
