export interface GwtEditor {
  getContent(): Promise<string>;
  setContent(content: string): void;
  isDirty(): boolean;
}
