import Tools from "../utils/Tools";

export default abstract class Page {

    protected readonly tools: Tools;

    public constructor(tools: Tools) {
        this.tools = tools;
    }

    public abstract async waitUntilLoaded(): Promise<void>;

    public async scrollToTop(): Promise<void> {
        await this.tools.window().scrollToTop();
    }

    public static async create<T extends Page>(type: new (tools: Tools) => T, tools: Tools): Promise<T> {
        const page: T = new type(tools);
        await page.waitUntilLoaded();
        return page;
    }
}
