/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { ReferenceDictionary } from "@kie-tools-core/i18n/dist/core";
import { names } from "./names";

export type CommonDictionary = {
  apply: string;
  available: string;
  back: string;
  cancel: string;
  change: string;
  close: string;
  configure: string;
  confirm: string;
  connected: string;
  continue: string;
  copy: string;
  cut: string;
  deploy: string;
  disconnected: string;
  dismiss: string;
  done: string;
  download: string;
  edit: string;
  edited: string;
  evaluation: string;
  exit: string;
  file: string;
  files: string;
  forum: string;
  fullScreen: string;
  host: string;
  inputs: string;
  install: string;
  launch: string;
  loading: string;
  macosApplicationFolder: string;
  namespace: string;
  new: string;
  next: string;
  note: string;
  open: string;
  oops: string;
  os: {
    initials: string;
    full: string;
  };
  outputs: string;
  paste: string;
  poweredBy: string;
  quit: string;
  readonly: string;
  redo: string;
  reset: string;
  run: string;
  save: string;
  selection: string;
  setup: string;
  start: string;
  token: string;
  undo: string;
  uninstall: string;
  username: string;
  validation: string;
};

export interface CommonI18n extends ReferenceDictionary {
  names: typeof names;
  terms: CommonDictionary;
}
