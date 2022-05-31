import { SW_SPEC_WORKFLOW_SCHEMA } from "../schemas";
import { getLanguageService, TextDocument } from "vscode-json-languageservice";

export async function SwfJsonValidation(jsonContent: any, jsonContentUri: any, jsonSchemaUri: any) {
  const jsonSchema = SW_SPEC_WORKFLOW_SCHEMA;

  const textDocument = TextDocument.create(jsonContentUri, "serverless-workflow-json", 1, jsonContent);

  const jsonLanguageService = getLanguageService({
    schemaRequestService: (uri) => {
      if (uri === jsonSchemaUri) {
        return Promise.resolve(JSON.stringify(jsonSchema));
      }
      return Promise.reject(`Unabled to load schema at ${uri}`);
    },
  });

  jsonLanguageService.configure({ allowComments: false, schemas: [{ fileMatch: ["*.sw.json"], uri: jsonSchemaUri }] });

  const jsonDocument = jsonLanguageService.parseJSONDocument(textDocument);

  const diagnostics = await jsonLanguageService.doValidation(textDocument, jsonDocument);

  return diagnostics;
}
