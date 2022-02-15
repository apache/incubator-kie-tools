import { editor, Position } from "monaco-editor";

export interface Holder<T> {
  value?: T;
}

export function openWidget(
  editorInstance: editor.IStandaloneCodeEditor,
  args: {
    backgroundColor: string;
    widgetId: string;
    position: Position;
    domNodeHolder: Holder<HTMLDivElement | null>;
    onReady: (args: { container: HTMLDivElement }) => any;
    onClose: (args: { container: HTMLDivElement }) => any;
  }
) {
  const widgetPosition = {
    position: { lineNumber: args.position.lineNumber, column: args.position.column },
    preference: [editor.ContentWidgetPositionPreference.BELOW, editor.ContentWidgetPositionPreference.ABOVE],
  };

  const widgetId = args.widgetId + Math.random();

  const widget: editor.IContentWidget = {
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
          args.onClose({ container: args.domNodeHolder.value!.querySelector("#widget-container")! });
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

  args.onReady({ container: args.domNodeHolder.value!.querySelector("#widget-container")! });
}
