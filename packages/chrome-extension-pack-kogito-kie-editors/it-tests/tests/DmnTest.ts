import DecisionNavigator from "../framework/editor/dmn/DecisionNavigator";
import DmnEditor from "../framework/editor/dmn/DmnEditor";
import DmnSideBar from "../framework/editor/dmn/DmnSideBar";
import GitHubEditorPage from "../framework/github-editor/GitHubEditorPage";
import GitHubListItem from "../framework/github-file-list/GitHubListItem";
import GitHubListPage from "../framework/github-file-list/GitHubListPage";
import Properties from "../framework/editor/Properties";
import Tools from "../utils/Tools";

const TEST_NAME = "DmnTest";

let tools: Tools;

beforeEach(async () => {
    tools = await Tools.init(TEST_NAME);
});

test(TEST_NAME, async () => {
    const WEB_PAGE = "https://github.com/kiegroup/kogito-examples/blob/stable/dmn-quarkus-example/src/main/resources";
    const EXPECTED_LINK = "kiegroup/kogito-examples/stable/dmn-quarkus-example/src/main/resources/Traffic%20Violation.dmn";
    const DMN_NAME = "Traffic Violation";
    const FILE_NAME = DMN_NAME + ".dmn";
    const DRIVER_NODE_NAME = "Driver";

    // check link to online editor in the list
    const gitHubListPage: GitHubListPage = await tools.openPage(GitHubListPage, WEB_PAGE);
    const gitHubFile: GitHubListItem = await gitHubListPage.getFile(FILE_NAME);
    const linkText: string = await gitHubFile.getLinkToOnlineEditor();
    expect(linkText).toContain(EXPECTED_LINK);

    // open DMN editor
    const editorPage: GitHubEditorPage = await gitHubFile.open();
    const dmnEditor: DmnEditor = await editorPage.getDmnEditor();

    // move annotation to canvas
    await dmnEditor.enter();
    await dmnEditor.dragAndDropAnnotationToCanvas();

    // check dmn properties
    const sideBar: DmnSideBar = await dmnEditor.getSideBar();
    const processProps: Properties = await sideBar.openProperties();
    expect(await processProps.getDmnNameFromInput()).toEqual(DMN_NAME);

    //check DMN nodes in navigator
    const decisionNavigator: DecisionNavigator = await sideBar.openDecisionNavigator();
    expect((await decisionNavigator.getNodeNames()).sort())
        .toEqual(["Driver", "Fine", "Decision Table", "Should the driver be suspended?", "Context", "Violation", "TextAnnotation-1"].sort());
    expect(await decisionNavigator.getDmnName()).toEqual(DMN_NAME);

    // check Driver node properties
    await decisionNavigator.selectNode(DRIVER_NODE_NAME);
    const nodeProps: Properties = await sideBar.openProperties();
    expect(await nodeProps.getDmnNameFromInput()).toEqual(DRIVER_NODE_NAME);

    await dmnEditor.leave();

    // open and check source/editor
    expect(await editorPage.isSourceVisible()).toBe(false);
    expect(await editorPage.isEditorVisible()).toBe(true);
    await editorPage.seeAsSource();
    expect(await editorPage.isSourceVisible()).toBe(true);
    expect(await editorPage.isEditorVisible()).toBe(false);
    await editorPage.seeAsDiagram();
    expect(await editorPage.isSourceVisible()).toBe(false);
    expect(await editorPage.isEditorVisible()).toBe(true);

    // check link to online editor from clipboard
    await editorPage.copyLinkToOnlineEditor();
    const clipboadText: string = await tools.clipboard().getContent();
    expect(clipboadText).toContain(EXPECTED_LINK);
});

afterEach(async () => {
    await tools.finishTest();
});
