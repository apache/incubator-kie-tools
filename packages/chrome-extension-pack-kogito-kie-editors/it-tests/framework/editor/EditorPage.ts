import BpmnEditor from "./bpmn/BpmnEditor";
import { By } from "selenium-webdriver";
import DmnEditor from "./dmn/DmnEditor";
import Element from "../Element";
import Page from "../Page";

export default abstract class EditorPage extends Page {
    protected static readonly FRAME_LOCATOR = By.xpath("//iframe[contains(@class,'kogito-iframe') or contains(@id,'kogito-iframe')]");

    private async getEditor(): Promise<Element> {
        const frame: Element = await this.tools.by(EditorPage.FRAME_LOCATOR)
            .wait(2000)
            .untilPresent();
        await frame.scroll();
        return frame;
    }

    public async getDmnEditor(): Promise<DmnEditor> {
        const editor: Element = await this.getEditor();
        return await this.tools.createPageFragment(DmnEditor, editor);
    }

    public async getBpmnEditor(): Promise<BpmnEditor> {
        const editor: Element = await this.getEditor();
        return await this.tools.createPageFragment(BpmnEditor, editor);
    }
}
