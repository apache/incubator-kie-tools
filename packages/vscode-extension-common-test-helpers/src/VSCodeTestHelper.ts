/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { assert, expect } from "chai";
import * as path from "path";
import * as fs from "fs-extra";
import { sanitize } from "sanitize-filename-ts";
import { Key } from "selenium-webdriver";
import {
  ActivityBar,
  By,
  EditorGroup,
  EditorView,
  InputBox,
  ModalDialog,
  SideBarView,
  TextEditor,
  until,
  ViewControl,
  ViewItem,
  ViewSection,
  VSBrowser,
  WebDriver,
  WebView,
  Workbench,
} from "vscode-extension-tester";
import {
  webViewReady,
  activeFrame,
  envelopeApp,
  kogitoLoadingSpinner,
  inputBox,
  explorerFolder,
} from "./CommonLocators";
import { isKieEditorWithDualView, isKieEditorWithSingleView, isDashbuilderEditor } from "./KieFileExtensions";

/**
 * Common test helper class for VSCode extension testing.
 * Provides common API to work with VSCode test instance.
 * Allows you  to open folders, files, close open editor.
 * Aquire notifications, input CLI commands etc.
 */
export class VSCodeTestHelper {
  /**
   * Handle for VSCode workbench.
   * Initialized in constructor.
   */
  private workbench: Workbench;

  /**
   * Instance of VSBrowser.
   */
  private browser: VSBrowser;

  /**
   * WebDriver of currently used VSBrowser.
   */
  private driver: WebDriver;

  /**
   * Handle for workspace section with directory tree and files that
   * is located in sidebarview. Used to lookup resources and open them.
   */
  private workspaceSectionView: ViewSection;

  /**
   * Handle for sidebarview. Usually used to work with
   * open folder.
   * When you open folder sidebarview is initialized.
   */
  private sidebarView: SideBarView;

  /**
   * Loading timeout for editors.
   */
  private readonly EDITOR_LOADING_TIMEOUT: number = 60000;

  constructor() {
    this.workbench = new Workbench() as Workbench;
    this.browser = VSBrowser.instance;
    this.driver = this.browser.driver;
  }

  /**
   * Opens folder using commmand suplied by vscode-extension-tester
   * and open the dedicated SideBarView with the folder.
   *
   * @param absolutePath absolute path to the folder that needs to be openned
   * @returns a promise that resolves to a SideBarView of the openned folder
   */
  public openFolder = async (absolutePath: string): Promise<SideBarView> => {
    await this.browser.openResources(absolutePath);

    const control = (await new ActivityBar().getViewControl("Explorer")) as ViewControl;
    this.sidebarView = await control.openView();
    assert.isTrue(await this.sidebarView.isDisplayed(), "Explorer side bar view was not opened");

    const folderName = absolutePath.split("/").pop();
    if (folderName == undefined) {
      throw new Error("Wrong absolutePath format: " + absolutePath);
    }

    this.workspaceSectionView = await this.sidebarView.getContent().getSection(folderName);
    return this.sidebarView;
  };

  /**
   * Opens file from a sidebarview. Expects that the sideBarView will be defined and open.
   * To define sideBarView a folder needs to be opened using openFolder function.
   * All webviews are loaded and returned in an array.
   *
   * @param fileName name of the file to open
   * @param fileParentPath optional, use when file is not in root of resources. This is the path of file's parent directory, relative to resources
   *                       if not used the file will be looked in root of resources.
   * @returns promise that resolves to an array of WebViews of the opened file.
   *          For editors with one webview the editor is at index 0.
   *          For editors with two webviews the TextEditor as WebView is at index 0 and the graphical editor as WebView is at index 1.
   *          For any other files all its WebViews are returned.
   */
  public openFileFromSidebar = async (fileName: string, fileParentPath?: string): Promise<WebView[]> => {
    if (isKieEditorWithSingleView(fileName)) {
      const webView = await this.openEditorWithOneWebView(fileName, fileParentPath);
      return [webView];
    } else if (isKieEditorWithDualView(fileName)) {
      return await this.openEditorWithTwoWebViews(fileName, fileParentPath);
    }
    return await this.openNonKieEditorFile(fileName, fileParentPath);
  };

