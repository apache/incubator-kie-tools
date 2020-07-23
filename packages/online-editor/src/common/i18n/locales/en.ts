import { OnlineI18n } from "../OnlineI18n";
import { en_US as en_US_terms } from "@kogito-tooling/i18n-terms"

export const en: OnlineI18n = {
  ...en_US_terms,
  downloadHubModal: {
    beforeDownload: {
      title: "my title",
      vscodeDescription: "",
      githubChromeDescription: "",
      desktopDescription: "",
      businessModelerDescription: "",
      operationSystem: ""
    },
    afterDownload: {
      title: ""
    }
  }
};
