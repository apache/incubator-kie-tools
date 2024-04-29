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

import { XmlParserTsIdRandomizer } from "../src/idRandomizer";
import { generateUuid, uuidRegExp } from "./uuid";

const elements = {
  root: "root",
  nested: "nested",
  person: "person",
  address: "address",
  education: "education",
};

const meta = {
  root: {
    nested: { type: "nested", isArray: false, fromType: "root", xsdType: "// local type" },
  },
  nested: {
    id: { type: "string", isArray: false, fromType: "root", xsdType: "xsd:ID" },
    person: { type: "person", isArray: false, fromType: "nested", xsdType: "// local type" },
    people: { type: "person", isArray: true, fromType: "nested", xsdType: "// local type" },
  },
  address: {
    id: { type: "string", isArray: false, fromType: "root", xsdType: "xsd:ID" },
    street: { type: "string", isArray: false, fromType: "address", xsdType: "xsd:string" },
    country: { type: "string", isArray: false, fromType: "address", xsdType: "xsd:string" },
    number: { type: "integer", isArray: false, fromType: "address", xsdType: "xsd:int" },
  },
  education: {
    id: { type: "string", isArray: false, fromType: "root", xsdType: "xsd:ID" },
    school: { type: "string", isArray: false, fromType: "education", xsdType: "xsd:string" },
  },
  person: {
    id: { type: "string", isArray: false, fromType: "root", xsdType: "xsd:ID" },
    address: { type: "address", isArray: false, fromType: "address", xsdType: "// local type" },
    education: { type: "education", isArray: true, fromType: "education", xsdType: "// local type" },
    luckyIds: { type: "string", isArray: true, fromType: "person", xsdType: "xsd:ID" },
  },
} as const;

type Meta = typeof meta;

interface Model<X extends keyof Meta> {
  json: any | undefined;
  type: X;
  attr: keyof Meta[X];
}

function getXmlParserTsIdRandomizer() {
  return new XmlParserTsIdRandomizer({
    meta: meta,
    elements: elements,
    newIdGenerator: generateUuid,
    matchers: [],
  });
}

