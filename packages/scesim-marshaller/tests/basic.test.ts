import * as fs from "fs";
import * as path from "path";
import { getMarshaller } from "@kie-tools/scesim-marshaller";

describe("basic", () => {
  test("simple.scesim", () => {
    const xml = fs.readFileSync(path.join(__dirname, "../tests-data--manual/simple.scesim"), "utf-8");

    const { parser, instanceNs, version } = getMarshaller(xml);
    expect(version).toStrictEqual("1.8");

    const { json } = parser.parse({ xml, instanceNs });
    const xml_firstPass = parser.build({ json, instanceNs });
    const xml_secondPass = parser.build({ json: parser.parse({ xml: xml_firstPass, instanceNs }).json, instanceNs });

    expect(xml_firstPass).toStrictEqual(xml_secondPass);
    console.log(xml.length);
    console.log(xml_secondPass);
  });
});
