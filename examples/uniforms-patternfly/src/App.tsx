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
import { useState } from "react";
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import { Nav, NavItem, NavList } from "@patternfly/react-core/dist/js/components/Nav";
import { Page, PageHeader } from "@patternfly/react-core/dist/js/components/Page";
import { Form } from "./Form";

export enum SchemaType {
  JsonSchema,
  SimpleSchema,
}

export function App() {
  const [schemaType, setSchemaType] = useState<SchemaType>(SchemaType.JsonSchema);

  return (
    <Page
      header={
        <PageHeader
          logo={<Brand src={"logo.png"} alt="Logo" />}
          topNav={
            <Nav onSelect={(e) => setSchemaType(e.itemId as SchemaType)} aria-label="Nav" variant="horizontal">
              <NavList>
                <NavItem itemId={SchemaType.JsonSchema} isActive={schemaType === SchemaType.JsonSchema}>
                  JSON Schema
                </NavItem>
                <NavItem itemId={SchemaType.SimpleSchema} isActive={schemaType === SchemaType.SimpleSchema}>
                  Simple Schema
                </NavItem>
              </NavList>
            </Nav>
          }
        />
      }
    >
      <Form schemaType={schemaType} />
    </Page>
  );
}
