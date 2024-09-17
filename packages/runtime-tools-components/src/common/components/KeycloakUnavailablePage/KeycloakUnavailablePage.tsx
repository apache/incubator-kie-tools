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
import { Card, CardBody, CardHeader, CardHeaderMain, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import React from "react";
import kogitoLogo from "../../static/favicon.svg";
import { OUIAProps, componentOuiaProps } from "../../ouiaTools";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";

export const KeycloakUnavailablePage: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe }) => {
  return (
    <div
      style={{
        background: "#d2d2d2",
        height: "100%",
        textAlign: "center",
        display: "flex",
        flexDirection: "column",
        justifyContent: "center",
        color: "#6a6e73",
        alignItems: "center",
      }}
      {...componentOuiaProps(ouiaId, "server-unavailable", ouiaSafe)}
    >
      <Brand src={kogitoLogo} alt="Kogito keycloak" heights={{ default: "100px" }} />
      <TextContent>
        <Text component={TextVariants.h1}>Error: 503 - Server unavailable</Text>
      </TextContent>
      <Title headingLevel="h6" style={{ marginTop: "10px" }}>
        Sorry.. the keycloak server seems to be down.
      </Title>
      <Title headingLevel="h6">
        Please contact administrator or{" "}
        <Button variant="link" onClick={() => window.location.reload()} isInline>
          {" "}
          click here to retry
        </Button>
      </Title>
    </div>
  );
};
