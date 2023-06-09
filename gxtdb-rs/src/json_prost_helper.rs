use std::collections::HashMap;

use protobuf::{ListValue, Struct};

use crate::api::google::protobuf;

pub fn json_to_prost(json: serde_json::Value) -> Result<protobuf::Struct, tonic::Status> {
    let mut map = HashMap::new();
    let json = json.as_object().ok_or(tonic::Status::invalid_argument(
        "Json document needs to be an JSON Object",
    ))?;

    for (k, v) in json.into_iter() {
        map.insert(k.to_owned(), parser(v.to_owned()));
    }

    Ok(protobuf::Struct { fields: map })
}

fn parser(value: serde_json::Value) -> protobuf::Value {
    if let Some(b) = value.as_bool() {
        return protobuf::Value {
            kind: Some(protobuf::value::Kind::BoolValue(b)),
        };
    }

    if let Some(n) = value.as_f64() {
        return protobuf::Value {
            kind: Some(protobuf::value::Kind::NumberValue(n)),
        };
    }

    if let Some(f) = value.as_i64() {
        return protobuf::Value {
            kind: Some(protobuf::value::Kind::NumberValue(f as f64)),
        };
    }

    if let Some(f) = value.as_u64() {
        return protobuf::Value {
            kind: Some(protobuf::value::Kind::NumberValue(f as f64)),
        };
    }

    if let Some(_) = value.as_null() {
        return protobuf::Value {
            kind: Some(protobuf::value::Kind::NullValue(0)),
        };
    }

    if let Some(s) = value.as_str() {
        return protobuf::Value {
            kind: Some(protobuf::value::Kind::StringValue(s.to_owned())),
        };
    }

    if let Some(arr) = value.as_array() {
        return protobuf::Value {
            kind: Some(protobuf::value::Kind::ListValue(ListValue {
                values: arr.into_iter().map(|v| parser(v.clone())).collect(),
            })),
        };
    }

    if let Some(map) = value.as_object() {
        return protobuf::Value {
            kind: Some(protobuf::value::Kind::StructValue(Struct {
                fields: map
                    .into_iter()
                    .map(|(k, v)| (k.clone(), parser(v.clone())))
                    .collect(),
            })),
        };
    }

    protobuf::Value {
        kind: Some(protobuf::value::Kind::NullValue(0)),
    }
}
