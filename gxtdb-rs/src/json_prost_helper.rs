use std::collections::HashMap;

use protobuf::{ListValue, Struct};

use crate::api::google::protobuf;

pub(crate) fn json_to_prost(json: &serde_json::Value) -> Result<protobuf::Struct, tonic::Status> {
    let mut map = HashMap::new();
    let json = json.as_object().ok_or_else(|| {
        tonic::Status::invalid_argument(
            "document needs to be an JSON Object with `serde_json::Value`",
        )
    })?;

    for (k, v) in json {
        map.insert(k.clone(), parser(v));
    }

    Ok(protobuf::Struct { fields: map })
}

fn parser(value: &serde_json::Value) -> protobuf::Value {
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

    #[allow(clippy::cast_precision_loss)]
    if let Some(f) = value.as_i64() {
        return protobuf::Value {
            kind: Some(protobuf::value::Kind::NumberValue(f as f64)),
        };
    }

    #[allow(clippy::cast_precision_loss)]
    if let Some(f) = value.as_u64() {
        return protobuf::Value {
            kind: Some(protobuf::value::Kind::NumberValue(f as f64)),
        };
    }

    if value.as_null().is_some() {
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
                values: arr.iter().map(parser).collect(),
            })),
        };
    }

    if let Some(map) = value.as_object() {
        return protobuf::Value {
            kind: Some(protobuf::value::Kind::StructValue(Struct {
                fields: map
                    .into_iter()
                    .map(|(k, v)| (k.clone(), parser(v)))
                    .collect(),
            })),
        };
    }

    protobuf::Value {
        kind: Some(protobuf::value::Kind::NullValue(0)),
    }
}

#[cfg(test)]
mod tests {
    use std::collections::HashMap;

    use crate::{
        api::google::protobuf::{self, Struct},
        json_prost_helper::parser,
    };
    use serde_json::json;

    #[test]
    fn parser_bool() {
        let v = json!(true);
        assert_eq!(
            (parser(&v)),
            protobuf::Value {
                kind: Some(protobuf::value::Kind::BoolValue(true)),
            }
        );
    }

    #[test]
    fn parser_f64() {
        let v = json!(3.14);
        assert_eq!(
            (parser(&v)),
            protobuf::Value {
                kind: Some(protobuf::value::Kind::NumberValue(3.14)),
            }
        );
    }
    #[test]
    fn parser_i64() {
        let v = json!(3.14);
        assert_eq!(
            (parser(&v)),
            protobuf::Value {
                kind: Some(protobuf::value::Kind::NumberValue(3.14)),
            }
        );
    }

    #[test]
    fn parser_u64() {
        let v = json!(64.0);
        assert_eq!(
            (parser(&v)),
            protobuf::Value {
                kind: Some(protobuf::value::Kind::NumberValue(64.0)),
            }
        );
    }

    #[test]
    fn parser_null() {
        let v = json!(null);
        assert_eq!(
            (parser(&v)),
            protobuf::Value {
                kind: Some(protobuf::value::Kind::NullValue(0)),
            }
        );
    }

    #[test]
    fn parser_string() {
        let v = json!("gxtdb-rs".to_string());
        assert_eq!(
            parser(&v),
            protobuf::Value {
                kind: Some(protobuf::value::Kind::StringValue("gxtdb-rs".to_string())),
            }
        );
    }

    #[test]
    fn parser_array() {
        let v = json!([1, 2, 3]);
        assert_eq!(
            parser(&v),
            protobuf::Value {
                kind: Some(protobuf::value::Kind::ListValue(protobuf::ListValue {
                    values: vec![
                        protobuf::Value {
                            kind: Some(protobuf::value::Kind::NumberValue(1.))
                        },
                        protobuf::Value {
                            kind: Some(protobuf::value::Kind::NumberValue(2.))
                        },
                        protobuf::Value {
                            kind: Some(protobuf::value::Kind::NumberValue(3.))
                        }
                    ],
                })),
            }
        );
    }

    #[test]
    fn parser_map() {
        let v = json!({"a": 1, "b": 2});
        let mut hm = HashMap::new();
        hm.insert(
            "a".to_owned(),
            protobuf::Value {
                kind: Some(protobuf::value::Kind::NumberValue(1.)),
            },
        );
        hm.insert(
            "b".to_owned(),
            protobuf::Value {
                kind: Some(protobuf::value::Kind::NumberValue(2.)),
            },
        );
        assert_eq!(
            parser(&v),
            protobuf::Value {
                kind: Some(protobuf::value::Kind::StructValue(Struct { fields: hm }))
            }
        )
    }
}
