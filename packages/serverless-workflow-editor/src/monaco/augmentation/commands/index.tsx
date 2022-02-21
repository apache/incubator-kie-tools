import { editor } from "monaco-editor";
import { EmbeddedDivPingPong } from "@kie-tools-examples/ping-pong-view/dist/embedded";
import * as ReactDOM from "react-dom";
import * as React from "react";
import { openWidget } from "../widgets";

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

export type SwfMonacoEditorCommandTypes = "OpenFunctionsWidget" | "OpenStatesWidget" | "RunFunctionsCompletion";

export type SwfMonacoEditorCommandIds = Record<SwfMonacoEditorCommandTypes, string>;

export function initAugmentationCommands(editorInstance: editor.IStandaloneCodeEditor): SwfMonacoEditorCommandIds {
  return {
    RunFunctionsCompletion: editorInstance.addCommand(0, async (ctx, args) => {
      console.info("Running functions completion command...");
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
