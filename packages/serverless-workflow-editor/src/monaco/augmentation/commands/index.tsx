import { editor } from "monaco-editor";
import { EmbeddedDivPingPong } from "@kie-tools-examples/ping-pong-view/dist/embedded";
import * as ReactDOM from "react-dom";
import * as React from "react";
import { pingPongEnvelopViewRenderDiv as renderPingPongReact } from "@kie-tools-examples/ping-pong-view-react";
import { pingPongEnvelopViewRenderDiv as renderPingPongAngular } from "@kie-tools-examples/ping-pong-view-angular/dist/wc/lib";
import { SwfMonacoEditorCommandIds } from "../../SwfMonacoEditorApi";
import { openWidget } from "../widgets";

const pingPongChannelApiImpl = {
  pingPongView__ping(source: string) {
    console.info(`Received PING from '${source}'`);
  },
  pingPongView__pong(source: string, replyingTo: string) {
    console.info(`Received PONG from '${source}' in reply to '${replyingTo}'`);
  },
};

export function initAugmentationCommands(editorInstance: editor.IStandaloneCodeEditor): SwfMonacoEditorCommandIds {
  return {
    RunFunctionsCompletion: editorInstance.addCommand(0, async (ctx, args) => {
      window.alert("functions autocomplete: " + JSON.stringify(args));
    })!,
    OpenFunctionsWidget: editorInstance.addCommand(0, async (ctx, args) => {
      openWidget(editorInstance, {
        position: args.position,
        widgetId: "swf.functions.widget",
        backgroundColor: "lightgreen",
        domNodeHolder: {},
        onReady: ({ container }) => {
          ReactDOM.render(
            <EmbeddedDivPingPong
              apiImpl={pingPongChannelApiImpl}
              name={"React " + Math.random()}
              targetOrigin={window.location.origin}
              renderView={renderPingPongReact}
            />,
            container
          );
        },
        onClose: ({ container }) => ReactDOM.unmountComponentAtNode(container),
      });
    })!,
    OpenStatesWidget: editorInstance.addCommand(0, async (ctx, args) => {
      openWidget(editorInstance, {
        position: args.position,
        widgetId: "swf.states.widget",
        backgroundColor: "lightblue",
        domNodeHolder: {},
        onReady: ({ container }) => {
          ReactDOM.render(
            <EmbeddedDivPingPong
              apiImpl={pingPongChannelApiImpl}
              name={"React " + Math.random()}
              targetOrigin={window.location.origin}
              renderView={renderPingPongAngular}
            />,
            container
          );
        },
        onClose: ({ container }) => ReactDOM.unmountComponentAtNode(container),
      });
    })!,
  };
}
