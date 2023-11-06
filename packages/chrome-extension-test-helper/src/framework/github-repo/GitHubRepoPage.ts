import { By, Key } from "selenium-webdriver";
import Page from "../Page";

export default class GitHubRepoPage extends Page {
  private static readonly TOKEN_ICON: By = By.className("kogito-menu-icon");
  private static readonly TOKEN_INPUT: By = By.className("kogito-github-token-input");

  public async waitUntilLoaded(): Promise<void> {
    return await this.tools.by(GitHubRepoPage.TOKEN_ICON).wait(1000).untilPresent();
  }

  public async addToken(token: string): Promise<void> {
    const tokenIcon = await this.tools.by(GitHubRepoPage.TOKEN_ICON).getElement();
    await tokenIcon.click();
    await this.tools.by(GitHubRepoPage.TOKEN_INPUT).wait(1000).untilPresent();
    const tokenInput = await this.tools.by(GitHubRepoPage.TOKEN_INPUT).getElement();
    await tokenInput.click();
    await this.tools.clipboard().setContent(token);
    await tokenInput.sendKeys(this.tools.clipboard().getCtrvKeys());
    await this.tools.by(GitHubRepoPage.TOKEN_INPUT).wait(5000).untilAbsent();
  }
}
