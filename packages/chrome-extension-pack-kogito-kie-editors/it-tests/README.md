# Kogito Google Chrome extension selenium tests

Integration tests for Kogito Chrome extrension. Uses selenium to test the Kogito plugin in Chrome browser.

## Installation

1. Set `UNZIPPED_CHROME_EXTENSION_PATH` variable

Tests need `UNZIPPED_CHROME_EXTENSION_PATH` environment variable to be set to directory with unzipped Chome Extension.
If the variable is not set, `chrome-extension-pack-kogito-kie-editors/dist` is used.

```bash
unzip chrome_extension_kogito_kie_editors_X.Y.Z.zip
export UNZIPPED_CHROME_EXTENSION_PATH=/path/to/unzipped/dist
```

2. Run the tests

Use `yarn run test:it` command to run the tests.

## File structure

| File / directory | Description                                |
| ---------------- | ------------------------------------------ |
| `samples/`       | Test BPMN and DMN files.                   |
| `tests/`         | Test directory contains only test classes. |
| `utils/`         | Test utils contains other testing tools.   |
| `README.md`      | This README file                           |

## Basic test structure

```typescript
import Tools from "../utils/Tools";

const TEST_NAME = "SimpleTest";

let tools: Tools;

beforeEach(async () => {
  tools = await Tools.init(TEST_NAME);
});

test(TEST_NAME, async () => {
  // ...

  await tools.open(url);

  // ...
});

afterEach(async () => {
  await tools.finishTest();
});
```

## Framework

- `Tools` class shoud be inicialized before every test by `let tools = await Tools.init(TEST_NAME)`. It creates selenium driver and it serves as basic entry point for the test. After every test `finishTest()` method should be called to quit the driver and create screenshots.
- `Element` class represents element on Page. Element can be created by:
  - Find by locator: `await tools.find(locatorBy).getElement()`
  - Waiting for element: `await tools.find(locatorBy).wait(1000).untilPresent()`
  - Using parent element: `const parent: Element = await tools.find(parentLocatorBy).getElement(); await parent.findElement(childLocatorBy)`
