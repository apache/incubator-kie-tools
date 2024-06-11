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
  private readonly toAttribute = new Map<string, XmlParserTsIdRandomizerUpdater>();

  private readonly attributed = new Map<string, string>(); // Path -> New
  private readonly randomized = new Map<string, string>(); // Prev -> New

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
    arrayIndex,
    path,
  }: {
    json: any | undefined;
    type: X;
    attr: keyof M[X];
    arrayIndex?: number;
    __$$element?: string;
    parentJson?: any;
    path?: string;
  }): XmlParserTsIdRandomizer<M> {
    const rootMetaProp = this.args.meta[type][attr];

    if (json === undefined && !(rootMetaProp.xsdType === "xsd:ID")) {
      console.debug(`ID RANDOMIZER: ack: ${String(type)}.${String(attr)}: ${json}. skip.`);
      return this;
    }

    const resolvedRootMetaPropTypeName = this.args.elements[__$$element ?? json?.__$$element] ?? rootMetaProp.type;

    // Array
    // Arrays and arrays element will have the same `rootMetaProp`, but array elements will have an array index associated with it
    if (rootMetaProp.isArray && arrayIndex === undefined) {
      for (let index = 0; index < json.length; index++) {
        this.ack({
          json: json[index],
          parentJson: json,
          attr: attr,
          type: type,
          arrayIndex: index,
          path: path === undefined ? `${String(attr)}.${index}` : `${path}.${index}`,
        });
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
          path: path === undefined ? `${String(attr)}.${metaPropName}` : `${path}.${metaPropName}`,
        });
      }
    }

    // Primitive
    else {
      console.debug(`ID RANDOMIZER: ack: ${String(type)}.${String(attr)}: ${json} --> ${rootMetaProp.xsdType}`);

      // When dealing with primitive array elements, `arrayIndex` will not be undefined
      // the `parentJson` will be the array itself. So we use it to access the correct position.
      const accessor = arrayIndex ?? attr;

      // ID, IDREF
      if (rootMetaProp.xsdType === "xsd:ID" || rootMetaProp.xsdType === "xsd:IDREF") {
        // On leaf values, `json` is the string representing an xsd:ID or an xsd:IDREF.
        // When `json` === undefined it means that an object that has an xsd:ID property doesn't have an id.
        // In other words `parentJson` is an undentified object.
        // The second condition of this if statement allows attributing an id to an xsd:ID property
        // that previously wasn't defined.
        const u: XmlParserTsIdRandomizerUpdater = ({ newId }) => {
          console.debug(
            `ID RANDOMIZER: [ID,IDREF] Updating id from ${parentJson[accessor]} to ${newId} @ (${String(type)}.${String(
              attr
            )}: ${json})`
          );
          return (parentJson[accessor] = newId);
        };

        // Attributes an ID in case `json` === undefined
        if (json === undefined) {
          this.toAttribute.set(path === undefined ? String(attr) : path, u);
        } else {
          this.updaters.set(json, [...(this.updaters.get(json) ?? []), u]);
        }
      }

      // QName
      else if (rootMetaProp.xsdType === "xsd:QName") {
        const qname = parseXmlQName(json ?? "");
        const u: XmlParserTsIdRandomizerUpdater = ({ newId }) => {
          console.debug(
            `ID RANDOMIZER: [QName] Updating id from ${qname.localPart} to ${newId} @ (${String(type)}.${String(
              attr
            )}: ${json})`
          );
          return (parentJson[accessor] = buildXmlQName({ ...qname, localPart: newId }));
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

  // skipAlreadyAttributedIds is true by default
  public randomize(args?: { skipAlreadyAttributedIds: boolean }) {
    for (const [path, u] of this.toAttribute) {
      const newId = this.args.newIdGenerator();
      this.attributed.set(path, newId);
      u({ newId });
    }

    for (const [id, us] of this.updaters) {
      // Generates new unique id's for all properties of type xsd:ID that were undefined.
      if (id === undefined) {
        const newId = this.args.newIdGenerator();
        this.randomized.set(id, newId);
        for (const u of us) {
          u({ newId });
        }
      }

      // Generates a new id an updates all references to the old one with the same value.
      else if (args?.skipAlreadyAttributedIds === false) {
        const newId = this.args.newIdGenerator();
        this.randomized.set(id, newId);
        for (const u of us) {
          u({ newId });
        }
      }
    }
    return this;
  }

  public getAttributed() {
    return this.attributed;
  }

  public getRandomized() {
    return this.randomized;
  }
}
