/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { Annotation, Header, PMML, PMML2XML, Timestamp, XML2PMML } from "@kogito-tooling/pmml-editor-marshaller";

describe("Header tests", () => {
  test("Attributes", () => {
    const pmml: PMML = XML2PMML(`
      <PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
        <Header copyright="copyright" description="description" modelVersion="modelVersion"/>
      </PMML>
    `);

    expect(pmml).not.toBeNull();

    const header: Header = pmml.Header;
    expect(header).not.toBeNull();
    expect(header.Application).toBeUndefined();
    expect(header.Annotation).toBeUndefined();
    expect(header.Timestamp).toBeUndefined();

    expect(header.copyright).toBe("copyright");
    expect(header.description).toBe("description");
    expect(header.modelVersion).toBe("modelVersion");

    pmml.Header.copyright = "copyright-changed";
    pmml.Header.description = "description-changed";
    pmml.Header.modelVersion = "modelVersion-changed";

    const xml: string = PMML2XML(pmml);

    expect(xml).toContain(
      `<Header copyright="copyright-changed" description="description-changed" modelVersion="modelVersion-changed"/>`
    );
  });

  test("Application", () => {
    const pmml: PMML = XML2PMML(`
      <PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
        <Header>
          <Application name="name" version="version"/>
        </Header>
      </PMML>
    `);

    expect(pmml).not.toBeNull();

    const header: Header = pmml.Header;
    expect(header).not.toBeNull();
    expect(header.Application).not.toBeUndefined();
    expect(header.Annotation).toBeUndefined();
    expect(header.Timestamp).toBeUndefined();

    expect(header.Application?.name).toBe("name");
    expect(header.Application?.version).toBe("version");

    if (header.Application) {
      header.Application.name = "name-changed";
      header.Application.version = "version-changed";
    }

    const xml: string = PMML2XML(pmml);

    expect(xml).toContain(`<Application name="name-changed" version="version-changed"/>`);
  });

  test("Annotation", () => {
    const pmml: PMML = XML2PMML(`
      <PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
        <Header>
          <Annotation>annotation1</Annotation>
        </Header>
      </PMML>
    `);

    expect(pmml).not.toBeNull();

    const header: Header = pmml.Header;
    expect(header).not.toBeNull();
    expect(header.Application).toBeUndefined();
    expect(header.Annotation).not.toBeUndefined();
    expect(header.Timestamp).toBeUndefined();

    const annotations: Annotation[] = header.Annotation as Annotation[];

    expect(annotations.length).toBe(1);
    expect(annotations[0]).not.toBeUndefined();
    expect((annotations[0] as any)["text"]).toBe("annotation1");

    const annotation: Annotation[] = [new Annotation({})];
    (annotation[0] as any)["type"] = "text";
    (annotation[0] as any)["text"] = "annotation2";

    annotations.push(annotation[0]);
    expect(annotations.length).toBe(2);
    expect(annotations[1]).not.toBeUndefined();

    const xml: string = PMML2XML(pmml);

    expect(xml).toContain(`<Annotation>annotation1</Annotation>`);
    expect(xml).toContain(`<Annotation>annotation2</Annotation>`);
  });

  test("Timestamp", () => {
    const pmml: PMML = XML2PMML(`
      <PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
        <Header>
          <Timestamp>timestamp</Timestamp>
        </Header>
      </PMML>
    `);

    expect(pmml).not.toBeNull();

    const header: Header = pmml.Header;
    expect(header).not.toBeNull();
    expect(header.Application).toBeUndefined();
    expect(header.Annotation).toBeUndefined();
    expect(header.Timestamp).not.toBeUndefined();

    const timestamp: Timestamp[] = [header.Timestamp as Timestamp];
    expect((timestamp[0] as any)["text"]).toBe("timestamp");

    (timestamp[0] as any)["text"] = "timestamp-changed";
    const xml: string = PMML2XML(pmml);
    expect(xml).toContain(`<Timestamp>timestamp-changed</Timestamp>`);
  });
});
