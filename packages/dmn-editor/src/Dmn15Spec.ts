// Those two below are defined by the spec. See S-FEEL grammar. Rule 22: "name".
const feelNameStart =
  /^[?A-Z_a-z\uC0-\uD6\uD8-\uF6\uF8-\u2FF\u370-\u37D\u37F-\u1FFF\u200C-\u200D\u2070-\u218F\u2C00-\u2FEF\u3001-\uD7FF\uF900-\uFDCF\uFDF0-\uFFFD\u10000-\uEFFFF].*$/;
const feelNamePart =
  // ------------------------------------------------------------------------------------------------------ same as nameStart ------------------------------- ----------name part------------ --extra--
  /^.[?A-Z_a-z\uC0-\uD6\uD8-\uF6\uF8-\u2FF\u370-\u37D\u37F-\u1FFF\u200C-\u200D\u2070-\u218F\u2C00-\u2FEF\u3001-\uD7FF\uF900-\uFDCF\uFDF0-\uFFFD\u10000-\uEFFFF\uB7\d\u0300-\u036F\u203F-\u2040.+-/*’\s^]*$/;

// This is not part of the spec.
const forbiddenEndingChars = /^.*[.:+-/*\s^]$/; // Although they're fine by the Spec, they can seriously complicate FEEL expressions readability.

export type UniqueNameIndex = Map<string, string>;

export const DMN15_SPEC = {
  namedElement: {
    isValidName: (id: string, name: string | undefined, allUniqueNames: UniqueNameIndex): boolean => {
      return (
        !!name?.trim() && // Names need to be non-empty.
        !!name.match(feelNameStart) &&
        !!name.match(feelNamePart) &&
        !name?.trim().match(forbiddenEndingChars) &&
        (!allUniqueNames.get(name?.trim()) || allUniqueNames.get(name?.trim()) === id) // All names need to be unique.
      );
    },
  },
  expressionLanguage: { default: `https://www.omg.org/spec/DMN/20230324/FEEL/` },
  typeLanguage: { default: `https://www.omg.org/spec/DMN/20230324/FEEL/` },
  IMPORT: {
    name: {
      isValid: (id: string, name: string, allUniqueNames: UniqueNameIndex) => {
        // Empty strings are allowed for imports, so that imported elements can be used without a prefix.
        // Source: https://www.omg.org/spec/DMN/1.5/Beta1/PDF. PDF page 40, document page 32. Section "6.3.3 Import Metamodel".
        return name === "" || DMN15_SPEC.namedElement.isValidName(id, name, allUniqueNames);
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
