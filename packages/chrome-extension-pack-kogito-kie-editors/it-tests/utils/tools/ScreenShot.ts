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
        const image: string = await ErrorProcessor.run(
            async () => {
                return await this.driver.takeScreenshot();
            },
            "Error while taking png screenshot with name: " + fileName
        );
        const pngPath = join(this.screenshotsDir, fileName + ".png");
        writeFileSync(pngPath, image, "base64");
    }

    public async takeHtml(fileName: string): Promise<void> {
        const pageSource: string = await ErrorProcessor.run(
            async () => {
                return await this.driver.getPageSource();
            },
            "Error while getting page source with name: " + fileName
        );
        const htmlPath = join(this.screenshotsDir, fileName + ".html");
        writeFileSync(htmlPath, pageSource, "utf8");
    }
}
