import { By } from "selenium-webdriver";
import Element from "../Element";
import Explorer from "./Explorer";
import Locator from "../Locator";
import PageFragment from "../PageFragment";
import Properties from "./Properties";

export default class SideBar extends PageFragment {

    private static readonly PROP_BUTTON_LOCATOR = By.xpath("//div[./button[@data-title='Properties']]");
    private static readonly EXPLORER_BUTTON_LOCATOR = By.xpath("//div[./button[@data-title='Explore Diagram' or @data-title='Explore diagram']]");
    private static readonly EXPANDED_BAR_LOCATOR = By.className("qe-docks-bar-expanded-E");
    private static readonly TITLE_LOCATOR = By.xpath("./div/h3");

    public async waitUntilLoaded(): Promise<void> {
        await this.tools.by(SideBar.EXPLORER_BUTTON_LOCATOR).wait(1000).untilPresent();
    }

    protected async openSideBar(byIcon: Element, sideBarTitle: string): Promise<Element> {
        const expandedBar: Locator = this.tools.by(SideBar.EXPANDED_BAR_LOCATOR);

        if (!await this.isSideBarOpen(sideBarTitle)) {
            await byIcon.click();
            await expandedBar.wait(5000).untilVisible();
            // move to make the tooltip diappear 
            await byIcon.offsetMove(-200, 0);
        }

        return await expandedBar.getElement();
    }

    private async isSideBarOpen(title: string): Promise<boolean> {
        const sideBarLocator: Locator = this.tools.by(SideBar.EXPANDED_BAR_LOCATOR);
        const isSideBarOpen: boolean = await sideBarLocator.wait().isVisible();
        if (isSideBarOpen) {
            const sideBar: Element = await sideBarLocator.getElement();
            const sideBarTitle: Element = await sideBar.findElement(SideBar.TITLE_LOCATOR);
            const actualTitle: string = await sideBarTitle.getText();
            if (actualTitle === title) {
                return true;
            }
        }
        return false;
    }

    public async openExplorer(): Promise<Explorer> {
        const diagramButton: Element = await this.tools.by(SideBar.EXPLORER_BUTTON_LOCATOR).wait(2000).untilPresent();
        const sideBar = await this.openSideBar(diagramButton, "Explore Diagram");
        return await this.tools.createPageFragment(Explorer, sideBar);
    }

    public async openProperties(): Promise<Properties> {
        const propButton = await this.tools.by(SideBar.PROP_BUTTON_LOCATOR).getElement();
        const sideBar = await this.openSideBar(propButton, "Properties");
        return await this.tools.createPageFragment(Properties, sideBar);
    }
}
