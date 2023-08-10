import { spawn } from "child_process";

const argv = process.argv.slice(2).flatMap((e) => e.split("="));

let storybookArgs: string[] = [];
if (argv.indexOf("--storybookArgs") !== -1) {
  storybookArgs = argv[argv.indexOf("--storybookArgs") + 1].split(" ");
}

if (argv.indexOf("--env") !== -1 && argv[argv.indexOf("--env") + 1] === "live") {
  process.env.STORYBOOK_BASE__live = "true";
}

const storybook = spawn(`storybook`, storybookArgs);

storybook.stdout.setEncoding("utf8");
storybook.stdout.on("data", (data) => {
  console.log(data);
});

storybook.stderr.setEncoding("utf8");
storybook.stderr.on("data", (data: string) => {
  if (data.includes("[webpack.Progress]")) {
    return;
  }
  if (data.includes("[webpack-dev-middleware]")) {
    const cleanLog = data.replace(/(\r\n|\n|\r)/gm, "");
    console.log(cleanLog);
    return;
  }
  console.log(data);
});

storybook.on("close", (code) => {
  console.log(`[STORYBOOK CLOSE]: child process exited with code ${code}`);
});

storybook.on("error", (error) => {
  console.log(`[STORYBOOK ERROR]: ${error}`);
});
