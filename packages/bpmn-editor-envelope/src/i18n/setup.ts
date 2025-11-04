import * as React from "react";
import { useContext } from "react";
import { en } from "./locales";
import { BpmnEditorEnvelopeI18n } from "./BpmnEditorEnvelopeI18n";
import { I18nDefaults, I18nDictionaries } from "@kie-tools-core/i18n/dist/core";
import { I18nContextType } from "@kie-tools-core/i18n/dist/react-components";

export const bpmnEditorEnvelopeI18nDefaults: I18nDefaults<BpmnEditorEnvelopeI18n> = { locale: "en", dictionary: en };
export const bpmnEditorEnvelopeI18nDictionaries: I18nDictionaries<BpmnEditorEnvelopeI18n> = new Map([["en", en]]);
export const BpmnEditorEnvelopeI18nContext = React.createContext<I18nContextType<BpmnEditorEnvelopeI18n>>({} as never);

export function useBpmnEditorEnvelopeI18n(): I18nContextType<BpmnEditorEnvelopeI18n> {
  return useContext(BpmnEditorEnvelopeI18nContext);
}
