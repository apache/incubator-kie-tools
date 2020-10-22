import BpmnEditor from "../framework/editor/bpmn/BpmnEditor";
import Explorer from "../framework/editor/Explorer";
import GitHubPrPage from "../framework/github-pr/GitHubPrPage";
import SideBar from "../framework/editor/SideBar";
import Tools from "../utils/Tools";

const TEST_NAME = "BpmnPrTest";

let tools: Tools;

beforeEach(async () => {
    tools = await Tools.init(TEST_NAME);
});

test.skip(TEST_NAME, async () => {
    // TODO create PR in kiegroup
    const PR_WEB_PAGE = "https://github.com/tomasdavidorg/chrome-extension-pr-test/pull/2/files";

    // open PR and check that source is opened
    const gitHubPrPage: GitHubPrPage = await tools.openPage(GitHubPrPage, PR_WEB_PAGE);
    expect(await gitHubPrPage.isSourceOpened()).toBe(true);
    expect(await gitHubPrPage.isDiagramOpened()).toBe(false);

    // open diagram and check
    await gitHubPrPage.seeAsDiagram();
    expect(await gitHubPrPage.isSourceOpened()).toBe(false);
    expect(await gitHubPrPage.isDiagramOpened()).toBe(true);

    // check editor with changes
    const changesEditor: BpmnEditor = await gitHubPrPage.getBpmnEditor();
    await gitHubPrPage.scrollToPrHeader();
    await changesEditor.enter();
    const sideBar: SideBar = await changesEditor.getSideBar();
    const exlorer: Explorer = await sideBar.openExplorer();
    expect((await exlorer.getNodeNames()).sort()).toEqual(["Start", "Task", "End", "Intermediate Timer"].sort());
    await changesEditor.leave();

    // check editor with original
    await gitHubPrPage.original();
    const originalEditor: BpmnEditor = await gitHubPrPage.getBpmnEditor();
    await gitHubPrPage.scrollToPrHeader();
    await originalEditor.enter();
    const originalSideBar: SideBar = await originalEditor.getSideBar();
    const originalExlorer: Explorer = await originalSideBar.openExplorer();
    expect((await originalExlorer.getNodeNames()).sort()).toEqual(["Start", "Task", "End"].sort());
    await originalEditor.leave();

    // close diagram and check that source is opened 
    await gitHubPrPage.closeDiagram();
    expect(await gitHubPrPage.isSourceOpened()).toBe(true);
    expect(await gitHubPrPage.isDiagramOpened()).toBe(false);
});

afterEach(async () => {
    await tools.finishTest();
});
