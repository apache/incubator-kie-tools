import { By } from "selenium-webdriver";
import Element from "../Element";
import GitHubListItem from "./GitHubListItem";
import Page from "../Page";

export default class GitHubListPage extends Page {

    private static readonly DOUBLE_DOT_LOCATOR: By = By.xpath("//a[@title='Go to parent directory']");

    public async waitUntilLoaded(): Promise<void> {
        await this.tools.by(GitHubListPage.DOUBLE_DOT_LOCATOR).wait(1000).untilPresent();
    }

    public async getFile(name: string): Promise<GitHubListItem> {
        const item: Element = await this.tools.by(By.xpath(`//div[@role="rowheader"]/span[.//a[text()='${name}']]`))
            .wait(5000)
            .untilPresent();
        return await this.tools.createPageFragment(GitHubListItem, item);
    }
}
