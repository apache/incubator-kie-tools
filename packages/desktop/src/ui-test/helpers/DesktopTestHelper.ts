import { BasicAppSettings, Application } from "spectron";
import { DESKTOP_APP_TITLE } from "../utils/DesktopConstants";
import * as path from 'path';

/**
 * Test helper for desktop applications.
 * 
 * Offers basic operations with the application you want to test.
 * It allows you to start and stop the applications, switch to an <iframe> within.
 * Offers functions to verify basic properties of application.
 * 
 * Offers functions that peform actions on DMN editor page. Each function
 * also verifies the action completed with desired result
 */
export default class DesktopTestHelper {

    private applicationOptions: BasicAppSettings = {
        path: this.getElectronPath(),
        args: [path.join(__dirname, '..', '..', '..')],
        startTimeout: 30000,
        chromeDriverArgs: ['--no-sandbox','--disable-dev-shm-usage', '--headless', '--remote-debugging-port=9515'],
        chromeDriverLogPath: path.join(__dirname, '..', '..', '..', 'chrome-logs.txt')
    }

    private testedApplication: Application;

    public startApplication = async (): Promise<Application> => {
        this.testedApplication = new Application(this.applicationOptions);
        await this.testedApplication.start()
        expect(this.testedApplication.isRunning()).toBeTruthy();
        return this.testedApplication;
    }

    public stopApplication = async (): Promise<void> => {
        if (this.testedApplication && this.testedApplication.isRunning()) {
            await this.testedApplication.stop()
        };
        expect(this.testedApplication.isRunning()).toBeFalsy();
        return;
    }

    /**
     * Checks basic dimensions of provided application.
     * Verifies that it has one window, is not minimized, is visible and is focused
     * Verifies bounds are above 0 and checks title is correct.
     * 
     * @param testedApplication application that is being tested
     */
    public checkBasicProperties = async (): Promise<void> => {
        const windowCount = await this.testedApplication.client.getWindowCount();
        expect(windowCount).toEqual(1);

        this.testedApplication.browserWindow.focus()
        
        expect(await this.testedApplication.browserWindow.isMinimized()).toBeFalsy();
        expect(this.testedApplication.browserWindow.isVisible()).toBeTruthy();
        expect(this.testedApplication.browserWindow.isFocused()).toBeTruthy();

        const bounds = await this.testedApplication.browserWindow.getBounds();
        expect(bounds.width).toBeGreaterThan(0);
        expect(bounds.height).toBeGreaterThan(0);

        expect(await this.testedApplication.client.getTitle()).toEqual(DESKTOP_APP_TITLE);
    }

    /**
     * Switches to frame that matches given ID within provided application.
     * 
     * @param testedApplication application that is being tested
     * @param frameId id of the frame you want to focus
     */
    public switchToFrame = async (frameId: string): Promise<void> => {
      expect(await this.testedApplication.client.waitForExist("#" + frameId)).toBeTruthy();
      this.testedApplication.client.frame(frameId);
    }

    /**
     * Switches back to parent frame. Use after you done the work
     * needed in frame that was switched to and want to locate element
     * that are present in main browser context e.g. navbar
     * 
     */
    public switchToParentFrame = async (): Promise<void> => {
        this.testedApplication.client.frameParent();
    }

    private getElectronPath() {
        let electronPath = path.join(__dirname, '..', '..', '..', 'node_modules', '.bin', 'electron');
        if (process.platform === 'win32') {
            electronPath += '.cmd'
        }
        return electronPath;
    }
}