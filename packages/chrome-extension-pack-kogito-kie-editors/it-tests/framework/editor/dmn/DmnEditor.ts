import { By } from "selenium-webdriver";
import DmnPalette from "./DmnPalette";
import DmnSideBar from "./DmnSideBar";
import Editor from "../Editor";
import Element from "../../Element";
import Locator from "../../Locator";

export default class DmnEditor extends Editor {

    private static readonly PALETTE_LOCATOR: By = By.className("kie-palette");
    private static readonly SIDE_BAR_LOCATOR: By = By.className("qe-docks-bar-E");

    public async getDmnPalette(): Promise<DmnPalette> {
        const palette: Element = await this.tools.by(DmnEditor.PALETTE_LOCATOR).getElement();
        return this.tools.createPageFragment(DmnPalette, palette);
    }

    public async getSideBar(): Promise<DmnSideBar> {
        const sideBar: Locator = this.tools.by(DmnEditor.SIDE_BAR_LOCATOR);
        await sideBar.wait(1000).untilPresent();
        return this.tools.createPageFragment(DmnSideBar, await sideBar.getElement());
    }

    public async dragAndDropAnnotationToCanvas(): Promise<void> {
        const dmnPalette: DmnPalette = await this.getDmnPalette();
        await dmnPalette.dragAndDropAnnotationToCanvas();
    }
}
