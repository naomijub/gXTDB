pub use crate::api::com::xtdb::protos as proto_api;

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct Response {
    pub version: String,
    pub index_version: i32,
    pub kv_store: String,
    pub estimate_num_keys: i32,
    pub size: i64,
    pub revision: Option<String>,
    pub consumer_state: Option<String>,
}

impl From<proto_api::StatusResponse> for Response {
    fn from(value: proto_api::StatusResponse) -> Self {
        Self {
            version: value.version,
            index_version: value.index_version,
            kv_store: value.kv_store,
            estimate_num_keys: value.estimate_num_keys,
            size: value.size,
            revision: value.revision.and_then(std::convert::Into::into),
            consumer_state: value.consumer_state.and_then(std::convert::Into::into),
        }
    }
}
