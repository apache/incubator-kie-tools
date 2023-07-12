import * as fs from "fs";
import * as path from "path";
import * as os from "os";
import * as child_process from "child_process";
import { getMarshaller } from "@kie-tools/dmn-marshaller";

const files = [
  "../tests-data--manual/other/attachment.dmn",
  "../tests-data--manual/other/empty13.dmn",
  "../tests-data--manual/other/list.dmn",
  // FIXME: Tiago --> Failing due to empty boxed expression. According to the DMN spec, it's not valid.
  // "../tests-data--manual/other/list2.dmn",
  "../tests-data--manual/other/external.dmn",
  "../tests-data--manual/other/sample12.dmn",
  "../tests-data--manual/other/weird.dmn",
  // FIXME: Tiago --> This is failing due to vendor-specific properties. If we remove them manually, everything works well. How to do it?
  // "../tests-data--manual/dmn-1_4--examples/Chapter 11 Example 1 Originations/Chapter 11 Example.dmn",
];

const tmpDir = path.join(os.tmpdir(), "dmn-marshaller-type-safety-tests");

describe("type safety", () => {
  beforeAll(() => {
    if (fs.existsSync(tmpDir)) {
      fs.rmSync(tmpDir, { recursive: true });
    }
    fs.mkdirSync(tmpDir, { recursive: true });
    console.log(`[dmn-marshaller] Type safety tests running on '${tmpDir}'.`);
  });

  afterAll(() => {
    // fs.rmdirSync(tmpDir, { recursive: true });
  });

  for (const file of files) {
    test(path.basename(file), () => {
      const xml = fs.readFileSync(path.join(__dirname, file), "utf-8");

      const { parser, version } = getMarshaller(xml);
      const json = parser.parse();

      const thisPath = path.resolve(__dirname);

      const minorVersion = version.split(".")[1];
      const tmpFile = `
import { DMN1${minorVersion}__tDefinitions } from "${thisPath}/../dist/schemas/dmn-1_${minorVersion}/ts-gen/types";
import "${thisPath}/../dist/kie-extensions";

const dmn: DMN1${minorVersion}__tDefinitions = ${JSON.stringify(json.definitions, undefined, 2)};`;

      const tmpFilePath = path.join(tmpDir, `${path.basename(file)}.ts`);
      fs.writeFileSync(tmpFilePath, tmpFile);

      const tsc = child_process.spawnSync("tsc", ["--noEmit", "--strict", tmpFilePath], { stdio: "pipe" });
      const tscOutput = tsc.output
        .map((line) => line?.toString())
        .join("\n")
        .trim();

      expect(tscOutput).toStrictEqual("");
    });
  }
});
