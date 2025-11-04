import * as React from "react";
import { useContext } from "react";
import { en } from "./locales";
import { I18nContextType } from "@kie-tools-core/i18n/dist/react-components";
import { BpmnEditorI18n } from "./BpmnEditorI18n";
import { I18nDefaults, I18nDictionaries } from "@kie-tools-core/i18n/dist/core";

export const bpmnEditorI18nDefaults: I18nDefaults<BpmnEditorI18n> = {
  locale: "en",
  dictionary: en,
};
export const bpmnEditorDictionaries: I18nDictionaries<BpmnEditorI18n> = new Map([["en", en]]);
export const BpmnEditorI18nContext = React.createContext<I18nContextType<BpmnEditorI18n>>({} as never);

export function useBpmnEditorI18n(): I18nContextType<BpmnEditorI18n> {
  return useContext(BpmnEditorI18nContext);
}
