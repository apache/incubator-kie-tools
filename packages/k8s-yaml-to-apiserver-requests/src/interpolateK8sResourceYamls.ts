import { K8sResourceYaml } from "./types";

export type TokenMap = {
  [x: string]: string | TokenMap;
};

type FlattenedTokenMap = Record<string, string>;

const TEMPLATE = {
  OPEN: "${{",
  CLOSE: "}}",
} as const;

function escapeRegex(regexInput: string) {
  return regexInput.replace(/[/\-\\^$*+?.()|[\]{}]/g, "\\$&");
}

function flattenTokenMap(
  obj: Record<string, any>,
  parent: any = undefined,
  res: Record<string, string> = {}
): FlattenedTokenMap {
  for (const key in obj) {
    const propName = parent ? parent + "." + key : key;
    if (typeof obj[key] == "object") {
      flattenTokenMap(obj[key], propName, res);
    } else {
      res[propName] = obj[key];
    }
  }
  return res;
}

function trimTokensFromInputText(inputText: string, flattenedTokens: FlattenedTokenMap) {
  const regex = new RegExp(`${escapeRegex(TEMPLATE.OPEN)}.*?${escapeRegex(TEMPLATE.CLOSE)}`, "gm");

  let trimmedInputText = inputText;

  inputText.match(regex)?.forEach((match) => {
    const strippedAndTrimmedTemplateMatch = match.replaceAll(TEMPLATE.OPEN, "").replaceAll(TEMPLATE.CLOSE, "").trim();

    if (Object.keys(flattenedTokens).includes(strippedAndTrimmedTemplateMatch)) {
      trimmedInputText = trimmedInputText.replaceAll(
        match,
        `${TEMPLATE.OPEN}${strippedAndTrimmedTemplateMatch}${TEMPLATE.CLOSE}`
      );
    }
  });
  return trimmedInputText;
}

export function interpolateK8sResourceYamls(k8sResourceYaml: string, tokenMap: TokenMap) {
  const flattenedTokens = flattenTokenMap(tokenMap);

  const trimmedTokensFromInputText = trimTokensFromInputText(k8sResourceYaml, flattenedTokens);

  return Object.entries(flattenedTokens).reduce(
    (result, [tokenName, tokenValue]) => result.replaceAll(`${TEMPLATE.OPEN}${tokenName}${TEMPLATE.CLOSE}`, tokenValue),
    trimmedTokensFromInputText
  );
}
