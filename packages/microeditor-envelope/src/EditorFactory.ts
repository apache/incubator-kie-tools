import { LanguageData } from "appformer-js-microeditor-router/src";
import * as AppFormer from "appformer-js-core";

export interface EditorFactory {
  createEditor(languageData: LanguageData): Promise<AppFormer.Editor>;
}
