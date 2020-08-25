import { By } from "selenium-webdriver";
import EditorPage from "../editor/EditorPage";
import Element from "../Element";

export default class OnlineEditorPage extends EditorPage {
    
    private static readonly TOOLBAR_LOCATOR: By = By.className("kogito--editor__toolbar");
    private static readonly FILE_NAME_LOCATOR: By = By.xpath("//input[@aria-label='Edit file name']")

    public async waitUntilLoaded(): Promise<void> {
        await this.tools.by(OnlineEditorPage.TOOLBAR_LOCATOR).wait(2000).untilPresent();
    }

    public async getFileName(): Promise<string> {
        const filename: Element = await this.tools.by(OnlineEditorPage.FILE_NAME_LOCATOR).getElement();
        return await filename.getAttribute('value');
    }
}
