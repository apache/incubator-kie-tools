import { By } from "selenium-webdriver";
import Element from "../../Element";
import PageFragment from "../../PageFragment";

export default class DecisionNavigator extends PageFragment {

    private static readonly DECISION_GRAPH_LOCATOR: By = By.xpath("//div[@data-i18n-prefix='DecisionNavigatorTreeView.']");
    private static readonly ITEM_LOCATOR: By = By.xpath("//li[@data-field='item']");

    public async waitUntilLoaded(): Promise<void> {
        await this.tools.by(DecisionNavigator.DECISION_GRAPH_LOCATOR).wait(5000).untilPresent();
    }

    private async getItems(): Promise<Element[]> {
        return await this.tools.by(DecisionNavigator.ITEM_LOCATOR).getElements();
    }

    private async getNodes(): Promise<Element[]> {
        const items: Element[] = await this.getItems();
        items.shift(); // remove DMN name
        return items;
    }

    public async getDmnName(): Promise<string> {
        const items: Element[] = await this.getItems();
        return await items[0].getAttribute("title");
    }

    public async getNodeNames(): Promise<string[]> {
        const nodes: Element[] = await this.getNodes();
        return Promise.all(nodes.map(node => node.getAttribute("title")));
    }

    public async selectNode(name: string): Promise<void> {
        const node: Element = await this.tools.by(By.xpath(`//li[@data-field='item'][@title='${name}']/div`)).getElement();
        await node.click();
    }
}
