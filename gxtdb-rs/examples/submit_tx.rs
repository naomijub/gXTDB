use gxtdb_rs::{
    transactions::{Transactions, XtdbID},
    Client,
};
use serde_json::json;

#[tokio::main]
async fn main() {
    let mut client = Client::new("http://localhost", 8080).await.unwrap();

    let datalog_tx =
        Transactions::builder().put(XtdbID::String("gXTDB".to_string()), json!({"a": 1, "b": 2}));
    let submit_tx_response = client.submit_tx(datalog_tx).await.unwrap();
    println!("{submit_tx_response:#?}");
}
