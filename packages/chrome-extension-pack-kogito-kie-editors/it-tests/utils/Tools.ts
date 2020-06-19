import { By, WebDriver } from "selenium-webdriver";
import Clipboard from "./tools/Clipboard";
import Driver from "./tools/Driver";
import Element from "../framework/Element";
import Locator from "../framework/Locator";
import Page from "../framework/Page";
import PageFragment from "../framework/PageFragment";
import Screenshot from "./tools/ScreenShot";
import Window from "./tools/Window";

export default class Tools {

    private static readonly SCREENSHOTS_DIR: string = "screenshots";

    private readonly driver: WebDriver;

    private readonly screenShot: Screenshot;

    private readonly testName: string;

    private constructor(driver: WebDriver, testName: string) {
        this.driver = driver;
        this.screenShot = new Screenshot(this.driver, Tools.SCREENSHOTS_DIR);
        this.testName = testName;
    }

    public async finishTest(): Promise<void> {
        const screenshotName: string = "screenshot_after_" + this.testName;
        await this.window().leaveFrame();
        await this.makeScreenshots(screenshotName);
        await Driver.quit(this.driver);
    }

    public async makeScreenshots(screenshotNamePrefix: string): Promise<void> {
        await this.screenShot.takeHtml(screenshotNamePrefix);
        await this.screenShot.takePng(screenshotNamePrefix);
    }

    public pause(timeout: number): Promise<void> {
        return new Promise(resolve => setTimeout(resolve, timeout));
    }

    public by(by: By): Locator {
        return new Locator(this.driver, by);
    }

    public clipboard(): Clipboard {
        return new Clipboard(this.driver);
    }

    public window(): Window {
        return new Window(this.driver);
    }

    public async openPage<T extends Page>(type: new (tools: Tools) => T, url: string): Promise<T> {
        await Driver.openUrl(this.driver, url);
        return await this.createPage(type);
    }

    public async createPage<T extends Page>(type: new (tools: Tools) => T): Promise<T> {
        return Page.create(type, this);
    }

    public async createPageFragment<T extends PageFragment>(type: new (tools: Tools, root: Element) => T, root: Element): Promise<T> {
        return PageFragment.create(type, this, root);
    }

    public static async init(testName: string): Promise<Tools> {
        return new Tools(await Driver.init(), testName);
    }
}
