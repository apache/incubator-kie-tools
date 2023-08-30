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

import { Card, CardHeader, CardHeaderMain } from "@patternfly/react-core/dist/js/components/Card";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Skeleton } from "@patternfly/react-core/dist/js/components/Skeleton";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import * as React from "react";
import {
  DataList,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
} from "@patternfly/react-core/dist/js/components/DataList";
import { MenuItem } from "@patternfly/react-core/dist/js/components/Menu";

export function WorkspaceLoadingMenuItem() {
  return (
    <MenuItem
      style={{
        borderTop: "var(--pf-global--BorderWidth--sm) solid var(--pf-global--BorderColor--100)",
      }}
      className={"kie-tools--file-switcher-no-padding-menu-item"}
      direction={"down"}
    >
      <WorkspaceLoadingDataList />
    </MenuItem>
  );
}

export function WorkspaceLoadingDataList() {
  return (
    <DataList aria-label="workspace-loading-data-list" style={{ border: 0 }}>
      {/* Need to replicate DatList's border here because of the angle bracket of drilldown menus */}
      <DataListItem style={{ border: 0, backgroundColor: "transparent" }}>
        <DataListItemRow>
          <DataListItemCells
            dataListCells={[
              <DataListCell key="link" isFilled={false}>
                <WorkspaceLoadingListItem isBig={false} />
              </DataListCell>,
            ]}
          />
        </DataListItemRow>
      </DataListItem>
    </DataList>
  );
}

export function WorkspaceLoadingListItem(props: { isBig: boolean }) {
  return (
    <>
      <Flex>
        <Label color={"grey"} style={{ width: "80px" }}>
          <Skeleton width={"100%"} />
        </Label>
        <Label color={"grey"} style={{ width: "50px" }}>
          <Skeleton width={"100%"} />
        </Label>
        <TextContent>
          <Text
            component={TextVariants.small}
            style={{
              whiteSpace: "nowrap",
              overflow: "hidden",
              textOverflow: "ellipsis",
              width: "200px",
            }}
          >
            <Skeleton width={"100%"} />
          </Text>
        </TextContent>
      </Flex>
      <br />
      <TextContent>
        <Text
          component={props.isBig ? TextVariants.h3 : TextVariants.p}
          style={{
            whiteSpace: "nowrap",
            overflow: "hidden",
            textOverflow: "ellipsis",
          }}
        >
          <Skeleton width={"100px"} />
        </Text>
      </TextContent>
      <TextContent style={{ marginTop: "6px" }}>
        <Text component={TextVariants.small}>
          <Skeleton width={"300px"} />
        </Text>
      </TextContent>
    </>
  );
}

export function WorkspaceLoadingCard(props: { isBig: boolean }) {
  return (
    <Card isHoverable={true} isCompact={true}>
      <CardHeader>
        <CardHeaderMain style={{ width: "100%" }}>
          <WorkspaceLoadingListItem isBig={props.isBig} />
        </CardHeaderMain>
      </CardHeader>
    </Card>
  );
}
