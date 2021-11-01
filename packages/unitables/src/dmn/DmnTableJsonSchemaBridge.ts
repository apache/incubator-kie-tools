import { JSONSchemaBridge } from "uniforms-bridge-json-schema/esm";

export class DmnTableJsonSchemaBridge extends JSONSchemaBridge {
  public getProps(name: string, props: Record<string, any> = {}) {
    const ready = super.getProps(name, props);
    ready.label = "";
    ready.style = { height: "100%" };
    if (ready.required) {
      ready.required = false;
    }
    return ready;
  }

  public getField(name: string) {
    const field = super.getField(name);
    if (field.format === "days and time duration") {
      field.placeholder = "P1DT5H or P2D or PT1H2M10S";
    }
    if (field.format === "years and months duration") {
      field.placeholder = "P1Y5M or P2Y or P1M";
    }
    if (field.type === "string" && field.enum) {
      field.placeholder = "Select...";
    }
    if (!field.type && field["x-dmn-type"] === "FEEL:context") {
      field.placeholder = `{ "x": <value> }`;
    }
    return field;
  }
}
