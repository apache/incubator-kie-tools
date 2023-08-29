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

import * as React from "react";
import { ExtractWrappedComponentNames, Wrapped } from "../core";

type Components<Component extends Wrapped<string>> = {
  [K in ExtractWrappedComponentNames<Component>]: React.ReactNode;
};

interface ComponentChildren<Component> {
  [x: number]: string | number | Component;
}

interface Children {
  [x: number]: string | number;
}

interface Props<Component> {
  components: Component extends Wrapped<string> ? Components<Component> : undefined;
  children: Component extends Wrapped<string> ? ComponentChildren<Component> : Children;
}

export function I18nWrapped<Component>(props: Props<Component>) {
  return (
    <>
      {Object.values(props.children).map((piece: any) => {
        if (typeof piece === "string" || typeof piece === "number") {
          return piece;
        }
        return props.components?.[piece.name as ExtractWrappedComponentNames<Component>];
      })}
    </>
  );
}
