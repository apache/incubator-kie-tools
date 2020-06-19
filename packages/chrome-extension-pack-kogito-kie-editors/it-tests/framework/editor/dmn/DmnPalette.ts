import { By } from "selenium-webdriver";
import PageFragment from "../../PageFragment";

export default class DmnPalette extends PageFragment {

    private static readonly ANNOTATION_LOCATOR: By = By.xpath("//button[@title='DMN Text Annotation']");

    public async waitUntilLoaded(): Promise<void> {
        await this.tools.by(DmnPalette.ANNOTATION_LOCATOR).wait(1000).untilPresent();
    }

    public async dragAndDropAnnotationToCanvas(): Promise<void> {

        // click annotation
        const annotation = await this.tools.by(DmnPalette.ANNOTATION_LOCATOR).getElement();

        // move to canvas
        await annotation.dragAndDrop(200, 0);

        // click to canvas
        await annotation.offsetClick(100, 0);
    }
}
