# gXTDB-rs

Rust gRPC client for XTDB.

## gXTDB-rs Client

Once you have called `Client` you will have an instance of the `gRPC Client` struct which has the following functions to query XTDB via gRPC.

- [status](To do: Update to Doc.rs) queries endpoint `/status` via gRPC. No args. Returns status and status response.
- [submit_tx] queries endpoint `/SubmitTx` via `gRPC`. `DatalogTransactions` and optional transaction time are expected as input: [`Transactions`](transactions::Transactions)

### `Client` code example:

```
let mut client = Client::new("http://localhost", 8080).await.unwrap();
```

## Status Example Response:

```
StatusResponse {
    version: <String>, // "1.22.1"
    index_version:  <i32>, // 22
    kv_store: <String>, //  "xtdb.mem_kv.MemKv"
    estimate_num_keys: <i32>, //1
    size: <i64>, //0
    revision:  <Option<String>>, // Some("0823e6b7fbb03f1e93f65c916c6d787ad2b4212c")
    consumer_state: <Option<String>> // None
} 
```

## Submit Tx Example Response:

```
SubmitResponse {
    tx_time: <string:datetime>, // "2023-07-08T22:10:06.111-05:00"
    tx_id: <i32>, // 0
}
```
