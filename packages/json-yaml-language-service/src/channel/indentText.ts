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

/**
 * Indent a text.
 *
 * @param text the text to indent
 * @param indentNum number of characters to indent
 * @param indentChar character to use to indent
 * @param skipFirstLine true to skip first line
 * @returns the indented string
 */
export function indentText(text: string, indentNum = 0, indentChar = " ", skipFirstLine = true): string {
  if (!text) {
    return "";
  }

  const indentation = new Array(indentNum).fill(indentChar).join("");

  // replace all start of line with the indentation
  const indentedText = text.replace(/^/gm, indentation);

  return !skipFirstLine ? indentedText : indentedText.replace(indentation, "");
}
