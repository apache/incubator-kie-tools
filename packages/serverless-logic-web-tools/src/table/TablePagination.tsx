/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
import {
  OnPerPageSelect,
  Pagination,
  PaginationProps,
  PerPageOptions,
} from "@patternfly/react-core/dist/js/components/Pagination";
import { useCallback } from "react";

export const defaultPerPageOptions: PerPageOptions[] = [5, 10, 20, 50, 100].map((n) => ({
  title: n.toString(),
  value: n,
}));

export type TablePaginationProps = Pick<PaginationProps, "variant" | "isCompact" | "perPageOptions"> & {
  itemCount: number;
  page: number;
  perPage: number;
  setPage: (newPage: number) => void;
  setPerPage: (newPerPage: number) => void;
};

export function TablePagination(props: TablePaginationProps) {
  const { isCompact, itemCount, page, perPage, perPageOptions, setPage, setPerPage, variant } = props;

  const onPerPageSelect: OnPerPageSelect = useCallback(
    (_e, v) => {
      // When changing the number of results per page, keep the start row approximately the same
      const firstRow = (page - 1) * perPage;
      setPage(Math.floor(firstRow / v) + 1);
      setPerPage(v);
    },
    [page, perPage, setPage, setPerPage]
  );

  return (
    <Pagination
      isCompact={isCompact}
      itemCount={itemCount}
      onPerPageSelect={onPerPageSelect}
      onSetPage={(_e, v) => setPage(v)}
      page={page}
      perPage={perPage}
      perPageOptions={perPageOptions}
      variant={variant}
    />
  );
}
