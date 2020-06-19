import { By } from "selenium-webdriver";
import Element from "../Element";
import PageFragment from "../PageFragment";

export default class Explorer extends PageFragment {

    private static readonly PANEL_LOCATOR = By.xpath("//div[@data-field='explorerPanelBody']");
    private static readonly ITEM_LOCATOR = By.className("gwt-Anchor");

    public async waitUntilLoaded(): Promise<void> {
        await this.tools.by(Explorer.PANEL_LOCATOR).wait(5000).untilPresent();
    }

    private async getItems(): Promise<Element[]> {
        return await this.tools.by(Explorer.ITEM_LOCATOR).getElements();
    }

    private async getNodes(): Promise<Element[]> {
        const items: Element[] = await this.getItems();
        items.shift(); // remove asset name
        return items;
    }

    private async getNode(name: string): Promise<Element> {
        for (const node of await this.getNodes()) {
            if (await node.getText() === name) {
                return node;
            }
        }
        throw new Error("Node '" + name + "' was not found.");
    }

    public async getProcessName(): Promise<string> {
        const items: Element[] = await this.getItems();
        return await items[0].getText();
    }

    public async getNodeNames(): Promise<string[]> {
        const nodes: Element[] = await this.getNodes();
        return Promise.all(nodes.map(node => node.getText()));
    }

    public async selectNode(name: string): Promise<void> {
        const node: Element = await this.getNode(name);
        await node.click();
    }
}
