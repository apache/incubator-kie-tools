import { editor, Position } from "monaco-editor";

interface Holder<T> {
  value: T;
}

export function initCommands(instance: editor.IStandaloneCodeEditor) {
  return {
    FunctionsCompletion: instance.addCommand(0, (ctx, args) => {
      window.alert("functions autocomplete: " + JSON.stringify(args));
    })!,
    FunctionsWidget: instance.addCommand(0, (ctx, args) => {
      provideWidget(instance, {
        position: args.position,
        widgetId: "swf.functions.widget",
        title: "My functions widget",
        color: "lightgreen",
        domNodeHolder: { value: null },
      });
    })!,
    StatesWidget: instance.addCommand(0, (ctx, args) => {
      provideWidget(instance, {
        position: args.position,
        widgetId: "swf.states.widget",
        title: "My states widget",
        color: "lightblue",
        domNodeHolder: { value: null },
      });
    })!,
  };
}

function provideWidget(
  instance: editor.IStandaloneCodeEditor,
  args: {
    color: string;
    widgetId: string;
    title: string;
    position: Position;
    domNodeHolder: Holder<HTMLDivElement | null>;
  }
) {
  const widget = {
    getId: () => args.widgetId,
    getDomNode: () => {
      if (!args.domNodeHolder.value) {
        args.domNodeHolder.value = document.createElement("div");
        args.domNodeHolder.value.innerHTML = args.title;
        args.domNodeHolder.value.style.background = args.color;
        args.domNodeHolder.value.style.userSelect = "text";
        const button = document.createElement("button");
        button.innerText = "Click to close";
        button.onclick = () => {
          instance.removeContentWidget({
            getId: () => args.widgetId,
            getDomNode: () => args.domNodeHolder.value!,
            getPosition: () => ({
              position: {
                lineNumber: args.position.lineNumber,
                column: args.position.column,
              },
              preference: [editor.ContentWidgetPositionPreference.BELOW, editor.ContentWidgetPositionPreference.ABOVE],
            }),
          });
        };
        args.domNodeHolder.value.appendChild(button);
      }
      return args.domNodeHolder.value;
    },
    getPosition: () => ({
      position: {
        lineNumber: args.position.lineNumber,
        column: args.position.column,
      },
      preference: [editor.ContentWidgetPositionPreference.BELOW, editor.ContentWidgetPositionPreference.ABOVE],
    }),
  };

  instance.removeContentWidget(widget);
  instance.addContentWidget(widget);
}
