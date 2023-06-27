import * as fs from "fs";
import * as path from "path";
import { getMarshaller } from "@kie-tools/dmn-marshaller";

const files = [
  { path: "../tests-data--manual/attachment.dmn", version: "1.2" },
  { path: "../tests-data--manual/empty13.dmn", version: "1.3" },
  { path: "../tests-data--manual/sample12.dmn", version: "1.2" },
  { path: "../tests-data--manual/weird.dmn", version: "1.2" },
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
