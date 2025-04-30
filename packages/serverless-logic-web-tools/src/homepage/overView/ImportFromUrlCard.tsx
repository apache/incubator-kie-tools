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

import React from "react";
import { Card, CardBody, CardFooter, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers";
import { CodeIcon } from "@patternfly/react-icons/dist/js/icons/code-icon";
import { useCallback, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useRoutes } from "../../navigation/Hooks";
import { ImportFromUrlForm } from "../../workspace/components/ImportFromUrlForm";
import { ImportFromUrlButton } from "./ImportFromUrlButton";

export function ImportFromUrlCard() {
  const routes = useRoutes();
  const navigate = useNavigate();
  const [url, setUrl] = useState("");
  const [isUrlValid, setIsUrlValid] = useState(ValidatedOptions.default);

  const importFromUrl = useCallback(() => {
    navigate({
      pathname: routes.importModel.path({}),
      search: routes.importModel.queryString({ url: url }),
    });
  }, [navigate, routes, url]);

  return (
    <Card isFullHeight={true} isPlain={true} isSelected={url.length > 0} isCompact>
      <CardTitle>
        <TextContent>
          <Text component={TextVariants.h2}>
            <CodeIcon />
            &nbsp;&nbsp;From URL
          </Text>
        </TextContent>
      </CardTitle>
      <CardBody>
        <TextContent>
          <Text component={TextVariants.p}>Import a GitHub Repository, a GitHub Gist, or any other file URL.</Text>
        </TextContent>
        <br />
        <ImportFromUrlForm url={url} onChange={setUrl} onSubmit={importFromUrl} onValidate={setIsUrlValid} />
      </CardBody>
      <CardFooter>
        <ImportFromUrlButton url={url} isUrlValid={isUrlValid} onClick={importFromUrl} />
      </CardFooter>
    </Card>
  );
}
