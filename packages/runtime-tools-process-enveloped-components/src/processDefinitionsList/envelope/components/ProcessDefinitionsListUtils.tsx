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
import React from "react";
import { PlayIcon } from "@patternfly/react-icons/dist/js/icons/play-icon";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { DataTableColumn } from "@kie-tools/runtime-tools-components/dist/components/DataTable";
import { ProcessDefinition } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";

export const getColumn = (columnPath: string, columnLabel: string): DataTableColumn => {
  return {
    label: columnLabel,
    path: columnPath,
    bodyCellTransformer: (value) => <span>{value}</span>,
  };
};

export const getActionColumn = (
  startProcess: (processDefinition: ProcessDefinition) => void,
  singularProcessLabel: string
): DataTableColumn => {
  return {
    label: "Actions",
    path: "actions",
    bodyCellTransformer: (value, rowData: ProcessDefinition) => (
      <Tooltip content={`Start new ${singularProcessLabel.toLowerCase()}`}>
        <Button onClick={() => startProcess(rowData)} variant="link">
          <PlayIcon />
        </Button>
      </Tooltip>
    ),
  };
};
