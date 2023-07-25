const MiniCssExtractPlugin = require("mini-css-extract-plugin");
// const common = require('@kie-tools-core/webpack-base');
// const merge = require('webpack-merge');

module.exports = {
  entry: ["./src/api/index.ts", "./src/channel/index.ts", "./src/embedded/index.ts", "./src/envelope/index.ts"],
  plugins: [
    new MiniCssExtractPlugin({
      filename: "style.css",
    }),
  ],
  module: {
    rules: [
      {
        test: /\.scss$/,
        use: [MiniCssExtractPlugin.loader, "css-loader", "sass-loader"],
      },
      {
        test: /\.(tsx|ts)?$/,
        use: "ts-loader",
        exclude: /node_modules/,
      },
    ],
  },
  resolve: {
    extensions: [".ts", "tsx", ".js", "scss"],
  },
};
// export default async (env: any, argv: any) => {
//   return [
//     merge(common(env), {
//       entry: [
//         './src/api/index.ts', './src/channel/index.ts', './src/embedded/index.ts', './src/envelope/index.ts'
//       ],
//       // output: {
//       //   path: path.resolve( 'dist'),
//       //   filename: 'index.js',
//       //   libraryTarget: 'umd'
//       // },
//       plugins: [
//         new MiniCssExtractPlugin({
//           filename: 'style.css'
//         })
//       ],
//       module: {
//         rules: [
//           {
//             test: /\.scss$/,
//             use: [
//               MiniCssExtractPlugin.loader,
//               'css-loader',
//               'sass-loader'
//             ]
//           },
//           {
//             test: /\.(tsx|ts)?$/,
//             use: 'ts-loader',
//             exclude: /node_modules/
//           }
//
//         ]
//       },
//       resolve: {
//         extensions: ['.ts', 'tsx', '.js', 'scss']
//       }
//     }),
//   ];
// };
