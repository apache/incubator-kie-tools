import * as fs from "fs";
import * as path from "path";
import { getMarshaller } from "@kie-tools/dmn-marshaller";
import { DMN14__tKnowledgeSource } from "../dist/schemas/dmn-1_4/ts-gen/types";

describe("kie-extensions", () => {
  test("kie:attachment", () => {
    const xml = fs.readFileSync(path.join(__dirname, "../tests-data--manual/other/attachment.dmn"), "utf-8");
    const { parser } = getMarshaller(xml);
    const json = parser.parse();

    const attachments = (json.definitions.drgElement ?? [])
      .filter((drgElement) => drgElement["__$$element"] === "knowledgeSource" ?? [])
      .flatMap((knowledgeSource: DMN14__tKnowledgeSource) => knowledgeSource.extensionElements?.["kie:attachment"]);

    expect(attachments.length).toStrictEqual(1);
  });

  test("kie:ComponentWidthsExtension", () => {
    const xml = fs.readFileSync(path.join(__dirname, "../tests-data--manual/other/sample12.dmn"), "utf-8");
    const { parser } = getMarshaller(xml);
    const json = parser.parse();

    const componentWidthsExtension = (json.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"] ?? []).flatMap(
      (d) => d["di:extension"]?.["kie:ComponentsWidthsExtension"] ?? []
    );

    expect(componentWidthsExtension.length).toStrictEqual(1);
    expect(componentWidthsExtension.flatMap((s) => s["kie:ComponentWidths"] ?? []).length).toStrictEqual(24);
  });
});
