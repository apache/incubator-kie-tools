import { DmnFormSchema } from "@kogito-tooling/form/dist/dmn";
import { DmnRunnerPayload } from "../../../../editor/DmnRunner/DmnRunnerService";

export class DmnRunnerService {
  constructor(private readonly port = "") {}

  public async check(): Promise<boolean> {
    return new Promise((res) => res(true));
  }

  public async version(): Promise<string> {
    return new Promise((res) => res(""));
  }

  public async result(payload: DmnRunnerPayload) {
    return new Promise((res) => res({}));
  }

  public async validate(model: string): Promise<[]> {
    return new Promise((res) => res([]));
  }

  public async formSchema(model: string): Promise<DmnFormSchema> {
    return new Promise((res) => res({}));
  }
}
