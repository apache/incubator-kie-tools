import BpmnEditor from "../framework/editor/bpmn/BpmnEditor";
import Explorer from "../framework/editor/Explorer";
import GitHubEditorPage from "../framework/github-editor/GitHubEditorPage";
import GitHubListItem from "../framework/github-file-list/GitHubListItem";
import GitHubListPage from "../framework/github-file-list/GitHubListPage";
import Properties from "../framework/editor/Properties";
import SideBar from "../framework/editor/SideBar";
import Tools from "../utils/Tools";

const TEST_NAME = "BpmnTest";

let tools: Tools;

beforeEach(async () => {
    tools = await Tools.init(TEST_NAME);
});

test(TEST_NAME, async () => {
    const WEB_PAGE = "https://github.com/kiegroup/kogito-examples/blob/stable/process-business-rules-quarkus/src/main/resources/org/acme/travels/";
    const EXPECTED_LINK = "kiegroup/kogito-examples/stable/process-business-rules-quarkus/src/main/resources/org/acme/travels/persons.bpmn";
    const PROCESS_NAME = "persons";
    const FILE_NAME = PROCESS_NAME + ".bpmn";
    const EVALUATE_NODE_NAME = "Evaluate Person";

    // check link to online editor in the list
    const gitHubListPage: GitHubListPage = await tools.openPage(GitHubListPage, WEB_PAGE);
    const gitHubFile: GitHubListItem = await gitHubListPage.getFile(FILE_NAME);
    const linkText: string = await gitHubFile.getLinkToOnlineEditor();
    expect(linkText).toContain(EXPECTED_LINK);

    // open BPMN editor
    const editorPage: GitHubEditorPage = await gitHubFile.open();
    const bpmnEditor: BpmnEditor = await editorPage.getBpmnEditor();

    // move startEvent to canvas
    await bpmnEditor.enter();
    await bpmnEditor.dragAndDropStartEventToCanvas();

    // check process properties
    const sideBar: SideBar = await bpmnEditor.getSideBar();
    const processProps: Properties = await sideBar.openProperties();
    expect(await processProps.getProcessNameFromInput()).toEqual(PROCESS_NAME);

    // check process nodes in explorer
    const explorer: Explorer = await sideBar.openExplorer();
    expect((await explorer.getNodeNames()).sort())
        .toEqual(["StartProcess", "End Event 1", "End Event 2", "Evaluate Person", "Exclusive Gateway 1", "Special handling for children", "Start"].sort());
    expect(await explorer.getProcessName()).toEqual(PROCESS_NAME);

    // check task properties
    await explorer.selectNode(EVALUATE_NODE_NAME);
    const nodeProps: Properties = await sideBar.openProperties();
    expect(await nodeProps.getNameFromTextArea()).toEqual(EVALUATE_NODE_NAME);

    await bpmnEditor.leave();

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
