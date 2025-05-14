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

import React, { useMemo, useState } from "react";
import {
  EmptyState,
  EmptyStateIcon,
  EmptyStateVariant,
  EmptyStateBody,
  EmptyStateHeader,
  EmptyStateFooter,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Button } from "@patternfly/react-core/dist/js/components/Button";

import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { Navigate, useLocation } from "react-router-dom";
import { componentOuiaProps, OUIAProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools/OuiaUtils";

interface IOwnProps {
  defaultPath: string;
  defaultButton: string;
}

export interface PageNotFoundProps extends IOwnProps, OUIAProps {}

export const PageNotFound: React.FC<PageNotFoundProps> = ({ ouiaId, ouiaSafe, ...props }) => {
  const location = useLocation();

  const prevPath = useMemo(() => {
    return (location.state?.prev ?? props.defaultPath).split("/").filter((item: string) => item);
  }, [location.state, props.defaultPath]);

  const [isRedirect, setIsredirect] = useState(false);
  const redirectHandler = () => {
    setIsredirect(true);
  };
  return (
    <>
      {isRedirect && <Navigate to={`/${prevPath?.[0] ?? ""}`} />}
      <PageSection variant="light" {...componentOuiaProps(ouiaId, "page-not-found", ouiaSafe ? ouiaSafe : !isRedirect)}>
        <Bullseye>
          <EmptyState variant={EmptyStateVariant.full}>
            <EmptyStateHeader
              titleText="404 Error: page not found"
              icon={<EmptyStateIcon icon={ExclamationCircleIcon} color="var(--pf-v5-global--danger-color--100)" />}
              headingLevel="h1"
            />
            <EmptyStateBody>This page could not be found.</EmptyStateBody>
            <EmptyStateFooter>
              <Button variant="primary" onClick={redirectHandler} data-testid="redirect-button">
                {props.defaultButton}
              </Button>
            </EmptyStateFooter>
          </EmptyState>
        </Bullseye>
      </PageSection>
    </>
  );
};
