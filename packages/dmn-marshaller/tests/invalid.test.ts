import * as fs from "fs";
import * as path from "path";
import { getMarshaller } from "../dist";

describe("invalid", () => {
  test("basic", () => {
    const xml = fs.readFileSync(path.join(__dirname, "../tests-data--manual/other/invalid.dmn"), "utf-8");
    try {
      getMarshaller(xml);
      fail(`An exception should've been thrown for invalid.dmn`);
    } catch (e) {
      // empty on purpose. Nothing to assert.
    }
  });
});
