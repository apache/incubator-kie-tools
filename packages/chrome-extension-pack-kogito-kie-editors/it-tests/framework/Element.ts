import { By, WebElement } from "selenium-webdriver";
import ErrorProcessor from "../utils/tools/ErrorProcessor";

export default class Element {

    private readonly webElement: WebElement;

    constructor(webElement: WebElement) {
        this.webElement = webElement;
    }

    public async dragAndDrop(x: number, y: number): Promise<void> {
        await ErrorProcessor.run(
            async () => {
                await this.click();

                // no other way of drag and drop works
                const actions = this.webElement.getDriver().actions();
                await actions.move({ origin: this.webElement, x, y }).perform();
                await actions.click().perform();
            },
            "Error while drag and drop element to: x=" + x + " y=" + y
        );
    }

    public async sendKeys(keys: string): Promise<void> {
        return await ErrorProcessor.run(
            async () => {
                await this.webElement.sendKeys(keys);
            },
            "Error while sending keys " + keys
        );
    }

    public async getText(): Promise<string> {
        return await ErrorProcessor.run(
            async () => {
                return this.webElement.getText();
            },
            "Error while getting text from element."
        );
    }

    // do not use, it might break tests, regular click sometimes does not work then 
    public async clickJs(): Promise<void> {
        await ErrorProcessor.run(
            async () => {
                // there is an issue that after using js click, sometimes regular click does not work
                await this.webElement.getDriver().executeScript("arguments[0].click();", this.webElement);
            },
            "Error while clicking by JavaScript on element."
        );
    }

    public async click(): Promise<void> {
        await ErrorProcessor.run(
            async () => {
                await this.webElement.click();
            },
            "Error while clicking on element."
        );
    }

    public async offsetClick(x: number, y: number): Promise<void> {
        await ErrorProcessor.run(
            async () => {
                const actions = this.webElement.getDriver().actions();
                await actions.move({ origin: this.webElement, x, y }).perform();
                await actions.click().perform();
            },
            "Error while clicking on element by offset: x=" + x + " ,y=" + y
        );
    }

    public async offsetMove(x: number, y: number): Promise<void> {
        await ErrorProcessor.run(
            async () => {
                const actions = this.webElement.getDriver().actions();
                await actions.move({ origin: this.webElement, x, y }).perform();
            },
            "Error while moving from element by offset: x=" + x + " ,y=" + y
        );
    }

    public async scroll(): Promise<void> {
        await ErrorProcessor.run(
            async () => {
                await this.webElement.getDriver().executeScript("arguments[0].scrollIntoView(true);", this.webElement);
            },
            "Error while scrolling to element."
        );
    }

    public async getAttribute(attributeName: string): Promise<string> {
        return await ErrorProcessor.run(
            async () => {
                return await this.webElement.getAttribute(attributeName);
            },
            "Error while getting attribute: " + attributeName
        );
    }

    public async findElement(by: By): Promise<Element> {
        return await ErrorProcessor.run(
            async () => {
                return new Element(await this.webElement.findElement(by));
            },
            "Error while finding element: " + by
        );
    }

    public async findElements(by: By): Promise<Element[]> {
        return await ErrorProcessor.run(
            async () => {
                const webElements = await this.webElement.findElements(by);
                return webElements.map(webElement => new Element(webElement));
            },
            "Error while finding elements: " + by
        );
    }

    public async enterFrame(): Promise<void> {
        await ErrorProcessor.run(
            async () => {
                await this.webElement.getDriver().switchTo().frame(this.webElement);
            },
            "Error while entering element frame."
        );
    }

    public async markWithRedColor(): Promise<void> {
        await ErrorProcessor.run(
            async () => {
                await this.webElement.getDriver().executeScript("arguments[0].style.backgroundColor = '#ff0000';", this.webElement);
            },
            "Error while coloring element."
        );
    }
}
