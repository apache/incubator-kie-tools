import { KogitoEditor } from "./KogitoEditor";

export class KogitoEditorStore {
  public activeEditor?: KogitoEditor;
  public openEditors: Set<KogitoEditor>;

  constructor() {
    this.openEditors = new Set();
  }

  public addAsActive(editor: KogitoEditor) {
    this.activeEditor = editor;
    this.openEditors.add(editor);
  }

  public setActive(editor: KogitoEditor) {
    this.activeEditor = editor;
  }

  public isActive(editor: KogitoEditor) {
    return this.activeEditor === editor;
  }

  public setNoneActive() {
    this.activeEditor = undefined;
  }

  public withActive(consumer: (activeEditor: KogitoEditor) => void) {
    if (this.activeEditor) {
      consumer(this.activeEditor);
    }
  }

  public close(editor: KogitoEditor) {
    if (this.isActive(editor)) {
      this.setNoneActive();
    }

    this.openEditors.delete(editor);
  }
}
