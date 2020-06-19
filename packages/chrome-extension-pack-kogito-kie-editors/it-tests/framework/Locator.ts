import { By, WebDriver, WebElement } from "selenium-webdriver";
import Element from "./Element";
import ErrorProcessor from "../utils/tools/ErrorProcessor";
import LocatorWaitAction from "./LocatorWaitAction";

export default class Locator {

    private readonly driver: WebDriver
    private readonly by: By;

    constructor(driver: WebDriver, by: By) {
        this.driver = driver;
        this.by = by;
    }

    public wait(timeout?: number): LocatorWaitAction {
        return new LocatorWaitAction(this.driver, this.by, timeout);
    }

    public async getElements(): Promise<Element[]> {
        return await ErrorProcessor.run(
            async () => {
                const webElements: WebElement[] = await this.driver.findElements(this.by);
                return webElements.map(webElement => new Element(webElement));
            },
            "Error while getting elements: " + this.by
        );
    }

    public async getElement(): Promise<Element> {
        return await ErrorProcessor.run(
            async () => {
                const webElement: WebElement = await this.driver.findElement(this.by);
                return new Element(webElement);
            },
            "Error while getting element: " + this.by);
    }
}
