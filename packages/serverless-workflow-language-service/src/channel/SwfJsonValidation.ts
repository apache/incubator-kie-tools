import { SW_SPEC_WORKFLOW_SCHEMA } from "../schemas";
import { getLanguageService, JSONDocument, LanguageService, TextDocument } from "vscode-json-languageservice";
import * as ls from "vscode-languageserver-types";

export async function SwfJsonValidation(jsonContent: string, jsonContentUri: string, jsonSchemaUri: string) {
  const jsonSchema = SW_SPEC_WORKFLOW_SCHEMA;

  const textDocument: TextDocument = TextDocument.create(jsonContentUri, "serverless-workflow-json", 1, jsonContent);

  const jsonLanguageService: LanguageService = getLanguageService({
    schemaRequestService: (uri) => {
      if (uri === jsonSchemaUri) {
        return Promise.resolve(JSON.stringify(jsonSchema));
      }
      return Promise.reject(`Unabled to load schema at ${uri}`);
    },
  });

  jsonLanguageService.configure({ allowComments: false, schemas: [{ fileMatch: ["*.sw.json"], uri: jsonSchemaUri }] });

  const jsonDocument: JSONDocument = jsonLanguageService.parseJSONDocument(textDocument);

  const diagnostics: ls.Diagnostic[] = await jsonLanguageService.doValidation(textDocument, jsonDocument);

  return diagnostics;
}
