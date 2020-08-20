import { Application } from "spectron";
import { HomePageSelector } from "../utils/HomePageSelector";
import { EditorPageSelector } from "../utils/EditorPageSelector";
import { UNSAVED_FILE_TITLE } from "../utils/DesktopConstants";

/**
 * Test helper for BPMN editor inside desktop application.
 * Use the contructor to provide a live instance of application
 * you want to test. Use with {@link DesktopTestHelper} to start get
 * the application.
 */
export default class HomePageTestHelper {

    /**
     * Application to use the editor tester with.
     */
    private testedApplication: Application;

    /**
     * Selectors for home page
     */
    private homePage = new HomePageSelector();

    /**
     * Selectors for editor page
     */
    private editorPage = new EditorPageSelector();
    
    constructor(application: Application) {
        this.testedApplication = application;
    }

    /**
     * Opens a editor with new BPMN process.
     * Clicks the respective button on home page.
     * 
     * Checks window count is one and that there is a header
     * with "unsaved file" visible.
     */
    public openNewBpmn = async (): Promise<void> => {
      await this.testedApplication.client.click(this.homePage.openNewBpmnDiagramButtonSelector());

      this.checkWindowCountIsOne();            
      this.checkDiagramNameHeaderIsPresent(UNSAVED_FILE_TITLE);
    }

    /**
     * Opens a editor with new DMN process.
     * Clicks the respective button on home page.
     * 
     * Checks window count is one and that there is a header
     * with "unsaved file" visible.
     */
    public openNewDmn = async (): Promise<void> => {
        await this.testedApplication.client.click(this.homePage.openNewDmnDiagramButtonSelector());
  
        this.checkWindowCountIsOne();            
        this.checkDiagramNameHeaderIsPresent(UNSAVED_FILE_TITLE);
    }

    /**
     * Opens a editor with sample BPMN process.
     * Clicks the respective button on home page.
     * 
     * Checks window count is one and that there is a header
     * with "unsaved file" visible.
     */
    public openSampleBpmn = async (): Promise<void> => {
        await this.testedApplication.client.click(this.homePage.openSampleBpmnDiagramButtonSelector());
  
        this.checkWindowCountIsOne();            
        this.checkDiagramNameHeaderIsPresent(UNSAVED_FILE_TITLE);
    }
  
    /**
     * Opens a editor with sample DMN process.
     * 
     * Checks window count is one and that there is a header
     * with "unsaved file" visible.
     */
    public openSampleDmn = async (): Promise<void> => {
        await this.testedApplication.client.click(this.homePage.openSampleDmnDiagramButtonSelector());
    
        this.checkWindowCountIsOne();            
        this.checkDiagramNameHeaderIsPresent(UNSAVED_FILE_TITLE);
    }

    private checkDiagramNameHeaderIsPresent = async (name: string): Promise<void> => {
       const diagramNameHeader = await this.testedApplication.client.getText(this.editorPage.diagramNameHeaderLocator());
       expect(diagramNameHeader).toEqual(name);
    }

    private checkWindowCountIsOne = async (): Promise<void> => {
        const count = await this.testedApplication.client.getWindowCount();
        expect(count).toEqual(1);
    }
}