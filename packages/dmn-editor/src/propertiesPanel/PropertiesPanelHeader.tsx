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

import { useMemo } from "react";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Truncate } from "@patternfly/react-core/dist/js/components/Truncate";
import { AngleDownIcon } from "@patternfly/react-icons/dist/js/icons/angle-down-icon";
import { AngleRightIcon } from "@patternfly/react-icons/dist/js/icons/angle-right-icon";
import "./PropertiesPanelHeader.css";

export function PropertiesPanelHeader(props: {
  icon?: React.ReactNode;
  title: string | React.ReactNode;
  expands?: boolean;
  fixed?: boolean;
  isSectionExpanded?: boolean;
  toogleSectionExpanded?: () => void;
  action?: React.ReactNode;
}) {
  const propertiesPanelHeaderClass = useMemo(() => {
    let className = "kie-dmn-editor--properties-panel-header";
    if (props.fixed) {
      className += " kie-dmn-editor--properties-panel-header-fixed";
    } else {
      className += " kie-dmn-editor--properties-panel-header-not-fixed";
    }

    if (props.fixed || !props.expands || !props.isSectionExpanded) {
      className += " kie-dmn-editor--properties-panel-header-border";
    }

    return className;
  }, [props.expands, props.fixed, props.isSectionExpanded]);

  return (
    <div className={propertiesPanelHeaderClass}>
      <div className={"kie-dmn-editor--properties-panel-header-items"}>
        {props.expands && (
          <div className={"kie-dmn-editor--properties-panel-header-toogle-expanded"}>
            <Button
              title={`Expand / collapse ${props.title}`}
              variant={ButtonVariant.plain}
              className={"kie-dmn-editor--documentation-link--row-expand-toogle"}
              onClick={() => props.toogleSectionExpanded?.()}
            >
              {(props.isSectionExpanded && <AngleDownIcon />) || <AngleRightIcon />}
            </Button>
          </div>
        )}
        {props.icon && <div className={"kie-dmn-editor--properties-panel-header-icon"}>{props.icon}</div>}
        <div className={"kie-dmn-editor--properties-panel-header-title"}>
          {typeof props.title === "string" ? <Truncate content={props.title} /> : props.title}
        </div>
        {props.action && <div className={"kie-dmn-editor--properties-panel-header-action"}>{props.action}</div>}
      </div>
    </div>
  );
}
