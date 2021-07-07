/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import * as ReactDOMServer from "react-dom/server";
import { AutoForm } from "./index";
import { Bridge } from "uniforms";
import unescape from "lodash/unescape";

interface Args {
  id: string;
  disabled?: boolean;
  placeholder?: boolean;
  schema: Bridge;
}

export function renderForm(args: Args): string {
  const form = React.createElement(AutoForm, { ...args });

  return unescape(ReactDOMServer.renderToString(form));
}
