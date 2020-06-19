import { By } from "selenium-webdriver";
import PageFragment from "../PageFragment";

export default abstract class Editor extends PageFragment {

    private static readonly LOADING_POPUP_LOCATOR: By = By.className("pf-l-bullseye");
    private static readonly EXPLORE_ICON_LOCATOR: By = By.className("fa-eye");

    public async waitUntilLoaded(): Promise<void> {
        await this.enter();
        if (await this.tools.by(Editor.LOADING_POPUP_LOCATOR).wait(5000).isPresent()) {
            await this.tools.by(Editor.LOADING_POPUP_LOCATOR).wait(15000).untilAbsent();
        }
        await this.tools.by(Editor.EXPLORE_ICON_LOCATOR).wait(5000).untilPresent();
        await this.leave();
    }

    public async enter(): Promise<void> {
        await this.root.enterFrame();
    }

    public async leave(): Promise<void> {
        await this.tools.window().leaveFrame();
    }
}
