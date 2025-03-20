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
import { Toolbar, ToolbarGroup, ToolbarItem } from "@patternfly/react-core/dist/js/components/Toolbar";
import accessibleStyles from "@patternfly/react-styles/css/utilities/Accessibility/accessibility";
import { css } from "@patternfly/react-styles";
import { SelectPosition } from "@patternfly/react-core/deprecated";
import { AuthSessionSelect } from "../authSessions/components/AuthSessionSelect";

export const ManagementConsoleToolbar: React.FunctionComponent = () => {
  return (
    <React.Fragment>
      <Toolbar>
        <ToolbarGroup align={{ default: "alignRight" }}>
          <ToolbarItem className={css(accessibleStyles.screenReader, accessibleStyles.visibleOnMd)}>
            <AuthSessionSelect isPlain={true} position={SelectPosition.right} />
          </ToolbarItem>
        </ToolbarGroup>
      </Toolbar>
    </React.Fragment>
  );
};
