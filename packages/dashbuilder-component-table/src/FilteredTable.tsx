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
import { TableComposable, Thead, Tbody, Tr, Th, Td, selectable } from "@patternfly/react-table";

import { useState, useCallback, useMemo } from "react";
import { Flex, FlexItem, Pagination, SearchInput } from "@patternfly/react-core";

const DEFAULT_PAGE = 1;
const DEFAULT_PER_PAGE = 10;

enum AlertColors {
  DANGER = "red",
  GOOD = "blue",
  GREAT = "green",
}

export interface Alert {
  danger: string;
  good: string;
  great: string;
}

interface Props {
  columns: string[];
  rows: any[][];
  onRowSelected?: (i: number) => void;
  selectable?: boolean;
  alerts?: Map<number, Alert>;
}

interface Sort {
  index: number;
  order: "asc" | "desc" | undefined;
}

export const FilteredTable = (props: Props) => {
  // PAGING
  const [page, setPage] = useState(DEFAULT_PAGE);
  const [perPage, setPerPage] = useState(DEFAULT_PER_PAGE);

  // SORTING
  const [activeSortIndex, setActiveSortIndex] = useState(-1);
  const [activeSortDirection, setActiveSortDirection] = useState();

  // SEARCH
  const [search, setSearch] = useState("");

  const [selectedRow, setSelectedRow] = useState<any[]>([]);

  const rows = useMemo(() => {
    let filteredRows: any[][] = [];

    // Filter
    if (search !== "") {
      props.rows.forEach((row) => {
        const value = row.join().toLowerCase();
        if (value.indexOf(search.toLowerCase()) !== -1) {
          filteredRows.push(row);
        }
      });
    } else {
      filteredRows = props.rows;
    }

    // Sorting
    if (activeSortIndex !== -1) {
      filteredRows = filteredRows.sort((aList, bList) => {
        const a = aList[activeSortIndex];
        const b = bList[activeSortIndex];
        if (typeof a === "number") {
          // numeric sort
          if (activeSortDirection === "asc") {
            return a - b;
          }
          return b - a;
        } else {
          // string sort
          if (activeSortDirection === "asc") {
            return a.localeCompare(b);
          }
          return b.localeCompare(a);
        }
      });
    }
    return filteredRows;
  }, [search, activeSortIndex, activeSortDirection, props]);

  const onSort = useCallback((event: any, index: any, direction: any) => {
    setActiveSortIndex(index);
    setActiveSortDirection(direction);
  }, []);

  const isSelectedRow = useCallback(
    (row: any[]) => {
      return (
        selectedRow &&
        selectedRow.length > 0 &&
        selectedRow.length === row.length &&
        selectedRow.every((v, index) => v === row[index])
      );
    },
    [selectedRow]
  );

  const onSearch = useCallback((_search: string) => {
    setSearch(_search);
    setPage(DEFAULT_PAGE);
    setPerPage(DEFAULT_PER_PAGE);
  }, []);

  const cellColor = useCallback(
    (value: any, i: number) => {
      const alert = props.alerts?.get(i);
      const str = value.toString();
      if (str === alert?.danger) {
        return AlertColors.DANGER;
      }

      if (str === alert?.good) {
        return AlertColors.GOOD;
      }

      if (str === alert?.great) {
        return AlertColors.GREAT;
      }
      return "";
    },
    [props.alerts]
  );

  return (
    <>
      <Flex>
        <FlexItem>
          {" "}
          <SearchInput
            placeholder="Filter"
            value={search}
            onChange={(v: any) => onSearch(v as string)}
            onClear={() => onSearch("")}
          />
        </FlexItem>
        <FlexItem align={{ default: "alignRight" }}>
          <Pagination
            itemCount={rows.length}
            perPage={perPage}
            page={page}
            onSetPage={(evt: any, _page: any) => setPage(_page)}
            onPerPageSelect={(evt: any, _perPage: any) => setPerPage(_perPage)}
            widgetId="pagination-options-menu-top"
          />
        </FlexItem>
      </Flex>
      <TableComposable aria-label="Filtered Table" variant="compact">
        <Thead>
          <Tr>
            {props.columns.map((column, columnIndex) => {
              const sortParams = {
                sort: {
                  sortBy: {
                    index: activeSortIndex,
                    direction: activeSortDirection,
                  },
                  onSort,
                  columnIndex,
                },
              };
              return (
                <Th key={columnIndex} {...sortParams}>
                  {column}
                </Th>
              );
            })}
          </Tr>
        </Thead>
        <Tbody>
          {rows?.slice((page - 1) * perPage, (page - 1) * perPage + perPage).map((row, rowIndex) => (
            <Tr
              key={rowIndex}
              isHoverable={props.selectable}
              onClick={() => {
                if (props.selectable) {
                  if (isSelectedRow(row)) {
                    setSelectedRow([]);
                    props.onRowSelected!(-1);
                  } else {
                    setSelectedRow(row);
                    props.onRowSelected!(rowIndex);
                  }
                }
              }}
              selected={isSelectedRow(row)}
              style={
                isSelectedRow(row) && props.selectable
                  ? {
                      backgroundColor: "#DFDFDF",
                    }
                  : {}
              }
            >
              {row.map((cell, cellIndex) => (
                <Td
                  key={`${rowIndex}_${cellIndex}`}
                  dataLabel={props.columns[cellIndex]}
                  style={{ color: cellColor(cell, cellIndex) }}
                >
                  {cell}
                </Td>
              ))}
            </Tr>
          ))}
        </Tbody>
      </TableComposable>
    </>
  );
};
