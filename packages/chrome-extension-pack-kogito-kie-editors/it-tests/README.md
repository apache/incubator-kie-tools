# Kogito Google Chrome extension selenium tests
Integration tests for Kogito Chrome extrension. Uses selenium to test the Kogito plugin in Chrome browser.

## Installation
1. Install yarn

```
sudo dnf install yarnpkg -y
```

2. Install project dependencies

###
```
yarn install
```

3. Add chrome driver to path

Download chromedriver from https://chromedriver.chromium.org/downloads.
```
unzip chromedriver_linux64.zip
export PATH=/path/to/chromedriver:$PATH
```

4. Run tests

Tests need UNZIPPED_CHROME_EXTENSION_PATH environment variable to be set to directory with unzipped Chome Extension.
```
unzip chrome_extension_kogito_kie_editors_0.X.Y.zip
export UNZIPPED_CHROME_EXTENSION_PATH=dist 
yarn run tests
```

## Repository strucure

| File / directory     | Description                                     |
| -------------------- | ----------------------------------------------- |
| `test/`              | Tests and test framework                        |
| `.gitignore`         | List of files ignored by git                    |
| `README.md`          | This README file                                |
| `package.json`       | Main declaration file                           |
| `tsconfig.json`      | TypeScript configuration file                   |

## Basic test structure
```typescript
import Tools from "./utils/Tools";

const TEST_NAME = "SimpleTest"

let tools: Tools;

beforeEach(async () => {
    tools = await Tools.init(TEST_NAME);
});

test(TEST_NAME, async () => {

    // ...

    await tools.openPage(PageClass, "url");

    // ...

});

afterEach(async () => {
    await tools.finishTest();
});
```

## Framework

- `Tools` class shoud be inicialized before every tests by `let tools = await Tools.init()`. It creates selenium driver and it servers as basic entry point for the framework. 
After every test `finishTest()` method should be called to quit the driver and create screenshots.
- `Page` class represents whole browser page. Page can be created by `await tools.openPage(PageClass, url)` or `await tools.createPage(PageClass)`.
- `PageFragment` class represent part of Page defined by root `Element`. PageFragment can be created by `await tools.createPageFragment(PageFragmentClass, rootElement)`.
- `Element` class represents element on Page. Element can be created by:
  - Find by locator: `await tools.by(locatorBy).getElement()`
  - Waiting for element: `await tools.by(locatorBy).wait(1000).untilPresent()`
  - Using parent element: `const parent: Element = await tools.by(parentLocatorBy).getElement(); await parent.find(childLocatorBy)`
