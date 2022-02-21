import { editor, Position } from "monaco-editor";

export interface Holder<T> {
  value?: T;
}

export type StoredWidget = {
  close: () => void;
};

const WIDGET_CONTAINER_DIV_ID = "widget-container";

const widgetsStore = new WeakMap<editor.IStandaloneCodeEditor, Map<number, StoredWidget>>();

export function openWidget(
  editorInstance: editor.IStandaloneCodeEditor,
  args: {
    backgroundColor: string;
    widgetId: string;
    position: Position;
    domNodeHolder: Holder<HTMLDivElement | undefined>;
    onReady: (args: { container: HTMLDivElement }) => any;
    onClose: (args: { container: HTMLDivElement }) => any;
  }
) {
  const widgetPosition = {
    position: args.position,
    preference: [editor.ContentWidgetPositionPreference.BELOW, editor.ContentWidgetPositionPreference.ABOVE],
  };

  // Improve this?
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

        // TODO: Improve this "Close" button.
        const button = document.createElement("button");
        args.domNodeHolder.value.appendChild(button);
        button.innerText = "Close";
        button.style.float = "right";
        button.onclick = async () => {
          args.onClose({ container: getWidgetContainer(args.domNodeHolder) });
          editorInstance.removeContentWidget({
            getId: () => widgetId,
            getPosition: () => widgetPosition,
            getDomNode: () => args.domNodeHolder.value!,
          });
        };
      }

      const widgetContainer = document.createElement("div");
      widgetContainer.setAttribute("id", WIDGET_CONTAINER_DIV_ID);
      args.domNodeHolder.value.appendChild(widgetContainer);

      return args.domNodeHolder.value;
    },
  };

  widgetsStore.set(editorInstance, widgetsStore.get(editorInstance) ?? new Map<number, StoredWidget>());
  widgetsStore.get(editorInstance)?.get(args.position.lineNumber)?.close();

  editorInstance.addContentWidget(widget);

  widgetsStore.get(editorInstance)?.set(args.position.lineNumber, {
    close: () => {
      args.onClose({ container: getWidgetContainer(args.domNodeHolder) });
      editorInstance.removeContentWidget(widget);
    },
  });

  args.onReady({ container: getWidgetContainer(args.domNodeHolder) });
}

function getWidgetContainer(domNodeHolder: Holder<HTMLDivElement | undefined>) {
  return domNodeHolder.value!.querySelector<HTMLDivElement>("#" + WIDGET_CONTAINER_DIV_ID)!;
}
