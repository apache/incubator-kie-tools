/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { TranslationBundle } from "@kogito-tooling/i18n";
import { NamesBundle } from "./names";

// tslint:disable-next-line:interface-over-type-literal
export type TermsBundle = {
  cancel: string;
  close: string;
  download: string;
  exit: string;
  save: string;
  os: {
    initials: string;
    full: string;
  };
  fullScreen: string;
  edited: string;
  reset: string;
  continue: string;
  token: string;
  note: string;
  poweredBy: string;
  or: string;
};

export interface TermsI18n extends TranslationBundle<TermsI18n> {
  terms: TermsBundle;
  names: NamesBundle;
}
