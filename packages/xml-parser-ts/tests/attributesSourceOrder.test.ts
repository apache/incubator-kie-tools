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

import * as jsdom from "jsdom";
import {
  domParser,
  getInstanceNs,
  getParser,
  parse,
  registerAttributesSourceOrder,
  scanAttributeNamesInSourceOrder,
} from "@kie-tools/xml-parser-ts";
import * as mySchema10 from "./schemas/my-schema-1.0/ts-gen/meta";
import type { MySchema10__RootElementType } from "./schemas/my-schema-1.0/ts-gen/types";

// `xmlns` declarations deliberately out of alphabetical order and interleaved with regular attributes.
const originalXml = `<?xml version="1.0" encoding="UTF-8" ?>
<rootElement xmlns:zz="urn:zz" foo="1.0" xmlns="https://kie.apache.org/my-schema-1.0" xmlns:mm="urn:mm" bar="2" xmlns:aa="urn:aa">
  <childElement>Example child element content</childElement>
</rootElement>
`;

const expectedRootAttributeKeys = ["@_xmlns:zz", "@_foo", "@_xmlns", "@_xmlns:mm", "@_bar", "@_xmlns:aa"];

function isXmlns(name: string) {
  return name === "xmlns" || name.startsWith("xmlns:");
}

/** Re-adds the attributes of `element` in the order given by `sort`. */
function reorderAttributes(
  element: Element,
  sort: (attrs: { name: string; value: string }[]) => { name: string; value: string }[]
) {
  const attrs = Array.from(element.attributes).map((a) => ({ name: a.name, value: a.value }));
  for (const a of attrs) {
    element.removeAttribute(a.name);
  }
  for (const a of sort(attrs)) {
    element.setAttribute(a.name, a.value);
  }
}

/** What `DOMParser` does on Chrome up to 152 and on WebKit: `xmlns` declarations first (source order), then the rest. */
function likeChrome152(attrs: { name: string; value: string }[]) {
  return [...attrs.filter((a) => isXmlns(a.name)), ...attrs.filter((a) => !isXmlns(a.name))];
}

/** What `DOMParser` does on Chrome 153+: `xmlns` declarations first, sorted alphabetically, then the rest. */
function likeChrome153(attrs: { name: string; value: string }[]) {
  return [
    ...attrs.filter((a) => isXmlns(a.name)).sort((a, b) => a.name.localeCompare(b.name)),
    ...attrs.filter((a) => !isXmlns(a.name)),
  ];
}

/** The `@_`-prefixed keys of a parsed element, in insertion order. Child elements are parsed before attributes. */
function attributeKeys(parsedElement: object) {
  return Object.keys(parsedElement).filter((k) => k.startsWith("@_"));
}

