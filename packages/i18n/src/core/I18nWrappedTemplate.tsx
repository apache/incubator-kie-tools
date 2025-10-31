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

// component to replace placeholders in text with React components
export const I18nWrappedTemplate = ({
  text,
  interpolationMap,
}: {
  text: string;
  interpolationMap: Record<string, React.ReactNode>;
}) => {
  // Matches {key} where key is one of the placeholder keys
  const interpolationMapRegex = new RegExp(`\\{(${Object.keys(interpolationMap).join("|")})\\}`, "g");

  return (
    <>
      {text
        .split(interpolationMapRegex)
        .map((value, i) =>
          value in interpolationMap ? <React.Fragment key={i}>{interpolationMap[value]}</React.Fragment> : value
        )}
    </>
  );
};
