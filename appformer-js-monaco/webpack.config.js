const CopyPlugin = require("copy-webpack-plugin");

module.exports = {
    mode: 'production',
    entry: {
        index: "./src/index.ts"
    },
    plugins: [
        new CopyPlugin([
            { from: "node_modules/@kiegroup/monaco-editor/dist/standalone/monaco.min.js", to: "./monaco-editor" }
        ])
    ]
}