pub mod api {
    tonic::include_proto!("com.xtdb.protos");
}

use api::{grpc_api_client::GrpcApiClient, Empty};
use tonic::transport::Channel;

impl From<api::OptionString> for Option<String> {
    fn from(value: api::OptionString) -> Self {
        value.value.and_then(|val| match val {
            api::option_string::Value::None(_) => None,
            api::option_string::Value::Some(s) => Some(s),
        })
    }
}

#[derive(Debug)]
pub struct Client {
    client: GrpcApiClient<Channel>,
}

impl Client {
    pub async fn new(host: &str, port: u16) -> Result<Self, tonic::transport::Error> {
        let url = format!("{host}:{port}");
        Ok(Self {
            client: GrpcApiClient::connect(url).await?,
        })
    }

    pub fn new_with_channel(channel: Channel) -> Self {
        Self {
            client: GrpcApiClient::new(channel),
        }
    }

    pub async fn status(&mut self) -> Result<tonic::Response<api::StatusResponse>, tonic::Status> {
        let request = tonic::Request::new(Empty {});
        self.client.status(request).await
    }
}
