import { SwfServiceCatalogService } from "../../../api";
import { parseAsyncApi } from "../asyncapi";
import { ArgsType, SpecParser } from "../SpecParser";

export class AsyncApiParser implements SpecParser<any> {
  canParse(content: any): boolean {
    return content.asyncapi && content.info && content.channels;
  }

  parse(serviceAsyncApiDocument: any, args: ArgsType): SwfServiceCatalogService {
    console.log("AsyncApiParser", serviceAsyncApiDocument);
    return parseAsyncApi(args, serviceAsyncApiDocument);
  }
}
