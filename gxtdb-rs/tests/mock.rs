use gxtdb_rs::api::grpc_api_server::{GrpcApi, GrpcApiServer};
use gxtdb_rs::api::{self};
use tonic::transport::{Endpoint, Server, Uri};
use tower::service_fn;

#[derive(Debug, Default)]
pub struct ServerMock;

#[tonic::async_trait]
impl GrpcApi for ServerMock {
    async fn status(
        &self,
        _request: tonic::Request<api::Empty>,
    ) -> Result<tonic::Response<api::StatusResponse>, tonic::Status> {
        Ok(tonic::Response::new(api::StatusResponse::default()))
    }

    async fn submit_tx(
        &self,
        _request: tonic::Request<api::SubmitRequest>,
    ) -> Result<tonic::Response<api::SubmitResponse>, tonic::Status> {
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
