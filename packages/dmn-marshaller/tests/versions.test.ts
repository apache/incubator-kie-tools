import * as fs from "fs";
import * as path from "path";
import { getMarshaller } from "@kie-tools/dmn-marshaller";

const files = [
  {
    path: "../tests-data--manual/other/attachment.dmn",
    version: "1.2",
  },
  {
    path: "../tests-data--manual/other/empty13.dmn",
    version: "1.3",
  },
  {
    path: "../tests-data--manual/other/sample12.dmn",
    version: "1.2",
  },
  {
    path: "../tests-data--manual/other/weird.dmn",
    version: "1.2",
  },
  {
    path: "../tests-data--manual/dmn-1_4--examples/Chapter 11 Example 1 Originations/Chapter 11 Example.dmn",
    version: "1.4",
  },
];

describe("versions", () => {
  for (const file of files) {
    test(path.basename(file.path), () => {
      const xml = fs.readFileSync(path.join(__dirname, file.path), "utf-8");
      const { version } = getMarshaller(xml);
      expect(version).toStrictEqual(file.version);
    });
  }
});
