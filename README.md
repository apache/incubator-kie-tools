# kogito-tooling-go

Go applications for Kogito Tooling. Right now we have:

- KIE Tooling Extended Service backend

## Requirements

Golang version: `1.16`

## Application Parameters

- `-p <PORT_NUMBER>`: Sets app port, otherwise it will use config.yaml port.

## Build and run

First thing to do it init the git submodule of `kogito-apps`. Run:
- `git submodule update --init --recursive`

The, to build, execute the following commands from root path. Please use the one suited for your current OS.
- `make macos`
- `make win`
- `make linux`

The binaries are going to appear in the `build` folder. To run it, execute the binaries directory or run `make run`.

## Fedora

To use this application on Fedora, it's necessary to install some additional packages and enable the Gnome App Indicator.
Firstly install the following packages:

- `sudo dnf install gtk3-devel libappindicator-gtk3-devel-12.10.0-29.fc33.x86_64`

To enable the App Indicator extension
https://extensions.gnome.org/extension/615/appindicator-support/

## Configuration

In the `config.yaml` file you will be able to configure Proxy, Runner and Modeler properties as runner location or modeler URL. Runner ip is `127.0.0.1` and port is a random free port.

## Next Steps
- Limit GraalVM Heap Size

### How do I create the image.go?

Firstly install the 2goarray package
`go get github.com/cratonica/2goarray`

Then convert the image to a .go file
`cat icon2.png | /Users/aparedes/go/bin/2goarray Data images > icon.go`
