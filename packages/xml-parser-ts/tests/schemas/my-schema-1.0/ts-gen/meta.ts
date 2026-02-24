export const root = {
  element: "rootElement",
  type: "MySchema10__RootElementType",
} as const;

export const ns = new Map<string, string>([
  ["https://kie.apache.org/my-schema-1.0", ""],
  ["", "https://kie.apache.org/my-schema-1.0"],
]);

export const subs = {
  "": {},
};

export const elements = {
  rootElement: "MySchema10__RootElementType",
};

export const meta = {
  MySchema10__RootElementType: {
    "@_foo": { type: "string", isArray: false, fromType: "MySchema10__RootElementType", xsdType: "xsd:string" },
    "@_bar": { type: "integer", isArray: false, fromType: "MySchema10__RootElementType", xsdType: "xsd:integer" },
    childElement: { type: "string", isArray: false, fromType: "MySchema10__RootElementType", xsdType: "xsd:string" },
  },
} as const;
