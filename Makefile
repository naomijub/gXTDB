fmt:
	lein cljfmt fix

kibit:
	lein kibit --replace

lint: fmt kibit

proto:
	protoc --clojure_out=grpc-client,grpc-server:src --proto_path=resources resources/service.proto

all: proto lint

run:
	lein run

jar:
	lein uberjar

test:
	lein test