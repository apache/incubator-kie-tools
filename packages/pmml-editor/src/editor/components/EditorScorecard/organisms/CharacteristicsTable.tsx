/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import * as React from "react";
import { useMemo } from "react";
import { Characteristic } from "@kogito-tooling/pmml-editor-marshaller";
import { ICell, IComputedData, IExtraRowData, IRow, Table, TableBody, TableHeader } from "@patternfly/react-table";
import { EmptyStateNoCharacteristics } from "./EmptyStateNoCharacteristics";

interface CharacteristicsTableProps {
  characteristics: Characteristic[];
  onRowClick: (index: number) => void;
  onAddCharacteristic: () => void;
}

const columns: ICell[] = [
  { title: "Name" },
  { title: "Attributes" },
  { title: "Reason Code" },
  { title: "Baseline score" }
];

export const CharacteristicsTable = (props: CharacteristicsTableProps) => {
  const { characteristics, onRowClick, onAddCharacteristic } = props;

  const rows: IRow[] = useMemo(() => {
    return characteristics.map<IRow>((c, index) => {
      return { cells: [c.name, c.Attribute.length, c.reasonCode, c.baselineScore] };
    });
  }, [characteristics]);

  const rowClickHandler = (
    event: React.MouseEvent,
    row: IRow,
    rowProps: IExtraRowData,
    computedData: IComputedData
  ) => {
    window.alert(JSON.stringify(row, undefined, 2));
    window.alert(JSON.stringify(rowProps, undefined, 2));
  };

  return (
    <React.Fragment>
      <Table cells={columns} rows={rows}>
        <TableHeader />
        <TableBody onRowClick={rowClickHandler} />
      </Table>
      {rows.length === 0 && <EmptyStateNoCharacteristics createCharacteristic={onAddCharacteristic} />}
    </React.Fragment>
  );
};
