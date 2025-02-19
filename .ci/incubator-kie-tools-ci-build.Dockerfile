FROM cruizba/ubuntu-dind:jammy-26.1.4

SHELL ["/bin/bash", "-c"]

RUN apt-get update && apt-get upgrade -y && apt-get install -y --no-install-recommends \
    sudo bash wget gpg locales uidmap apt-transport-https ca-certificates curl software-properties-common \
    && rm -rf /var/lib/apt/lists/* \
    && localedef -i en_US -c -f UTF-8 -A /usr/share/locale/locale.alias en_US.UTF-8
ENV LANG en_US.utf8

# Google Chrome repository setup
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | sudo gpg --dearmour -o /usr/share/keyrings/chrome-keyring.gpg
RUN sudo sh -c 'echo "deb [arch=amd64 signed-by=/usr/share/keyrings/chrome-keyring.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google.list'

# Install dependencies
RUN apt-get update && apt-get install -yy \
build-essential \
dbus-user-session \
google-chrome-stable \
gir1.2-appindicator3-0.1 \
libayatana-appindicator3-dev \
libgtk-3-dev \
libssl-dev \
libxi6 \
libnss3 \
libpci-dev \
libglvnd0 \
libbtrfs-dev \
libgpgme-dev \
libdevmapper-dev \
libxml2-utils \
python3 \
python3-pip \
python3-dev \
python3-venv \
python3-gssapi \
gettext \
git \
jq \
vim \
make \
zip \
unzip \
bzip2 \
xvfb \
fluxbox \
rsync \
subversion && \
apt-get clean autoclean && apt-get autoremove --yes && \
rm -rf /var/lib/{apt,cache,log}/

# Install firefox
RUN wget -O /tmp/firefox-latest.tar.bz2 "https://download.mozilla.org/?product=firefox-latest&os=linux64&lang=en-US" && \
    tar xjf /tmp/firefox-latest.tar.bz2 -C /tmp && mv /tmp/firefox /opt/firefox-latest && \
    rm /tmp/firefox-latest.tar.bz2 && \
    ln -s /opt/firefox-latest/firefox /usr/bin/firefox

# Install chromedriver
RUN CHROME_VERSION=$(curl https://googlechromelabs.github.io/chrome-for-testing/last-known-good-versions.json | jq -r .channels.Stable.version) && \
    wget -O /tmp/chromedriver-linux64.zip https://storage.googleapis.com/chrome-for-testing-public/$CHROME_VERSION/linux64/chromedriver-linux64.zip && \
    unzip /tmp/chromedriver-linux64.zip -d /tmp && mv /tmp/chromedriver-linux64/chromedriver /usr/bin/ && \
    rm /tmp/chromedriver-linux64.zip

RUN echo fs.inotify.max_user_watches=524288 | tee -a /etc/sysctl.conf && sysctl -p

# User setup (non-root)
RUN groupadd -g 910 nonrootuser && useradd --create-home -u 910 -g 910 -s /bin/bash nonrootuser && \
    echo "nonrootuser ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers

# Docker user setup (non-root)
RUN groupadd docker && usermod -aG docker nonrootuser

USER nonrootuser

# NVM setup
RUN wget -qO- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash && \
    echo 'export NVM_DIR="${HOME}/.nvm"' | sudo tee /etc/profile.d/nvm.sh && \
    echo '[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"' | sudo tee -a /etc/profile.d/nvm.sh && \
    echo '[ -s "$NVM_DIR/bash_completion" ] && \. "$NVM_DIR/bash_completion"' | sudo tee -a /etc/profile.d/nvm.sh && \
    echo "source /etc/profile.d/nvm.sh" >> $HOME/.bashrc

# Node setup
RUN source $HOME/.nvm/nvm.sh && \
    nvm install 22.13.1 && \
    sudo update-alternatives --install /usr/local/bin/node node $(which node) 1 && \
    sudo update-alternatives --install /usr/local/bin/npm npm $(which npm) 1

# PNPM setup
RUN source $HOME/.nvm/nvm.sh && \
    npm install -g pnpm@9.3.0 && \
    sudo update-alternatives --install /usr/local/bin/pnpm pnpm $(which pnpm) 1

# Maven setup
RUN curl -s "https://get.sdkman.io" | bash && \
    source "$HOME/.sdkman/bin/sdkman-init.sh" && \
    sdk install java 17.0.11-tem && \
    sudo update-alternatives --install /usr/local/bin/java java $(which java) 1 && \
    sdk install maven 3.9.6 && \
    sudo update-alternatives --install /usr/local/bin/mvn mvn $(which mvn) 1 && \
    sdk flush

# Golang setup
RUN wget https://go.dev/dl/go1.22.12.linux-amd64.tar.gz -P /tmp && \
    sudo tar xzf /tmp/go1.22.12.linux-amd64.tar.gz -C /opt && rm /tmp/go1.22.12.linux-amd64.tar.gz && \
    echo 'export GOPATH=${HOME}/go' | sudo tee /etc/profile.d/go.sh && \
    echo 'export PATH=${PATH}:/opt/go/bin:${GOPATH}/bin' | sudo tee -a /etc/profile.d/go.sh && \
    echo "source /etc/profile.d/go.sh" >> $HOME/.bashrc && \
    sudo update-alternatives --install /usr/local/bin/go go /opt/go/bin/go 1

# CodeQL setup
RUN wget https://github.com/github/codeql-action/releases/latest/download/codeql-bundle-linux64.tar.gz -P /tmp && \
    sudo tar xzf /tmp/codeql-bundle-linux64.tar.gz -C /opt && rm /tmp/codeql-bundle-linux64.tar.gz && \
    sudo chown -R nonrootuser:nonrootuser /opt/codeql && \
    echo 'export PATH=/opt/codeql:${PATH}' | sudo tee -a /etc/profile.d/codeql.sh && \
    echo "source /etc/profile.d/codeql.sh" >> $HOME/.bashrc

# Openshift client setup
RUN wget https://mirror.openshift.com/pub/openshift-v4/clients/ocp/stable/openshift-client-linux.tar.gz -P /tmp && \
    sudo tar -C /usr/bin/ -xvzf /tmp/openshift-client-linux.tar.gz oc && rm /tmp/openshift-client-linux.tar.gz

# Helm CLI setup
RUN wget https://get.helm.sh/helm-v3.15.2-linux-amd64.tar.gz -P /tmp && \
    sudo tar -C /usr/bin/ -zxvf /tmp/helm-v3.15.2-linux-amd64.tar.gz linux-amd64/helm --strip-components 1 && rm /tmp/helm-v3.15.2-linux-amd64.tar.gz

# Python setup
RUN sudo update-alternatives --install /usr/local/bin/python python $(which python3) 1 && \
    sudo update-alternatives --install /usr/local/bin/pip pip $(which pip3) 1

# s2i (source-to-image) setup
RUN go install github.com/openshift/source-to-image/cmd/s2i@v1.3.9

# Env vars
ENV HOME="/home/nonrootuser"
ENV JAVA_HOME="${HOME}/.sdkman/candidates/java/current/"
ENV MAVEN_HOME="${HOME}/.sdkman/candidates/maven/current/"
ENV NODE_HOME="${HOME}/.nvm/versions/node/v22.13.1"
ENV DISPLAY=":99"
ENV NODE_OPTIONS="--max_old_space_size=4096"
ENV GOPATH="${HOME}/go"
ENV GOROOT="/opt/go"
ENV PATH="${PATH}:${GOROOT}/bin:${GOPATH}/bin"

ENTRYPOINT [""]

CMD ["bash"]
