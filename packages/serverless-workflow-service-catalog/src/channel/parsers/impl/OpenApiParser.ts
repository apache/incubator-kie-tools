import { OpenAPIV3 } from "openapi-types";
import { SwfServiceCatalogService, SwfServiceCatalogServiceSource } from "../../../api";
import { parseOpenApi } from "../openapi";
import { ArgsType, SpecParser } from "../SpecParser";

export class OpenApiParser implements SpecParser<OpenAPIV3.Document> {
  canParse(content: any): boolean {
    return content.openapi && content.info && content.paths;
  }

  parse(serviceOpenApiDocument: OpenAPIV3.Document, args: ArgsType): SwfServiceCatalogService {
    return parseOpenApi(args, serviceOpenApiDocument);
  }
}
