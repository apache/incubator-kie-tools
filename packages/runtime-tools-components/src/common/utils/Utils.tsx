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

import React from "react";
import { InfoCircleIcon } from "@patternfly/react-icons/dist/js/icons/info-circle-icon";
import { Title, TitleSizes } from "@patternfly/react-core/dist/js/components/Title";
import { BulkListItem } from "../components/BulkList/BulkList";
import { Job } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";

export const setTitle = (titleStatus: string, titleText: string): JSX.Element => {
  let icon;

  switch (titleStatus) {
    case "success":
      icon = <InfoCircleIcon className="pf-u-mr-sm" color="var(--pf-global--info-color--100)" />;
      break;
    case "failure":
      icon = <InfoCircleIcon className="pf-u-mr-sm" color="var(--pf-global--danger-color--100)" />;
      break;
  }

  return (
    <Title headingLevel="h1" size={TitleSizes["2xl"]}>
      {icon}
      <span>{titleText}</span>
    </Title>
  );
};

// function adds new property to existing object
export const constructObject = (obj: any, path: any, val: any) => {
  const keys = path.split(",");
  const lastKey = keys.pop();
  // tslint:disable-next-line: no-shadowed-variable
  const lastObj = keys.reduce(
    // tslint:disable-next-line: no-shadowed-variable
    (_obj: any, key: any) => (_obj[key] = obj[key] || {}),
    obj
  );
  lastObj[lastKey] = val;
};

export const formatForBulkListJob = (jobsList: (Job & { errorMessage?: string })[]): BulkListItem[] => {
  const formattedItems: BulkListItem[] = [];
  jobsList.forEach((item: Job & { errorMessage?: string }) => {
    const formattedObj: BulkListItem = {
      id: item.id,
      name: item.resourceId,
      description: item.id,
      errorMessage: item.errorMessage ? item.errorMessage : undefined,
    };
    formattedItems.push(formattedObj);
  });
  return formattedItems;
};
