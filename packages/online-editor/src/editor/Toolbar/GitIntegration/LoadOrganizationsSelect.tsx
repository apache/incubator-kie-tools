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

import React, { useMemo } from "react";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Select, SelectGroup, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { useCallback, useEffect, useState } from "react";
import { AuthProviderType, isSupportedGitAuthProviderType } from "../../../authProviders/AuthProvidersApi";
import { useAuthProvider } from "../../../authProviders/AuthProvidersContext";
import { useAuthSession } from "../../../authSessions/AuthSessionsContext";
import { useBitbucketClient } from "../../../bitbucket/Hooks";
import { useGitHubClient } from "../../../github/Hooks";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { BitbucketIcon } from "@patternfly/react-icons/dist/js/icons/bitbucket-icon";
import { GithubIcon } from "@patternfly/react-icons/dist/js/icons/github-icon";
import { SyncAltIcon } from "@patternfly/react-icons/dist/js/icons/sync-alt-icon";
import { UserIcon } from "@patternfly/react-icons/dist/js/icons/user-icon";
import { UsersIcon } from "@patternfly/react-icons/dist/js/icons/users-icon";
import { useOnlineI18n } from "../../../i18n";

export interface LoadOrganizationsReponse {
  organizations: {
    name: string;
  }[];
}

export type SelectOptionKindType = "user" | "organization";

export type SelectOptionObjectType = {
  value: string;
  kind: SelectOptionKindType;
  toString(): string;
  compareTo(selectOption: any): boolean;
};

const getSelectOptionValue = (value: string, kind: SelectOptionKindType): SelectOptionObjectType => {
  return {
    value,
    kind,
    toString: () => {
      return value;
    },
    compareTo: (selectOption: any) => {
      return value.toLowerCase() === selectOption.toString().toLowerCase();
    },
  };
};

export interface Props {
  workspace: WorkspaceDescriptor;
  onSelect: (organization?: SelectOptionObjectType) => void;
  readonly?: boolean;
  hideUser?: boolean;
}

