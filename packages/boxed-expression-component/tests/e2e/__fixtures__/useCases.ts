import { Page } from "@playwright/test";

type FindEmployees = "employees" | "find-employees-by-knowledge" | "find-by-employees";

interface LoanOriginationsPaths {
  "application-risk-score": "";
  "required-monthly-installment": "";
  "bureau-strategy-decision-service":
    | "bureau-call-type"
    | "eligibility"
    | "pre-bureau-affordability"
    | "pre-bureau-risk-category"
    | "strategy";
  functions: "affordability-calculation" | "installment-calculation";
  "routing-decision-service": "post-bureau-affordability" | "post-bureau-risk-category" | "routing";
}

export class UseCases {
  constructor(public page: Page, public baseURL?: string) {
    this.page = page;
    this.baseURL = baseURL;
  }

  public getIframeURL(iframeId: string) {
    return `iframe.html?id=${iframeId}&viewMode=story`;
  }

  public async openCanDrive() {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`use-cases-can-drive--expression`)}` ?? "");
  }

  public async openFindEmployees(type: FindEmployees) {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`use-cases-find-employees--${type}`)}` ?? "");
  }

  public async openLoanOriginations<Path extends keyof LoanOriginationsPaths>(
    path: Path,
    subpath?: LoanOriginationsPaths[Path]
  ) {
    if (subpath) {
      await this.page.goto(
        `${this.baseURL}/${this.getIframeURL(`use-cases-loan-originations-${path}-${subpath}--expression`)}` ?? ""
      );
    } else {
      await this.page.goto(
        `${this.baseURL}/${this.getIframeURL(`use-cases-loan-originations-${path}--expression`)}` ?? ""
      );
    }
  }
}