describe("randomize", () => {
  describe("getRandomized", () => {
    describe("skip already attributed ids", () => {
      test("undefined", () => {
        const randomizedIds = getXmlParserTsIdRandomizer()
          .ack({
            json: undefined,
            type: "person",
            attr: "address",
          })
          .randomize()
          .getRandomized();

        expect(randomizedIds).toEqual(new Map());
      });

      test("object", () => {
        const ids = Array.from({ length: 1 }, () => generateUuid());
        const [addressId] = ids;

        const randomizedIds = getXmlParserTsIdRandomizer()
          .ack({
            json: { id: addressId, street: "test", number: 1, country: "Brazil" },
            type: "person",
            attr: "address",
          })
          .randomize()
          .getRandomized();

        expect(randomizedIds).toEqual(new Map());
      });

      test("object with undefined id", () => {
        const randomizedIds = getXmlParserTsIdRandomizer()
          .ack({
            json: { id: undefined, street: "test", number: 1, country: "Brazil" },
            type: "person",
            attr: "address",
          })
          .randomize()
          .getRandomized();
        expect(randomizedIds).toEqual(new Map());
      });

      test("array of objects", () => {
        const ids = Array.from({ length: 1 }, () => generateUuid());
        const [educationId] = ids;

        const randomizedIds = getXmlParserTsIdRandomizer()
          .ack({
            json: [{ id: educationId, school: "MIT" }],
            type: "person",
            attr: "education",
          })
          .randomize()
          .getRandomized();

        expect(randomizedIds).toEqual(new Map());
      });

      test("array of ids", () => {
        const ids = Array.from({ length: 3 }, () => generateUuid());
        const [lucky1, lucky2, lucky3] = ids;

        const randomizedIds = getXmlParserTsIdRandomizer()
          .ack({
            json: [lucky1, lucky2, lucky3],
            type: "person",
            attr: "luckyIds",
          })
          .randomize()
          .getRandomized();

        expect(randomizedIds).toEqual(new Map());
      });

      test("array of undefined", () => {
        const randomizedIds = getXmlParserTsIdRandomizer()
          .ack({
            json: [undefined, undefined],
            type: "person",
            attr: "luckyIds",
          })
          .randomize()
          .getRandomized();

        expect(randomizedIds).toEqual(new Map());
      });

      test("complete example - nested objects", () => {
        const ids = Array.from({ length: 7 }, () => generateUuid());
        const [rootId, personId, addressId, educationId, lucky1, lucky2, lucky3] = ids;

        const randomizedIds = getXmlParserTsIdRandomizer()
          .ack({
            json: {
              id: rootId,
              person: {
                id: personId,
                address: { id: addressId, street: "test", number: 1, country: "Brazil" },
                education: [{ id: educationId, school: "MIT" }],
                luckyIds: [lucky1, lucky2, lucky3],
              },
            },
            type: "root",
            attr: "nested",
          })
          .randomize()
          .getRandomized();

        expect(randomizedIds).toEqual(new Map());
      });

      test("complete example - nested arrays", () => {
        const ids = Array.from({ length: 13 }, () => generateUuid());
        const [
          nestedId,
          firstPersonId,
          firstAddressId,
          firstEducationId,
          firstLucky1,
          firstLucky2,
          secondPersonId,
          secondAddressId,
          secondEducation1Id,
          secondEducation2Id,
          secondLucky1,
          secondLucky2,
          secondLucky3,
        ] = ids;

        const randomizedIds = getXmlParserTsIdRandomizer()
          .ack({
            json: {
              id: nestedId,
              people: [
                {
                  id: firstPersonId,
                  address: { id: firstAddressId, street: "foo", number: 1, country: "Brazil" },
                  education: [{ id: firstEducationId, school: "MIT" }],
                  luckyIds: [firstLucky1, firstLucky2],
                },
                {
                  id: secondPersonId,
                  address: { id: secondAddressId, street: "bar", number: 2, country: "US" },
                  education: [
                    { id: secondEducation1Id, school: "MIT" },
                    { id: secondEducation2Id, school: "Harvard" },
                  ],
                  luckyIds: [secondLucky1, secondLucky2, secondLucky3],
                },
              ],
            },
            type: "root",
            attr: "nested",
          })
          .randomize()
          .getRandomized();

        expect(randomizedIds).toEqual(new Map());
      });

      test("complete example - missing ids", () => {
        const ids = Array.from({ length: 10 }, () => generateUuid());
        const [
          nestedId,
          firstPersonId,
          firstAddressId,
          firstLucky1,
          firstLucky2,
          secondPersonId,
          secondEducation1Id,
          secondEducation2Id,
          secondLucky1,
          secondLucky3,
        ] = ids;

        const randomizedIds = getXmlParserTsIdRandomizer()
          .ack({
            json: {
              id: nestedId,
              people: [
                {
                  id: firstPersonId,
                  address: { id: firstAddressId, street: "foo", number: 1, country: "Brazil" },
                  education: [{ school: "MIT" }],
                  luckyIds: [firstLucky1, firstLucky2],
                },
                {
                  id: secondPersonId,
                  address: { street: "bar", number: 2, country: "US" },
                  education: [
                    { id: secondEducation1Id, school: "MIT" },
                    { id: secondEducation2Id, school: "Harvard" },
                  ],
                  luckyIds: [secondLucky1, undefined, secondLucky3],
                },
              ],
            },
            type: "root",
            attr: "nested",
          })
          .randomize()
          .getRandomized();

        expect(randomizedIds).toEqual(new Map());
      });
    });

    describe("replace already attribute ids", () => {
      test("undefined", () => {
        const randomizedIds = getXmlParserTsIdRandomizer()
          .ack({
            json: undefined,
            type: "person",
            attr: "address",
          })
          .randomize({ skipAlreadyAttributedIds: false })
          .getRandomized();

        expect(randomizedIds).toEqual(new Map());
      });

      test("object", () => {
        const ids = Array.from({ length: 1 }, () => generateUuid());
        const [addressId] = ids;

        const randomizedIds = getXmlParserTsIdRandomizer()
          .ack({
            json: { id: addressId, street: "test", number: 1, country: "Brazil" },
            type: "person",
            attr: "address",
          })
          .randomize({ skipAlreadyAttributedIds: false })
          .getRandomized();

        expect(Array.from(randomizedIds.keys())).toEqual(ids);
        ids.forEach((id) => {
          expect(randomizedIds.get(id)).toMatch(uuidRegExp);
        });
      });

      test("object with undefined id", () => {
        const randomizedIds = getXmlParserTsIdRandomizer()
          .ack({
            json: { id: undefined, street: "test", number: 1, country: "Brazil" },
            type: "person",
            attr: "address",
          })
          .randomize({ skipAlreadyAttributedIds: false })
          .getRandomized();

        expect(randomizedIds).toEqual(new Map());
      });

      test("array of objects", () => {
        const ids = Array.from({ length: 1 }, () => generateUuid());
        const [educationId] = ids;

        const randomizedIds = getXmlParserTsIdRandomizer()
          .ack({
            json: [{ id: educationId, school: "MIT" }],
            type: "person",
            attr: "education",
          })
          .randomize({ skipAlreadyAttributedIds: false })
          .getRandomized();

        expect(Array.from(randomizedIds.keys())).toEqual(ids);
        ids.forEach((id) => {
          expect(randomizedIds.get(id)).toMatch(uuidRegExp);
        });
      });

      test("array of ids", () => {
        const ids = Array.from({ length: 3 }, () => generateUuid());
        const [lucky1, lucky2, lucky3] = ids;

        const randomizedIds = getXmlParserTsIdRandomizer()
          .ack({
            json: [lucky1, lucky2, lucky3],
            type: "person",
            attr: "luckyIds",
          })
          .randomize({ skipAlreadyAttributedIds: false })
          .getRandomized();

        expect(Array.from(randomizedIds.keys())).toEqual(ids);
        ids.forEach((id) => {
          expect(randomizedIds.get(id)).toMatch(uuidRegExp);
        });
      });

      test("array of undefined", () => {
        const randomizedIds = getXmlParserTsIdRandomizer()
          .ack({
            json: [undefined, undefined],
            type: "person",
            attr: "luckyIds",
          })
          .randomize({ skipAlreadyAttributedIds: false })
          .getRandomized();

        expect(randomizedIds).toEqual(new Map());
      });

      test("complete example - nested objects", () => {
        const ids = Array.from({ length: 7 }, () => generateUuid());
        const [rootId, personId, addressId, educationId, lucky1, lucky2, lucky3] = ids;

        const randomizedIds = getXmlParserTsIdRandomizer()
          .ack({
            json: {
              id: rootId,
              person: {
                id: personId,
                address: { id: addressId, street: "test", number: 1, country: "Brazil" },
                education: [{ id: educationId, school: "MIT" }],
                luckyIds: [lucky1, lucky2, lucky3],
              },
            },
            type: "root",
            attr: "nested",
          })
          .randomize({ skipAlreadyAttributedIds: false })
          .getRandomized();

        expect(Array.from(randomizedIds.keys())).toEqual(ids);
        ids.forEach((id) => {
          expect(randomizedIds.get(id)).toMatch(uuidRegExp);
        });
      });

      test("complete example - nested arrays", () => {
        const ids = Array.from({ length: 13 }, () => generateUuid());
        const [
          nestedId,
          firstPersonId,
          firstAddressId,
          firstEducationId,
          firstLucky1,
          firstLucky2,
          secondPersonId,
          secondAddressId,
          secondEducation1Id,
          secondEducation2Id,
          secondLucky1,
          secondLucky2,
          secondLucky3,
        ] = ids;

        const randomizedIds = getXmlParserTsIdRandomizer()
          .ack({
            json: {
              id: nestedId,
              people: [
                {
                  id: firstPersonId,
                  address: { id: firstAddressId, street: "foo", number: 1, country: "Brazil" },
                  education: [{ id: firstEducationId, school: "MIT" }],
                  luckyIds: [firstLucky1, firstLucky2],
                },
                {
                  id: secondPersonId,
                  address: { id: secondAddressId, street: "bar", number: 2, country: "US" },
                  education: [
                    { id: secondEducation1Id, school: "MIT" },
                    { id: secondEducation2Id, school: "Harvard" },
                  ],
                  luckyIds: [secondLucky1, secondLucky2, secondLucky3],
                },
              ],
            },
            type: "root",
            attr: "nested",
          })
          .randomize({ skipAlreadyAttributedIds: false })
          .getRandomized();

        expect(Array.from(randomizedIds.keys())).toEqual(ids);
        ids.forEach((id) => {
          expect(randomizedIds.get(id)).toMatch(uuidRegExp);
        });
      });

      test("complete example - missing ids", () => {
        const ids = Array.from({ length: 10 }, () => generateUuid());
        const [
          nestedId,
          firstPersonId,
          firstAddressId,
          firstLucky1,
          firstLucky2,
          secondPersonId,
          secondEducation1Id,
          secondEducation2Id,
          secondLucky1,
          secondLucky3,
        ] = ids;

        const randomizedIds = getXmlParserTsIdRandomizer()
          .ack({
            json: {
              id: nestedId,
              people: [
                {
                  id: firstPersonId,
                  address: { id: firstAddressId, street: "foo", number: 1, country: "Brazil" },
                  education: [{ school: "MIT" }],
                  luckyIds: [firstLucky1, firstLucky2],
                },
                {
                  id: secondPersonId,
                  address: { street: "bar", number: 2, country: "US" },
                  education: [
                    { id: secondEducation1Id, school: "MIT" },
                    { id: secondEducation2Id, school: "Harvard" },
                  ],
                  luckyIds: [secondLucky1, undefined, secondLucky3],
                },
              ],
            },
            type: "root",
            attr: "nested",
          })
          .randomize({ skipAlreadyAttributedIds: false })
          .getRandomized();

        expect(Array.from(randomizedIds.keys())).toEqual(ids);
        ids.forEach((id) => {
          expect(randomizedIds.get(id)).toMatch(uuidRegExp);
        });
      });
    });
  });

  describe("getAttributed", () => {
    test("undefined", () => {
      const attributedIds = getXmlParserTsIdRandomizer()
        .ack({
          json: undefined,
          type: "person",
          attr: "address",
        })
        .randomize()
        .getAttributed();

      expect(attributedIds).toEqual(new Map());
    });

    test("object", () => {
      const ids = Array.from({ length: 1 }, () => generateUuid());
      const [addressId] = ids;

      const attributedIds = getXmlParserTsIdRandomizer()
        .ack({
          json: { id: addressId, street: "test", number: 1, country: "Brazil" },
          type: "person",
          attr: "address",
        })
        .randomize()
        .getAttributed();

      expect(attributedIds).toEqual(new Map());
    });

    test("object with undefined id", () => {
      const model: Model<"person"> = {
        json: { id: undefined },
        type: "person",
        attr: "address",
      };

      const attributedIds = getXmlParserTsIdRandomizer().ack(model).randomize().getAttributed();

      expect(Array.from(attributedIds.keys())).toEqual(["address.id"]);
      Array.from(attributedIds.values()).forEach((attributedId) => {
        expect(attributedId).toMatch(uuidRegExp);
      });
      expect(model.json.id).toEqual(attributedIds.get("address.id"));
    });

    test("array of objects", () => {
      const ids = Array.from({ length: 1 }, () => generateUuid());
      const [educationId] = ids;

      const attributedIds = getXmlParserTsIdRandomizer()
        .ack({
          json: [{ id: educationId, school: "MIT" }],
          type: "person",
          attr: "education",
        })
        .randomize()
        .getAttributed();

      expect(attributedIds).toEqual(new Map());
    });

    test("array of ids", () => {
      const ids = Array.from({ length: 3 }, () => generateUuid());
      const [lucky1, lucky2, lucky3] = ids;

      const attributedIds = getXmlParserTsIdRandomizer()
        .ack({
          json: [lucky1, lucky2, lucky3],
          type: "person",
          attr: "luckyIds",
        })
        .randomize()
        .getAttributed();

      expect(attributedIds).toEqual(new Map());
    });

    test("array of undefined", () => {
      const model: Model<"person"> = {
        json: [undefined, undefined],
        type: "person",
        attr: "luckyIds",
      };

      const attributedIds = getXmlParserTsIdRandomizer().ack(model).randomize().getAttributed();

      expect(Array.from(attributedIds.keys())).toEqual(["luckyIds.0", "luckyIds.1"]);
      Array.from(attributedIds.values()).forEach((attributedId) => {
        expect(attributedId).toMatch(uuidRegExp);
      });
      expect(model.json[0]).toEqual(attributedIds.get("luckyIds.0"));
      expect(model.json[1]).toEqual(attributedIds.get("luckyIds.1"));
    });

    test("complete example - nested objects", () => {
      const ids = Array.from({ length: 7 }, () => generateUuid());
      const [rootId, personId, addressId, educationId, lucky1, lucky2, lucky3] = ids;

      const attributedIds = getXmlParserTsIdRandomizer()
        .ack({
          json: {
            id: rootId,
            person: {
              id: personId,
              address: { id: addressId, street: "test", number: 1, country: "Brazil" },
              education: [{ id: educationId, school: "MIT" }],
              luckyIds: [lucky1, lucky2, lucky3],
            },
          },
          type: "root",
          attr: "nested",
        })
        .randomize()
        .getAttributed();

      expect(attributedIds).toEqual(new Map());
    });

    test("complete example - nested arrays", () => {
      const ids = Array.from({ length: 13 }, () => generateUuid());
      const [
        nestedId,
        firstPersonId,
        firstAddressId,
        firstEducationId,
        firstLucky1,
        firstLucky2,
        secondPersonId,
        secondAddressId,
        secondEducation1Id,
        secondEducation2Id,
        secondLucky1,
        secondLucky2,
        secondLucky3,
      ] = ids;

      const attributedIds = getXmlParserTsIdRandomizer()
        .ack({
          json: {
            id: nestedId,
            people: [
              {
                id: firstPersonId,
                address: { id: firstAddressId, street: "foo", number: 1, country: "Brazil" },
                education: [{ id: firstEducationId, school: "MIT" }],
                luckyIds: [firstLucky1, firstLucky2],
              },
              {
                id: secondPersonId,
                address: { id: secondAddressId, street: "bar", number: 2, country: "US" },
                education: [
                  { id: secondEducation1Id, school: "MIT" },
                  { id: secondEducation2Id, school: "Harvard" },
                ],
                luckyIds: [secondLucky1, secondLucky2, secondLucky3],
              },
            ],
          },
          type: "root",
          attr: "nested",
        })
        .randomize()
        .getAttributed();

      expect(attributedIds).toEqual(new Map());
    });

    test("complete example - missing ids", () => {
      const ids = Array.from({ length: 10 }, () => generateUuid());
      const [
        nestedId,
        firstPersonId,
        firstAddressId,
        firstLucky1,
        firstLucky2,
        secondPersonId,
        secondEducation1Id,
        secondEducation2Id,
        secondLucky1,
        secondLucky3,
      ] = ids;

      const model: Model<"root"> = {
        json: {
          id: nestedId,
          people: [
            {
              id: firstPersonId,
              address: { id: firstAddressId, street: "foo", number: 1, country: "Brazil" },
              education: [{ school: "MIT" }],
              luckyIds: [firstLucky1, firstLucky2],
            },
            {
              id: secondPersonId,
              address: { street: "bar", number: 2, country: "US" },
              education: [
                { id: secondEducation1Id, school: "MIT" },
                { id: secondEducation2Id, school: "Harvard" },
              ],
              luckyIds: [secondLucky1, undefined, secondLucky3],
            },
          ],
        },
        type: "root",
        attr: "nested",
      };

      const attributedIds = getXmlParserTsIdRandomizer().ack(model).randomize().getAttributed();

      expect(Array.from(attributedIds.keys())).toEqual([
        "nested.people.0.education.0.id",
        "nested.people.1.address.id",
        "nested.people.1.luckyIds.1",
      ]);
      Array.from(attributedIds.values()).forEach((attributedId) => {
        expect(attributedId).toMatch(uuidRegExp);
      });
      expect(model.json.people[0].education[0].id).toEqual(attributedIds.get("nested.people.0.education.0.id"));
      expect(model.json.people[1].address.id).toEqual(attributedIds.get("nested.people.1.address.id"));
      expect(model.json.people[1].luckyIds[1]).toEqual(attributedIds.get("nested.people.1.luckyIds.1"));
    });
  });
});
