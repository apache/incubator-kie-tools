## JSON and YAML Language Service

JSON and YAML language service to be reused in VSCode and Monaco editor.

## Features

- getCompletionItems() provides Code Completions for given locations.
- getCodeLenses() provides Code Lenses for given locations to be defined with custom editor commands.
- getDiagnostics() provides Code Diagnostic from a schema and/or from custom references.
- doRefValidation() provide Code Diagnostic by references.
- findNodesAtLocation() finds nodes at a JSONPath location. It's similar to `jsonc.findNodeAtLocation`, but it allows the use of '\*' as a wildcard selector.
- positions_equals() Test if position `a` equals position `b`.
- getLineContentFromOffset() Gets a line from a content at a specific offset.
- getNodeFormat() Detect the format of a node's content.
- indentText() Indent a text.
- matchNodeWithLocation() Check if a Node is in Location.
- nodeUpUntilType() From a node goes up to levels until a certain node type.
