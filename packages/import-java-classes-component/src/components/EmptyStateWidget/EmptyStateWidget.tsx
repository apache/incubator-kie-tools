/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { EmptyState, EmptyStateBody, EmptyStateIcon, Title } from "@patternfly/react-core";

export interface EmptyStateWidgetProps {
  /** Icon applied in the center of the Empty State Widget */
  emptyStateIcon: React.ComponentType;
  /** Empty State Widget title heading */
  emptyStateTitleHeading: "h1" | "h2" | "h3" | "h4" | "h5" | "h6";
  /** Empty State Widget title size */
  emptyStateTitleSize?: "md" | "lg" | "xl" | "2xl" | "3xl" | "4xl";
  /** Empty State Widget title text */
  emptyStateTitleText: string;
  /** Empty State Widget body text */
  emptyStateBodyText: string;
}

export const EmptyStateWidget: React.FunctionComponent<EmptyStateWidgetProps> = ({
  emptyStateIcon,
  emptyStateTitleHeading,
  emptyStateTitleSize,
  emptyStateTitleText,
  emptyStateBodyText,
}: EmptyStateWidgetProps) => {
  return (
    <EmptyState>
      <EmptyStateIcon icon={emptyStateIcon} />
      <Title headingLevel={emptyStateTitleHeading} size={emptyStateTitleSize}>
        {emptyStateTitleText}
      </Title>
      <EmptyStateBody>{emptyStateBodyText}</EmptyStateBody>
    </EmptyState>
  );
};
