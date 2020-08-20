import { Application } from "spectron";
import { DmnEditorPageSelector } from "../utils/DmnEditorPageSelector";


/**
 * Test helper for DMN editor inside desktop application.
 * 
 * Use the contructor to provide a live instance of application
 * you want to test. Use with {@link DesktopTestHelper} to start get
 * the application.
 * 
 * Offers functions that peform actions on DMN editor page. Each function
 * also verifies the action completed with desired result
 */
export default class DmnEditorTestHelper {

    /**
     * Application to use the editor tester with.
     */
    private testedApplication: Application;

    /**
     * Selectors for editor page
     */
    private editorPage = new DmnEditorPageSelector();
    
    constructor(application: Application) {
        this.testedApplication = application;
    }

    /**
     * Waits until the editor is loaded.
     * Fails if the loading screen is still visible after 10 seconds
     * 
     * @param testedApplication 
     */
    public waitUntilEditorLoaded = async (): Promise<void> => {
        await this.testedApplication.client.waitUntil(
            async () => await this.testedApplication.client.element(this.editorPage.diagramLoadingScreen()).isVisible() === false,
            10000,
            "Editor didn't load in 10 seconds.");
    }

    /**
     * Opens diagram properties and verifies it is openned by checking 
     * properties panel title is visible.
     * Fails if it can't locate the tile after 2 seconds.
     * 
     * @param testedApplication 
     */
    public openDiagramProperties = async (): Promise<void> => {
        this.testedApplication.client.click(this.editorPage.diagramPropertiesLocator());
        await this.testedApplication.client.waitUntil(
            async () => await this.testedApplication.client.element(this.editorPage.diagramPropertiesTitle()).isVisible() === true,
            2000,
            "Properties panel didn't load in 2 seconds."
        )
    }

    /**
     * Opens diagram explorer and verifies it is openned by checking 
     * explorer panel title is visible.
     * Fails if it can't locate the tile after 2 seconds.
     * 
     * @param testedApplication 
     */
    public openDiagramExplorer = async (): Promise<void> => {
        this.testedApplication.client.click(this.editorPage.diagramExplorerLocator());
        await this.testedApplication.client.waitUntil(
            async () => await this.testedApplication.client.element(this.editorPage.diagramExplorerTitle()).isVisible() === true,
            2000,
            "Explorer panel didn't load in 2 seconds."
        )
    }
}