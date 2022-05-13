# Integration tests

There is a set of cypress tests in `it-tests` folder. To run them, please refer to one option bellow.

## Run tests manually

More suitable for running particular tests from `it-tests`.

- `packages/extended-services$ pnpm start`
- `packages/online-editor$ pnpm start`
- `packages/online-editor$ pnpm cy:open`

## Run tests automatically

More suitable for running `it-tests` completely.

- `packages/online-editor$ START_SERVER_AND_TEST_INSECURE=true KOGITO_TOOLING_BUILD_testIT=true pnpm test:it`

> **NOTE:**
> Before test development, you may need to build `online-editor` as:
>
> - `kie-tools$ pnpm bootstrap`
> - `kie-tools$ pnpm -r -F @kie-tools/online-editor... build:dev`
