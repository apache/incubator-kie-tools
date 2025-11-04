import * as React from "react";
import { useContext } from "react";
import { I18nContextType } from "@kie-tools-core/i18n/dist/react-components";
import { KieDiagramI18n } from "./KieDiagramI18n";
import { en } from "./locales";
import { I18n, I18nDefaults, I18nDictionaries } from "@kie-tools-core/i18n/dist/core";

export const kieDiagramI18nDefaults: I18nDefaults<KieDiagramI18n> = { locale: "en", dictionary: en };
export const kieDiagramI18nDictionaries: I18nDictionaries<KieDiagramI18n> = new Map([["en", en]]);

export const KieDiagramI18nContext = React.createContext<I18nContextType<KieDiagramI18n>>({} as never);
export const kieDiagramI18n = new I18n(kieDiagramI18nDefaults, kieDiagramI18nDictionaries);

export function useKieDiagramI18n(): I18nContextType<KieDiagramI18n> {
  return useContext(KieDiagramI18nContext);
}
