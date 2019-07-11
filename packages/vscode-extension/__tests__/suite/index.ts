import * as path from "path";
import * as Mocha from "mocha";
import * as glob from "glob";

export function run(testsRoot: string, callback: (error: any, failures?: number) => void): void {
  const mocha = new Mocha({
    ui: "tdd",
    useColors: true,
    timeout: 10000
  });

  glob("**/**.test.js", { cwd: testsRoot }, (err, files) => {
    if (err) {
      return callback(err);
    }

    files.forEach(f => mocha.addFile(path.resolve(testsRoot, f)));

    try {
      mocha.run(failures => callback(null, failures));
    } catch (err) {
      callback(err);
    }
  });
}
