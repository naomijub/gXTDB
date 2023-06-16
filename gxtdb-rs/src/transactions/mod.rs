// use chrono::prelude::*;

use crate::{
    api::google::protobuf,
    json_prost_helper::json_to_prost,
    proto_api::{Put, SubmitRequest, Transaction},
};

#[derive(Debug, PartialEq, Eq, PartialOrd, Ord, Clone)]
pub enum XtdbID {
    Uuid(uuid::Uuid),
    Keyword(String),
    String(String),
    Int(usize),
}

impl From<&XtdbID> for i32 {
    fn from(value: &XtdbID) -> Self {
        match value {
            &XtdbID::Uuid(_) => 0,
            &XtdbID::Keyword(_) => 1,
            &XtdbID::String(_) => 2,
            &XtdbID::Int(_) => 3,
        }
    }
}

impl ToString for XtdbID {
    fn to_string(&self) -> String {
        match self {
            XtdbID::Uuid(s) => s.to_string(),
            XtdbID::String(s) => s.to_string(),
            XtdbID::Keyword(s) => s.to_string(),
            XtdbID::Int(s) => s.to_string(),
        }
    }
}

#[derive(Debug, PartialEq, Clone)]
pub(crate) enum DatalogTransaction {
    Put(
        XtdbID,
        protobuf::Struct, /*,Option<DateTime<FixedOffset>>*/
    ),
    // Delete(String),
    // Evict(String),
}

impl From<DatalogTransaction> for Transaction {
    fn from(value: DatalogTransaction) -> Self {
        Self {
            transaction_type: Some(match value {
                DatalogTransaction::Put(id, doc) => {
                    crate::proto_api::transaction::TransactionType::Put(Put {
                        id_type: (&id).into(),
                        xt_id: id.to_string(),
                        document: Some(doc),
                    })
                }
            }),
        }
    }
}

/// Transactions to perform in XDTB. It is a builder struct to help you create a `Vec<DatalogTransaction>` for `tx_log`.
///
/// Allowed actions:
/// * `PUT` - Write a version of a document with its XTDB-ID. Functions are `append_put` and `append_put_timed`.
#[derive(Debug, PartialEq, Clone)]
pub struct Transactions {
    transactions: Vec<DatalogTransaction>,
}

impl From<Transactions> for SubmitRequest {
    fn from(value: Transactions) -> Self {
        Self {
            tx_ops: value.transactions.into_iter().map(|t| t.into()).collect(),
        }
    }
}

impl Transactions {
    pub fn new() -> Self {
        Self {
            transactions: Vec::new(),
        }
    }

    /// Appends an `DatalogTransaction::Put` enforcing types for `transaction` field to be a `T: Serialize`
    pub fn append_put(
        mut self,
        id: XtdbID,
        document: serde_json::Value,
    ) -> Result<Self, tonic::Status> {
        self.transactions
            .push(DatalogTransaction::put(id, document)?);
        Ok(self)
    }
}

impl DatalogTransaction {
    fn put(id: XtdbID, document: serde_json::Value) -> Result<DatalogTransaction, tonic::Status> {
        // crate::proto_api::
        Ok(DatalogTransaction::Put(id, json_to_prost(document)?))
    }
}

#[cfg(test)]
mod tests {
    use crate::{
        json_prost_helper::{self},
    };
    use serde_json::json;

    use super::{DatalogTransaction, Transactions, XtdbID};

    #[test]
    fn transactions() {
        let xtdb_id = XtdbID::String(String::from("gxtdb"));
        let document: serde_json::Value = json!({
            "code": 200,
            "success": true,
            "payload": {
                "features": [
                    "gxtdb-rs"
                ]
            }
        });
        let transactions = Transactions::new().append_put(xtdb_id, document);

        assert_eq!(transactions.clone().unwrap(), expected_transactions());
    }

    fn expected_transactions() -> Transactions {
        let xtdb_id = XtdbID::String(String::from("alex"));
        let document: serde_json::Value = json!({
            "code": 200,
            "success": true,
            "payload": {
                "features": [
                    "gxtdb-rs"
                ]
            }
        });

        Transactions {
            transactions: vec![DatalogTransaction::Put(
                xtdb_id,
                json_prost_helper::json_to_prost(document).unwrap(),
            )],
        }
    }
}
