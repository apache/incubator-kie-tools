/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import {
  Card,
  CardActions,
  CardBody,
  CardExpandableContent,
  CardHeader,
  CardHeaderMain,
} from "@patternfly/react-core/dist/js/components/Card";
import { Stack } from "@patternfly/react-core/dist/js/layouts/Stack";
import { AuthSessionLabel } from "./AuthSessionLabel";
import { useAuthSessions, useAuthSessionsDispatch } from "./AuthSessionsContext";
import { useMemo, useState } from "react";
import {
  DescriptionList,
  DescriptionListDescription,
  DescriptionListGroup,
  DescriptionListTerm,
} from "@patternfly/react-core/dist/js/components/DescriptionList";
import { obfuscate } from "../ConnectToGitHubSection";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import ExclamationCircleIcon from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { AuthSession, AuthSessionStatus } from "./AuthSessionApi";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { useWorkspaceDescriptorsPromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspacesHooks";

export function AuthSessionsList(props: {}) {
  const { authSessions } = useAuthSessions();
  const workspaceDescriptorsPromise = useWorkspaceDescriptorsPromise();

  const usagesByWorkspace = useMemo(() => {
    if (!workspaceDescriptorsPromise.data) {
      return new Map<string, WorkspaceDescriptor[]>();
    }

    const initialUsages = new Map<string, WorkspaceDescriptor[]>(
      [...authSessions.keys()].map((authSession) => [authSession, []])
    );

    return workspaceDescriptorsPromise.data.reduce(
      (acc, workspaceDescriptor) =>
        !workspaceDescriptor.gitAuthSessionId
          ? acc
          : acc.set(workspaceDescriptor.gitAuthSessionId, [
              ...(acc.get(workspaceDescriptor.gitAuthSessionId) ?? []),
              workspaceDescriptor,
            ]),
      initialUsages
    );
  }, [authSessions, workspaceDescriptorsPromise.data]);

  return (
    <>
      <Stack hasGutter={true} style={{ height: "auto" }}>
        {[...authSessions.values()].map((authSession) => {
          if (authSession.type === "none") {
            // This is never going to happen, as we don't persist the "none" auth session.
            return <></>;
          }

          return (
            <AuthSessionCard
              key={authSession.id}
              authSession={authSession}
              usages={usagesByWorkspace.get(authSession.id)}
            />
          );
        })}
      </Stack>
    </>
  );
}

function AuthSessionCard(props: { authSession: AuthSession; usages: WorkspaceDescriptor[] | undefined }) {
  const authSessionsDispatch = useAuthSessionsDispatch();
  const [isExpanded, setExpanded] = useState(false);
  const { authSessionStatus } = useAuthSessions();

  return (
    <Card key={props.authSession.id} isCompact={true} isExpanded={isExpanded}>
      <CardHeader onExpand={() => setExpanded((prev) => !prev)}>
        <CardActions>
          {authSessionStatus.get(props.authSession.id) === AuthSessionStatus.INVALID && (
            <Tooltip content={"Could not authenticate using this session. Its Token was probably revoked, or expired."}>
              <>
                {/* Color copied from PF4 */}
                <ExclamationCircleIcon color={"#c9190b"} />
              </>
            </Tooltip>
          )}
          <Button variant={ButtonVariant.link} onClick={() => authSessionsDispatch.remove(props.authSession)}>
            Remove
          </Button>
        </CardActions>
        <CardHeaderMain style={{ display: "flex", opacity: (props.usages?.length ?? 0) <= 0 ? 0.5 : 1 }}>
          <AuthSessionLabel authSession={props.authSession} />
          &nbsp; &nbsp; &nbsp;
          <Label>
            &nbsp;{props.usages ? (props.usages.length === 1 ? "1 usage" : `${props.usages.length} usages`) : "-"}&nbsp;
          </Label>
        </CardHeaderMain>
      </CardHeader>
      <CardExpandableContent>
        <CardBody>
          <>
            <br />
            <Divider inset={{ default: "insetXl" }} />
            <br />
            <AuthSessionDescriptionList authSession={props.authSession} usages={props.usages} />
          </>
        </CardBody>
      </CardExpandableContent>
    </Card>
  );
}

export function AuthSessionDescriptionList(props: { authSession: AuthSession; usages?: WorkspaceDescriptor[] }) {
  return (
    <DescriptionList isHorizontal={true} isCompact={true} isFluid={true}>
      <DescriptionListGroup>
        <DescriptionListTerm>Login</DescriptionListTerm>
        <DescriptionListDescription>{props.authSession.login}</DescriptionListDescription>
      </DescriptionListGroup>
      <DescriptionListGroup>
        <DescriptionListTerm>Name</DescriptionListTerm>
        <DescriptionListDescription>{props.authSession.name ?? <Empty />}</DescriptionListDescription>
      </DescriptionListGroup>
      {props.authSession.type === "git" && (
        <>
          <DescriptionListGroup>
            <DescriptionListTerm>Email</DescriptionListTerm>
            <DescriptionListDescription>{props.authSession.email ?? <Empty />}</DescriptionListDescription>
          </DescriptionListGroup>
          <DescriptionListGroup>
            <DescriptionListTerm>Token</DescriptionListTerm>
            <DescriptionListDescription>{obfuscate(props.authSession.token)}</DescriptionListDescription>
          </DescriptionListGroup>
          <DescriptionListGroup>
            <DescriptionListTerm>Created at</DescriptionListTerm>
            <DescriptionListDescription>{props.authSession.createdAtDateISO}</DescriptionListDescription>
          </DescriptionListGroup>
        </>
      )}
      {props.usages && (
        <>
          <DescriptionListGroup>
            <DescriptionListTerm>Usages</DescriptionListTerm>
            <DescriptionListDescription>{props.usages.length}</DescriptionListDescription>
          </DescriptionListGroup>
        </>
      )}
    </DescriptionList>
  );
}

function Empty() {
  return (
    <TextContent>
      <Text component={TextVariants.small}>
        <i>(Empty)</i>
      </Text>
    </TextContent>
  );
}
