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
import { css } from "@patternfly/react-styles";
import { ICell, IRow, Table, TableBody, TableHeader } from "@patternfly/react-table";
import styles from "@patternfly/react-styles/css/components/Table/table";
import { BaselineMethod } from "@kogito-tooling/pmml-editor-marshaller";

const columns: Array<ICell | string> = new Array<ICell | string>();
const rows: Array<IRow | string[]> = new Array<IRow | string[]>();

interface CorePropertiesTableProps {
  baselineScore: number | undefined;
  baselineMethod: BaselineMethod | undefined;
  initialScore: number | undefined;
  useReasonCodes: boolean | undefined;
}

export const CorePropertiesTable = (props: CorePropertiesTableProps) => {
  columns.push(
    { title: "Baseline Score" },
    { title: "Baseline Method" },
    { title: "Initial Score" },
    { title: "Use Reason Code" }
  );
  rows.push([props.baselineScore, props.baselineMethod, props.initialScore, props.useReasonCodes]);

  return (
    <Table aria-label="Simple Table" cells={columns} rows={rows}>
      <TableHeader className={css(styles.modifiers.nowrap)} />
      <TableBody />
    </Table>
  );
};
