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
import {
  Button,
  DataList,
  DataListAction,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow
} from "@patternfly/react-core";
import "./OutputsTable.scss";

import { TrashIcon } from "@patternfly/react-icons";

export const OutputsTableHeader = () => {
  return (
    <DataList className="outputs__header" aria-label="outputs header">
      <DataListItem className="outputs__header__row" key={"none"} aria-labelledby="outputs-header">
        <DataListItemRow>
          <DataListItemCells
            dataListCells={[
              <DataListCell key="0" width={4}>
                <div>Name</div>
              </DataListCell>,
              <DataListCell key="1" width={4}>
                <div>Data Type</div>
              </DataListCell>,
              <DataListCell key="2" width={5}>
                <div>&nbsp;</div>
              </DataListCell>,
              <DataListAction
                id="delete-output-header"
                aria-label="delete header"
                aria-labelledby="delete-output-header"
                key="103"
                width={1}
              >
                {/*This is a hack to ensure the column layout is correct*/}
                <Button variant="link" icon={<TrashIcon />} isDisabled={true} style={{ visibility: "hidden" }}>
                  &nbsp;
                </Button>
              </DataListAction>
            ]}
          />
        </DataListItemRow>
      </DataListItem>
    </DataList>
  );
};