  private openEditorWithOneWebView = async (fileName: string, fileParentPath?: string): Promise<WebView> => {
    const editorGroups = await this.openItemFromSidebar(fileName, fileParentPath);

    assert.equal(editorGroups.length, 1);

    const webview = new WebView(this.workbench.getEditorView(), By.linkText(fileName));
    await this.waitUntilKogitoEditorIsLoaded(webview);
    return webview;
  };

  private openEditorWithTwoWebViews = async (fileName: string, fileParentPath?: string): Promise<WebView[]> => {
    const editorGroups = await this.openItemFromSidebar(fileName, fileParentPath);

    assert.equal(editorGroups.length, 2);

    const webviewLeft = new WebView(editorGroups[0], By.linkText(fileName));

    if (isDashbuilderEditor(fileName)) {
      this.forceOpeningDashbuilderEditor(webviewLeft);
    }

    const webviewRight = new WebView(editorGroups[1], By.linkText(fileName));
    await this.waitUntilKogitoEditorIsLoaded(webviewRight);

    const webviews = [] as WebView[];
    webviews.push(webviewLeft);
    webviews.push(webviewRight);

    return Promise.resolve(webviews);
  };

  private openNonKieEditorFile = async (fileName: string, fileParentPath?: string): Promise<WebView[]> => {
    const editorGroups = await this.openItemFromSidebar(fileName, fileParentPath);

    const webviews = [] as WebView[];
    editorGroups.forEach((editorGroup) => {
      const webview = new WebView(editorGroup, By.linkText(fileName));
      webviews.push(webview);
    });

    return Promise.resolve(webviews);
  };

  private openItemFromSidebar = async (fileName: string, fileParentPath?: string): Promise<EditorGroup[]> => {
    if (fileParentPath == undefined || fileParentPath == "") {
      await this.workspaceSectionView.openItem(fileName);
    } else {
      const pathPieces = fileParentPath.split("/");
      await this.workspaceSectionView.openItem(...pathPieces);
      await this.waitUntilFolderStructureIsExpanded(pathPieces[0]);
      const fileItem = await this.workspaceSectionView.findItem(fileName);
      if (fileItem != undefined) {
        await fileItem.click();
      }
    }
    await sleep(5000);

    return await this.workbench.getEditorView().getEditorGroups();
  };

  private async forceOpeningDashbuilderEditor(textEditorWebView: WebView): Promise<void> {
    const webDriver = textEditorWebView.getDriver();
    const consoleHelper = await webDriver.findElement(webViewReady());
    await consoleHelper.sendKeys(Key.ENTER);
  }

  /**
   * Waits until folder structure in explorer is loaded and expanded.
   *
   * @param topLevelFolderName the name of the top level folder in the explorer
   */
  private waitUntilFolderStructureIsExpanded = async (topLevelFolderName: string): Promise<void> => {
    await this.driver.wait(
      async () => {
        const currentValue = await this.driver
          .findElement(explorerFolder(topLevelFolderName))
          .getAttribute("aria-expanded");
        return currentValue === "true";
      },
      25000,
      "Folder structure didn't expand in time. Please investigate."
    );
  };

