import * as fs from "fs";
import * as path from "path";
import { getMarshaller } from "@kie-tools/scesim-marshaller";

const files = [
  { path: "../tests-data--manual/simple.scesim", version: "1.8" },
  { path: "../tests-data--manual/TrafficViolationTest.scesim", version: "1.8" },
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
