// manually written

export const ns = new Map([
  ["http://www.w3.org/2001/XMLSchema", "xsd:"],
  ["http://www.w3.org/2001/XMLSchema-hasFacetAndProperty", "hfp:"],
  ["xsd:", "http://www.w3.org/2001/XMLSchema"],
  ["hfp:", "http://www.w3.org/2001/XMLSchema-hasFacetAndProperty"],
]);

export const meta = {
  schema: {
    "xsd:import": { type: "import", isArray: true, isOptional: false },
    "xsd:element": { type: "element", isArray: true, isOptional: false },
    "xsd:annotation": { type: "annotation", isArray: true, isOptional: false },
    "xsd:simpleType": { type: "simpleType", isArray: true, isOptional: false },
    "xsd:complexType": { type: "complexType", isArray: true, isOptional: false },
  },
  all: {
    "@_minOccurs": { type: "integer", isArray: false, isOptional: true },
    "@_maxOccurs": { type: "allNNI", isArray: false, isOptional: true },
    "xsd:element": { type: "element", isArray: true, isOptional: false },
  },
  import: {},
  annotation: {},
  simpleType: {},
  attribute: {},
  element: {
    "@_abstract": { type: "boolean", isArray: false, isOptional: true },
    "@_minOccurs": { type: "integer", isArray: false, isOptional: true },
    "@_maxOccurs": { type: "allNNI", isArray: false, isOptional: true },
    "xsd:complexType": { type: "complexType", isArray: false, isOptional: true },
  },
  complexType: {
    "@_abstract": { type: "boolean", isArray: false, isOptional: true },
    "xsd:complexContent": { type: "complexContent", isArray: false, isOptional: true },
    "xsd:sequence": { type: "sequence", isArray: false, isOptional: true },
    "xsd:all": { type: "all", isArray: false, isOptional: true },
    "xsd:attribute": { type: "attribute", isArray: true, isOptional: false },
  },
  complexContent: {
    "xsd:extension": { type: "extension", isArray: false, isOptional: false },
  },
  extension: {
    "xsd:sequence": { type: "sequence", isArray: false, isOptional: true },
    "xsd:choice": { type: "choice", isArray: false, isOptional: true },
    "xsd:attribute": { type: "attribute", isArray: true, isOptional: false },
  },
  sequence: {
    "xsd:element": { type: "element", isArray: true, isOptional: true },
    "xsd:choice": { type: "choice", isArray: false, isOptional: true },
    "xsd:any": { type: "any", isArray: false, isOptional: true },
  },
  any: {
    "@_minOccurs": { type: "integer", isArray: false, isOptional: true },
    "@_maxOccurs": { type: "allNNI", isArray: false, isOptional: true },
  },
  choice: {
    "@_minOccurs": { type: "integer", isArray: false, isOptional: true },
    "@_maxOccurs": { type: "allNNI", isArray: false, isOptional: true },
    "xsd:element": { type: "element", isArray: true, isOptional: false },
    "xsd:sequence": { type: "sequence", isArray: false, isOptional: true },
  },
};
