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

import { query as jsonPathQuery } from "jsonpath-rfc9535";

export type TokenMap = {
  [x: string]: string | number | boolean | TokenMap | TokenMap[];
};

type FlattenedTokenMap = Record<string, string>;

const TEMPLATE = {
  OPEN: "${{",
  CLOSE: "}}",
} as const;

const MAX_INTERPOLATION_DEPTH = 50;

const TOKEN_REGEX_TEST = new RegExp(`${escapeRegex(TEMPLATE.OPEN)}[\\s\\S]*?${escapeRegex(TEMPLATE.CLOSE)}`);

function escapeRegex(regexInput: string) {
  return regexInput.replace(/[/\-\\^$*+?.()|[\]{}]/g, "\\$&");
}

export function flattenTokenMap(
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
  // Matches TEMPLATE.OPEN<anything in between>TEMPLATE.CLOSE.
  // <anything in between> can have special characters, such as line breaks.
  const regex = new RegExp(`${escapeRegex(TEMPLATE.OPEN)}[\\s\\S]*?${escapeRegex(TEMPLATE.CLOSE)}`, "gm");

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

function extractTokenBody(token: string): string {
  const openLen = TEMPLATE.OPEN.length;
  const closeLen = TEMPLATE.CLOSE.length;
  return token.slice(openLen, token.length - closeLen).trim();
}

function containsToken(input: string): boolean {
  TOKEN_REGEX_TEST.lastIndex = 0;
  return TOKEN_REGEX_TEST.test(input);
}

/**
 * Finds the end position of a balanced token starting at startPos.
 * Handles nested ${{ }} by counting depth.
 */
function findBalancedTokenEnd(input: string, startPos: number): number {
  const openLen = TEMPLATE.OPEN.length;
  const closeLen = TEMPLATE.CLOSE.length;
  let depth = 0;
  let i = startPos;

  while (i < input.length) {
    if (input.substring(i, i + openLen) === TEMPLATE.OPEN) {
      depth++;
      i += openLen;
    } else if (input.substring(i, i + closeLen) === TEMPLATE.CLOSE) {
      depth--;
      if (depth === 0) {
        return i + closeLen;
      }
      i += closeLen;
    } else {
      i++;
    }
  }
  return -1; // Unbalanced
}

function evaluateJsonPath(jsonPath: string, tokenMap: TokenMap): string | undefined {
  const result = jsonPathQuery(tokenMap, jsonPath);

  if (result.length === 0) {
    return undefined;
  }

  const value = result[0];

  if (value === null || value === undefined) {
    return undefined;
  }

  return String(value);
}

function resolveToken(token: string, tokenMap: TokenMap, depth: number, cache: Map<string, string>): string {
  const body = extractTokenBody(token);

  if (cache.has(body)) {
    return cache.get(body)!;
  }

  const resolvedBody = containsToken(body) ? interpolateStringRecursive(body, tokenMap, depth + 1, cache) : body;

  const value = evaluateJsonPath(resolvedBody, tokenMap);
  if (value === undefined) {
    throw new Error(`Unresolvable token: ${resolvedBody}`);
  }

  const finalValue = containsToken(value) ? interpolateStringRecursive(value, tokenMap, depth + 1, cache) : value;

  cache.set(body, finalValue);
  return finalValue;
}

function interpolateStringRecursive(
  input: string,
  tokenMap: TokenMap,
  depth: number = 0,
  cache: Map<string, string> = new Map()
): string {
  if (depth >= MAX_INTERPOLATION_DEPTH) {
    throw new Error(
      `Max interpolation depth (${MAX_INTERPOLATION_DEPTH}) exceeded. Possible circular reference in tokens.`
    );
  }

  if (!containsToken(input)) {
    return input;
  }

  const parts: string[] = [];
  let lastIndex = 0;
  let currentIndex = 0;

  while (currentIndex < input.length) {
    const openIndex = input.indexOf(TEMPLATE.OPEN, currentIndex);

    if (openIndex === -1) {
      parts.push(input.substring(lastIndex));
      break;
    }

    if (openIndex > lastIndex) {
      parts.push(input.substring(lastIndex, openIndex));
    }

    const tokenEnd = findBalancedTokenEnd(input, openIndex);
    if (tokenEnd === -1) {
      const preview = input.substring(openIndex, Math.min(openIndex + 50, input.length));
      throw new Error(`Unbalanced token braces at position ${openIndex}: "${preview}..."`);
    }

    const token = input.substring(openIndex, tokenEnd);
    const resolvedValue = resolveToken(token, tokenMap, depth, cache);
    parts.push(resolvedValue);

    lastIndex = tokenEnd;
    currentIndex = tokenEnd;
  }

  return parts.join("");
}

export function interpolateK8sResourceYaml(k8sResourceYaml: string, tokenMap?: TokenMap): string {
  if (!tokenMap) {
    return k8sResourceYaml;
  }

  const flattenedTokens = flattenTokenMap(tokenMap);
  const trimmedTokensFromInputText = trimTokensFromInputText(k8sResourceYaml, flattenedTokens);

  let result = Object.entries(flattenedTokens).reduce(
    (result, [tokenName, tokenValue]) => result.replaceAll(`${TEMPLATE.OPEN}${tokenName}${TEMPLATE.CLOSE}`, tokenValue),
    trimmedTokensFromInputText
  );

  result = interpolateStringRecursive(result, tokenMap);

  return result;
}
