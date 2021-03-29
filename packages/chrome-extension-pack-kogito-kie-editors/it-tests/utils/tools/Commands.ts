import { By } from "selenium-webdriver";
import Element from "./Element";
import Tools from "../Tools";

export default class Commands {
  constructor(private readonly tools: Tools) {}

  public async getEditor(): Promise<Element> {
    const byIframeLocator = By.id("kogito-iframe");
    await this.tools
      .find(byIframeLocator)
      .wait(5000)
      .untilPresent();
    const iframe = await this.tools.find(byIframeLocator).getElement();
    await iframe.scroll();
    await iframe.enterFrame();
    return iframe;
  }

  public async loadEditor(): Promise<void> {
    const byLoadingDialogLocator = By.className("pf-l-bullseye");
    if (
      await this.tools
        .find(byLoadingDialogLocator)
        .wait(15000)
        .isPresent()
    ) {
      await this.tools
        .find(byLoadingDialogLocator)
        .wait(60000)
        .untilAbsent();
    }
  }

  public async checkSourceVisible(isSourceVisible: boolean): Promise<void> {
    expect(
      await this.tools
        .find(By.id("kogito-iframe"))
        .wait(1000)
        .isVisible()
    ).toEqual(!isSourceVisible);
    expect(
      await this.tools
        .find(By.css("[class~='js-file-content'],[itemprop='text']"))
        .wait(1000)
        .isVisible()
    ).toEqual(isSourceVisible);
  }

  public async testSampleBpmnInEditor(): Promise<void> {
    // open properties panel
    const propertiesButton = await this.tools
      .find(By.css("[data-ouia-component-id='docks-item-DiagramEditorPropertiesScreen']"))
      .getElement();
    await propertiesButton.click();

    // check process name
    const processNameInput = await this.tools.find(By.css("input[name$='.diagramSet.name']")).getElement();
    expect(await processNameInput.getAttribute("value")).toEqual("myProcess");

    // open explorer panel
    const explorerDiagramButton = await this.tools
      .find(By.css("[data-ouia-component-id='docks-item-ProjectDiagramExplorerScreen']"))
      .getElement();
    await explorerDiagramButton.click();

    // check node names
    const nodes = await this.tools.find(By.css("[data-ouia-component-type='tree-item'] a")).getElements();
    expect(await Promise.all(nodes.map(async n => await n.getText()))).toEqual([
      "myProcess",
      "MyStart",
      "MyTask",
      "MyEnd"
    ]);

    // click on task node
    const myTaskNode = await this.tools.find(By.css("[data-ouia-component-id='tree-item-MyTask']")).getElement();
    await myTaskNode.click();

    // check task node name
    await propertiesButton.click();
    const nodePropName = await this.tools.find(By.css("textarea[name$='.general.name']")).getElement();
    expect(await nodePropName.getAttribute("value")).toEqual("MyTask");
  }

  public async testSampleDmnInEditor(): Promise<void> {
    // open properties side bar
    const propertiesButton = await this.tools.find(By.css("[data-title='Properties']")).getElement();
    await propertiesButton.click();

    // check dmn name
    const dmnNameInput = await this.tools.find(By.css("[name$='.definitions.nameHolder']")).getElement();
    expect(await dmnNameInput.getAttribute("value")).toEqual("myDmn");

    // open decision navigator
    await propertiesButton.click();
    const decisionNavigatorButton = await this.tools
      .find(By.css("[data-ouia-component-id='docks-item-org.kie.dmn.decision.navigator']"))
      .getElement();
    await decisionNavigatorButton.click();

    // check dmn nodes
    const nodes = await this.tools
      .find(By.css("[data-i18n-prefix='DecisionNavigatorTreeView.'] > div > span[data-field='text-content']"))
      .getElements();
    expect(await Promise.all(nodes.map(async n => n.getText()))).toEqual([
      "myDmn",
      "MyDecision",
      "MyInputData",
      "MyModel",
      "Function"
    ]);

    // select input data node
    const inputDataItem = await this.tools.find(By.css("[title='MyInputData'] > div")).getElement();
    await inputDataItem.click();

    // switch to properties
    await propertiesButton.click();

    // check node name
    const intputDataNodeName = await this.tools.find(By.css("[name$='.nameHolder']")).getElement();
    expect(await intputDataNodeName.getAttribute("value")).toEqual("MyInputData");
  }
}
