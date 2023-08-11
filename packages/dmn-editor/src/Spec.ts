export const SPEC = {
  namedElement: {
    isValidName: (name: string) => {
      return true; // FIXME: Tiago --> Implement
    },
  },
  expressionLanguage: { default: `https://www.omg.org/spec/DMN/20211108/FEEL/` }, // FIXME: Tiago --> This is not quite right, as DMN now has multiple versions of FEEL
  typeLanguage: { default: `https://www.omg.org/spec/DMN/20211108/FEEL/` }, // FIXME: Tiago --> This is not quite right, as DMN now has multiple versions of FEEL
  BOXED: {
    DECISION_TABLE: {
      PreferredOrientation: { default: "Rule-as-Row" },
      HitPolicy: { default: "UNIQUE" },
    },
    FUNCTION: {
      kind: { default: "FEEL" },
      JAVA: {
        classFieldName: "class",
        methodSignatureFieldName: "method signature",
      },
      PMML: {
        documentFieldName: "document",
        modelFieldName: "model",
      },
    },
  },
  ITEM_DEFINITIONS: {
    isCollection: { default: "false" },
  },
  ANNOTATIONS: {
    format: { default: "text/plain" },
  },
  ASSOCIATIONS: {
    direction: { default: "None" },
  },
  SHAPE: {
    isCollapsed: {
      default: "false",
    },
  },
};
