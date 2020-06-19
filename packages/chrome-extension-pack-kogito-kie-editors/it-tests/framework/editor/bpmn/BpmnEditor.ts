import BpmnPalette from "./BpmnPalette";
import { By } from "selenium-webdriver";
import Editor from "../Editor";
import Element from "../../Element";
import Locator from "../../Locator";
import SideBar from "../SideBar";

export default class BpmnEditor extends Editor {

    private static readonly CANVAS_LOCATOR: By = By.className("canvas-panel");
    private static readonly PALETTE_LOCATOR: By = By.className("kie-palette");
    private static readonly SIDE_BAR_LOCATOR: By = By.className("qe-docks-bar-E");

    public async dragAndDropStartEventToCanvas(): Promise<void> {
        const bpmnPalette: BpmnPalette = await this.getBpmnPalette();
        await bpmnPalette.dragAndDropStartEventToCanvas();
        await this.clickToCanvas();
    }

    private async getBpmnPalette(): Promise<BpmnPalette> {
        const palette: Element = await this.tools.by(BpmnEditor.PALETTE_LOCATOR).getElement();
        return await this.tools.createPageFragment(BpmnPalette, palette);
    }

    private async clickToCanvas(): Promise<void> {
        const canvas: Element = await this.tools.by(BpmnEditor.CANVAS_LOCATOR).getElement();
        await canvas.click();
    }

    public async getSideBar(): Promise<SideBar> {
        const sideBar: Locator = await this.tools.by(BpmnEditor.SIDE_BAR_LOCATOR);
        await sideBar.wait(1000).untilPresent();
        return this.tools.createPageFragment(SideBar, await sideBar.getElement());
    }
}
