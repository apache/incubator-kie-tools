# Integration tests

There is a set of cypress tests in `it-tests` folder. To run them, please refer to one option bellow.

## Run tests manually

More suitable for running particular tests from `it-tests`.

- `packages/extended-services$ yarn start`
- `packages/online-editor$ yarn start`
- `packages/online-editor$ yarn cy:open`

## Run tests automatically

More suitable for running `it-tests` completely.

- `packages/online-editor$ START_SERVER_AND_TEST_INSECURE=true KOGITO_TOOLING_BUILD_testIT=true yarn test:it`

> **NOTE:**
> Before test development, you may need to build `online-editor` as:
>
> - `kogito-tooling$ yarn bootstrap`
> - `kogito-tooling$ yarn build:dev:until @kogito-tooling/online-editor`
