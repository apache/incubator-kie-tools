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
import { useEffect, useMemo, useRef } from "react";

import { UnitablesWrapper } from "@kie-tools/unitables/dist/UnitablesWrapper";

import { TestScenarioUnitablesValidator } from "./unitables/TestScenarioUnitablesValidator";
import { useTestScenarioEditorI18n } from "../i18n";

function TestScenarioTable({ fileName }: { fileName?: string }) {
  const { i18n } = useTestScenarioEditorI18n();

  const inputsScrollableElementRef = useRef<{ current: HTMLDivElement | null }>({ current: null });
  const outputsScrollableElementRef = useRef<{ current: HTMLDivElement | null }>({ current: null });

  useEffect(() => {
    inputsScrollableElementRef.current.current =
      document.querySelector(".kie-tools--dmn-runner-table--drawer")?.querySelector(".pf-c-drawer__content") ?? null;
    outputsScrollableElementRef.current.current =
      document.querySelector(".kie-tools--dmn-runner-table--drawer")?.querySelector(".pf-c-drawer__panel-main") ?? null;
  }, []);

  const jsonSchemaBridge = useMemo(() => {
    try {
      return new TestScenarioUnitablesValidator(i18n.testScenarioGrid.table).getBridge();
    } catch (err) {
      throw Error(`getBridge ${err}`);
    }
  }, [i18n]);

  return (
    <UnitablesWrapper
      rows={[{ uno: "prova", due: "maoo" }]}
      setRows={function (
        previousStateFunction: Record<string, any>[] | ((previous: Record<string, any>[]) => Record<string, any>[])
      ): void {
        console.log("Function not implemented. 1 ");
      }}
      error={false}
      setError={function (value: React.SetStateAction<boolean>): void {
        console.log("Function not implemented. 2 value:" + value);
      }}
      openRow={function (rowIndex: number): void {
        console.log("Function not implemented. 3");
      }}
      i18n={i18n.testScenarioGrid.table}
      jsonSchemaBridge={jsonSchemaBridge}
      scrollableParentRef={inputsScrollableElementRef.current}
      onRowAdded={function (args: { beforeIndex: number }): void {
        console.log("Function not implemented. 4");
      }}
      onRowDuplicated={function (args: { rowIndex: number }): void {
        console.log("Function not implemented.5 ");
      }}
      onRowReset={function (args: { rowIndex: number }): void {
        console.log("Function not implemented.6");
      }}
      onRowDeleted={function (args: { rowIndex: number }): void {
        console.log("Function not implemented.7");
      }}
      configs={{}}
      setWidth={function (newWidth: number, fieldName: string): void {
        throw new Error("Function not implemented.");
      }}
    />
  );
}

export default TestScenarioTable;
