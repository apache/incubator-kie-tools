import * as fs from "fs";
import * as path from "path";
import * as os from "os";
import * as child_process from "child_process";
import { getMarshaller } from "@kie-tools/scesim-marshaller";

const files = ["../tests-data--manual/TrafficViolationTest.scesim"];

const tmpDir = path.join(os.tmpdir(), "scesim-marshaller-type-safety-tests");

describe("type safety", () => {
  beforeAll(() => {
    if (fs.existsSync(tmpDir)) {
      fs.rmSync(tmpDir, { recursive: true });
    }
    fs.mkdirSync(tmpDir, { recursive: true });
    console.log(`[scesim-marshaller] Type safety tests running on '${tmpDir}'.`);
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
import { SceSim__ScenarioSimulationModelType } from "${thisPath}/../dist/schemas/scesim-1_${minorVersion}/ts-gen/types";

const scesim: SceSim__ScenarioSimulationModelType = ${JSON.stringify(json.ScenarioSimulationModel, undefined, 2)};`;

      const tmpFilePath = path.join(tmpDir, `${path.basename(file)}.ts`);
      fs.writeFileSync(tmpFilePath, tmpFile);

      const tsc = child_process.spawnSync("tsc", ["--noEmit", "--strict", tmpFilePath], {
        stdio: "pipe",
        shell: "true",
      });
      const tscOutput = tsc.output
        .map((line) => line?.toString())
        .join("\n")
        .trim();

      expect(tscOutput).toStrictEqual("");
    });
  }
});
