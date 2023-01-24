import * as yaml from "js-yaml";
import { posix as posixPath } from "path";
import { SwfServiceCatalogServiceSource } from "../../api";
import { AsyncApiParser } from "./impl/AsyncApiParser";
import { OpenApiParser } from "./impl/OpenApiParser";
import { SpecParser } from "./SpecParser";

const specParsers: SpecParser<any>[] = [new OpenApiParser(), new AsyncApiParser()];

export function parseApiContent(args: {
  serviceFileName: string;
  serviceFileContent: string;
  source: SwfServiceCatalogServiceSource;
}) {
  console.log("args,", args);
  const tempFileContent = serviceFileContentToOpenApiDocument(args);
  console.log("fileContent", tempFileContent);
  return tempFileContent;
}

function serviceFileContentToOpenApiDocument(args: any) {
  let specContent: any;

  if (posixPath.extname(args.serviceFileName) === ".json") {
    specContent = JSON.parse(args.serviceFileContent);
  } else {
    specContent = yaml.load(args.serviceFileContent);
  }
  console.log("specContent", specContent);
  const parser = specParsers.find((parser) => parser.canParse(specContent));
  console.log("check", parser);
  if (!parser) {
    throw new Error(`'${args.serviceFileName}' is not a supported spec file`);
  }

  return parser.parse(specContent, args);
}
