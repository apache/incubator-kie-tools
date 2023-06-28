import * as fs from "fs";
import * as path from "path";
import { getMarshaller } from "../dist";

describe("invalid", () => {
  test("invalid content", () => {
    try {
      getMarshaller(`invalid content`);

      fail(`An exception should've been thrown.`);
    } catch (e) {
      console.error(e);
    }
  });

  test("invalid closing tag", () => {
    try {
      getMarshaller(`
<dmn:definitions
    xmlns="https://kiegroup.org/dmn/_5BF56984-FDC7-441B-8307-FF06B0E5B17F"
    xmlns:dmn="https://www.omg.org/spec/DMN/20191111/MODEL/"
><invalid`);

      fail(`An exception should've been thrown.`);
    } catch (e) {
      console.error(e);
    }
  });

  // FIXME: How to make sure that the parsed XML is actually a DMN, and not some random XML?
  test("non-dmn, valid xml", () => {
    try {
      const { parser } = getMarshaller(`
<dmn:definitions
    xmlns="https://kiegroup.org/dmn/_5BF56984-FDC7-441B-8307-FF06B0E5B17F"
    xmlns:dmn="https://www.omg.org/spec/DMN/20191111/MODEL/">
    invalid
</dmn:definitions>`);

      const json = parser.parse();

      fail(`An exception should've been thrown. Parsed content is ${JSON.stringify(json, undefined, 2)}`);
    } catch (e) {
      console.error(e);
    }
  });
});
