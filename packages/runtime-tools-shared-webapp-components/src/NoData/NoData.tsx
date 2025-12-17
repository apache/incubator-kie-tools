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

import React, { useState, useMemo } from "react";
import {
  EmptyState,
  EmptyStateIcon,
  EmptyStateVariant,
  EmptyStateBody,
  EmptyStateHeader,
  EmptyStateFooter,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Button } from "@patternfly/react-core/dist/js/components/Button";

import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { SearchIcon } from "@patternfly/react-icons/dist/js/icons/search-icon";
import { Navigate, useLocation } from "react-router-dom";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";

export interface IOwnProps {
  defaultPath: string;
  defaultButton: string;
}

export const NoData: React.FC<IOwnProps & OUIAProps> = ({ ouiaId, ouiaSafe, ...props }) => {
  const location = useLocation();

  const prevPath = useMemo(() => {
    return (location.state?.prev ?? props.defaultPath).split("/").filter((item: string) => item);
  }, [location.state, props.defaultPath]);

  const [isRedirect, setIsRedirect] = useState(false);
  const redirectHandler = () => {
    setIsRedirect(true);
  };
  return (
    <>
      {isRedirect && <Navigate replace to={`/${prevPath?.[0] ?? ""}`} />}
      <PageSection isFilled={true} {...componentOuiaProps(ouiaId, "no-data", ouiaSafe ? ouiaSafe : !isRedirect)}>
        <Bullseye>
          <EmptyState variant={EmptyStateVariant.full}>
            <EmptyStateHeader
              titleText={<>{location.state ? location.state.title : "No matches"}</>}
              icon={<EmptyStateIcon icon={SearchIcon} />}
              headingLevel="h1"
            />
            <EmptyStateBody>{location.state ? location.state.description : "No data to display"}</EmptyStateBody>
            <EmptyStateFooter>
              <Button variant="primary" onClick={redirectHandler} data-testid="redirect-button">
                {location.state ? location.state.buttonText : props.defaultButton}
              </Button>
            </EmptyStateFooter>
          </EmptyState>
        </Bullseye>
      </PageSection>
    </>
  );
};
