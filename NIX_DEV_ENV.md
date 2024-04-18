## Nix-based development environment shell

### Installing

- [Install Nix](https://nixos.org/download/) using the multi-user installation. (You can use the single-user installation, but things may not work as expected)
- Enable nix-command and flakes:
  - Open the Nix config:
    - `sudo nano /etc/nix/nix.conf`
  - Add the following at the bottom of the file:
    - `experimental-features = nix-command flakes`
- Test your installation by running the Hello World Nix package:
  - Open a new shell;
  - Run the hello world Nix package:
    - `nix run 'nixpkgs#hello'`
    - You should see `Hello, world!` printed on your terminal.
- Install [devbox](https://www.jetify.com/devbox/docs/installing_devbox/).

### Running

- Start the development environment:
  - `devbox shell`
- To exit the devbox shell, terminate it with `exit` or _Ctrl+D_.

---

### Automating

The steps below are optional but can make your life easier.

#### Starting the shell automatically when navigating into the kie-tools directory

- [Install direnv](https://direnv.net/docs/installation.html);
- Remember to [hook it into your shell](https://direnv.net/docs/hook.html);
- Reload your shell (`source ~/.bashrc` or `source ~/.zshrc`, for example) or open a new one;
- `cd` back into the kie-tools directory;
- Run `direnv allow` to allow direnv to exectue;
- If `devbox` is not loaded, run `cd .` to trigger it.

#### Make VSCode use the devbox shell automatically

- Install the [devbox VSCode extension](vscode:extension/jetify-com.devbox);
- Install the [direnv VSCode extension](vscode:extension/mkhl.direnv);
- Run the steps above, if not already;
- Open your Devbox project in VSCode. Direnv extension should show a prompt notification to reload your environment.

---

### Maintaining

#### Adding new packages

- Search for them in [nixhub.io](https://www.nixhub.io/) or via `devbox search <package_name>`;
  <blockquote>
  If you can't find it there, it may have a different name or belong to a package set.

  Try searching for the package name in [search.nixos.org](https://search.nixos.org/), and then finding the equivalent package in [nixhub.io](https://www.nixhub.io/).
  </blockquote>

- Copy and run the `devbox add <package_name>@<version>` command.

#### Upgrading packages

- Run `devbox add <package_name>@<new_version>` and it should update both `devbox.json` and `devbox.lock`.

#### Learn more about [devbox](https://www.jetify.com/devbox/docs/)!
