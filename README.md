# gXTDB

gRPC Plugin for XTDB

## Usage
This is the output of a `lein new protojure` template run. The output is a set of sample
files demonstrating the use of the protojure lib + protoc plugin to expose a GRPC service.

The overall structure of the code/doc was originally modified from what you get when you run:

```lein new pedestal-service gxtdb```

The output of the pedestal-service template was
augmented in the following ways:

1. Added resources/addressbook.proto
2. Modified the src/gxtdb/service.clj to implement the GRPC service
   (see `-- PROTOC-GEN-CLOJURE --` annotations in the code)
3. Added dependencies to project.clj for [undertow](http://undertow.io/)
   and [protobuf](https://developers.google.com/protocol-buffers/).  For example:

```
[io.undertow/undertow-core "2.0.25.Final"]
[io.undertow/undertow-servlet "2.0.25.Final"]
[com.google.protobuf/protobuf-java "3.9.1"]
```

**Note**: See the [Pedestal](https://github.com/pedestal/pedestal) set of libraries on github
for more information on Pedestal, which Protojure uses in this project to help integrate with
a server for hosting our GRPC endpoints.

## Prerequisites

The [Protocol Buffer 'protoc' compiler](https://github.com/protocolbuffers/protobuf/releases)
and [Protojure protoc-plugin](https://github.com/protojure/protoc-plugin/releases) must be installed.

You can confirm these dependencies are installed by either using the `all` Makefile target
or manually running

```
protoc --clojure_out=grpc-client,grpc-server:src --proto_path=resources resources/addressbook.proto
```

## Getting Started

After you have run `make all` or the protoc command mentioned in the prerequisites above:

1. Start the application: `lein run`
2. Go to [localhost:8080](http://localhost:8080/) to see: `Hello from gxtdb, backed by Protojure Template! `

You now have a grpc endpoint written in clojure running locally!

The `.proto` definition file can be found at `./resources/addressbook.proto`

In order to invoke the hello endpoint:

3. Run `lein repl` while in the project root (Have `lein run` executing in a separate terminal session, as above)
4. In the repl, run:

```
(use 'com.example.addressbook.Greeter.client)
(use 'protojure.grpc.client.providers.http2)
@(Hello @(connect {:uri (str "http://localhost:" 8080)}) {:name "John Doe"})
```

You should see output like the below in the repl after the 3rd command:

```
#com.example.addressbook.HelloResponse{:message "Hello, John Doe"}

```

## Making this project your own:

From here, a probable next step is adding your own `.proto` Protocol Buffer
and/or GRPC Service definition files.

You may place these whereever you like, alongside `addressbook,proto`, somewhere else in this
project, or anywhere else on your local system. Once you have a selected a location, either
update the command above to:

```
protoc --clojure_out=grpc-server:src --proto_path=<path to your.protos> <.protos to compile>
```

or update the Makefile `all` target similarly.

This should then result in directories in the `src/` folder of this project containing clojure
namespaces corresponding to the packages of your `.proto` files.

If you then update the `require` block of gxtdb/service.clj to use these generated clojure
namespaces created by the protoc command above and placed in your source directory, model your
GRPC endpoint definition on the sample `Greeter` GRPC Service, and then re-run `lein run`, you
will have deployed endpoints exposing your defined GRPC services using your Protocol Buffer
definitions.

Note that you may also easily expose HTTP/1, HTTP/2 endpoints alongside your GRPC endpoints (a feature
that may be unique to Protojure among libraries implementing language .proto & GRPC support but
that we hope you'll find useful). The home and about page handlers are just standard run-of-the-mill
.clj [ring](https://github.com/ring-clojure/ring) handlers.

## License

This project is licensed under the Apache License 2.0.
