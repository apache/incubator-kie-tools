import Element from "./Element";
import Tools from "../utils/Tools";

export default abstract class PageFragment {

    protected readonly tools: Tools;
    protected readonly root: Element;

    public constructor(tools: Tools, root: Element) {
        this.tools = tools;
        this.root = root;
    }

    public abstract async waitUntilLoaded(): Promise<void>;

    public static async create<T extends PageFragment>(type: new (tools: Tools, root: Element) => T, tools: Tools, root: Element): Promise<T> {
        const pageFragment: T = new type(tools, root);
        await pageFragment.waitUntilLoaded();
        return pageFragment;
    }
}
