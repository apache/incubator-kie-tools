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

import React from "react";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStatePrimary,
  PageSection,
  PageSectionVariants,
  TextContent,
  Title,
} from "@patternfly/react-core/dist/js/components";
import { WarningTriangleIcon } from "@patternfly/react-icons/dist/js/icons";
import { NavLink } from "react-router-dom";

const ApplicationError = () => {
  return (
    <>
      <PageSection variant={PageSectionVariants.light}>
        <TextContent>
          <Title size="3xl" headingLevel="h2">
            Error
          </Title>
        </TextContent>
      </PageSection>
      <PageSection isFilled={false}>
        <EmptyState variant={"xl"}>
          <EmptyStateIcon icon={WarningTriangleIcon} />
          <Title size="2xl" headingLevel="h4">
            Server Error
          </Title>
          <EmptyStateBody>Something went wrong with your server. Reach out to your IT team for help.</EmptyStateBody>
          <EmptyStatePrimary>
            <NavLink to="/">Return to home page</NavLink>
          </EmptyStatePrimary>
        </EmptyState>
      </PageSection>
    </>
  );
};

export default ApplicationError;
