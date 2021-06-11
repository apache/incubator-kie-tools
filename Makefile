all:
	$(error "Please use OS-specific commands to build.")

run:
	ENV=dev go run main.go

clean:
	$(RM) -rf ./build

submodule:
	cd kogito-apps && git submodule sync --recursive && git submodule update --init --force --recursive

# macOS
macos: clean build-jitexecutor copy-jitexecutor build-macos package-macos

macos-snapshot: clean build-jitexecutor copy-jitexecutor-snapshot build-macos package-macos

build-macos: 
	GO111MODULE=on GOOS=darwin GOARCH=amd64 go build -o build/darwin/dmn_runner main.go

package-macos:
	cd scripts/macos && ./build.sh

# Linux
linux: clean build-jitexecutor copy-jitexecutor build-linux package-linux

linux-snapshot: clean build-jitexecutor copy-jitexecutor-snapshot build-linux package-linux

build-linux:
	GOOS=linux GOARCH=amd64 go build -o build/linux/dmn_runner main.go

package-linux:
	cd build/linux && tar -pcvzf dmn_runner_linux.tar.gz dmn_runner

# Windows
win: clean build-jitexecutor copy-jitexecutor-win build-win

win-snapshot: clean build-jitexecutor copy-jitexecutor-win-snapshot build-win

build-win:
	GOOS=windows GOARCH=amd64 GO111MODULE=on go build -o build/win/dmn_runner.exe main.go


# JIT Executor
build-jitexecutor:
	mvn clean package -B -DskipTests -f ./kogito-apps/jitexecutor && mvn clean package -B -DskipTests -Pnative -am -f ./kogito-apps/jitexecutor

copy-jitexecutor:
	cp ./kogito-apps/jitexecutor/jitexecutor-runner/target/jitexecutor-runner-*.Final-runner jitexecutor
	chmod +x jitexecutor

copy-jitexecutor-win:
	cp ./kogito-apps/jitexecutor/jitexecutor-runner/target/jitexecutor-runner-*.Final-runner.exe jitexecutor


copy-jitexecutor-snapshot:
	cp ./kogito-apps/jitexecutor/jitexecutor-runner/target/jitexecutor-runner-*-SNAPSHOT-runner jitexecutor
	chmod +x jitexecutor

copy-jitexecutor-win-snapshot:
	cp ./kogito-apps/jitexecutor/jitexecutor-runner/target/jitexecutor-runner-*-SNAPSHOT-runner.exe jitexecutor
