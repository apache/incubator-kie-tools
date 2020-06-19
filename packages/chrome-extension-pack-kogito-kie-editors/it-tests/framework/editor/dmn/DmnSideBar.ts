import { By } from "selenium-webdriver";
import DecisionNavigator from "./DecisionNavigator";
import Element from "../../Element";
import SideBar from "../SideBar";

export default class DmnSideBar extends SideBar {

    private static readonly NAVIGATOR_BUTTON_LOCATOR: By = By.xpath("//div[./button[@data-title='Decision Navigator']]");

    public async openDecisionNavigator(): Promise<DecisionNavigator> {
        const navigatorButton: Element = await this.tools.by(DmnSideBar.NAVIGATOR_BUTTON_LOCATOR).wait(2000).untilPresent();
        const sideBar: Element = await this.openSideBar(navigatorButton, "Decision Navigator");
        return this.tools.createPageFragment(DecisionNavigator, sideBar);
    }
}
