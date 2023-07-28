const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const { merge } = require("webpack-merge");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const path = require("path");

module.exports = (env, argv) => [
  merge(common(env, argv), {
    entry: {
      index: "./src/index.ts",
    },
    plugins: [
      new MiniCssExtractPlugin({
        filename: "style.css",
      }),
    ],
    module: {
      rules: [
        {
          test: /\.(woff|woff2|eot|ttf|otf)$/i,
          use: require.resolve("url-loader"),
        },
        {
          test: /\.(css|sass|scss)$/,
          include: [path.resolve("./src")],
          use: [MiniCssExtractPlugin.loader, require.resolve("css-loader"), require.resolve("sass-loader")],
        },
        {
          test: /\.(css|sass|scss)$/,
          exclude: [path.resolve("./src")],
          use: [require.resolve("css-loader"), require.resolve("sass-loader")],
        },
      ],
    },
    output: {
      path: path.resolve(__dirname, "dist"),
      libraryTarget: "umd",
      library: "editor",
      filename: "[name].js",
    },
  }),
];