export const LoadOrganizationsSelect = (props: Props) => {
  const { authSession, authInfo } = useAuthSession(props.workspace.gitAuthSessionId);
  const authProvider = useAuthProvider(authSession);
  const gitHubClient = useGitHubClient(authSession);
  const bitbucketClient = useBitbucketClient(authSession);

  const { i18n } = useOnlineI18n();

  const [isOrganizationDropdownOpen, setOrganizationDropdownOpen] = useState(false);
  const [organizations, setOrganizations] = useState<{ name: string }[]>();
  const [isLoadingOrganizations, setLoadingOrganizations] = useState(false);
  const [internalSelectedOption, setInternalSelectedOption] = useState<SelectOptionObjectType>();

  const hideUser = useMemo(() => {
    return authProvider?.type === AuthProviderType.bitbucket;
  }, [authProvider?.type]);

  const getGitHubOrganizationsForUser = useCallback(async (): Promise<LoadOrganizationsReponse> => {
    const orgs = await gitHubClient.orgs.listForAuthenticatedUser();
    return { organizations: orgs.data.map((it) => ({ name: it.login })) };
  }, [gitHubClient.orgs]);

  const getBitbucketWorkspacesForUser = useCallback(async (): Promise<LoadOrganizationsReponse> => {
    const orgResponse = await bitbucketClient.listWorkspaces();
    const json = await orgResponse.json();
    const emptyResponse = { organizations: [] };
    if (!json.values || !Array.isArray(json?.values)) {
      return emptyResponse;
    }
    const values: any[] = json.values;
    return { organizations: values.map((it) => ({ name: it.workspace.slug })) };
  }, [bitbucketClient]);

  const { onSelect } = props;
  const setSelectedOption = useCallback(
    (option?: SelectOptionObjectType) => {
      onSelect(option);
      setInternalSelectedOption(option);
    },
    [onSelect]
  );

  const selectDefaultOption = useCallback(
    (orgs: LoadOrganizationsReponse) => {
      if (!isSupportedGitAuthProviderType(authProvider?.type)) {
        return;
      }
      setSelectedOption(
        switchExpression(authProvider?.type, {
          bitbucket: getSelectOptionValue(orgs.organizations[0].name, "organization"),
          github: getSelectOptionValue(authInfo!.username, "user"),
        })
      );
    },
    [authInfo, authProvider?.type, setSelectedOption]
  );

  const loadOrganizations = useCallback(() => {
    if (!isSupportedGitAuthProviderType(authProvider?.type)) {
      return;
    }
    setLoadingOrganizations(true);
    switchExpression(authProvider?.type, {
      bitbucket: getBitbucketWorkspacesForUser,
      github: getGitHubOrganizationsForUser,
    })()
      .then((it) => {
        setOrganizations(it.organizations);
        selectDefaultOption(it);
      })
      .finally(() => {
        setLoadingOrganizations(false);
      });
  }, [authProvider?.type, getBitbucketWorkspacesForUser, getGitHubOrganizationsForUser, selectDefaultOption]);

  useEffect(() => {
    setOrganizations(undefined);
    setSelectedOption(undefined);
    if (authProvider && isSupportedGitAuthProviderType(authProvider?.type)) {
      loadOrganizations();
    }
  }, [loadOrganizations, setSelectedOption, authProvider]);

  const selectOptions = useMemo(() => {
    const options: JSX.Element[] = [];
    if (!authProvider || !isSupportedGitAuthProviderType(authProvider.type)) {
      return options;
    }
    options.push(
      <SelectGroup label={i18n.loadOrganizationsSelect[authProvider.type].user} key="group1" hidden={hideUser}>
        <SelectOption key={0} value={getSelectOptionValue(authInfo!.username, "user")}>
          <Flex>
            <FlexItem>
              <UserIcon />
            </FlexItem>
            <FlexItem>{authInfo?.username}</FlexItem>
          </Flex>
        </SelectOption>
      </SelectGroup>
    );
    if (organizations && organizations?.length > 0) {
      if (!hideUser) {
        options.push(<Divider component="li" key={1} />);
      }
      options.push(
        <SelectGroup label={i18n.loadOrganizationsSelect[authProvider.type].organizations} key="group2">
          {organizations?.map((it, index) => (
            <SelectOption key={2 + index} value={getSelectOptionValue(it.name, "organization")}>
              <Flex>
                <FlexItem>
                  <UsersIcon />
                </FlexItem>
                <FlexItem>{it.name}</FlexItem>
              </Flex>
            </SelectOption>
          ))}
        </SelectGroup>
      );
    }
    return options;
  }, [authInfo, authProvider, hideUser, i18n.loadOrganizationsSelect, organizations]);

  if (!authProvider || !isSupportedGitAuthProviderType(authProvider?.type)) {
    return <></>;
  }
  return (
    <Flex>
      <FlexItem grow={{ default: "grow" }}>
        {authProvider && isSupportedGitAuthProviderType(authProvider.type) && (
          <Select
            variant={SelectVariant.single}
            isOpen={isOrganizationDropdownOpen}
            onSelect={(_, selection: SelectOptionObjectType) => {
              setOrganizationDropdownOpen(false);
              setSelectedOption(selection);
            }}
            toggleIcon={switchExpression(authProvider.type, {
              bitbucket: <BitbucketIcon />,
              github: <GithubIcon />,
            })}
            onToggle={setOrganizationDropdownOpen}
            selections={internalSelectedOption}
            isDisabled={props.readonly}
          >
            {selectOptions}
          </Select>
        )}
      </FlexItem>
      <FlexItem>
        <Button
          variant="link"
          aria-label="Reload"
          isSmall={true}
          isLoading={isLoadingOrganizations}
          onClick={loadOrganizations}
          isDisabled={props.readonly}
        >
          {isLoadingOrganizations ? "" : <SyncAltIcon />}
        </Button>
      </FlexItem>
    </Flex>
  );
};
