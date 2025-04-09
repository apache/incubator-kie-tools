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

import * as React from "react";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Card, CardBody, CardExpandableContent, CardHeader } from "@patternfly/react-core/dist/js/components/Card";
import { Stack } from "@patternfly/react-core/dist/js/layouts/Stack";
import { AuthSessionLabel } from "./AuthSessionLabel";
import { useAuthSessions, useAuthSessionsDispatch } from "./AuthSessionsContext";
import { useMemo, useState, useCallback } from "react";
import {
  DescriptionList,
  DescriptionListDescription,
  DescriptionListGroup,
  DescriptionListTerm,
} from "@patternfly/react-core/dist/js/components/DescriptionList";
import { obfuscate } from "../accounts/git/ConnectToGitSection";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import ExclamationCircleIcon from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { AuthSession, AuthSessionStatus } from "./AuthSessionApi";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { useWorkspaceDescriptorsPromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspacesHooks";
import { useDevDeployments } from "../devDeployments/DevDeploymentsContext";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { KieSandboxDeployment } from "../devDeployments/services/types";

export function AuthSessionsList(props: {}) {
  const { authSessions, authSessionStatus } = useAuthSessions();
  const workspaceDescriptorsPromise = useWorkspaceDescriptorsPromise();
  const devDeployments = useDevDeployments();
  const [devDeploymentsUsages, setDevDeploymentsUsages] = useState(new Map<string, KieSandboxDeployment[]>());

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        [...authSessions.values()].map((authSession) => {
          if (
            (authSession.type === "openshift" || authSession.type === "kubernetes") &&
            authSessionStatus.get(authSession.id) === AuthSessionStatus.VALID
          ) {
            devDeployments
              .loadDevDeployments({ authSession })
              .then((deployments) => {
                if (canceled.get()) {
                  return;
                }
                setDevDeploymentsUsages((prev) => new Map([...prev, [authSession.id, deployments]]));
              })
              .catch((e) => console.error(e));
          }
        });
      },
      [authSessionStatus, authSessions, devDeployments]
    )
  );

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
              usages={
                authSession.type === "openshift" || authSession.type === "kubernetes"
                  ? devDeploymentsUsages.get(authSession.id)
                  : usagesByWorkspace.get(authSession.id)
              }
            />
          );
        })}
      </Stack>
    </>
  );
}

function AuthSessionCard(props: {
  authSession: AuthSession;
  usages: WorkspaceDescriptor[] | KieSandboxDeployment[] | undefined;
}) {
  const authSessionsDispatch = useAuthSessionsDispatch();
  const [isExpanded, setExpanded] = useState(false);
  const { authSessionStatus } = useAuthSessions();

  return (
    <Card key={props.authSession.id} isCompact={true} isExpanded={isExpanded}>
      <CardHeader
        actions={{
          actions: (
            <>
              {authSessionStatus.get(props.authSession.id) === AuthSessionStatus.INVALID && (
                <Tooltip
                  content={"Could not authenticate using this session. Its Token was probably revoked, or expired."}
                >
                  <>
                    <ExclamationCircleIcon style={{ color: "var(--pf-v5-global--palette--red-100)" }} />
                  </>
                </Tooltip>
              )}
              <Button variant={ButtonVariant.link} onClick={() => authSessionsDispatch.remove(props.authSession)}>
                Remove
              </Button>
            </>
          ),
          hasNoOffset: false,
          className: undefined,
        }}
        onExpand={() => setExpanded((prev) => !prev)}
      >
        {
          <>
            <AuthSessionLabel authSession={props.authSession} />
            {(props.authSession.type === "git" ||
              props.authSession.type === "openshift" ||
              props.authSession.type === "kubernetes") && (
              <>
                &nbsp; &nbsp; &nbsp;
                <Label>
                  &nbsp;{props.usages ? (props.usages.length === 1 ? "1 usage" : `${props.usages.length} usages`) : "-"}
                  &nbsp;
                </Label>
              </>
            )}
          </>
        }
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

export function AuthSessionDescriptionList(props: {
  authSession: AuthSession;
  usages?: WorkspaceDescriptor[] | KieSandboxDeployment[];
}) {
  return (
    <>
      {(props.authSession.type === "openshift" || props.authSession.type === "kubernetes") && (
        <>
          <DescriptionList isHorizontal={true} isCompact={true} isFluid={true}>
            <DescriptionListGroup>
              <DescriptionListTerm>Namespace</DescriptionListTerm>
              <DescriptionListDescription>{props.authSession.namespace}</DescriptionListDescription>
            </DescriptionListGroup>
            <>
              <DescriptionListGroup>
                <DescriptionListTerm>Host</DescriptionListTerm>
                <DescriptionListDescription>{props.authSession.host ?? <Empty />}</DescriptionListDescription>
              </DescriptionListGroup>
              <DescriptionListGroup>
                <DescriptionListTerm>Token</DescriptionListTerm>
                <DescriptionListDescription>{obfuscate(props.authSession.token)}</DescriptionListDescription>
              </DescriptionListGroup>
              <DescriptionListGroup>
                <DescriptionListTerm>Created at</DescriptionListTerm>
                <DescriptionListDescription>{props.authSession.createdAtDateISO}</DescriptionListDescription>
              </DescriptionListGroup>
              {
                // TODO: remove check when enabling kubernetes deployments to use cors-proxy
                props.authSession.type === "openshift" && (
                  <DescriptionListGroup>
                    <DescriptionListTerm>TLS Certificate Verification</DescriptionListTerm>
                    <DescriptionListDescription>
                      {props.authSession.insecurelyDisableTlsCertificateValidation ? "Disabled" : "Enabled"}
                    </DescriptionListDescription>
                  </DescriptionListGroup>
                )
              }

              {props.usages && (
                <>
                  <DescriptionListGroup>
                    <DescriptionListTerm>Usages</DescriptionListTerm>
                    <DescriptionListDescription>{props.usages.length}</DescriptionListDescription>
                  </DescriptionListGroup>
                </>
              )}
            </>
          </DescriptionList>
        </>
      )}
      {props.authSession.type === "git" && (
        <DescriptionList isHorizontal={true} isCompact={true} isFluid={true}>
          <DescriptionListGroup>
            <DescriptionListTerm>Login</DescriptionListTerm>
            <DescriptionListDescription>{props.authSession.login}</DescriptionListDescription>
          </DescriptionListGroup>
          <DescriptionListGroup>
            <DescriptionListTerm>Name</DescriptionListTerm>
            <DescriptionListDescription>{props.authSession.name ?? <Empty />}</DescriptionListDescription>
          </DescriptionListGroup>
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
          {props.usages && (
            <>
              <DescriptionListGroup>
                <DescriptionListTerm>Usages</DescriptionListTerm>
                <DescriptionListDescription>{props.usages.length}</DescriptionListDescription>
              </DescriptionListGroup>
            </>
          )}
        </DescriptionList>
      )}
    </>
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
