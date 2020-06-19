import ErrorProcessor from "./ErrorProcessor";
import { WebDriver } from "selenium-webdriver";

export default class Window {

    private readonly driver: WebDriver;

    public constructor(driver: WebDriver) {
        this.driver = driver;
    }

    public async leaveFrame(): Promise<void> {
        await ErrorProcessor.run(
            async () => {
                await this.driver.switchTo().defaultContent();
            },
            "Error when switching to default content."
        );
    }

    public async scrollToTop(): Promise<void> {
        await ErrorProcessor.run(
            async () => {
                await this.driver.executeScript("window.scrollTo(0, 0);");
            },
            "Error when scrolling to top of page."
        );
    }

    public async switchToSecondWindow(): Promise<void> {
        await this.waitForAnotherWindow();
        const windowHandles: string[] = await this.getWindowHandles();
        if (windowHandles.length > 1) {
            await this.switchToWindow(windowHandles[1]);
        } else {
            throw new Error("Second window was not found.");
        }
    }

    private async switchToWindow(windowHandle: string): Promise<void> {
        await ErrorProcessor.run(
            async () => {
                await this.driver.switchTo().window(windowHandle);
            },
            "Error while switching to window."
        );
    }

    private async getWindowHandles(): Promise<string[]> {
        return await ErrorProcessor.run(
            async () => {
                return await this.driver.getAllWindowHandles();
            },
            "Error while getting window handles."
        );
    }

    private async waitForAnotherWindow(): Promise<void> {
        await ErrorProcessor.run(
            async () => {
                await this.driver.wait(async () => {
                    const windowHandles: string[] = await this.driver.getAllWindowHandles();
                    return (windowHandles.length > 1);
                }, 5000);
            },
            "Error when waiting for second window."
        );
    }
}