describe("attributes keep their source order regardless of the DOM's attribute order", () => {
  test("scanAttributeNamesInSourceOrder skips prolog, comments, CDATA, PIs and DOCTYPE, and handles quotes", () => {
    const xml = `<?xml version="1.0"?>
<!DOCTYPE root [ <!ENTITY e "<fake xmlns:no='1' xmlns:no2='2'>"> ]>
<!-- <ignored xmlns:c="1" xmlns:d="2"/> -->
<root xmlns:zz="urn:zz" id="1" xmlns='urn:default' title="a > b" xmlns:aa="urn:aa">
  <?pi xmlns:p="1" xmlns:q="2"?>
  <empty xmlns:b="urn:b" xmlns:a="urn:a" />
  <text xmlns:y='urn:y'><![CDATA[ <cdata xmlns:x="1" xmlns:w="2"/> ]]></text>
  <spaced   attr = "v"   other='w'/>
  <plain/>
</root>`;
    expect(scanAttributeNamesInSourceOrder(xml)).toEqual([
      ["xmlns:zz", "id", "xmlns", "title", "xmlns:aa"], // root
      ["xmlns:b", "xmlns:a"], // empty
      ["xmlns:y"], // text
      ["attr", "other"], // spaced
      [], // plain
    ]);
  });

  test("round-trip from XML text", () => {
    const parser = getParser<{ [mySchema10.root.element]: MySchema10__RootElementType }>(mySchema10);
    const { json, instanceNs } = parser.parse({ type: "xml", xml: originalXml });
    expect(attributeKeys(json.rootElement)).toEqual(expectedRootAttributeKeys);
    expect(parser.build({ json, instanceNs })).toStrictEqual(originalXml);
  });

  test.each([
    ["Chrome <= 152 / WebKit", likeChrome152, ["xmlns:zz", "xmlns", "xmlns:mm", "xmlns:aa", "foo", "bar"]],
    ["Chrome 153+", likeChrome153, ["xmlns", "xmlns:aa", "xmlns:mm", "xmlns:zz", "foo", "bar"]],
  ])("round-trip from a Document whose DOM reorders attributes like %s", (_, sort, domOrder) => {
    const parser = getParser<{ [mySchema10.root.element]: MySchema10__RootElementType }>(mySchema10);

    const domdoc = domParser.getDomDocument(originalXml);
    reorderAttributes(domdoc.documentElement, sort);
    expect(Array.from(domdoc.documentElement.attributes).map((a) => a.name)).toEqual(domOrder);

    const instanceNs = getInstanceNs(domdoc);
    expect([...instanceNs.keys()].slice(0, 2)).toEqual(["urn:zz", "zz:"]); // First declaration on the source comes first.

    const { json } = parser.parse({ type: "domdoc", domdoc, instanceNs });
    expect(attributeKeys(json.rootElement)).toEqual(expectedRootAttributeKeys);
    expect(parser.build({ json, instanceNs })).toStrictEqual(originalXml);
  });

  test("applies to nested elements too", () => {
    const xml = `<rootElement xmlns="https://kie.apache.org/my-schema-1.0"><childElement xmlns:q="urn:q" data-b="1" xmlns:p="urn:p" data-a="2">text</childElement></rootElement>`;
    const domdoc = domParser.getDomDocument(xml);
    reorderAttributes(domdoc.documentElement.firstElementChild!, likeChrome153);
    expect(Array.from(domdoc.documentElement.firstElementChild!.attributes).map((a) => a.name)).toEqual([
      "xmlns:p",
      "xmlns:q",
      "data-b",
      "data-a",
    ]);

    const json = parse({
      ...mySchema10,
      node: domdoc,
      nodeMetaType: { rootElement: { type: mySchema10.root.type, isArray: false, xsdType: "", fromType: "" } },
      instanceNs: getInstanceNs(domdoc),
    });
    expect(attributeKeys(json.rootElement.childElement)).toEqual(["@_xmlns:q", "@_data-b", "@_xmlns:p", "@_data-a"]);
  });

  test("without the source order registered, the DOM order leaks into the output (control)", () => {
    const parser = getParser<{ [mySchema10.root.element]: MySchema10__RootElementType }>(mySchema10);

    // A Document not created through `domParser.getDomDocument`, so no source order is known.
    const domdoc = new jsdom.JSDOM(originalXml, { contentType: "application/xml" }).window.document;
    reorderAttributes(domdoc.documentElement, likeChrome153);

    const { json } = parser.parse({ type: "domdoc", domdoc, instanceNs: getInstanceNs(domdoc) });
    expect(attributeKeys(json.rootElement)).toEqual([
      "@_xmlns",
      "@_xmlns:aa",
      "@_xmlns:mm",
      "@_xmlns:zz",
      "@_foo",
      "@_bar",
    ]);

    // ...and registering the source order afterwards fixes it.
    registerAttributesSourceOrder(domdoc, originalXml);
    const fixed = parser.parse({ type: "domdoc", domdoc, instanceNs: getInstanceNs(domdoc) });
    expect(attributeKeys(fixed.json.rootElement)).toEqual(expectedRootAttributeKeys);
    expect(parser.build({ json: fixed.json, instanceNs: fixed.instanceNs })).toStrictEqual(originalXml);
  });

  test("falls back to the DOM order when the text can't be aligned with the Document", () => {
    const domdoc = new jsdom.JSDOM(originalXml, { contentType: "application/xml" }).window.document;
    reorderAttributes(domdoc.documentElement, likeChrome153);

    // Different number of elements: nothing is registered.
    registerAttributesSourceOrder(domdoc, `<other xmlns:zz="urn:zz" xmlns:aa="urn:aa"><a/><b/><c/></other>`);
    expect([...getInstanceNs(domdoc).keys()].slice(0, 2)).toEqual(["https://kie.apache.org/my-schema-1.0", ""]);

    // Same number of elements, but attribute names that don't exist on the DOM: the DOM order is kept for that element.
    registerAttributesSourceOrder(domdoc, `<other a="1" b="2" c="3" d="4" e="5" f="6"><childElement/></other>`);
    expect([...getInstanceNs(domdoc).keys()].slice(0, 2)).toEqual(["https://kie.apache.org/my-schema-1.0", ""]);
  });
});
