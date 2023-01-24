import { SwfServiceCatalogService, SwfServiceCatalogServiceSource } from "../../api";

export interface ArgsType {
  source: SwfServiceCatalogServiceSource;
  serviceFileName: string;
  serviceFileContent: string;
}

export interface SpecParser<T> {
  canParse(content: any): boolean;
  parse(content: T, args: ArgsType): SwfServiceCatalogService;
}
