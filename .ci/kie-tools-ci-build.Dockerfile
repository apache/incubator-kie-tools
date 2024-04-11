FROM cruizba/ubuntu-dind:latest

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
libgconf-2-4 \
libpci-dev \
openjdk-17-jdk \
git \
jq \
vim \
make \
zip \
unzip \
bzip2 \
xvfb \
fluxbox && \
apt-get clean autoclean && apt-get autoremove --yes && \
rm -rf /var/lib/{apt,dpkg,cache,log}/

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
RUN wget -qO- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.5/install.sh | bash && \
    echo 'export NVM_DIR="${HOME}/.nvm"' | sudo tee /etc/profile.d/nvm.sh && \
    echo '[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"' | sudo tee -a /etc/profile.d/nvm.sh && \
    echo '[ -s "$NVM_DIR/bash_completion" ] && \. "$NVM_DIR/bash_completion"' | sudo tee -a /etc/profile.d/nvm.sh && \
    echo "source /etc/profile.d/nvm.sh" >> $HOME/.bashrc

# Node setup
RUN bash -c 'source $HOME/.nvm/nvm.sh && \
             nvm install 18.14.0'

# PNPM setup
RUN bash -c 'source $HOME/.nvm/nvm.sh && \
             npm install -g pnpm@8.7.6'

# Maven setup
RUN wget https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz -P /tmp && \
    sudo tar xzf /tmp/apache-maven-3.9.6-bin.tar.gz -C /opt && rm /tmp/apache-maven-3.9.6-bin.tar.gz && \
    sudo ln -s /opt/apache-maven-3.9.6 /opt/maven && \
    echo 'export M2_HOME=/opt/maven' | sudo tee -a /etc/profile.d/maven.sh && \
    echo 'export MAVEN_HOME=${M2_HOME}' | sudo tee -a /etc/profile.d/maven.sh && \
    echo 'export PATH=${M2_HOME}/bin:${PATH}' | sudo tee -a /etc/profile.d/maven.sh && \
    echo "source /etc/profile.d/maven.sh" >> $HOME/.bashrc

# Golang setup
RUN wget https://go.dev/dl/go1.21.5.linux-amd64.tar.gz -P /tmp && \
    sudo tar xzf /tmp/go1.21.5.linux-amd64.tar.gz -C /opt && rm /tmp/go1.21.5.linux-amd64.tar.gz && \
    echo 'export GOPATH=${HOME}/go' | sudo tee /etc/profile.d/go.sh && \
    echo 'export PATH=${PATH}:/opt/go/bin:${GOPATH}/bin' | sudo tee -a /etc/profile.d/go.sh && \
    echo "source /etc/profile.d/go.sh" >> $HOME/.bashrc

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
RUN wget https://get.helm.sh/helm-v3.13.3-linux-amd64.tar.gz -P /tmp && \
    sudo tar -C /usr/bin/ -zxvf /tmp/helm-v3.13.3-linux-amd64.tar.gz linux-amd64/helm --strip-components 1 && rm /tmp/helm-v3.13.3-linux-amd64.tar.gz

# Env vars
ENV JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"
ENV DISPLAY=":99"
ENV NODE_OPTIONS="--max_old_space_size=4096"

ENTRYPOINT [""]

CMD ["bash"]
