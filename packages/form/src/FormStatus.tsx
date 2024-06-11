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
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { I18nWrapped } from "@kie-tools-core/i18n/dist/react-components";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { FormI18n } from "./i18n";

const ISSUES_URL = "https://github.com/apache/incubator-kie-issues/issues";

export enum FormStatus {
  WITHOUT_ERROR,
  VALIDATOR_ERROR,
  AUTO_GENERATION_ERROR,
  EMPTY,
}

interface CommonFormStatusProps {
  i18n: FormI18n;
}

interface EmptyFormStatusProps extends CommonFormStatusProps {}

export function EmptyFormStatus({ i18n }: EmptyFormStatusProps) {
  return (
    <div>
      <EmptyState>
        <EmptyStateIcon icon={CubesIcon} />
        <TextContent>
          <Text component={"h2"}>{i18n.form.status.emptyForm.title}</Text>
        </TextContent>
        <EmptyStateBody>
          <TextContent>
            <Text component={TextVariants.p}>{i18n.form.status.emptyForm.explanation}</Text>
          </TextContent>
        </EmptyStateBody>
      </EmptyState>
    </div>
  );
}

interface AutoGenerationErrorWithNotificationFormStatusProps extends CommonFormStatusProps {
  notificationsPanel: true;
  openValidationTab: () => void;
}

interface AutoGenerationErrorWithoutNotificationFormStatusProps extends CommonFormStatusProps {
  notificationsPanel: false;
}

type AutoGenerationErrorFormStatusProps =
  | AutoGenerationErrorWithNotificationFormStatusProps
  | AutoGenerationErrorWithoutNotificationFormStatusProps;

export function AutoGenerationErrorFormStatus(props: AutoGenerationErrorFormStatusProps) {
  return (
    <div>
      <EmptyState>
        <EmptyStateIcon icon={ExclamationIcon} />
        <TextContent>
          <Text component={"h2"}>{props.i18n.form.status.autoGenerationError.title}</Text>
        </TextContent>
        <EmptyStateBody>
          <TextContent>{props.i18n.form.status.autoGenerationError.explanation}</TextContent>
          <br />
          {props.notificationsPanel && (
            <TextContent>
              <I18nWrapped
                components={{ link: <a onClick={props.openValidationTab}>{props.i18n.terms.validation}</a> }}
              >
                {props.i18n.form.status.autoGenerationError.checkNotificationPanel}
              </I18nWrapped>
            </TextContent>
          )}
        </EmptyStateBody>
      </EmptyState>
    </div>
  );
}

interface ValidatorErrorFormStatusProps extends CommonFormStatusProps {}

export function ValidatorErrorFormStatus({ i18n }: ValidatorErrorFormStatusProps) {
  return (
    <div>
      <EmptyState>
        <EmptyStateIcon icon={ExclamationTriangleIcon} />
        <TextContent>
          <Text component={"h2"}>{i18n.form.status.validatorError.title}</Text>
        </TextContent>
        <EmptyStateBody>
          <TextContent>
            <Text>
              <I18nWrapped
                components={{
                  jira: (
                    <a href={ISSUES_URL} target={"_blank"} rel={"noopener noreferrer"}>
                      {ISSUES_URL}
                    </a>
                  ),
                }}
              >
                {i18n.form.status.validatorError.message}
              </I18nWrapped>
            </Text>
          </TextContent>
        </EmptyStateBody>
      </EmptyState>
    </div>
  );
}
