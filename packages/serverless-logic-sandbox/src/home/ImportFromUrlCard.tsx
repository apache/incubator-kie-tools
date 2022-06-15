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

import { Card, CardBody, CardFooter, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { CodeIcon } from "@patternfly/react-icons/dist/js/icons/code-icon";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { useRoutes } from "../navigation/Hooks";
import { useHistory } from "react-router";
import { ImportFromUrlForm } from "../workspace/components/ImportFromUrlForm";
import { UrlType, useImportableUrl } from "../workspace/hooks/ImportableUrlHooks";

export function ImportFromUrlCard() {
  const routes = useRoutes();
  const history = useHistory();
  const [url, setUrl] = useState("");

  const importFromUrl = useCallback(() => {
    history.push({
      pathname: routes.importModel.path({}),
      search: routes.importModel.queryString({ url: url }),
    });
  }, [history, routes, url]);

  const importableUrl = useImportableUrl(url);

  const buttonLabel = useMemo(() => {
    if (importableUrl.type === UrlType.GITHUB || importableUrl.type === UrlType.GIST) {
      return "Clone";
    }

    return "Import";
  }, [importableUrl]);

  return (
    <Card isFullHeight={true} isLarge={true} isPlain={true} isSelected={url.length > 0}>
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
        <ImportFromUrlForm url={url} onChange={setUrl} onSubmit={importFromUrl} />
      </CardBody>
      <CardFooter>
        <Button
          variant={url.length > 0 ? ButtonVariant.primary : ButtonVariant.secondary}
          onClick={importFromUrl}
          ouiaId="import-from-url-button"
        >
          {buttonLabel}
        </Button>
      </CardFooter>
    </Card>
  );
}
