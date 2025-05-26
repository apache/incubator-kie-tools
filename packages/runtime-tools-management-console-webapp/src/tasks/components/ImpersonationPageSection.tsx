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
import { Hint } from "@patternfly/react-core/dist/esm/components/Hint";
import { Button, ButtonType, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import {
  ActionGroup,
  Form,
  FormFieldGroupExpandable,
  FormFieldGroupHeader,
  FormGroup,
  FormHelperText,
} from "@patternfly/react-core/dist/js/components/Form";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import UserTagIcon from "@patternfly/react-icons/dist/esm/icons/user-tag-icon";
import UserIcon from "@patternfly/react-icons/dist/js/icons/user-icon";
import React, { useCallback, useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { AuthSessionType, getAuthSessionDisplayInfo, useAuthSessions } from "../../authSessions";
import { QueryParams } from "../../navigation/Routes";
import { useQueryParams } from "../../navigation/queryParams/QueryParamsContext";
import {
  RuntimePathSearchParamsRoutes,
  useRuntime,
  useRuntimeDispatch,
  useRuntimeInfo,
} from "../../runtime/RuntimeContext";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";

export const ImpersonationPageSection: React.FC<{}> = () => {
  const { impersonationUsername, impersonationGroups } = useRuntime();
  const { canImpersonate } = useRuntimeInfo();
  const queryParams = useQueryParams();
  const navigate = useNavigate();
  const location = useLocation();

  const {
    setImpersonationUsername,
    setImpersonationGroups: setImpersonationGroup,
    setRuntimePathSearchParams,
  } = useRuntimeDispatch();

  const [username, setUsername] = useState<string>();
  const [groups, setGroups] = useState<string>();

  useEffect(() => {
    setUsername((prev) => prev || impersonationUsername);
    setGroups((prev) => prev || impersonationGroups);
  }, [impersonationGroups, impersonationUsername]);

  const impersonate = useCallback(
    (u: string | undefined, g: string | undefined) => {
      setImpersonationUsername(u);
      setImpersonationGroup(g);

      const newQueryParams = {
        [QueryParams.IMPERSONATION_USER]: u,
        [QueryParams.IMPERSONATION_GROUPS]: g,
      };

      setRuntimePathSearchParams((currentRuntimePathSearchParams) => {
        currentRuntimePathSearchParams.set(RuntimePathSearchParamsRoutes.TASKS, {
          ...currentRuntimePathSearchParams.get(RuntimePathSearchParamsRoutes.TASKS),
          ...newQueryParams,
        });
        currentRuntimePathSearchParams.set(RuntimePathSearchParamsRoutes.TASK_DETAILS, {
          ...currentRuntimePathSearchParams.get(RuntimePathSearchParamsRoutes.TASK_DETAILS),
          ...newQueryParams,
        });

        return new Map(currentRuntimePathSearchParams);
      });

      navigate(
        {
          pathname: location.pathname,
          search: queryParams
            .with(QueryParams.IMPERSONATION_USER, newQueryParams[QueryParams.IMPERSONATION_USER])
            .with(QueryParams.IMPERSONATION_GROUPS, newQueryParams[QueryParams.IMPERSONATION_GROUPS])
            .toString(),
        },
        { replace: true }
      );
    },
    [
      location.pathname,
      navigate,
      queryParams,
      setImpersonationGroup,
      setImpersonationUsername,
      setRuntimePathSearchParams,
    ]
  );

  const onClear = useCallback(() => {
    setUsername(undefined);
    setGroups(undefined);
    impersonate(undefined, undefined);
  }, [impersonate]);

  const onApply = useCallback(
    (e: React.FormEvent) => {
      e.stopPropagation();
      e.preventDefault();
      impersonate(username, groups);
    },
    [groups, impersonate, username]
  );

  useEffect(() => {
    if (!canImpersonate) {
      onClear();
    }
  }, [canImpersonate, onClear]);

  const { currentAuthSession } = useAuthSessions();
  const authSessionInfo = useMemo(() => getAuthSessionDisplayInfo(currentAuthSession), [currentAuthSession]);

  return canImpersonate ? (
    <PageSection padding={{ default: "noPadding" }}>
      <Hint className={"kogito-management-console__impersonation-hint"}>
        <Form isHorizontal={true} onSubmit={onApply}>
          <FormFieldGroupExpandable
            isExpanded={false}
            header={
              <FormFieldGroupHeader
                titleDescription={
                  <small>
                    {impersonationUsername
                      ? `Viewing and completing Tasks as '${impersonationUsername}'`
                      : `View and complete Tasks as if you were another user`}
                  </small>
                }
                titleText={{
                  id: "impersonation-hint",
                  text: impersonationUsername ? (
                    <>
                      <UserTagIcon />
                      &nbsp;
                      <b>{`Impersonating '${impersonationUsername}'`}</b>
                    </>
                  ) : (
                    <>
                      <UserIcon />
                      &nbsp;
                      <b>{`Impersonate`}</b>
                    </>
                  ),
                }}
              />
            }
          >
            <FormGroup label={"User"} style={{ maxWidth: "500px" }}>
              <TextInput
                validated={username && username === impersonationUsername ? "success" : "default"}
                id="username"
                aria-label="Username"
                autoFocus={false}
                placeholder={`None (currently as '${authSessionInfo.username}')`}
                tabIndex={1}
                value={username ?? ""}
                onChange={(_event, val) => setUsername(val)}
              />
            </FormGroup>
            <FormGroup label={"Groups"} style={{ maxWidth: "500px" }}>
              <TextInput
                validated={groups && groups === impersonationGroups ? "success" : "default"}
                id="groups"
                aria-label="Groups"
                tabIndex={2}
                value={groups ?? ""}
                onChange={(_event, val) => setGroups(val)}
                placeholder={`None (currently ${currentAuthSession?.type === AuthSessionType.OPENID_CONNECT ? currentAuthSession.roles?.join(",") ?? "empty" : "empty"})`}
              />
              <FormHelperText>
                <HelperText>
                  <HelperTextItem>Comma-separated list, no spaces.</HelperTextItem>
                </HelperText>
              </FormHelperText>
            </FormGroup>
            <ActionGroup>
              <Button type={ButtonType.submit} variant={ButtonVariant.secondary}>
                Apply
              </Button>
              <Button variant={ButtonVariant.link} onClick={onClear}>
                Reset
              </Button>
            </ActionGroup>
          </FormFieldGroupExpandable>
        </Form>
      </Hint>
    </PageSection>
  ) : (
    <></>
  );
};
