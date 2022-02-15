import { editor, Position } from "monaco-editor";
import { EmbeddedDivPingPong } from "@kie-tools-examples/ping-pong-view/dist/embedded";
import * as ReactDOM from "react-dom";
import * as React from "react";
import { pingPongEnvelopViewRenderDiv as renderPingPongReact } from "@kie-tools-examples/ping-pong-view-react";
import { pingPongEnvelopViewRenderDiv as renderPingPongAngular } from "@kie-tools-examples/ping-pong-view-angular/dist/wc/lib";
import IContentWidget = editor.IContentWidget;

interface Holder<T> {
  value?: T;
}

const pingPongChannelApiImpl = {
  pingPongView__ping(source: string) {
    console.info(`Received PING from '${source}'`);
  },
  pingPongView__pong(source: string, replyingTo: string) {
    console.info(`Received PONG from '${source}' in reply to '${replyingTo}'`);
  },
};

export function initAugmentationCommands(editorInstance: editor.IStandaloneCodeEditor) {
  return {
    FunctionsCompletion: editorInstance.addCommand(0, (ctx, args) => {
      window.alert("functions autocomplete: " + JSON.stringify(args));
    })!,
    FunctionsWidget: editorInstance.addCommand(0, async (ctx, args) => {
      const domNodeHolder: Holder<HTMLDivElement> = {};
      openWidget(editorInstance, {
        position: args.position,
        widgetId: "swf.functions.widget",
        backgroundColor: "lightgreen",
        domNodeHolder,
        onReady: ({ componentContainer }) => {
          ReactDOM.render(
            <EmbeddedDivPingPong
              apiImpl={pingPongChannelApiImpl}
              name={"React " + Math.random()}
              targetOrigin={window.location.origin}
              renderView={renderPingPongReact}
            />,
            componentContainer
          );
        },
        onClose: ({ componentContainer }) => ReactDOM.unmountComponentAtNode(componentContainer),
      });
    })!,
    StatesWidget: editorInstance.addCommand(0, async (ctx, args) => {
      const domNodeHolder: Holder<HTMLDivElement> = {};
      openWidget(editorInstance, {
        position: args.position,
        widgetId: "swf.states.widget",
        backgroundColor: "lightblue",
        domNodeHolder,
        onReady: ({ componentContainer }) => {
          ReactDOM.render(
            <EmbeddedDivPingPong
              apiImpl={pingPongChannelApiImpl}
              name={"React " + Math.random()}
              targetOrigin={window.location.origin}
              renderView={renderPingPongAngular}
            />,
            componentContainer
          );
        },
        onClose: ({ componentContainer }) => ReactDOM.unmountComponentAtNode(componentContainer),
      });
    })!,
  };
}

function openWidget(
  editorInstance: editor.IStandaloneCodeEditor,
  args: {
    backgroundColor: string;
    widgetId: string;
    position: Position;
    domNodeHolder: Holder<HTMLDivElement | null>;
    onReady: (args: { componentContainer: HTMLDivElement }) => any;
    onClose: (args: { componentContainer: HTMLDivElement }) => any;
  }
) {
  const widgetPosition = {
    position: { lineNumber: args.position.lineNumber, column: args.position.column },
    preference: [editor.ContentWidgetPositionPreference.BELOW, editor.ContentWidgetPositionPreference.ABOVE],
  };

  const widgetId = args.widgetId + Math.random();

  const widget: IContentWidget = {
    suppressMouseDown: true,
    getId: () => widgetId,
    getPosition: () => widgetPosition,
    getDomNode: () => {
      if (!args.domNodeHolder.value) {
        args.domNodeHolder.value = document.createElement("div");
        args.domNodeHolder.value.style.background = args.backgroundColor;
        args.domNodeHolder.value.style.userSelect = "text";
        args.domNodeHolder.value.style.paddingRight = "14px"; // Prevents staying on top of the right gutter?
        args.domNodeHolder.value.style.width = "99999px"; // This is restrained by the max-width, so this is basically width: 100%;
        args.domNodeHolder.value.style.zIndex = "99999"; // This makes the cursor not blink on top of the widget.
        args.domNodeHolder.value.style.marginTop = "-1.2em"; // Go up one line (approx.);

        const button = document.createElement("button");
        args.domNodeHolder.value.appendChild(button);
        button.innerText = "Close";
        button.style.float = "right";
        button.onclick = async () => {
          args.onClose({ componentContainer: args.domNodeHolder.value!.querySelector("#widget-container")! });
          editorInstance.removeContentWidget({
            getId: () => widgetId,
            getPosition: () => widgetPosition,
            getDomNode: () => args.domNodeHolder.value!,
          });
        };
      }

      const reactContainer = document.createElement("div");
      reactContainer.setAttribute("id", "widget-container");
      args.domNodeHolder.value.appendChild(reactContainer);

      return args.domNodeHolder.value;
    },
  };

  editorInstance.addContentWidget(widget);

  args.onReady({ componentContainer: args.domNodeHolder.value!.querySelector("#widget-container")! });
}
