import Ajv from "ajv";
import metaSchemaDraft04 from "ajv/lib/refs/json-schema-draft-04.json";
import { DmnTableJsonSchemaBridge } from "./DmnTableJsonSchemaBridge";

const DAYS_AND_TIME = /^(-|\+)?P(?:([-+]?[0-9]*)D)?(?:T(?:([-+]?[0-9]*)H)?(?:([-+]?[0-9]*)M)?(?:([-+]?[0-9]*)S)?)?$/;
const YEARS_AND_MONTHS = /^(-|\+)?P(?:([-+]?[0-9]*)Y)?(?:([-+]?[0-9]*)M)?$/;

export class DmnValidator {
  protected readonly ajv = new Ajv({ allErrors: true, schemaId: "auto", useDefaults: true });
  private readonly SCHEMA_DRAFT4 = "http://json-schema.org/draft-04/schema#";

  constructor() {
    this.setupValidator();
  }

  private setupValidator() {
    this.ajv.addMetaSchema(metaSchemaDraft04);
    this.ajv.addFormat("days and time duration", {
      type: "string",
      validate: (data: string) => !!data.match(DAYS_AND_TIME),
    });

    this.ajv.addFormat("years and months duration", {
      type: "string",
      validate: (data: string) => !!data.match(YEARS_AND_MONTHS),
    });
  }

  public createValidator(jsonSchema: any) {
    const validator = this.ajv.compile(jsonSchema);

    return (model: any) => {
      // AJV doesn't handle dates objects. This transformation converts Dates to their UTC format.
      validator(JSON.parse(JSON.stringify(model)));

      if (validator.errors && validator.errors.length) {
        const details = validator.errors
          .filter((error: any) => error.keyword !== "required")
          .map((error: any) => {
            if (error.keyword === "format") {
              if ((error.params as any).format === "days and time duration") {
                return { ...error, message: "" };
              }
              if ((error.params as any).format === "years and months duration") {
                return { ...error, message: "" };
              }
            }
          });
        return { details };
      }
      return null;
    };
  }

  public getBridge(formSchema: any): DmnTableJsonSchemaBridge {
    const formDraft4 = { ...formSchema, $schema: this.SCHEMA_DRAFT4 };
    const validator = this.createValidator(formDraft4);
    return new DmnTableJsonSchemaBridge(formDraft4, validator);
  }
}
