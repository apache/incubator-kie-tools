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
import { useCallback, useMemo } from "react";
import { Characteristic } from "@kogito-tooling/pmml-editor-marshaller";
import {
  ICell,
  IRow,
  OnRowClick,
  RowWrapperProps,
  RowWrapperRow,
  Table,
  TableBody,
  TableHeader
} from "@patternfly/react-table";
import { EmptyStateNoCharacteristics } from "./EmptyStateNoCharacteristics";
import { Button, Label } from "@patternfly/react-core";
import { TrashIcon } from "@patternfly/react-icons";
import "./CharacteristicsTable.scss";
import styles from "@patternfly/react-styles/css/components/Table/table";
import inlineStyles from "@patternfly/react-styles/css/components/InlineEdit/inline-edit";
import { css } from "@patternfly/react-styles";
import { getOUIAProps } from "@patternfly/react-core/dist/js/helpers/ouia";

export interface IndexedCharacteristic {
  index: number;
  characteristic: Characteristic;
}

interface CharacteristicsTableProps {
  characteristics: IndexedCharacteristic[];
  selectedCharacteristic: IndexedCharacteristic | undefined;
  onRowClick: (index: number) => void;
  onRowDelete: (index: number) => void;
  onAddCharacteristic: () => void;
}

const columns: ICell[] = [
  { title: "Name" },
  { title: "Attributes" },
  { title: "Reason Code" },
  { title: "Baseline score" },
  { title: <div>&nbsp;</div> }
];

export const CharacteristicsTable = (props: CharacteristicsTableProps) => {
  const { characteristics, selectedCharacteristic, onRowClick, onRowDelete, onAddCharacteristic } = props;

  const rows: IRow[] = useMemo(() => {
    return characteristics.map<IRow>((ic, index) => {
      const c: Characteristic = ic.characteristic;
      return {
        props: { index: ic.index },
        cells: [
          c.name,
          <>
            <Label key={index}>{c.Attribute.length}</Label>
          </>,
          c.reasonCode,
          c.baselineScore,
          <>
            <Button variant="link" icon={<TrashIcon />}>
              &nbsp;
            </Button>
          </>
        ]
      };
    });
  }, [characteristics]);

  const rowClickHandler: OnRowClick = useCallback(
    (event, row) => {
      const index = row.props.index;
      if (event.target instanceof HTMLButtonElement) {
        event.stopPropagation();
        onRowDelete(index);
        return;
      }
      onRowClick(index);
    },
    [characteristics]
  );

  const rowWrapper = useCallback(
    (rowWrapperProps: RowWrapperProps) => {
      const { row, rowProps, trRef, className, ouiaId, ...additionalProps } = rowWrapperProps;
      const { isExpanded, isEditable } = row as RowWrapperRow;
      const isSelectedRow = rowProps?.rowIndex === selectedCharacteristic?.index;

      return (
        <tr
          {...additionalProps}
          ref={trRef as React.Ref<any>}
          className={css(
            className,
            isExpanded !== undefined && styles.tableExpandableRow,
            isExpanded && styles.modifiers.expanded,
            isEditable && inlineStyles.modifiers.inlineEditable,
            isSelectedRow && "characteristics__table__row__selected"
          )}
          hidden={isExpanded !== undefined && !isExpanded}
          {...getOUIAProps("TableRow", ouiaId)}
          style={{ backgroundColor: isSelectedRow ? "red" : "white" }}
        />
      );
    },
    [selectedCharacteristic]
  );

  return (
    <React.Fragment>
      <Table
        aria-label="Characteristics"
        cells={columns}
        rows={rows}
        className="characteristics__table"
        rowWrapper={rowWrapper}
      >
        <TableHeader />
        <TableBody onRowClick={rowClickHandler} />
      </Table>
      {rows.length === 0 && <EmptyStateNoCharacteristics createCharacteristic={onAddCharacteristic} />}
    </React.Fragment>
  );
};
