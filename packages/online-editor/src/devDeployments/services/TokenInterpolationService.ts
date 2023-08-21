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

export type DevDeploymentTokens = {
  uniqueId: string;
  name: string;
  defaultContainerImageUrl: string;
};

export type WorkspaceTokens = {
  id: string;
  name: string;
  resourceName: string;
};

export type KubernetesTokens = {
  namespace: string;
};

export type UploadServiceTokens = {
  apiKey: string;
};

export type LabelTokens = {
  createdBy: string;
};

export type AnnotationTokens = {
  uri: string;
  workspaceId: string;
};

const defaultLabelTokens: LabelTokens = {
  createdBy: "tools.kie.org/created-by",
} as const;

const defaultAnnotationTokens: AnnotationTokens = {
  uri: "tools.kie.org/uri",
  workspaceId: "tools.kie.org/workspace-id",
} as const;

export const CREATED_BY_KIE_TOOLS = "kie-tools";

export const TOKENS_PREFIX = "devDeployment";

export type Tokens = DevDeploymentTokens & {
  workspace: WorkspaceTokens;
  kubernetes: KubernetesTokens;
  uploadService: UploadServiceTokens;
  labels: LabelTokens;
  annotations: AnnotationTokens;
};

export type TokensArg = Omit<Tokens, "labels" | "annotations"> & Partial<Tokens>;

const TEMPLATE = {
  OPEN: "{{",
  CLOSE: "}}",
} as const;

function escapeRegex(regexInput: string) {
  return regexInput.replace(/[/\-\\^$*+?.()|[\]{}]/g, "\\$&");
}

function flattenObj(
  obj: Record<string, any>,
  parent: any = undefined,
  res: Record<string, any> = {}
): Record<string, any> {
  for (const key in obj) {
    const propName = parent ? parent + "." + key : key;
    if (typeof obj[key] == "object") {
      flattenObj(obj[key], propName, res);
    } else {
      res[propName] = obj[key];
    }
  }
  return res;
}

export class TokenInterpolationService {
  tokensMap: Tokens;

  constructor(args: TokensArg) {
    this.tokensMap = this.buildTokensMap(args);
  }

  public buildTokensMap(args: TokensArg): Tokens {
    return {
      labels: defaultLabelTokens,
      annotations: defaultAnnotationTokens,
      ...args,
    };
  }

  get flattenedTokens() {
    return flattenObj(this.tokensMap, "devDeployment");
  }

  private trimTokensFromInputText(inputText: string) {
    const regex = new RegExp(`${escapeRegex(TEMPLATE.OPEN)}.*?${escapeRegex(TEMPLATE.CLOSE)}`, "gm");

    let trimmedInputText = inputText;

    console.log(this.flattenedTokens);

    inputText.match(regex)?.forEach((match) => {
      const strippedAndTrimmedTemplateMatch = match.replaceAll(TEMPLATE.OPEN, "").replaceAll(TEMPLATE.CLOSE, "").trim();

      if (Object.keys(this.flattenedTokens).includes(strippedAndTrimmedTemplateMatch)) {
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

    return Object.entries(this.flattenedTokens).reduce(
      (result, [tokenName, tokenValue]) =>
        result.replaceAll(`${TEMPLATE.OPEN}${tokenName}${TEMPLATE.CLOSE}`, tokenValue),
      trimmedTokensFromInputText
    );
  }
}
