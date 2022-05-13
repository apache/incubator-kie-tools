const path = require("path");
const basePath = "../../";
module.exports = {
  stylePaths: [
    path.resolve(basePath, "node_modules/patternfly"),
    path.resolve(basePath, "node_modules/@patternfly/patternfly"),
    path.resolve(basePath, "node_modules/@patternfly/react-styles/css"),
    path.resolve(basePath, "node_modules/@patternfly/react-core/dist/styles/base.css"),
    path.resolve(basePath, "node_modules/@patternfly/react-core/dist/esm/@patternfly/patternfly"),
    path.resolve(basePath, "node_modules/@patternfly/react-core/node_modules/@patternfly/react-styles/css"),
    path.resolve(basePath, "node_modules/@patternfly/react-table/node_modules/@patternfly/react-styles/css"),
    path.resolve(
      basePath,
      "node_modules/@patternfly/react-inline-edit-extension/node_modules/@patternfly/react-styles/css"
    ),
  ],
};
