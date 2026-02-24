/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, foo 2.0 (the
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

import { getParser } from "@kie-tools/xml-parser-ts";
import * as mySchema10 from "./schemas/my-schema-1.0/ts-gen/meta";
import type { MySchema10__RootElementType } from "./schemas/my-schema-1.0/ts-gen/types";

describe("my shema 1.0", () => {
  test("basic usage", () => {
    const myParser = getParser<{ [mySchema10.root.element]: MySchema10__RootElementType }>(mySchema10);

    const originalXml = `<?xml version="1.0" encoding="UTF-8" ?>
<rootElement xmlns="https://kie.apache.org/my-schema-1.0" foo="1.0">
  <childElement>Example child element content</childElement>
</rootElement>
`;

    const { json, instanceNs } = myParser.parse({ type: "xml", xml: originalXml });
    expect(json).toEqual({
      rootElement: {
        "@_foo": "1.0",
        "@_xmlns": "https://kie.apache.org/my-schema-1.0",
        childElement: {
          __$$text: "Example child element content",
        },
      },
    });

    const xml = myParser.build({ json, instanceNs });
    expect(xml).toStrictEqual(originalXml);
  });

  test("ns instead of instanceNs", () => {
    const myParser = getParser<{ [mySchema10.root.element]: MySchema10__RootElementType }>(mySchema10);

    const originalXml = `<?xml version="1.0" encoding="UTF-8" ?>
<myschema:rootElement xmlns:myschema="https://kie.apache.org/my-schema-1.0" foo="1.0">
  <myschema:childElement>Example child element content</myschema:childElement>
</myschema:rootElement>
`;

    const { json } = myParser.parse({ type: "xml", xml: originalXml });
    expect(json).toEqual({
      rootElement: {
        "@_foo": "1.0",
        "@_xmlns:myschema": "https://kie.apache.org/my-schema-1.0",
        childElement: {
          __$$text: "Example child element content",
        },
      },
    });

    const xml = myParser.build({ json, instanceNs: mySchema10.ns });
    expect(xml).toStrictEqual(`<?xml version="1.0" encoding="UTF-8" ?>
<rootElement xmlns:myschema="https://kie.apache.org/my-schema-1.0" foo="1.0" xmlns="https://kie.apache.org/my-schema-1.0">
  <childElement>Example child element content</childElement>
</rootElement>
`);
  });

  test("modifying", () => {
    const myParser = getParser<{ [mySchema10.root.element]: MySchema10__RootElementType }>(mySchema10);

    const originalXml = `<?xml version="1.0" encoding="UTF-8" ?>
<rootElement xmlns="https://kie.apache.org/my-schema-1.0" foo="1.0">
  <childElement>Example child element content</childElement>
</rootElement>
`;

    const { json, instanceNs } = myParser.parse({ type: "xml", xml: originalXml });
    expect(json).toEqual({
      rootElement: {
        "@_foo": "1.0",
        "@_xmlns": "https://kie.apache.org/my-schema-1.0",
        childElement: {
          __$$text: "Example child element content",
        },
      },
    });

    json.rootElement["@_foo"] = "1.1";

    const xml = myParser.build({ json, instanceNs });
    expect(xml).toStrictEqual(`<?xml version="1.0" encoding="UTF-8" ?>
<rootElement xmlns="https://kie.apache.org/my-schema-1.0" foo="1.1">
  <childElement>Example child element content</childElement>
</rootElement>
`);
  });
});
