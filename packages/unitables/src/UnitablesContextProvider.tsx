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
import { useRef, useMemo } from "react";
import { UnitablesContext } from "./UnitablesContext";
import { UnitablesRowApi } from "./UnitablesRow";

export function UnitablesContextProvider(props: React.PropsWithChildren<{ rowsInputs: Array<Record<string, any>> }>) {
  const isBeeTableChange: React.MutableRefObject<boolean> = useRef<boolean>(false);
  const rowsRefs = useMemo(() => new Map<number, UnitablesRowApi>(), []);

  return (
    <UnitablesContext.Provider
      value={{
        isBeeTableChange,
        rowsRefs,
        rowsInputs: props.rowsInputs,
      }}
    >
      {props.children}
    </UnitablesContext.Provider>
  );
}
