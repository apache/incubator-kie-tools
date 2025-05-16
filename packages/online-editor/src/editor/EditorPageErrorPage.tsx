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

import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateHeader,
  EmptyStateFooter,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { useRoutes } from "../navigation/Hooks";
import { useNavigate } from "react-router-dom";
import { ClipboardCopy, ClipboardCopyVariant } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";

export interface Props {
  errors: string[];
  path: string;
  title: string;
}

export function EditorPageErrorPage(props: Props) {
  const routes = useRoutes();
  const navigate = useNavigate();
  const [showDetails, setShowDetails] = useState(false);

  const returnHome = useCallback(() => {
    navigate({ pathname: routes.home.path({}) });
  }, [navigate, routes]);

  const detailsString = useMemo(() => {
    return props.errors.join("\n");
  }, [props.errors]);

  return (
    <Bullseye>
      <EmptyState>
        <EmptyStateHeader icon={<EmptyStateIcon icon={ExclamationTriangleIcon} />} />
        <TextContent>
          <Text component={"h2"}>{props.title}</Text>
        </TextContent>
        <EmptyStateBody>
          <PageSection>
            <TextContent style={{ textOverflow: "ellipsis", overflow: "hidden" }}>{props.path}</TextContent>
            <br />
            {props.errors && (
              <>
                <Button variant={ButtonVariant.link} onClick={() => setShowDetails((prev) => !prev)}>
                  {showDetails ? "Hide details" : "Show details"}
                </Button>

                {showDetails && (
                  <PageSection variant={"light"} isFilled={true} style={{ height: "100%", minWidth: "1000px" }}>
                    <ClipboardCopy
                      variant={ClipboardCopyVariant.expansion}
                      isReadOnly={true}
                      hoverTip="Copy"
                      clickTip="Copied"
                    >{`${detailsString}`}</ClipboardCopy>
                  </PageSection>
                )}
              </>
            )}
            <br />
          </PageSection>
        </EmptyStateBody>
        <EmptyStateFooter>
          <Button variant={ButtonVariant.tertiary} onClick={returnHome}>
            Return home
          </Button>
        </EmptyStateFooter>
      </EmptyState>
    </Bullseye>
  );
}
