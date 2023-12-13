const path = require("path");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const TsconfigPathsPlugin = require("tsconfig-paths-webpack-plugin");
const webpack = require("webpack");
const BG_IMAGES_DIRNAME = "bgimages";
const CopyPlugin = require("copy-webpack-plugin");
const FileManagerPlugin = require("filemanager-webpack-plugin");
const MonacoWebpackPlugin = require("monaco-editor-webpack-plugin");
const NodePolyfillPlugin = require("node-polyfill-webpack-plugin");
const swEditor = require("@kie-tools/serverless-workflow-diagram-editor-assets");
const { env } = require("./env");
const buildEnv = env;

module.exports = {
  mode: "development",
  devtool: "source-map",
  output: {
    publicPath: "/",
  },
  entry: {
    standalone: path.resolve(__dirname, "src", "standalone", "standalone.ts"),
    envelope: path.resolve(__dirname, "src", "standalone", "EnvelopeApp.ts"),
    "resources/form-displayer": "./src/resources/form-displayer.ts",
    "resources/serverless-workflow-text-editor-envelope": "./src/resources/ServerlessWorkflowTextEditorEnvelopeApp.ts",
    "resources/serverless-workflow-combined-editor-envelope":
      "./src/resources/ServerlessWorkflowCombinedEditorEnvelopeApp.ts",
    "resources/serverless-workflow-diagram-editor-envelope":
      "./src/resources/ServerlessWorkflowDiagramEditorEnvelopeApp.ts",
  },
  devServer: {
    static: {
      directory: "./dist",
    },
    port: buildEnv.runtimeToolsDevUiWebapp.dev.port,
    compress: true,
    historyApiFallback: true,
    hot: true,
    open: true,
    client: {
      overlay: false,
      progress: true,
    },
  },
  plugins: [
    new MonacoWebpackPlugin({
      languages: ["typescript", "json"],
      customLanguages: [
        {
          label: "yaml",
          entry: ["monaco-yaml", "vs/basic-languages/yaml/yaml.contribution"],
          worker: {
            id: "monaco-yaml/yamlWorker",
            entry: "../../monaco-yaml/yaml.worker.js",
          },
        },
      ],
      globalAPI: true,
    }),
    new webpack.EnvironmentPlugin({
      KOGITO_APP_VERSION: "DEV",
      KOGITO_APP_NAME: "Runtime tools dev-ui",
    }),
    new CopyPlugin({
      patterns: [
        { from: "./resources", to: "./resources" },
        { from: "./src/static", to: "./static" },
        { from: "./src/components/styles.css", to: "./components/styles.css" },
        { from: "../runtime-tools-monitoring-webapp/dist/", to: "./monitoring-webapp" },
        // {
        //   from: '../custom-dashboard-view/dist/',
        //   to: './custom-dashboard-view'
        // },
        {
          from: swEditor.swEditorPath(),
          to: "./diagram",
          globOptions: { ignore: ["**/WEB-INF/**/*"] },
        },
      ],
    }),
    new FileManagerPlugin({
      events: {
        onEnd: {
          mkdir: ["./dist/resources/webapp/"],
          copy: [
            { source: "./dist/*.js", destination: "./dist/resources/webapp/" },
            { source: "./dist/*.map", destination: "./dist/resources/webapp/" },
            { source: "./dist/fonts", destination: "./dist/resources/webapp/" },
            {
              source: "./dist/monitoring-webapp",
              destination: "./dist/resources/webapp/monitoring-webapp",
            },
            // {
            //   source: './dist/custom-dashboard-view',
            //   destination: './dist/resources/webapp/custom-dashboard-view'
            // }
          ],
        },
      },
    }),
    new NodePolyfillPlugin(),
    new HtmlWebpackPlugin({
      template: path.resolve(__dirname, "resources", "index.html"),
      favicon: "src/favicon.ico",
      chunks: ["app"],
    }),
  ],
  module: {
    rules: [
      {
        test: /\.(tsx|ts)?$/,
        include: [path.resolve("./src")],
        exclude: path.resolve(__dirname, "node_modules"),
        use: [
          {
            loader: "ts-loader",
            options: {
              configFile: path.resolve("./tsconfig.json"),
              allowTsInNodeModules: true,
            },
          },
        ],
      },
      {
        test: /\.(svg|ttf|eot|woff|woff2)$/,
        use: {
          loader: "file-loader",
          options: {
            // Limit at 50k. larger files emited into separate files
            limit: 5000,
            outputPath: "fonts",
            name: "[path][name].[ext]",
          },
        },
      },
      {
        test: /\.svg$/,
        include: (input) => input.indexOf("background-filter.svg") > 1,
        use: [
          {
            loader: "url-loader",
            options: {
              limit: 5000,
              outputPath: "svgs",
              name: "[name].[ext]",
            },
          },
        ],
      },
      {
        test: /\.svg$/,
        include: (input) => input.indexOf(BG_IMAGES_DIRNAME) > -1,
        use: {
          loader: "svg-url-loader",
          options: {},
        },
      },
      {
        test: /\.(jpg|jpeg|png|gif)$/i,
        use: [
          {
            loader: "url-loader",
            options: {
              limit: 5000,
              outputPath: "images",
              name: "[name].[ext]",
            },
          },
        ],
      },
      {
        test: /\.m?js/,
        resolve: {
          fullySpecified: false,
        },
      },
      {
        test: /\.(css|sass|scss)$/,
        use: [require.resolve("style-loader"), require.resolve("css-loader"), require.resolve("sass-loader")],
      },
      {
        test: /\.css$/,
        include: [path.resolve("../../node_modules/monaco-editor")],
        use: [require.resolve("style-loader"), require.resolve("css-loader")],
      },
    ],
  },
  output: {
    path: path.resolve(__dirname, "dist"),
  },
  resolve: {
    fallback: {
      https: require.resolve("https-browserify"),
      path: require.resolve("path-browserify"),
      http: require.resolve("stream-http"),
      os: require.resolve("os-browserify/browser"),
      fs: false,
      child_process: false,
      net: false,
      buffer: require.resolve("buffer/"),
    },
    plugins: [
      new TsconfigPathsPlugin({
        configFile: path.resolve(__dirname, "./tsconfig.json"),
      }),
    ],
    cacheWithContext: false,
    extensions: [".tsx", ".ts", ".js", ".jsx"],
    modules: [path.resolve("src"), "node_modules"],
  },
};
