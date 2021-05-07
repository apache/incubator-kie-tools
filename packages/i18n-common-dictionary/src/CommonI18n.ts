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

import { ReferenceDictionary } from "@kogito-tooling/i18n/dist/core";
import { names } from "./names";

// tslint:disable-next-line:interface-over-type-literal
export type CommonDictionary = {
  available: string;
  cancel: string;
  close: string;
  continue: string;
  copy: string;
  cut: string;
  dismiss: string;
  done: string;
  download: string;
  edit: string;
  edited: string;
  exit: string;
  file: string;
  files: string;
  forum: string;
  fullScreen: string;
  install: string;
  launch: string;
  loading: string;
  new: string;
  note: string;
  open: string;
  os: {
    initials: string;
    full: string;
  };
  paste: string;
  poweredBy: string;
  quit: string;
  redo: string;
  reset: string;
  save: string;
  token: string;
  undo: string;
  uninstall: string;
};

export interface CommonI18n extends ReferenceDictionary {
  names: typeof names;
  terms: CommonDictionary;
}
