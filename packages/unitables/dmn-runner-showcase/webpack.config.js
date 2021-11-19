const CopyPlugin = require("copy-webpack-plugin");
const path = require("path");
const { patternflyRules } = require("@kogito-tooling/patternfly-base/patternflyWebpackOptions");

module.exports = () => {
  return {
    mode: "development",
    entry: {
      index: "./src/index.tsx",
    },
    output: {
      path: path.resolve("./dist"),
      filename: "[name].js",
      chunkFilename: "[name].bundle.js",
    },
    plugins: [
      new CopyPlugin({
        patterns: [{ from: "./public", to: "./" }],
      }),
    ],
    module: {
      rules: [
        {
          test: /\.js$/,
          enforce: "pre",
          use: ["source-map-loader"],
        },
        {
          test: /\.tsx?$/,
          use: [
            {
              loader: "ts-loader",
              options: {
                configFile: path.resolve("./tsconfig.json"),
                compilerOptions: {
                  declaration: true,
                  outDir: "dist",
                  sourceMap: true,
                },
              },
            },
          ],
        },
        ...patternflyRules,
      ],
    },
    devtool: "inline-source-map",
    devServer: {
      historyApiFallback: false,
      disableHostCheck: true,
      watchContentBase: true,
      contentBase: [path.join(__dirname, "./dist"), path.join(__dirname, "./public")],
      compress: true,
      port: 4444,
    },
    resolve: {
      fallback: { path: require.resolve("path-browserify") }, // Required for `minimatch`, as Webpack 5 doesn't add polyfills automatically anymore.
      extensions: [".tsx", ".ts", ".js", ".jsx"],
      modules: [
        path.resolve("../../../node_modules"),
        path.resolve("../../node_modules"),
        path.resolve("./node_modules"),
        path.resolve("./src"),
      ],
    },
  };
};
