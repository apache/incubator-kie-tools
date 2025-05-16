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
import { Link, Location } from "react-router-dom";
import { useMemo, ReactElement } from "react";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Breadcrumb, BreadcrumbItem } from "@patternfly/react-core/dist/js/components/Breadcrumb";
import { PageTitle } from "../PageTitle";
import { componentOuiaProps, OUIAProps } from "../../ouiaTools";

interface PageSectionHeaderProps {
  titleText: string | ReactElement;
  breadcrumbText?: (string | ReactElement)[];
  breadcrumbPath?: Array<Partial<Location> | string>;
}
export const PageSectionHeader: React.FC<PageSectionHeaderProps & OUIAProps> = ({
  titleText,
  breadcrumbText,
  breadcrumbPath,
  ouiaId,
  ouiaSafe,
}) => {
  const breadcrumbContent = useMemo(() => {
    const items: JSX.Element[] = [];
    if (!breadcrumbPath || !breadcrumbText) {
      return;
    }
    breadcrumbText?.forEach((text, index) => {
      if (index === breadcrumbText.length - 1) {
        items.push(
          <BreadcrumbItem key={index} isActive>
            {text}
          </BreadcrumbItem>
        );
      } else {
        items.push(
          <BreadcrumbItem key={index}>
            <Link to={breadcrumbPath[index]}>{text}</Link>
          </BreadcrumbItem>
        );
      }
    });
    return items;
  }, [breadcrumbPath, breadcrumbText]);

  return (
    <PageSection variant="light" {...componentOuiaProps(ouiaId, "page-section-header", ouiaSafe)}>
      {breadcrumbContent && <Breadcrumb>{breadcrumbContent}</Breadcrumb>}
      <PageTitle title={titleText} />
    </PageSection>
  );
};
