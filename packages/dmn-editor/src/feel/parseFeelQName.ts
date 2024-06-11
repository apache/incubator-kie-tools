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

export type FeelQName = {
  type: "feel-qname"; // To differentiate from XmlQName
  importName?: string;
  localPart: string;
};

export function parseFeelQName(qName: string): FeelQName {
  const split = qName.split(".");

  if (split.length <= 1) {
    return { type: "feel-qname", localPart: qName };
  }

  if (split.length > 2) {
    throw new Error(
      `XML QNames can't have dots (.) on neither the importName or the localPart. Alledged QName: '${qName}'`
    );
  }

  return { type: "feel-qname", importName: split[0], localPart: split[1] };
}

export function buildFeelQName({ importName, localPart }: FeelQName) {
  return importName ? `${importName}.${localPart}` : localPart;
}
