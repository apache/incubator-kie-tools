# @kie-tools/xml-parser-ts-codegen

This package provides a Node.js script that generates TypeScript types and metadata from XSD file(s) to parse/build XML strings from/to JSON using `@kie-tools/xml-parser-ts`. It doesn't implement the entire XSD specification, so there might be bugs when attempting to use it with an arbitrary XSD file.

For more information about `@kie-tools/xml-parser-ts`, go to [../packages/xml-parser-ts](../xml-parser-ts/README.md)

It was created to provide a way to marshall/unmarshall DMN, SceSim, BPMN, and PMML files, so it is only tested against those formats XSDs.

> NOTE: This package is probably not suited for your use-case, as it is incomplete and was not tested with a large number of XSD files, but it should work for simple XSDs. If you want to use it, please go ahead! Let us know if you tried and it worked! Feel free to contribute too with issues and PRs too.

> NOTE: Since this package itself uses `@kie-tools/xml-parser-ts` to parse XSD files, in theory, it's possible to use this package to generate types and metadata for XSDs themselves. It was not done yet, so I'm pretty sure it wouldn't work without a few modifications, but it should be possible. Feel free to try it and contribute with a PR.

### Features:

1. Supports substitution groups.
1. Supports recursive types.
1. Supports anonymous element types (max depth: 2)
1. Supports `<xsd:import>` and `<xsd:include>` tags.
1. Supports multiple namespaces.
1. The generated types support extensions on types that contain `<xsd:any>` and `<xsd:anyAttribute>`.

### Usage:

`npx @kie-tools/xml-parser-ts-codegen relative/path/to/your-xsd-file.xsd rootElementName`

Types and metadata will be written to the same folder of `your-xsd-file.xsd`, at the `ts-gen` directory. Two files are going to be generated:

1. types.ts
   - Contains the types representing the JSON obtained from parsing an XML document.
   - Extensible types are interfaces, so you can extend them with `declare module ...`
   - Hierarchies are flattened, to make it simple to extend the exact type wanted.
2. meta.ts
   - `root`: Root element type and name
   - `ns`: Bi-directional Namespace mapping created from the XSD file(s).
   - `meta`: Type information to be used in runtime by `@kie-tools/xml-parser-ts`. It's main use is to determine whetever an element/attribute is a string/boolean/float/integer or an array, so the XMLs are parsed and built consistently.

### Future:

- Generate JSON Schemas to validate the parsed JSON file with relevant information present on the original XSDs.
- Resolve substitutionGroups with union types instead of optional properties.
- Add `<xsd:annotation>` information to the generated TypeScript types.

PRs are welcome!
