use gxtdb_rs::proto_api::grpc_api_server::{GrpcApi, GrpcApiServer};
use gxtdb_rs::proto_api::{self};
use tonic::transport::{Endpoint, Server, Uri};
use tower::service_fn;

#[derive(Debug, Default)]
pub struct ServerMock;

#[tonic::async_trait]
impl GrpcApi for ServerMock {
    async fn status(
        &self,
        _request: tonic::Request<proto_api::Empty>,
    ) -> Result<tonic::Response<proto_api::StatusResponse>, tonic::Status> {
        Ok(tonic::Response::new(proto_api::StatusResponse::default()))
    }

    async fn submit_tx(
        &self,
        _request: tonic::Request<proto_api::SubmitRequest>,
    ) -> Result<tonic::Response<proto_api::SubmitResponse>, tonic::Status> {
        todo!()
    }

    async fn speculative_tx(
        &self,
        _request: tonic::Request<proto_api::SpeculativeTxRequest>,
    ) -> Result<tonic::Response<proto_api::SpeculativeTxResponse>, tonic::Status> {
        todo!()
    }

    async fn entity_tx(
        &self,
        _request: tonic::Request<proto_api::EntityTxRequest>,
    ) -> Result<tonic::Response<proto_api::EntityTxResponse>, tonic::Status> {
        todo!()
    }
}

pub async fn client() -> gxtdb_rs::Client {
    let (client, server) = tokio::io::duplex(1024);
    let mock_server = ServerMock::default();

    tokio::spawn(async move {
        Server::builder()
            .add_service(GrpcApiServer::new(mock_server))
            .serve_with_incoming(futures::stream::iter(vec![Ok::<_, std::io::Error>(server)]))
            .await
    });
    let mut client = Some(client);
    let channel = Endpoint::try_from("http://[::]:50051")
        .unwrap()
        .connect_with_connector(service_fn(move |_: Uri| {
            let client = client.take();
            async move {
                if let Some(client) = client {
                    Ok(client)
                } else {
                    Err(std::io::Error::new(
                        std::io::ErrorKind::Other,
                        "Client already taken",
                    ))
                }
            }
        }))
        .await;
    gxtdb_rs::Client::new_with_channel(channel.unwrap())
}
