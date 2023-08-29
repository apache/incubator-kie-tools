export const SPEC = {
  namedElement: {
    isValidName: (name: string) => {
      return true; // FIXME: Tiago --> Implement
    },
  },
  expressionLanguage: { default: `https://www.omg.org/spec/DMN/20211108/FEEL/` }, // FIXME: Tiago --> This is not quite right, as DMN now has multiple versions of FEEL
  typeLanguage: { default: `https://www.omg.org/spec/DMN/20211108/FEEL/` }, // FIXME: Tiago --> This is not quite right, as DMN now has multiple versions of FEEL
  IMPORT: {
    name: {
      isValid: (name: string) => {
        // Empty strings are allowed for imports, so that imported elements can be used without a prefix.
        // Source: https://www.omg.org/spec/DMN/1.5/Beta1/PDF. PDF page 40, document page 32. Section "6.3.3 Import Metamodel".
        return name === "" || SPEC.namedElement.isValidName(name);
      },
    },
  },
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
