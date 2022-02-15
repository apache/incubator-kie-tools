import { editor, Position } from "monaco-editor";

interface Holder<T> {
  value?: T;
}

export function initAugmentationCommands(editorInstance: editor.IStandaloneCodeEditor) {
  return {
    FunctionsCompletion: editorInstance.addCommand(0, (ctx, args) => {
      window.alert("functions autocomplete: " + JSON.stringify(args));
    })!,
    FunctionsWidget: editorInstance.addCommand(0, (ctx, args) => {
      provideWidget(editorInstance, {
        position: args.position,
        widgetId: "swf.functions.widget",
        title: "My functions widget",
        color: "lightgreen",
        domNodeHolder: {},
      });
    })!,
    StatesWidget: editorInstance.addCommand(0, (ctx, args) => {
      provideWidget(editorInstance, {
        position: args.position,
        widgetId: "swf.states.widget",
        title: "My states widget",
        color: "lightblue",
        domNodeHolder: {},
      });
    })!,
  };
}

function provideWidget(
  editorInstance: editor.IStandaloneCodeEditor,
  args: {
    color: string;
    widgetId: string;
    title: string;
    position: Position;
    domNodeHolder: Holder<HTMLDivElement | null>;
  }
) {
  const position = {
    position: { lineNumber: args.position.lineNumber, column: args.position.column },
    preference: [editor.ContentWidgetPositionPreference.BELOW, editor.ContentWidgetPositionPreference.ABOVE],
  };

  const widget = {
    getId: () => args.widgetId,
    getPosition: () => position,
    getDomNode: () => {
      if (!args.domNodeHolder.value) {
        args.domNodeHolder.value = document.createElement("div");
        args.domNodeHolder.value.innerHTML = args.title;
        args.domNodeHolder.value.style.background = args.color;
        args.domNodeHolder.value.style.userSelect = "text";
        args.domNodeHolder.value.style.width = "99999px"; // This is restrained by the max-width, so this is basically width: 100%;
        args.domNodeHolder.value.style.zIndex = "99999"; // This makes the cursor not blink on top of the widget.
        args.domNodeHolder.value.style.marginTop = "-1.2em"; // Go up one line (approx.);
        const button = document.createElement("button");
        args.domNodeHolder.value.appendChild(button);
        button.innerText = "Click to close";
        button.onclick = () => {
          editorInstance.removeContentWidget({
            getId: () => args.widgetId,
            getPosition: () => position,
            getDomNode: () => args.domNodeHolder.value!,
          });
        };
      }

      return args.domNodeHolder.value;
    },
  };

  editorInstance.removeContentWidget(widget);
  editorInstance.addContentWidget(widget);
}
