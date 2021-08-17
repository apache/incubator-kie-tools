# How to to check my code before push

The `kogito-editors-js` components contain pre-push hooks to automatically:

- **Format the code** - we use `prettier` to format whole `kogito-editors-js` codebase.

- **Do the static code analysis** - we use `eslint` to check possible issues in `js`, `ts` and `tsx` files. Thank to `lint-staged` we check just files changed as part of current commit.

The hooks are stored in `kogito-editors-js/.husky` folder and should be ready for use once `yarn install` or `mvn clean install` was started for `kogito-editors-js` module.

For more details about hooks see this [link](https://typicode.github.io/husky/#/).
