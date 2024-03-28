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

import { Elements, Meta, MetaType } from ".";
import { buildXmlQName, parseXmlQName } from "./qNames";

export type XmlParserTsIdRandomizerUpdater = (args: { newId: string }) => void;

export type XmlParserTsIdRandomizerMatcher<M extends Meta> = (args: {
  parentJson: any;
  attr: string;
  metaTypeName: keyof M;
  metaType: MetaType;
}) => [string, XmlParserTsIdRandomizerUpdater] | undefined;

export class XmlParserTsIdRandomizer<M extends Meta> {
  private readonly updaters = new Map<string, XmlParserTsIdRandomizerUpdater[]>();

  constructor(
    private readonly args: {
      meta: M;
      elements: Elements;
      newIdGenerator: () => string;
      matchers?: XmlParserTsIdRandomizerMatcher<M>[];
    }
  ) {}

  public getOriginalIds(): Set<string> {
    return new Set([...this.updaters.keys()]);
  }

  public ack<X extends keyof M>({
    json,
    parentJson,
    type,
    attr,
    __$$element,
  }: {
    json: any;
    type: X;
    attr: keyof M[X];
    __$$element?: string;
    parentJson?: any;
  }): XmlParserTsIdRandomizer<M> {
    if (json === undefined) {
      console.debug(`ID RANDOMIZER: ack: ${String(type)}.${String(attr)}: ${json}. skip.`);
      return this;
    }

    const rootMetaProp = this.args.meta[type][attr];
    const resolvedRootMetaPropTypeName = this.args.elements[__$$element ?? json.__$$element] ?? rootMetaProp.type;

    // Array
    if (rootMetaProp.isArray) {
      for (const j of json as any[]) {
        const resolvedMetaTypeName = this.args.elements[__$$element ?? j.__$$element] ?? resolvedRootMetaPropTypeName;
        const resolvedMetaType = this.args.meta[resolvedMetaTypeName];

        for (const metaPropName in resolvedMetaType) {
          this.ack({
            json: j[metaPropName],
            parentJson: j,
            attr: metaPropName,
            type: resolvedMetaTypeName,
          });
        }
      }
    }

    // Object
    else if (this.args.meta[resolvedRootMetaPropTypeName]) {
      const resolvedMetaType = this.args.meta[resolvedRootMetaPropTypeName];
      for (const metaPropName in resolvedMetaType) {
        this.ack({
          json: json[metaPropName],
          parentJson: json,
          attr: metaPropName,
          type: resolvedRootMetaPropTypeName,
        });
      }
    }

    // Leaf
    else {
      console.debug(`ID RANDOMIZER: ack: ${String(type)}.${String(attr)}: ${json} --> ${rootMetaProp.xsdType}`);

      // ID, IDREF
      if (rootMetaProp.xsdType === "xsd:ID" || rootMetaProp.xsdType === "xsd:IDREF") {
        const u: XmlParserTsIdRandomizerUpdater = ({ newId }) => {
          console.debug(
            `ID RANDOMIZER: [ID,IDREF] Updating id from ${parentJson[attr]} to ${newId} @ (${String(type)}.${String(
              attr
            )}: ${json})`
          );
          return (parentJson[attr] = newId);
        };
        this.updaters.set(json, [...(this.updaters.get(json) ?? []), u]);
      }

      // QName
      else if (rootMetaProp.xsdType === "xsd:QName") {
        const qname = parseXmlQName(json);
        const u: XmlParserTsIdRandomizerUpdater = ({ newId }) => {
          console.debug(
            `ID RANDOMIZER: [QName] Updating id from ${qname.localPart} to ${newId} @ (${String(type)}.${String(
              attr
            )}: ${json})`
          );
          return (parentJson[attr] = buildXmlQName({ ...qname, localPart: newId }));
        };
        this.updaters.set(qname.localPart, [...(this.updaters.get(qname.localPart) ?? []), u]);
      }

      // Custom matchers
      else {
        for (const c of this.args.matchers ?? []) {
          const matcherResult = c({
            metaTypeName: type,
            metaType: this.args.meta[type],
            attr: String(attr),
            parentJson,
          });

          if (matcherResult) {
            const [id, u] = matcherResult;
            this.updaters.set(id, [...(this.updaters.get(id) ?? []), u]);
          }
        }
      }
    }

    return this;
  }

  public randomize(): Map<string, string> {
    const newIdsByOriginalId = new Map<string, string>();

    for (const [id, us] of this.updaters) {
      const newId = this.args.newIdGenerator();
      newIdsByOriginalId.set(id, newId);
      for (const u of us) {
        u({ newId });
      }
    }
    return newIdsByOriginalId;
  }
}
