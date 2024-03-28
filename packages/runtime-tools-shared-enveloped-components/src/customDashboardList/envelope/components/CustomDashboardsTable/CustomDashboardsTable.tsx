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

import React, { useEffect, useState } from "react";
import { DataTable, DataTableColumn } from "@kie-tools/runtime-tools-components/dist/components/DataTable";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { CustomDashboardInfo } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { CustomDashboardListDriver } from "../../../api";
import { getDashboardNameColumn, getDateColumn } from "../CustomDashboardListUtils/CustomDashboardListUtils";
import _ from "lodash";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { ISortBy } from "@patternfly/react-table/dist/js/components/Table";

export interface CustomDashboardTableProps {
  driver: CustomDashboardListDriver;
  customDashboardData: CustomDashboardInfo[];
  setDashboardsData: React.Dispatch<React.SetStateAction<CustomDashboardInfo[]>>;
  isLoading: boolean;
}
interface SortBy {
  property: string;
  direction: "asc" | "desc";
}

const CustomDashboardsTable: React.FC<CustomDashboardTableProps & OUIAProps> = ({
  driver,
  customDashboardData,
  setDashboardsData,
  isLoading,
  ouiaId,
  ouiaSafe,
}) => {
  const [columns] = useState<DataTableColumn[]>([
    getDashboardNameColumn(
      (customDashboardInfo: CustomDashboardInfo): Promise<void> => driver.openDashboard(customDashboardInfo)
    ),
    getDateColumn("lastModified", "Last Modified"),
  ]);
  const [sortBy, setSortBy] = useState<SortBy>({
    property: "lastModified",
    direction: "desc",
  });

  useEffect(() => {
    /* istanbul ignore else */
    if (!_.isEmpty(customDashboardData)) {
      onSort(2, "desc");
    }
  }, [isLoading]);

  const getSortBy = (): ISortBy => {
    return {
      index: columns.findIndex((column) => column.path === sortBy.property),
      direction: sortBy.direction,
    };
  };

  const onSort = async (index: number, direction: "asc" | "desc"): Promise<void> => {
    const sortObj: SortBy = {
      property: columns[index - 1].path,
      direction: direction,
    };

    const sortedData = _.orderBy(
      customDashboardData,
      _.keys({
        [sortObj.property]: sortObj.direction,
      }),
      _.values({
        [sortObj.property]: sortObj.direction,
      })
    );
    setDashboardsData(sortedData);
    setSortBy(sortObj);
  };

  const customDashboardLoadingComponent: JSX.Element = (
    <Bullseye>
      <KogitoSpinner spinnerText="Loading Dashboard..." ouiaId="custom-dashboard-list-custom-dashboard-list" />
    </Bullseye>
  );

  return (
    <div {...componentOuiaProps(ouiaId, "customDashboard-table", ouiaSafe)}>
      <DataTable
        data={customDashboardData}
        isLoading={isLoading}
        columns={columns}
        error={false}
        sortBy={getSortBy()}
        onSorting={onSort}
        LoadingComponent={customDashboardLoadingComponent}
      />
    </div>
  );
};

export default CustomDashboardsTable;
