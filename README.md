# gXTDB

gRPC Plugin for XTDB

## Usage:

### Clojure:

1. Install [leiningen](https://leiningen.org/)
2. execute `make all` after making changes to the `*.proto` files in `resources/`

* Execute server: `lein run` / `make run`
* Run tests: `lein test` / `make cljtest`
* Format code, Check: `lein cljfmt check`, Fix: `lein cljfmt fix` / `make cljfmt`
* Lint code, Check: `lein kibit`, Fix: `lein kibit --replace` / `make lint`

To execute queries on the server for testing purpose use tools like kreya.app or postman with gRPC support.

### Rust
1. Install [Rust](https://rustup.rs/)
2. In order to build `tonic >= 0.9.2`, you need the `protoc` Protocol Buffers compiler, along with Protocol Buffers resource files.

**Ubuntu**
```
sudo apt update && sudo apt upgrade -y
sudo apt install -y protobuf-compiler libprotobuf-dev
```

**Alpine Linux**
```
sudo apk add protoc protobuf-dev
```

**macOS**
Assuming Homebrew is already installed. (If not, see instructions for installing Homebrew on their [website](https://brew.sh/).)

```
brew install protobuf 
```


## [gRPC Rust Client](gxtdb-rs/docs/gxtdb-rs.md)

Available endpoints

 - [status](gxtdb-rs/docs/gxtdb-rs.md#status-example-response)



## License

This project is licensed under the Apache License 2.0.
