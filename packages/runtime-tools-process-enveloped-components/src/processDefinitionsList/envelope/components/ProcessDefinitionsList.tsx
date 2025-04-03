/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React, { useCallback, useEffect, useState, useMemo } from "react";
import { ProcessDefinitionsListDriver } from "../../api";
import ProcessDefinitionsListToolbar from "./ProcessDefinitionsListToolbar";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import "./styles.css";
import {
  ProcessDefinitionsListState,
  ProcessDefinitionsFilter,
  ProcessDefinition,
} from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { ServerErrors } from "@kie-tools/runtime-tools-components/dist/components/ServerErrors";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { DataTable, DataTableColumn } from "@kie-tools/runtime-tools-components/dist/components/DataTable";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import { getActionColumn, getColumn } from "./ProcessDefinitionsListUtils";

interface ProcessDefinitionsListProps {
  isEnvelopeConnectedToChannel: boolean;
  driver: ProcessDefinitionsListDriver;
  initialState: ProcessDefinitionsListState;
  singularProcessLabel: string;
}
const ProcessDefinitionsList: React.FC<ProcessDefinitionsListProps> = ({
  driver,
  isEnvelopeConnectedToChannel,
  initialState,
  singularProcessLabel,
}) => {
  const defaultFilters: ProcessDefinitionsFilter = useMemo(
    () =>
      initialState && initialState.filters
        ? { ...initialState.filters }
        : {
            processNames: [],
          },
    [initialState]
  );

  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [processDefinitions, setProcessDefinitions] = useState<ProcessDefinition[]>([]);
  const [error, setError] = useState<string>();
  const [filters, setFilters] = useState<ProcessDefinitionsFilter>(defaultFilters);
  const [isInitialLoadDone, setIsInitialLoadDone] = useState(false);

  useEffect(() => {
    if (!isEnvelopeConnectedToChannel) {
      setIsInitialLoadDone(false);
    }
  }, [isEnvelopeConnectedToChannel]);

  const doQuery = useCallback(async (): Promise<void> => {
    setIsLoading(true);
    setError(undefined);
    try {
      const response: ProcessDefinition[] = await driver.query();
      setProcessDefinitions(response);
    } catch (err) {
      setError(JSON.parse(JSON.parse(err.message).errorMessage));
    } finally {
      setIsLoading(false);
    }
  }, [driver]);

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (isEnvelopeConnectedToChannel && !isInitialLoadDone) {
          setFilters(defaultFilters);
          driver
            .initialLoad(defaultFilters)
            .then(() => {
              if (canceled.get()) {
                return;
              }
              return doQuery();
            })
            .then(() => {
              if (canceled.get()) {
                return;
              }
              setIsInitialLoadDone(true);
            });
        }
      },
      [defaultFilters, doQuery, driver, isEnvelopeConnectedToChannel, isInitialLoadDone]
    )
  );

  useEffect(() => {
    if (initialState && initialState.filters) {
      setFilters(initialState.filters);
    }
  }, [initialState]);

  const applyFilter = useCallback(
    async (filter: ProcessDefinitionsFilter): Promise<void> => {
      setProcessDefinitions([]);
      await driver.applyFilter(filter);
      await doQuery();
    },
    [doQuery, driver]
  );

  const doRefresh = useCallback(async (): Promise<void> => {
    setProcessDefinitions([]);
    await doQuery();
  }, [doQuery]);

  const columns: DataTableColumn[] = useMemo(
    () => [
      getColumn("processName", `${singularProcessLabel} Name`),
      getColumn("endpoint", "Endpoint"),
      getActionColumn((processDefinition) => {
        driver.openProcessDefinitionForm(processDefinition);
      }, singularProcessLabel),
    ],
    [driver, singularProcessLabel]
  );

  if (error) {
    return <ServerErrors error={error} variant={"large"} />;
  }

  return (
    <div>
      <ProcessDefinitionsListToolbar
        applyFilter={applyFilter}
        refresh={doRefresh}
        filters={filters}
        setFilters={setFilters}
      />
      <Divider />
      <DataTable
        data={processDefinitions}
        isLoading={isLoading}
        columns={columns}
        error={false}
        LoadingComponent={
          <Bullseye>
            <KogitoSpinner
              spinnerText={`Loading ${singularProcessLabel.toLowerCase()} definitions...`}
              ouiaId="forms-list-loading-process-definitions"
            />
          </Bullseye>
        }
      />
    </div>
  );
};

export default ProcessDefinitionsList;
