pub mod api {
    tonic::include_proto!("com.xtdb.protos");
}

use api::{grpc_api_client::GrpcApiClient, Empty};
use tonic::transport::Channel;

impl From<api::OptionString> for Option<String> {
    fn from(value: api::OptionString) -> Self {
        value.value.map_or(None, |val| match val {
            api::option_string::Value::None(_) => None,
            api::option_string::Value::Some(s) => Some(s),
        })
    }
}

pub async fn client(
    host: &str,
    port: u16,
) -> Result<GrpcApiClient<Channel>, tonic::transport::Error> {
    let url = format!("{host}:{port}");
    GrpcApiClient::connect(url).await
}

pub async fn status(
    client: &mut GrpcApiClient<Channel>,
) -> Result<tonic::Response<api::StatusResponse>, tonic::Status> {
    let request = tonic::Request::new(Empty {});
    client.status(request).await
}
