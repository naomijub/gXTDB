mod api {
    #![allow(clippy::enum_variant_names)]
    #![allow(clippy::nursery)]
    #![allow(clippy::pedantic)]
    tonic::include_proto!("mod");
}

pub mod status;
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

/// It has the `gRPC` Client for XTDB.
///
/// `Client` Contains the following functions:
/// * `status` requests endpoint `/status` via `gRPC`. No args expected as input

/// Struct to define all required paramenters to have a client.
#[derive(Debug)]
pub struct Client {
    client: GrpcApiClient<Channel>,
}

impl Client {
    /// Implement client needs in order to have a `gRPC` client instance.
    ///
    ///  Returns a `gRPC` client connection.
    ///
    /// # Arguments
    ///
    /// *`host`
    /// *`port`
    ///
    ///  # Errors
    ///
    /// The return will be `tonic::transport::Error` if this function does not encounter the host or port.
    pub async fn new(host: &str, port: u16) -> Result<Self, tonic::transport::Error> {
        let url = format!("{host}:{port}");
        Ok(Self {
            client: GrpcApiClient::connect(url).await?,
        })
    }
    #[must_use]
    pub fn new_with_channel(channel: Channel) -> Self {
        Self {
            client: GrpcApiClient::new(channel),
        }
    }
    /// * `status` requests endpoint `/status` via `gRPC`. No args. Returns `StatusResponse`.
    ///
    /// # Errors
    ///
    /// This function will return error `tonic::Status`.
    pub async fn status(&mut self) -> Result<status::Response, tonic::Status> {
        let request = tonic::Request::new(Empty {});
        self.client
            .status(request)
            .await
            .map(|status| status.into_inner().into())
    }
}
