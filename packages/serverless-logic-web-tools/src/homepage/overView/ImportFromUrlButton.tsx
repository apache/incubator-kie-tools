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
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers";
import { useMemo, useCallback } from "react";
import { useEditorEnvelopeLocator } from "../../envelopeLocator/EditorEnvelopeLocatorContext";
import { UrlType, useImportableUrl } from "../../workspace/hooks/ImportableUrlHooks";

type ImportFromUrlButtonProps = {
  allowedTypes?: UrlType[];
  url: string;
  isUrlValid: ValidatedOptions;
  isLoading?: boolean;
  onClick?: () => void;
};

export function ImportFromUrlButton(props: ImportFromUrlButtonProps) {
  const {
    allowedTypes = Object.values(UrlType),
    url = "",
    isUrlValid = ValidatedOptions.default,
    isLoading,
    onClick = () => {},
  } = props;
  const editorEnvelopeLocator = useEditorEnvelopeLocator();

  const importableUrl = useImportableUrl({
    isFileSupported: (path: string) => editorEnvelopeLocator.hasMappingFor(path),
    urlString: url,
  });

  const isUrlTypeImportable = useCallback(
    (urlType: UrlType): boolean => importableUrl.type === urlType && allowedTypes?.includes(urlType),
    [importableUrl, allowedTypes]
  );

  const buttonLabel = useMemo(() => {
    if (isUrlTypeImportable(UrlType.GITHUB) || isUrlTypeImportable(UrlType.GIST)) {
      return "Clone";
    }

    return "Import";
  }, [isUrlTypeImportable]);

  return (
    <Button
      variant={url.length > 0 ? ButtonVariant.primary : ButtonVariant.secondary}
      onClick={onClick}
      isDisabled={isUrlValid !== ValidatedOptions.success}
      isLoading={isLoading}
      ouiaId="import-from-url-button"
    >
      {buttonLabel}
    </Button>
  );
}
