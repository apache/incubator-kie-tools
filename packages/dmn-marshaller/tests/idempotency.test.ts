import * as fs from "fs";
import * as path from "path";
import { getMarshaller } from "@kie-tools/dmn-marshaller";

const files = [
  "../tests-data--manual/other/attachment.dmn",
  "../tests-data--manual/other/empty13.dmn",
  "../tests-data--manual/other/sample12.dmn",
  "../tests-data--manual/other/list.dmn",
  "../tests-data--manual/other/list2.dmn",
  "../tests-data--manual/other/external.dmn",
  "../tests-data--manual/other/weird.dmn",
  "../tests-data--manual/dmn-1_4--examples/Chapter 11 Example 1 Originations/Chapter 11 Example.dmn",
];

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
