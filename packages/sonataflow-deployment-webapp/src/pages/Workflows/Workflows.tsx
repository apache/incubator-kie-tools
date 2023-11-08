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
import { PromiseStateStatus, PromiseStateWrapper } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Skeleton } from "@patternfly/react-core/dist/js/components/Skeleton";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Toolbar, ToolbarContent, ToolbarItem } from "@patternfly/react-core/dist/js/components/Toolbar";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { CubesIcon, ExclamationCircleIcon, PlayIcon } from "@patternfly/react-icons/dist/js/icons";
import { Table } from "@patternfly/react-table/dist/js/components/Table";
import { Tbody, Td, Th, Thead, Tr } from "@patternfly/react-table/dist/js/components/TableComposable";
import { KUBESMARTS_URL } from "../../AppConstants";
import { useOpenApi } from "../../context/OpenApiContext";
import { BasePage } from "../BasePage";
import { Link } from "react-router-dom";
import { routes } from "../../routes";
import { ErrorKind, ErrorPage } from "../ErrorPage";

export function Workflows() {
  const openApi = useOpenApi();

  if (openApi.openApiPromise.status === PromiseStateStatus.REJECTED) {
    return <ErrorPage kind={ErrorKind.OPENAPI} errors={["OpenAPI service not available"]} />;
  }

  return (
    <BasePage>
      <PageSection variant={"light"}>
        <TextContent>
          <Text component={TextVariants.h1}>Workflows</Text>
        </TextContent>
      </PageSection>

      <PageSection>
        <Toolbar>
          <ToolbarContent style={{ paddingLeft: "10px", paddingRight: "10px" }}>
            <ToolbarItem alignment={{ default: "alignLeft" }}>
              <Link to={routes.workflows.cloudEvent.path({})}>
                <Button>Trigger Cloud Event</Button>
              </Link>
            </ToolbarItem>
          </ToolbarContent>
        </Toolbar>

        <Table aria-label="Simple table">
          <Thead>
            <Tr>
              <Th>Workflow Name</Th>
              <Th>Endpoint</Th>
              <Th>Actions</Th>
            </Tr>
          </Thead>
          <Tbody>
            <PromiseStateWrapper
              promise={openApi.openApiPromise}
              pending={
                <>
                  <Tr>
                    <Td>
                      <Skeleton></Skeleton>
                    </Td>
                    <Td>
                      <Skeleton></Skeleton>
                    </Td>
                    <Td>
                      <Skeleton></Skeleton>
                    </Td>
                  </Tr>
                  <Tr>
                    <Td>
                      <Skeleton></Skeleton>
                    </Td>
                    <Td>
                      <Skeleton></Skeleton>
                    </Td>
                    <Td>
                      <Skeleton></Skeleton>
                    </Td>
                  </Tr>
                </>
              }
              rejected={(errors) => <ErrorTableState errors={errors}></ErrorTableState>}
              resolved={(data) =>
                !data.tags || !data.tags.length ? (
                  <EmptyTableState />
                ) : (
                  data.tags?.map((tag) => (
                    <Tr key={tag.name}>
                      <Td>{tag.name}</Td>
                      <Td>
                        {window.location.origin}/{tag.name}
                      </Td>
                      <Td>
                        <Link to={routes.workflows.form.path({ workflowId: tag.name })}>
                          <PlayIcon />
                        </Link>
                      </Td>
                    </Tr>
                  ))
                )
              }
            />
          </Tbody>
        </Table>
      </PageSection>
    </BasePage>
  );
}

function EmptyTableState() {
  return (
    <Tr>
      <Td colSpan={3}>
        <Bullseye>
          <EmptyState>
            <EmptyStateIcon icon={CubesIcon} />
            <Title headingLevel="h4" size="lg">
              {`Nothing here`}
            </Title>
            <EmptyStateBody>
              <TextContent>
                <Text>
                  Start by creating a model on{" "}
                  <a href={KUBESMARTS_URL} target="_blank" rel="noopener noreferrer">
                    {KUBESMARTS_URL}
                  </a>
                </Text>
              </TextContent>
            </EmptyStateBody>
          </EmptyState>
        </Bullseye>
      </Td>
    </Tr>
  );
}

function ErrorTableState(props: { errors: string[] }) {
  return (
    <Tr>
      <Td colSpan={3}>
        <Bullseye>
          <EmptyState>
            <EmptyStateIcon icon={ExclamationCircleIcon} color="#a30000" />
            <Title headingLevel="h4" size="lg">
              {`Unable to connect`}
            </Title>
            <EmptyStateBody>
              <TextContent>
                <Text>There was a problem fetching the data: {props.errors.join(", ")}!</Text>
              </TextContent>
            </EmptyStateBody>
          </EmptyState>
        </Bullseye>
      </Td>
    </Tr>
  );
}
