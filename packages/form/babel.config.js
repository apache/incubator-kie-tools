module.exports = {
  presets: [
    [
      "@babel/env",
      {
        modules: "commonjs",
        targets: {
          node: "current",
        },
      },
    ],
    "@babel/react",
  ],
};
