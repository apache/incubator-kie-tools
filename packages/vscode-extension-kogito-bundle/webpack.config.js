const { merge } = require("webpack-merge");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");

module.exports = async (env) => [
  merge(common(env), {
    output: {
      library: "KogitoBundle",
      libraryTarget: "umd",
      umdNamedDefine: true,
    },
    externals: {
      vscode: "commonjs vscode",
    },
    target: "node",
    entry: {
      "extension/extension": "./src/extension/extension.ts",
    },
    plugins: [],
  }),
];
