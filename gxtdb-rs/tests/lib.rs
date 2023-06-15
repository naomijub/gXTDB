// pub mod api {
//     tonic::include_proto!("com.xtdb.protos");
// }

pub mod from;
pub mod mock;

#[tokio::test]
async fn test_status_response() {
    let expected = gxtdb_rs::proto_api::StatusResponse::default().into();
    let mut client = mock::client().await;
    let status = client.status().await.unwrap();
    assert_eq!(status, expected);
}
