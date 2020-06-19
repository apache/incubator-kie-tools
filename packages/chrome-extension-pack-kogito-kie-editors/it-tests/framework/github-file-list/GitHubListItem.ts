import { By } from "selenium-webdriver";
import Element from "../Element";
import GitHubEditorPage from "../github-editor/GitHubEditorPage";
import PageFragment from "../PageFragment";

export default class GitHubListItem extends PageFragment {

    private static readonly LINK_LOCATOR: By = By.className("js-navigation-open");
    private static readonly LINK_TO_ONLINE_EDITOR: By = By.xpath(".//a[@title='Open in Online Editor']");

    public async waitUntilLoaded(): Promise<void> {
        await this.tools.by(GitHubListItem.LINK_TO_ONLINE_EDITOR).wait(5000).untilPresent();
    }

    public async open(): Promise<GitHubEditorPage> {
        const link: Element = await this.root.findElement(GitHubListItem.LINK_LOCATOR);
        await link.click();
        return this.tools.createPage(GitHubEditorPage);
    }

    public async getLinkToOnlineEditor(): Promise<string> {
        const linkToOnlineEditor: Element = await this.root.findElement(GitHubListItem.LINK_TO_ONLINE_EDITOR);
        return linkToOnlineEditor.getAttribute("href");
    }
}
