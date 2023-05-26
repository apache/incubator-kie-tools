# Build the manager binary
FROM docker.io/library/golang:1.19.9 as builder

WORKDIR /workspace
# Copy the Go Modules manifests
COPY go.mod go.mod
COPY go.sum go.sum

# Copy internal dependency
COPY container-builder/ container-builder/

# cache deps before building and copying source so that we don't need to re-download as much
# and so that source changes don't invalidate our downloaded layer
RUN go mod download

# Copy the go source
COPY main.go main.go
COPY api/ api/
COPY controllers/ controllers/
COPY install/ install/
COPY resources/ resources/
COPY utils/ utils/
COPY version/ version/

# Build
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -a -o manager main.go

FROM registry.access.redhat.com/ubi8/ubi-micro:latest
WORKDIR /usr/local/bin

COPY --from=builder /workspace/manager /usr/local/bin/manager
COPY --from=builder /workspace/resources/ /usr/local/etc/serverless-operator/resources/

USER 65532:65532

ENTRYPOINT ["manager"]
