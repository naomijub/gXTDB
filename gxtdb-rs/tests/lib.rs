// pub mod api {
//     tonic::include_proto!("com.xtdb.protos");
// }

use gxtdb_rs::transactions::{Transactions, XtdbID};
use serde_json::json;

pub mod from;
pub mod mock;

#[tokio::test]
async fn test_status_response() {
    let expected = gxtdb_rs::proto_api::StatusResponse::default().into();
    let mut client = mock::client().await;
    let status = client.status().await.unwrap();
    assert_eq!(status, expected);
}

#[tokio::test]
async fn test_submit_tx_response() {
    let expected = gxtdb_rs::proto_api::SubmitResponse::default().into();
    let mut client = mock::client().await;
    let datalog_tx =
        Transactions::builder().put(XtdbID::String("gXTDB".to_string()), json!({"a": 1, "b": 2}));
    let submit_tx_response = client.submit_tx(datalog_tx).await.unwrap();
    assert_eq!(submit_tx_response, expected);
}