  /**
   * Renames file in SideBarView.
   *
   * Expects SideBarView is defined and open.
   * To define SideBarView a folder needs to be opened using openFolder function.
   *
   * You can specify the relative path to root directory in fileName. For example: "/a/b/filename.sw.json".
   * You can also specify the relative path in newFileName. The path of the new file will be relative to directory where the original file resides.
   *
   * Always use "/" separator in paths.
   *
   * @param fileName name of the file with or without path to rename
   * @param newFileName new name of the file with or without path
   */
  public renameFile = async (fileName: string, newFileName: string) => {
    let fileNameToRename: string | undefined = fileName;

    if (fileName.includes("/")) {
      const pathPieces = fileName.split("/");
      fileNameToRename = pathPieces.pop();
      await this.workspaceSectionView.openItem(...pathPieces);
    }

    let fileItem: ViewItem | undefined;

    if (fileNameToRename != undefined) {
      fileItem = await this.workspaceSectionView.findItem(fileNameToRename);
    }

    const menu = await fileItem?.openContextMenu();
    await menu?.select("Rename...");

    const inputElement = await this.workspaceSectionView.findElement(inputBox());
    await inputElement.sendKeys(Key.chord(Key.CONTROL, "a"));
    await inputElement.sendKeys(newFileName);
    await inputElement.sendKeys(Key.ENTER);

    // Check the presence of renamed item
    const renamedFileName = newFileName.includes("/") ? newFileName.split("/").pop() : newFileName;
    expect(renamedFileName).to.exist;
    expect(await this.workspaceSectionView.findItem(renamedFileName as string)).to.exist;
  };

  /**
   * Closes all editor views that are open.
   * Resolves even if there are no open editor views.
   */
  public closeAllEditors = async (): Promise<void> => {
    try {
      await this.workbench.getEditorView().closeAllEditors();
    } catch (error) {
      console.log("Error while closing all editors: " + error);
      try {
        // catch the error when there is nothing to close
        // or the Save Dialog appears
        const dialog = new ModalDialog();
        if (dialog != null && (await dialog.isDisplayed())) {
          await dialog.pushButton("Don't Save");
        }
      } catch (error) {
        console.log("Error while pushButton called: " + error);
      }
    }
  };

  /**
   * Closes all notifications that can be found using {@see Workbench}.
   */
  public closeAllNotifications = async (): Promise<void> => {
    try {
      const activeNotifications = await this.workbench.getNotifications();
      for (const notification of activeNotifications) {
        await notification.dismiss();
      }
    } catch (e) {
      console.log("Error while closing all notifications: " + e);
    }
  };

  /**
   * Waits until the provided webview has fully loaded kogito editor.
   * Method will look in the webview for active iframe and switches to the
   * iframe if located.
   * After that it looks for div#envelope-app in the iframe#active-frame and if found,
   * waits for kogito editor loading spinner to not be present.
   * Returns void promise if the loading spinner disappears in timeout that is
   * set in {@see this.EDITOR_LOADING_TIMEOUT} property.
   *
   * @param webview {@see WebView} that contains the kogito editor with envelope-app
   */
  public waitUntilKogitoEditorIsLoaded = async (webview: WebView): Promise<void> => {
    const driver = webview.getDriver();

    switchWebviewToFrame(webview);

    await driver.wait(
      until.elementLocated(envelopeApp()),
      this.EDITOR_LOADING_TIMEOUT,
      "No 'div#envelope-app' located in webview's active-frame in " +
        this.EDITOR_LOADING_TIMEOUT +
        "ms. Please investigate."
    );
    await driver.wait(
      async () => {
        const loadingSpinners = await webview.getDriver().findElements(kogitoLoadingSpinner());
        return !loadingSpinners || loadingSpinners.length <= 0;
      },
      this.EDITOR_LOADING_TIMEOUT,
      "Editor was still loading after " + this.EDITOR_LOADING_TIMEOUT + "ms. Please investigate."
    );

    await sleep(8000);

    await switchBack(webview);
  };

  /**
   * Opens commands prompt and select given command there
   */
  public executeCommandFromPrompt = async (command: string): Promise<void> => {
    const inputBox = (await this.workbench.openCommandPrompt()) as InputBox;
    await inputBox.setText(`>${command}`);

    const quickPicks = await inputBox.getQuickPicks();

    for (const quickPick of quickPicks) {
      const label = await quickPick.getLabel();
      if (label === command) {
        await quickPick.select();
        await sleep(1000);
        return;
      }
    }

    throw new Error(`'${command}' not found in prompt`);
  };

