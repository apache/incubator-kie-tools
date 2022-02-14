import { editor, Position } from "monaco-editor";

export function initCommands(instance: editor.IStandaloneCodeEditor) {
  return {
    FunctionsCompletion: instance.addCommand(0, (ctx, args) => {
      window.alert("functions autocomplete: " + JSON.stringify(args));
    })!,
    FunctionsWidget: instance.addCommand(0, (ctx, args) => {
      let domNode: HTMLDivElement | null;

      instance.addContentWidget({
        getId: () => "swf.functions.widget",
        getDomNode: () => {
          if (!domNode) {
            domNode = document.createElement("div");
            domNode.innerHTML = "My functions widget";
            domNode.style.background = "lightgreen";
          }
          return domNode;
        },
        getPosition: () => getWidgetPosition(args.position),
      });
    })!,
    StatesWidget: instance.addCommand(0, (ctx, args) => {
      let domNode: HTMLDivElement | null;

      instance.addContentWidget({
        getId: () => "swf.states.widget",
        getDomNode: () => {
          if (!domNode) {
            domNode = document.createElement("div");
            domNode.innerHTML = "My states widget";
            domNode.style.background = "lightblue";
          }
          return domNode;
        },
        getPosition: () => getWidgetPosition(args.position),
      });
    })!,
  };
}

function getWidgetDomNode() {}

function getWidgetPosition(position: Position) {
  return {
    position: {
      lineNumber: position.lineNumber,
      column: 0,
    },
    preference: [editor.ContentWidgetPositionPreference.BELOW, editor.ContentWidgetPositionPreference.ABOVE],
  };
}
