rsfmt:
	cargo fmt

cljfmt:
	lein cljfmt fix

kibit:
	lein kibit --replace

clippy:
	cargo clippy --all-features -- -W clippy::all -W clippy::nursery -W clippy::pedantic --deny "warnings"

lint: clippy kibit

fmt: cljfmt rsfmt

fix: fmt lint

fix-clj: cljfmt kibit

fix-rs: rsfmt clippy

proto:
	protoc --clojure_out=grpc-client,grpc-server:src --proto_path=resources resources/transactions.proto resources/service.proto

all: proto fix

run:
	lein run 

jar:
	lein uberjar

rstest:
	cargo test

cljtest:
	lein test

test: rstest cljtest