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
import { connectField, filterDOMProps, HTMLFieldProps } from "uniforms/esm";
import { Card, CardBody } from "@patternfly/react-core/dist/js/components/Card";
import wrapField from "@kie-tools/uniforms-patternfly/dist/esm/wrapField";
import { formDmnI18n } from "../i18n";
import { I18nWrappedTemplate } from "@kie-tools-core/i18n/dist/react-components";

export type FormDmnNotSupportedField = HTMLFieldProps<
  object,
  HTMLDivElement,
  { recursion: boolean; recursionRef: string }
>;

function FormDmnNotSupportedField({ recursion, recursionRef, ...props }: FormDmnNotSupportedField) {
  const i18n = formDmnI18n.getCurrent();
  return wrapField(
    props as any,
    <Card isCompact={true} {...filterDOMProps(props)}>
      <CardBody style={{ backgroundColor: "rgb(240,240,240)" }}>
        <div
          aria-label="field type not supported"
          style={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            width: "100%",
          }}
        >
          <I18nWrappedTemplate
            text={i18n.result.recursiveStructureNotSupported}
            interpolationMap={{
              linebreak: <br />,
            }}
          />
        </div>
      </CardBody>
    </Card>
  );
}

export default connectField(FormDmnNotSupportedField);
