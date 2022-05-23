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

import * as React from "react";
import { ColumnType, ComponentController, DataSet } from "@kie-tools/dashbuilder-component-api";
import { useState, useEffect, useMemo } from "react";
import { Alert, FilteredTable } from "./FilteredTable";

interface Props {
  controller: ComponentController;
}
export function FilteredTableComponent(props: Props) {
  const [dataset, setDataset] = useState<DataSet>();
  const [filterColumn, setFilterColumn] = useState(0);
  const [selectable, setSelectable] = useState<boolean>(false);
  const alerts = useMemo(() => new Map<number, Alert>(), []);

  useEffect(() => {
    props.controller.setOnInit((params: Map<string, any>) => {
      // init
      const selectableParam = params.get("selectable") === "true";
      const filterColumnParam = +params.get("filterColumn") || 0;
      const columnStr = params.get("alertColumn");

      if (columnStr && columnStr !== "") {
        alerts.set(+columnStr, {
          danger: params.get("alertDanger"),
          good: params.get("alertGood"),
          great: params.get("alertGreat"),
        });
      }

      setSelectable(selectableParam);
      setFilterColumn(filterColumnParam);
    });
    props.controller.setOnDataSet((_dataset: DataSet) => {
      setDataset(_dataset);
    });
  }, [props.controller, alerts]);

  const rows = useMemo(() => {
    const _rows: any[][] = [];
    dataset?.data.forEach((row, i) => {
      const values: any[] = [];
      row.forEach((v, j) => {
        const column = dataset?.columns[j];
        if (!v || v === "") {
          values.push(column.settings.emptyTemplate);
        } else {
          const value = column.type === ColumnType.NUMBER ? +v : v;
          values.push(value);
        }
      });
      _rows.push(values);
    });
    return _rows;
  }, [dataset]);

  const columns = useMemo(() => {
    return dataset?.columns.map((c) => c.settings.columnName) || [];
  }, [dataset]);

  return (
    <>
      <FilteredTable
        columns={columns}
        rows={rows}
        alerts={alerts}
        selectable={selectable}
        onRowSelected={(i: number) => {
          console.log(i);
          if (i === -1) {
            props.controller.filter({ reset: true, column: filterColumn, row: 0 });
          } else {
            props.controller.filter({ reset: true, column: filterColumn, row: 0 });
            props.controller.filter({ reset: false, column: filterColumn, row: i });
          }
        }}
      />
    </>
  );
}
