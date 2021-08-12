/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { I18nWrapped } from "@kogito-tooling/i18n/dist/react-components";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import * as React from "react";
import { useDmnFormI18n } from "./i18n";

const KOGITO_JIRA_LINK = "https://issues.jboss.org/projects/KOGITO";

export function DmnFormErrorPage() {
  const { i18n, locale } = useDmnFormI18n();

  return (
    <>
      <EmptyState>
        <EmptyStateIcon icon={ExclamationTriangleIcon} />
        <TextContent>
          <Text component={"h2"}>{i18n.page.error.title}</Text>
        </TextContent>
        <EmptyStateBody>
          <TextContent>
            <Text component={TextVariants.p}>{i18n.page.error.explanation}</Text>
          </TextContent>
          <br />
          <TextContent>
            <I18nWrapped
              components={{
                jira: (
                  <a href={KOGITO_JIRA_LINK} target={"_blank"}>
                    {KOGITO_JIRA_LINK}
                  </a>
                ),
              }}
            >
              {i18n.page.error.referToJira}
            </I18nWrapped>
          </TextContent>
        </EmptyStateBody>
      </EmptyState>
    </>
  );
}
