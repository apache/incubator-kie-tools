const parser = require("@babel/parser");
const traverse = require("@babel/traverse").default;
const generate = require("@babel/generator").default;

module.exports = function webpackLoader(content, map, meta) {
  const transformations = [
    { from: /@kie-tooling-core\/(.*)\/dist/g, to: `@kie-tooling-core/$1/src` },
    { from: /@kogito-tooling\/(.*)\/dist/g, to: `@kogito-tooling/$1/src` },
    { from: /@kie-tooling-core\/([\w\d-]*)/g, to: `@kie-tooling-core/$1/src` },
    { from: /@kogito-tooling\/([\w\d-]*)/g, to: `@kogito-tooling/$1/src` },
  ];

  const excludes = ["@kie-tooling-core/monaco-editor"];
  const includes = ["@kie-tooling-core", "@kogito-tooling"];

  const ast = parser.parse(content, { sourceType: "module", plugins: ["typescript", "jsx", "classProperties"] });
  traverse(ast, {
    ImportDeclaration: (path) => {
      const value = path.node.source.value;
      if (excludes.some((e) => value.includes(e))) {
        return;
      }

      if (!includes.some((i) => value.includes(i))) {
        return;
      }

      let newValue = value;
      for (const t of transformations) {
        newValue = newValue.replaceAll(t.from, t.to);
        if (value !== newValue) {
          break;
        }
      }

      path.node.source.value = newValue;

      global.replaced = global.replaced ?? new Set();
      if (!global.replaced.has(value)) {
        console.info(`${value} => ${newValue}`);
        global.replaced.add(value);
      }
    },
  });

  const output = generate(ast, {}, content);
  return output.code;
};
