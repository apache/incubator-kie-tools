/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import { useCallback, useEffect, useRef, useState } from "react";
import { useDmnRunner } from "./DmnRunnerContext";
import { DmnRunnerMode } from "./DmnRunnerStatus";
import { Button } from "@patternfly/react-core";
import { DmnAutoTable } from "@kogito-tooling/unitables";
import { DecisionResult } from "@kogito-tooling/form/dist/dmn";
import { diff } from "deep-object-diff";
import { ListIcon } from "@patternfly/react-icons/dist/js/icons/list-icon";
import { PanelId } from "../EditorPageDockDrawer";

interface Props {
  isReady?: boolean;
  setPanelOpen: React.Dispatch<React.SetStateAction<PanelId>>;
}

function usePrevious(value: any) {
  const ref = useRef();

  useEffect(() => {
    ref.current = value;
  }, [value]);

  return ref.current;
}

export function DmnRunnerTabular(props: Props) {
  const dmnRunner = useDmnRunner();
  const [dmnRunnerResults, setDmnRunnerResults] = useState<Array<DecisionResult[] | undefined>>();

  const updateDmnRunnerResults = useCallback(
    async (tableData: any[]) => {
      if (!props.isReady) {
        return;
      }

      const payloads = await Promise.all(
        tableData.map((data) => {
          if (Object.keys(data).length === 0) {
            return;
          }
          return dmnRunner.preparePayload(data);
        })
      );

      try {
        const results = await Promise.all(
          payloads.map((payload) => {
            if (payload === undefined) {
              return;
            }
            return dmnRunner.service.result(payload);
          })
        );

        const runnerResults: Array<DecisionResult[] | undefined> = [];
        for (const result of results) {
          if (Object.hasOwnProperty.call(result, "details") && Object.hasOwnProperty.call(result, "stack")) {
            dmnRunner.setFormError(true);
            break;
          }
          if (result) {
            runnerResults.push(result.decisionResults);
          }
        }
        setDmnRunnerResults(runnerResults);
      } catch (err) {
        return undefined;
      }
    },
    [props.isReady, dmnRunner.status, dmnRunner.service]
  );

  useEffect(() => {
    updateDmnRunnerResults(dmnRunner.tableData);
  }, [dmnRunner.tableData]);

  const previousFormSchema: any = usePrevious(dmnRunner.formSchema);
  useEffect(() => {
    dmnRunner.setTableData((previousTableData: any) => {
      const newTableData = [...previousTableData];
      const propertiesDifference = diff(
        previousFormSchema?.definitions?.InputSet?.properties ?? {},
        dmnRunner.formSchema?.definitions?.InputSet?.properties ?? {}
      );
      return newTableData.map((tableData) => {
        return Object.entries(propertiesDifference).reduce(
          (form, [property, value]) => {
            if (Object.keys(form).length === 0) {
              return form;
            }
            if (!value || value.type || value.$ref) {
              delete (form as any)[property];
            }
            if (value?.["x-dmn-type"]) {
              (form as any)[property] = undefined;
            }
            return form;
          },
          { ...tableData }
        );
      });
    });
  }, [dmnRunner.formSchema]);

  return (
    <div>
      <Button
        variant={"plain"}
        onClick={() => {
          dmnRunner.setMode(DmnRunnerMode.DRAWER);
          dmnRunner.setDrawerExpanded(true);
          props.setPanelOpen(PanelId.NONE);
        }}
      >
        <ListIcon />
      </Button>
      <DmnAutoTable
        schema={dmnRunner.formSchema}
        tableData={dmnRunner.tableData}
        setTableData={dmnRunner.setTableData}
        results={dmnRunnerResults}
        formError={dmnRunner.formError}
        setFormError={dmnRunner.setFormError}
      />
    </div>
  );
}