  /**
   * Takes a screenshot if the current test fails and saves it in the specified directory.
   *
   * @param {Mocha.Context} testMochaContext The current Mocha test context.
   * @param {string} parentScreenshotFolder The parent directory where the screenshot will be saved.
   */
  public takeScreenshotOnTestFailure = async (
    testMochaContext: Mocha.Context,
    parentScreenshotFolder: string
  ): Promise<void> => {
    if (testMochaContext.currentTest && testMochaContext.currentTest.state !== "passed") {
      const screenshotName = testMochaContext.currentTest?.fullTitle() + " (failed)";
      const screenshotDir = path.join(parentScreenshotFolder, "screenshots");
      await this.takeScreenshotAndSave(screenshotName, screenshotDir);
    }
  };

  /**
   * Creates screenshot of current VSCode window and saves it to given path.
   *
   * @param name screenshot file name without extension
   * @param dirPath path to a folder to store screenshots (will be created if doesn't exist)
   */
  private takeScreenshotAndSave = async (name: string, dirPath: string): Promise<void> => {
    const data = await this.driver.takeScreenshot();
    fs.mkdirpSync(dirPath);
    fs.writeFileSync(path.join(dirPath, `${sanitize(name)}.png`), data, "base64");
  };

  /**
   * Saves the currently open file in the text editor.
   */
  public saveFileInTextEditor = async (): Promise<void> => {
    const textEditor = new TextEditor(this.workbench.getEditorView());
    await textEditor.save();
    await sleep(1000);
  };

  /**
   * Open VSCode settings, change the specified settings to the desired values, and close the settings editor.
   *
   * @param settings an array of objects specifying the settings to be changed
   *   - `settingValue`: the new value to set for the setting
   *   - `settingName`: the name of the setting to be changed
   *   - `settingCategories` (optional): the category or categories under which the setting is located
   * @returns an array containing the previous values of the settings (in the order they were provided)
   *   These values can be used to revert the settings afterwards.
   */
  public setVSCodeSettings = async (
    ...settings: { settingValue: string; settingName: string; settingCategories?: string[] }[]
  ): Promise<(string | boolean)[]> => {
    const previousSettingValuesArray: (string | boolean)[] = [];
    const settingsEditor = await new Workbench().openSettings();

    for (const { settingValue, settingName, settingCategories } of settings) {
      const locatedSetting = await settingsEditor.findSetting(settingName, ...(settingCategories || []));
      const previousSettingValue = await locatedSetting.getValue();
      previousSettingValuesArray.push(previousSettingValue);
      await locatedSetting.setValue(settingValue);
    }

    await new EditorView().closeEditor("Settings");
    await sleep(1000);

    return previousSettingValuesArray;
  };
}

/**
 * Switches provided webview's context to iframe#active-frame within it.
 *
 * @param webview
 */
export async function switchWebviewToFrame(webview: WebView): Promise<void> {
  const driver = webview.getDriver();
  await driver.wait(
    until.elementLocated(webViewReady()),
    10000,
    "No iframe.webview.ready that was ready was located in webview under 10 seconds." +
      "This should not happen and is most probably issue of VSCode." +
      "In case this happens investigate vscode or vscode-extension-tester dependency."
  );
  await driver.switchTo().frame(await driver.findElement(webViewReady()));
  await driver.wait(
    until.elementLocated(activeFrame()),
    10000,
    "No iframe#active-frame located in webview under 10 seconds." +
      "This should not happen and is most probably issue of VSCode." +
      "In case this happens investigate vscode or vscode-extension-tester dependency."
  );
  await driver.switchTo().frame(await driver.findElement(activeFrame()));
}

/**
 * Switch back to the the topmost frame on the page.
 *
 * @param webview
 */
export async function switchBack(webview: WebView): Promise<void> {
  await webview.getDriver().switchTo().frame(null);
}

export function sleep(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}
