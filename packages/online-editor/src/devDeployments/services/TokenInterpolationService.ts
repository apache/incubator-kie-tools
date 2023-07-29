/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

export enum DeploymentTokens {
  "uniqueId" = "uniqueId",
  "name" = "name",
  "resourceName" = "resourceName",
  "workspaceId" = "workspaceId",
  "workspaceName" = "workspaceName",
  "namespace" = "namespace",
}

export enum LabelTokens {
  "createdByLabel" = "createdByLabel",
}

export enum AnnotationTokens {
  "uriAnnotation" = "uriAnnotation",
  "workspaceIdAnnotation" = "workspaceIdAnnotation",
}

export enum UploadServiceTokens {
  "apiKey" = "apiKey",
}

export type Tokens = DeploymentTokens | LabelTokens | AnnotationTokens | UploadServiceTokens;

export type TokensMap = {
  [P in DeploymentTokens | UploadServiceTokens]: string;
} &
  {
    [L in LabelTokens | AnnotationTokens]?: string;
  };

export type FullTokensMap = {
  [P in DeploymentTokens | UploadServiceTokens | LabelTokens | AnnotationTokens]: string;
};

const TEMPLATE = {
  OPEN: "${{",
  CLOSE: "}}",
} as const;

const defaultLabelTokens = {
  [LabelTokens.createdByLabel]: "tools.kie.org/created-by",
} as const;

const defaultAnnotationTokens = {
  [AnnotationTokens.uriAnnotation]: "tools.kie.org/uri",
  [AnnotationTokens.workspaceIdAnnotation]: "tools.kie.org/workspace-id",
} as const;

export const CREATED_BY_KIE_TOOLS = "kie-tools";

function escapeRegex(regexInput: string) {
  return regexInput.replace(/[/\-\\^$*+?.()|[\]{}]/g, "\\$&");
}

export class TokenInterpolationService {
  tokensMap: FullTokensMap;

  constructor(args: TokensMap) {
    this.tokensMap = this.buildTokensMap(args);
  }

  public buildTokensMap(args: TokensMap): FullTokensMap {
    return {
      ...defaultLabelTokens,
      ...defaultAnnotationTokens,
      ...args,
    };
  }

  private trimTokensFromInputText(inputText: string) {
    const regex = new RegExp(`${escapeRegex(TEMPLATE.OPEN)}.*?${escapeRegex(TEMPLATE.CLOSE)}`, "gm");

    let trimmedInputText = inputText;

    inputText.match(regex)?.forEach((match) => {
      const strippedAndTrimmedTemplateMatch = match.replaceAll(TEMPLATE.OPEN, "").replaceAll(TEMPLATE.CLOSE, "").trim();

      if (Object.keys(this.tokensMap).includes(strippedAndTrimmedTemplateMatch)) {
        trimmedInputText = trimmedInputText.replaceAll(
          match,
          `${TEMPLATE.OPEN}${strippedAndTrimmedTemplateMatch}${TEMPLATE.CLOSE}`
        );
      }
    });
    return trimmedInputText;
  }

  public doInterpolation(inputText: string): string {
    const trimmedTokensFromInputText = this.trimTokensFromInputText(inputText);

    return Object.entries(this.tokensMap).reduce(
      (result, [tokenName, tokenValue]) =>
        result.replaceAll(`${TEMPLATE.OPEN}${tokenName}${TEMPLATE.CLOSE}`, tokenValue),
      trimmedTokensFromInputText
    );
  }
}
