import * as fs from "fs";
import * as path from "path";
import { getMarshaller } from "@kie-tools/bpmn-marshaller";

const files = ["../tests-data--manual/other/sample.bpmn"];

describe("idempotency", () => {
  for (const file of files) {
    test(path.basename(file), () => {
      const xml_original = fs.readFileSync(path.join(__dirname, file), "utf-8");

      const { parser, builder } = getMarshaller(xml_original);
      const json = parser.parse();

      const xml_firstPass = builder.build(json);
      const xml_secondPass = builder.build(getMarshaller(xml_firstPass).parser.parse());

      expect(xml_firstPass).toStrictEqual(xml_secondPass);
    });
  }
});
