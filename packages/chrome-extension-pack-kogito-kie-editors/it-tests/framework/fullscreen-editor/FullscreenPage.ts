import { By, Key } from "selenium-webdriver";
import EditorPage from "../editor/EditorPage";
import Element from "../Element";
import GitHubEditorPage from "../github-editor/GitHubEditorPage";

export default class FullScreenPage extends EditorPage {

    private static readonly EXIT_BUTTON_LOCATOR = By.xpath("//a[@data-testid='exit-fullscreen-button']");

    public async exitFullscreen(): Promise<GitHubEditorPage> {
        const exitButton: Element = await this.tools.by(FullScreenPage.EXIT_BUTTON_LOCATOR).getElement();

        // regular click does not work, clickJs() breaks other tests, sendKeys() does not work on Mac
        await exitButton.sendKeys(Key.ENTER);
        return await this.tools.createPage(GitHubEditorPage);
    }

    public async waitUntilLoaded(): Promise<void> {
        await this.tools.by(FullScreenPage.EXIT_BUTTON_LOCATOR).wait(10000).untilPresent();
    }

    public async getExitFullScreenUrl(): Promise<string> {
        const exitButton: Element = await this.tools.by(FullScreenPage.EXIT_BUTTON_LOCATOR).getElement();
        return await exitButton.getAttribute("href");
    }
}
