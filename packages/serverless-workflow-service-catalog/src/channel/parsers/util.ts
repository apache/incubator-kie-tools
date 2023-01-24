import { SwfCatalogSourceType, SwfServiceCatalogFunctionSource, SwfServiceCatalogServiceSource } from "../../api";

export function convertSource(catalogSource: SwfServiceCatalogServiceSource): SwfServiceCatalogFunctionSource {
  console.log("inside converSource", catalogSource.type, SwfCatalogSourceType.LOCAL_FS);
  if (catalogSource?.type === SwfCatalogSourceType.LOCAL_FS) {
    return {
      type: catalogSource.type,
      serviceFileAbsolutePath: catalogSource.absoluteFilePath,
    };
  }

  return {
    type: catalogSource.type,
    registry: "",
    serviceId: "",
  };
}
