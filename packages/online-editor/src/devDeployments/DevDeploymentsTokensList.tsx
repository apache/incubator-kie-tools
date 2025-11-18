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

import React, { useMemo, useState, useCallback } from "react";
import { ExpandableSection } from "@patternfly/react-core/dist/js/components/ExpandableSection";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { AuthSession, isCloudAuthSession } from "../authSessions/AuthSessionApi";
import { flattenTokenMap } from "@kie-tools-core/k8s-yaml-to-apiserver-requests/dist";
import { RESOURCE_PREFIX } from "./services/KieSandboxDevDeploymentsService";
import { KubernetesService } from "./services/KubernetesService";
import { useWorkspacePromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceHooks";
import { Tokens } from "./services/types";
import { NEW_WORKSPACE_DEFAULT_NAME } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { v4 as uuid } from "uuid";
import { Text } from "@patternfly/react-core/dist/js/components/Text";
import {
  Card,
  CardBody,
  CardExpandableContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@patternfly/react-core/dist/js/components/Card";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";

type TokensToBeDisplayed = Omit<Tokens, "labels" | "annotations">;

const tokenMapDescriptions: { devDeployment: TokensToBeDisplayed } = {
  devDeployment: {
    uniqueName: "Unique name for this Dev Deployment, automatically generated",
    workspace: {
      id: "The current workspace ID",
      name: "The current workspace name",
    },
    kubernetes: {
      namespace: "The namespace for the target Kubernetes cluster",
    },
    uploadService: {
      apiKey: "Randomly generated code to be used by the Dev Deployment Upload Service to verify uploads",
    },
  },
};

type Props = {
  workspaceFile: WorkspaceFile;
  authSession: AuthSession;
};

export function DevDeploymentsTokensList(props: Props) {
  const workspacePromise = useWorkspacePromise(props.workspaceFile.workspaceId);

  const devDeploymentTokensWithDescriptions = useMemo(() => {
    return {
      descriptions: flattenTokenMap(tokenMapDescriptions),
      defaultValues: flattenTokenMap({
        devDeployment: {
          uniqueName: KubernetesService.newResourceName(RESOURCE_PREFIX, "xxxxxxxxx"),
          workspace: {
            id: props.workspaceFile.workspaceId,
            name:
              workspacePromise.data?.descriptor.name &&
              workspacePromise.data?.descriptor.name !== NEW_WORKSPACE_DEFAULT_NAME
                ? workspacePromise.data.descriptor.name
                : props.workspaceFile.name,
          },
          kubernetes: {
            namespace: props.authSession && isCloudAuthSession(props.authSession) ? props.authSession.namespace : "",
          },
          uploadService: {
            apiKey: uuid().replaceAll(/[^-]/g, "x"),
          },
        },
      }),
    };
  }, [
    props.authSession,
    props.workspaceFile.name,
    props.workspaceFile.workspaceId,
    workspacePromise.data?.descriptor.name,
  ]);

  const [expandedCardId, setExpandedCardId] = useState("");

  const toggleExpandedCard = useCallback((cardId: string) => {
    setExpandedCardId((currentCardId: string) => {
      if (cardId === currentCardId) {
        return "";
      }
      return cardId;
    });
  }, []);

  const tokens = useMemo(() => {
    return (
      <Grid hasGutter>
        {Object.entries(devDeploymentTokensWithDescriptions.descriptions).map(([key, value]) => (
          <GridItem key={key} span={12}>
            <Card isCompact isRounded isFlat isExpanded={expandedCardId === key}>
              <CardHeader onExpand={() => toggleExpandedCard(key)}>
                <CardTitle>{`\${{ ${key} }}`}</CardTitle>
              </CardHeader>
              <CardExpandableContent>
                <CardBody>{value}</CardBody>
                <CardFooter>
                  <b>Default value: </b>
                  <Text component="pre">{devDeploymentTokensWithDescriptions.defaultValues[key]}</Text>
                </CardFooter>
              </CardExpandableContent>
            </Card>
          </GridItem>
        ))}
      </Grid>
    );
  }, [devDeploymentTokensWithDescriptions, toggleExpandedCard, expandedCardId]);

  const [isExpanded, setIsExpanded] = useState(false);

  return (
    <ExpandableSection
      toggleText="Tokens List"
      onToggle={() => setIsExpanded(!isExpanded)}
      isExpanded={isExpanded}
      isIndented={true}
    >
      {tokens}
    </ExpandableSection>
  );
}
