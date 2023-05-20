mod api {
    #![allow(clippy::enum_variant_names)]
    tonic::include_proto!("mod");
}

pub use crate::api::com::xtdb::protos as proto_api;
use crate::api::com::xtdb::protos::{grpc_api_client::GrpcApiClient, Empty};

use tonic::transport::Channel;

impl From<proto_api::OptionString> for Option<String> {
    fn from(value: proto_api::OptionString) -> Self {
        value.value.and_then(|val| match val {
            proto_api::option_string::Value::None(_) => None,
            proto_api::option_string::Value::Some(s) => Some(s),
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

    pub async fn status(
        &mut self,
    ) -> Result<tonic::Response<proto_api::StatusResponse>, tonic::Status> {
        let request = tonic::Request::new(Empty {});
        self.client.status(request).await
    }
}
