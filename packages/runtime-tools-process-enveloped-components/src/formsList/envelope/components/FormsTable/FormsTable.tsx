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

import React, { useCallback, useEffect, useMemo, useState } from "react";
import { DataTable, DataTableColumn } from "@kie-tools/runtime-tools-components/dist/components/DataTable";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import { FormsListChannelApi } from "../../../api";
import { getFormNameColumn, getDateColumn, getFormTypeColumn } from "../FormsListUtils/FormsListUtils";
import _ from "lodash";
import { ISortBy } from "@patternfly/react-table/dist/js/components/Table";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { FormInfo } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";

export interface FormsTableProps {
  channelApi: MessageBusClientApi<FormsListChannelApi>;
  formsData: FormInfo[];
  setFormsData: React.Dispatch<React.SetStateAction<FormInfo[]>>;
  isLoading: boolean;
}
interface SortBy {
  property: string;
  direction: "asc" | "desc";
}

const FormsTable: React.FC<FormsTableProps> = ({ channelApi, formsData, setFormsData, isLoading }) => {
  const [columns] = useState<DataTableColumn[]>([
    getFormNameColumn((formData: FormInfo) => channelApi.notifications.formsList__openForm.send(formData)),
    getFormTypeColumn(),
    getDateColumn("lastModified", "Last Modified"),
  ]);
  const [sortBy, setSortBy] = useState<SortBy>({
    property: "lastModified",
    direction: "desc",
  });

  const dataTableSortBy = useMemo((): ISortBy => {
    return {
      index: columns.findIndex((column) => column.path === sortBy.property),
      direction: sortBy.direction,
    };
  }, [columns, sortBy.direction, sortBy.property]);

  const onSort = useCallback(
    async (index: number, direction: "asc" | "desc"): Promise<void> => {
      const sortObj: SortBy = {
        property: columns[index].path,
        direction: direction,
      };

      const sortedData = _.orderBy(
        formsData,
        _.keys({
          [sortObj.property]: sortObj.direction,
        }),
        _.values({
          [sortObj.property]: sortObj.direction,
        })
      );
      setFormsData(sortedData);
      setSortBy(sortObj);
    },
    [columns, formsData, setFormsData]
  );

  useEffect(() => {
    if (!_.isEmpty(formsData)) {
      onSort(2, "desc");
    }
  }, [isLoading]);

  const formsLoadingComponent: JSX.Element = (
    <Bullseye>
      <KogitoSpinner spinnerText="Loading forms..." ouiaId="forms-list-loading-forms" />
    </Bullseye>
  );

  return (
    <div>
      <DataTable
        data={formsData}
        isLoading={isLoading}
        columns={columns}
        error={false}
        sortBy={dataTableSortBy}
        onSorting={onSort}
        LoadingComponent={formsLoadingComponent}
      />
    </div>
  );
};

export default FormsTable;
