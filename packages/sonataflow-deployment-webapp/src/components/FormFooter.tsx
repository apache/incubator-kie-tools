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
import _ from "lodash";
import { ActionList, ActionListItem } from "@patternfly/react-core/dist/js/components/ActionList";
import { convertActionsToButton, FormAction } from "./utils";

interface IOwnProps {
  actions?: FormAction[];
  enabled?: boolean;
  onSubmitForm?: () => void;
}

export function FormFooter({ actions, enabled = true, onSubmitForm }: IOwnProps) {
  if (_.isEmpty(actions)) {
    return null;
  }

  const actionItems = convertActionsToButton(actions || [], enabled, onSubmitForm)?.map((button, index) => {
    return <ActionListItem key={`form-action-${index}`}>{button}</ActionListItem>;
  });

  return (
    <div className="sonataflow-deployment-common__form-footer-padding-top">
      <ActionList>{actionItems}</ActionList>
    </div>
  );
}
