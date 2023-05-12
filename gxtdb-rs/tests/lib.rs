pub mod api {
    tonic::include_proto!("com.xtdb.protos");
}

pub mod mock;

#[tokio::test]
async fn test_status_response(){
    let expected = gxtdb_rs::api::StatusResponse{
        version: "0".to_string(),
        index_version: 1,
        kv_store: "kv".to_string(),
        estimate_num_keys: 2,
        size: 2,
        revision: None,
        consumer_state: None
    };
    let mut client = mock::client().await;
    let status = client.status().await.unwrap();
    assert_eq!(status.into_inner(), expected);
}
