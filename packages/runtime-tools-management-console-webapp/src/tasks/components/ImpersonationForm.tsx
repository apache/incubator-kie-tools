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
import React, { useCallback, useEffect, useState } from "react";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import {
  RuntimePathSearchParamsRoutes,
  useRuntime,
  useRuntimeDispatch,
  useRuntimeInfo,
} from "../../runtime/RuntimeContext";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { QueryParams } from "../../navigation/Routes";
import { useQueryParams } from "../../navigation/queryParams/QueryParamsContext";
import { useHistory } from "react-router";

export const ImpersonationForm: React.FC = () => {
  const { impersonationUsername, impersonationGroup } = useRuntime();
  const { canImpersonate } = useRuntimeInfo();
  const queryParams = useQueryParams();
  const history = useHistory();
  const { setImpersonationUsername, setImpersonationGroup, setRuntimePathSearchParams } = useRuntimeDispatch();
  const [username, setUsername] = useState<string>();
  const [group, setGroup] = useState<string>();

  useEffect(() => {
    setUsername(impersonationUsername);
    setGroup(impersonationGroup);
  }, [impersonationGroup, impersonationUsername]);

  const onClear = useCallback(() => {
    setUsername(undefined);
    setGroup(undefined);
    setImpersonationUsername(undefined);
    setImpersonationGroup(undefined);
    const newQueryParams = {
      [QueryParams.IMPERSONATION_USER]: undefined,
      [QueryParams.IMPERSONATION_GROUP]: undefined,
    };

    setRuntimePathSearchParams((currentRuntimePathSearchParams) => {
      const tasksNewSearchParams = {
        ...currentRuntimePathSearchParams.get(RuntimePathSearchParamsRoutes.TASKS),
        ...newQueryParams,
      };
      const taskDetailsNewSearchParams = {
        ...currentRuntimePathSearchParams.get(RuntimePathSearchParamsRoutes.TASK_DETAILS),
        ...newQueryParams,
      };
      currentRuntimePathSearchParams.set(RuntimePathSearchParamsRoutes.TASKS, tasksNewSearchParams);
      currentRuntimePathSearchParams.set(RuntimePathSearchParamsRoutes.TASK_DETAILS, taskDetailsNewSearchParams);

      return new Map(currentRuntimePathSearchParams);
    });
    history.replace({
      pathname: history.location.pathname,
      search: queryParams
        .with(QueryParams.IMPERSONATION_USER, newQueryParams[QueryParams.IMPERSONATION_USER])
        .with(QueryParams.IMPERSONATION_GROUP, newQueryParams[QueryParams.IMPERSONATION_GROUP])
        .toString(),
    });
  }, [history, queryParams, setImpersonationGroup, setImpersonationUsername, setRuntimePathSearchParams]);

  const onApply = useCallback(() => {
    setImpersonationUsername(username);
    setImpersonationGroup(group);
    const newQueryParams = {
      [QueryParams.IMPERSONATION_USER]: username,
      [QueryParams.IMPERSONATION_GROUP]: group,
    };

    setRuntimePathSearchParams((currentRuntimePathSearchParams) => {
      const tasksNewSearchParams = {
        ...currentRuntimePathSearchParams.get(RuntimePathSearchParamsRoutes.TASKS),
        ...newQueryParams,
      };
      const taskDetailsNewSearchParams = {
        ...currentRuntimePathSearchParams.get(RuntimePathSearchParamsRoutes.TASK_DETAILS),
        ...newQueryParams,
      };
      currentRuntimePathSearchParams.set(RuntimePathSearchParamsRoutes.TASKS, tasksNewSearchParams);
      currentRuntimePathSearchParams.set(RuntimePathSearchParamsRoutes.TASK_DETAILS, taskDetailsNewSearchParams);

      return new Map(currentRuntimePathSearchParams);
    });
    history.replace({
      pathname: history.location.pathname,
      search: queryParams
        .with(QueryParams.IMPERSONATION_USER, newQueryParams[QueryParams.IMPERSONATION_USER])
        .with(QueryParams.IMPERSONATION_GROUP, newQueryParams[QueryParams.IMPERSONATION_GROUP])
        .toString(),
    });
  }, [
    group,
    history,
    queryParams,
    setImpersonationGroup,
    setImpersonationUsername,
    setRuntimePathSearchParams,
    username,
  ]);

  useEffect(() => {
    if (!canImpersonate) {
      onClear();
    }
  }, [canImpersonate, onClear]);

  return canImpersonate ? (
    <Flex style={{ padding: "1rem", alignSelf: "flex-end" }}>
      <FlexItem>
        <div>Impersonation: </div>
      </FlexItem>
      <FlexItem>
        <FormGroup>
          <TextInput
            id="username"
            aria-label="Username"
            autoFocus={false}
            placeholder="Username"
            tabIndex={1}
            value={username ?? ""}
            onChange={setUsername}
          />
        </FormGroup>
      </FlexItem>
      <FlexItem>
        <FormGroup>
          <TextInput
            id="group"
            aria-label="Group"
            tabIndex={2}
            value={group ?? ""}
            onChange={setGroup}
            placeholder="Group"
          />
        </FormGroup>
      </FlexItem>

      <FlexItem>
        <Button variant={ButtonVariant.primary} alt="Apply" onClick={onApply}>
          Apply
        </Button>
      </FlexItem>
      <FlexItem>
        <Button variant={ButtonVariant.secondary} alt="Clear" onClick={onClear}>
          Clear
        </Button>
      </FlexItem>
    </Flex>
  ) : (
    <></>
  );
};
