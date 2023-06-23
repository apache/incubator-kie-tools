export type TiagoComplexTypeBase = {
  childOf?: string;
  doc: string;
  type: "complex";
  needsExtensionType: boolean; // That's for sequences decalring <xsd:anyAttribute> or <xsd:any>
  declaredAtRelativeLocation: string;
  elements: Array<
    {
      isArray: boolean;
      isOptional: boolean;
    } & (
      | {
          name: string;
          kind: "ofNamedType"; // Type declared somewhere else.
          typeName: string;
        }
      | {
          name: string;
          kind: "ofAnonymousType"; // Types declared directly inside the element.
          anonymousType: TiagoComplexTypeAnonymous;
        }
      | {
          kind: "ofRef"; // References another element.
          ref: string;
        }
    )
  >;
  attributes: Array<{
    name: string;
    localTypeRef: string;
    isOptional: boolean;
  }>;
};

export type TiagoComplexTypeAnonymous = TiagoComplexTypeBase & {
  isAnonymous: true; // Declared directly inside elements.
  forElementWithName: string;
  parentIdentifierForExtensionType: string;
};

export type TiagoComplexTypeNamed = TiagoComplexTypeBase & {
  name: string;
  isAbstract: boolean;
  isAnonymous: false;
};

export type TiagoComplexType = TiagoComplexTypeNamed | TiagoComplexTypeAnonymous;

export type TiagoElement = {
  name: string;
  type: string;
  isAbstract: boolean;
  substitutionGroup?: string;
  declaredAtRelativeLocation: string;
};

export type TiagoSimpleType = {
  type: "simple";
  doc: string;
  name: string;
  restrictionBase?: string;
  declaredAtRelativeLocation: string;
} & (
  | {
      kind: "enum";
      values: string[];
    }
  | {
      kind: "int";
      minInclusive?: number;
      maxInclusive?: number;
    }
);

export type TiagoTsPrimitiveType = {
  doc: string;
  type: "primitive";
  tsEquivalent: string;
};

export type TiagoTsImports = {
  save: (name: string, location: string) => void;
};

export type TiagoMetaType = {
  name: string;
  properties: TiagoMetaTypeProperty[];
};

export type TiagoMetaTypeProperty = {
  name: string;
  elem: TiagoElement | undefined;
  metaType: {
    name: string;
    tiagoType: TiagoSimpleType | TiagoComplexType | undefined;
  };
  isArray: boolean;
  isOptional: boolean;
  fromType: string;
  declaredAt: string;
};
