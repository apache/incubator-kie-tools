import { getTextWidth } from "@kie-tools/boxed-expression-component/dist/resizing/WidthsToFitData";
import { DEFAULT_MIN_WIDTH } from "@kie-tools/boxed-expression-component/dist/resizing/WidthConstants";

export function getDefaultColumnWidth({ name, typeRef }: { name: string; typeRef: string | undefined }): number {
  return (
    8 * 2 + // FIXME: Tiago --> Copied from ContextEntry info `getWidthToFit`
    2 + // FIXME: Tiago --> Copied from ContextEntry info `getWidthToFit`
    Math.max(
      DEFAULT_MIN_WIDTH,
      getTextWidth(name, "700 11.2px Menlo, monospace"),
      getTextWidth(`(${typeRef ?? ""})`, "700 11.6667px RedHatText, Overpass, overpass, helvetica, arial, sans-serif")
    )
  );
}
