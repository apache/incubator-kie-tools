import { existsSync, mkdirSync, writeFileSync } from "fs";
import ErrorProcessor from "./ErrorProcessor";
import { WebDriver } from "selenium-webdriver";
import { join } from "path";

export default class Screenshots {

    private readonly driver: WebDriver;
    private readonly screenshotsDir: string;

    constructor(driver: WebDriver, screenshotsDir: string) {
        this.driver = driver;
        this.screenshotsDir = screenshotsDir;

        if (!existsSync(screenshotsDir)) {
            mkdirSync(screenshotsDir);
        }
    }

    public async takePng(fileName: string): Promise<void> {
        const pngPath = join(this.screenshotsDir, fileName + ".png");
        await ErrorProcessor.run(
            async () => {
                await this.driver.takeScreenshot().then((image) => {
                    writeFileSync(pngPath, image, "base64");
                });
            },
            "Error while taking png screenshot."
        );
    }

    public async takeHtml(fileName: string): Promise<void> {
        const pageSource = await ErrorProcessor.run(
            async () => {
                return await this.driver.getPageSource();
            },
            "Error while getting page source."
        );
        const htmlPath = join(this.screenshotsDir, fileName + ".html");
        writeFileSync(htmlPath, pageSource, "utf8");
    }
}
