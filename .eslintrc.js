module.exports = {
  root: true,
  parser: "@typescript-eslint/parser",
  plugins: ["@typescript-eslint"],
  extends: [
    "eslint:recommended",
    "plugin:@typescript-eslint/recommended",
    "plugin:react/recommended",
    "plugin:react-hooks/recommended",
  ],
  rules: {
    // envelope-bus
    "prefer-spread": "off",
    "@typescript-eslint/no-unused-vars": "off",
    "@typescript-eslint/no-explicit-any": "off",
    "@typescript-eslint/explicit-module-boundary-types": "off",
    "@typescript-eslint/no-non-null-assertion": "off",
    "@typescript-eslint/ban-types": "off",

    // pmml-editor-marshaller
    "@typescript-eslint/no-inferrable-types": "off",

    // keyboard-shortcuts
    "@typescript-eslint/no-non-null-asserted-optional-chain": "off",

    // guided-tour
    "@typescript-eslint/no-empty-interface": "off",
    "@typescript-eslint/no-empty-function": "off",

    //kie-bc-editors
    "@typescript-eslint/no-this-alias": "off",

    //pmml-editor
    "no-fallthrough": "off",
    "no-case-declarations": "off",
    "@typescript-eslint/ban-ts-comment": "off",


    // REACT

    "react/prop-types": "off",
    "react/display-name": "off",
    "react-hooks/exhaustive-deps": "off",
    "react/jsx-no-target-blank": "off",
  },
};
