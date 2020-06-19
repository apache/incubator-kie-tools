import { By, WebDriver, error, until } from "selenium-webdriver";
import Element from "./Element";
import ErrorProcessor from "../utils/tools/ErrorProcessor";

export default class LocatorWaitAction {

    private static readonly DEFAULT_TIMEOUT: number = 100;

    private readonly driver: WebDriver;
    private readonly by: By;
    private readonly timeout: number;

    public constructor(driver: WebDriver, by: By, timeout?: number) {
        this.driver = driver;
        this.by = by;
        this.timeout = timeout !== undefined ? timeout : LocatorWaitAction.DEFAULT_TIMEOUT;
    }

    private async absent(): Promise<void> {
        await this.driver.wait(async () => (await this.driver.findElements(this.by)).length === 0, this.timeout);
    }

    public async untilAbsent(): Promise<void> {
        await ErrorProcessor.run(
            async () => {
                await this.absent();
            },
            "Error while waiting until absent " + this.by
        );
    }

    public async isAbsent(): Promise<boolean> {
        try {
            await this.absent();
            return true;
        } catch (err) {
            if (err instanceof error.TimeoutError) {
                return false;
            } else {
                throw err;
            }
        }
    }

    private async present(): Promise<Element> {
        return new Element(await this.driver.wait(until.elementLocated(this.by), this.timeout));
    }

    public async untilPresent(): Promise<Element> {
        return await ErrorProcessor.run(
            async () => await this.present(),
            "Error while waiting until present: " + this.by
        );
    }

    public async isPresent(): Promise<boolean> {
        try {
            await this.present();
            return true;
        } catch (err) {
            if (err instanceof error.TimeoutError) {
                return false;
            } else {
                throw err;
            }
        }
    }

    private async visible(): Promise<void> {
        const webElement = await this.driver.findElement(this.by);
        await this.driver.wait(until.elementIsVisible(webElement), this.timeout);
    }

    public async untilVisible(): Promise<void> {
        await ErrorProcessor.run(
            async () => {
                await this.visible();
            },
            "Error while waiting until visible: " + this.by
        );
    }

    public async isVisible(): Promise<boolean> {
        try {
            await this.visible();
            return true;
        } catch (err) {
            if (err instanceof error.TimeoutError) {
                return false;
            } else {
                throw err;
            }
        }
    }

    private async value(): Promise<string> {
        const webElement = await this.driver.findElement(this.by);
        await this.driver.wait(async () => (await webElement.getAttribute("value")) !== "", this.timeout);
        return await webElement.getAttribute("value");
    }

    public async untilHasValue(): Promise<string> {
        return await ErrorProcessor.run(
            async () => {
                return await this.value();
            },
            "Error while waiting until has value: " + this.by
        );
    }

    public async hasValue(): Promise<boolean> {
        try {
            await this.value();
            return true;
        } catch (err) {
            if (err instanceof error.TimeoutError) {
                return false;
            } else {
                throw err;
            }
        }
    }

    private async enabled(): Promise<Element> {
        const webElement = await this.driver.findElement(this.by);
        return new Element(await this.driver.wait(until.elementIsEnabled(webElement), this.timeout));
    }

    public async untilEnabled(): Promise<Element> {
        return await ErrorProcessor.run(
            async () => {
                return await this.enabled();
            },
            "Error while waiting until enabled: " + this.by
        );
    }
}
