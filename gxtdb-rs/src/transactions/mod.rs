// use chrono::prelude::*;

use chrono::{DateTime, FixedOffset};
use tonic::Status;

use crate::{
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
        match *value {
            XtdbID::Uuid(_) => 0,
            XtdbID::Keyword(_) => 1,
            XtdbID::String(_) => 2,
            XtdbID::Int(_) => 3,
        }
    }
}

impl ToString for XtdbID {
    fn to_string(&self) -> String {
        match self {
            Self::Uuid(s) => s.to_string(),
            Self::Keyword(s) | Self::String(s) => s.to_string(),
            Self::Int(s) => s.to_string(),
        }
    }
}

#[derive(Debug, PartialEq, Clone)]
pub(crate) enum DatalogTransaction {
    Put {
        id: XtdbID,
        document: serde_json::Value,
        valid_time: Option<DateTime<FixedOffset>>,
        end_valid_time: Option<DateTime<FixedOffset>>,
    },
    // //Delete(
    // //    String,
    //     Option<DateTime<FixedOffset>>,
    // ),
    // Evict(String),
}

impl TryFrom<DatalogTransaction> for Transaction {
    type Error = Status;
    fn try_from(value: DatalogTransaction) -> Result<Self, Self::Error> {
        Ok(Self {
            transaction_type: Some(match value {
                DatalogTransaction::Put {
                    id,
                    document,
                    valid_time,
                    end_valid_time,
                } => crate::proto_api::transaction::TransactionType::Put(Put {
                    id_type: (&id).into(),
                    xt_id: id.to_string(),
                    document: Some(json_to_prost(&document)?),
                    valid_time: if valid_time.is_some() {
                        Some(valid_time.into())
                    } else {
                        None
                    },
                    end_valid_time: if end_valid_time.is_some() {
                        Some(end_valid_time.into())
                    } else {
                        None
                    },
                }),
            }),
        })
    }
}

/// Transactions to perform in XDTB. It is a builder struct to help you create a `Vec<DatalogTransaction>` for `tx_log`.
///
/// Allowed actions:
/// * `PUT` - Write a version of a document with its XT-ID. Functions are `append_put` and `append_put_timed`.
/// * `Delete` - Deletes the specific document at a given valid time. Functions are `append_delete` and `append_delete_timed`.
/// * `Evict` - Evicts a document entirely, including all historical versions (receives only the ID to evict). Function is `append_evict`.
/// * `Match` - Matches the current state of an entity, if the state doesn't match the provided document, the transaction will not continue. Functions are `append_match` and `append_match_timed`.
#[derive(Debug, PartialEq, Clone)]
pub struct Transactions {
    transactions: Vec<DatalogTransaction>,
    tx_time: Option<DateTime<FixedOffset>>,
}

impl TryFrom<Transactions> for SubmitRequest {
    type Error = Status;
    fn try_from(value: Transactions) -> Result<Self, Self::Error> {
        Ok(Self {
            tx_ops: value
                .transactions
                .into_iter()
                .map(Transaction::try_from)
                .collect::<Result<Vec<Transaction>, Status>>()?,
            tx_time: None,
        })
    }
}

impl Transactions {
    #[must_use]
    pub const fn builder() -> Self {
        Self {
            transactions: Vec::new(),
            tx_time: None,
        }
    }

    #[must_use]
    pub fn is_empty(&self) -> bool {
        self.transactions.is_empty()
    }

    #[must_use]
    #[allow(clippy::missing_const_for_fn)]
    /// Adds transaction time (`tx_time`) to `Transactions`
    pub fn tx_time(mut self, tx_time: DateTime<FixedOffset>) -> Self {
        self.tx_time = Some(tx_time);
        self
    }

    #[must_use]
    /// Appends an `DatalogTransaction::Put` enforcing types for `transaction` field to be a `T: Serialize`
    pub fn put(mut self, id: XtdbID, document: serde_json::Value) -> Self {
        self.transactions.push(DatalogTransaction::Put {
            id,
            document,
            valid_time: None,
            end_valid_time: None,
        });
        self
    }

    #[must_use]
    /// Appends an `DatalogTransaction::Put` with `valid_time` enforcing types for `transaction` field to be a `T: Serialize`
    pub fn put_with_valid_time(
        mut self,
        id: XtdbID,
        document: serde_json::Value,
        valid_time: DateTime<FixedOffset>,
    ) -> Self {
        self.transactions.push(DatalogTransaction::Put {
            id,
            document,
            valid_time: Some(valid_time),
            end_valid_time: None,
        });
        self
    }

    #[must_use]
    /// Appends an `DatalogTransaction::Put` with `valid_time` and `end_valid_time` enforcing types for `transaction` field to be a `T: Serialize`
    pub fn put_with_valid_time_range(
        mut self,
        id: XtdbID,
        document: serde_json::Value,
        valid_time: DateTime<FixedOffset>,
        end_valid_time: DateTime<FixedOffset>,
    ) -> Self {
        self.transactions.push(DatalogTransaction::Put {
            id,
            document,
            valid_time: Some(valid_time),
            end_valid_time: Some(end_valid_time),
        });
        self
    }
}

#[cfg(test)]
mod tests {
    use chrono::{DateTime, FixedOffset};
    use serde_json::json;

    use super::{DatalogTransaction, Transactions, XtdbID};

    #[test]
    fn simple_put_transaction() {
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
        let transactions = Transactions::builder().put(xtdb_id, document);

        assert_eq!(transactions, expected_transactions(None, None));
    }

    #[test]
    fn simple_put_with_valid_time_transaction() {
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
        let transactions = Transactions::builder().put_with_valid_time(
            xtdb_id,
            document,
            "2014-11-28T21:00:09+09:00"
                .parse::<DateTime<FixedOffset>>()
                .unwrap(),
        );

        assert_eq!(
            transactions,
            expected_transactions(
                "2014-11-28T21:00:09+09:00"
                    .parse::<DateTime<FixedOffset>>()
                    .ok(),
                None
            )
        );
    }

    #[test]
    fn simple_put_with_valid_time_range_transaction() {
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
        let transactions = Transactions::builder().put_with_valid_time_range(
            xtdb_id,
            document,
            "2014-11-28T21:00:09+09:00"
                .parse::<DateTime<FixedOffset>>()
                .unwrap(),
            "2014-11-28T21:00:09+09:00"
                .parse::<DateTime<FixedOffset>>()
                .unwrap(),
        );

        assert_eq!(
            transactions,
            expected_transactions(
                "2014-11-28T21:00:09+09:00"
                    .parse::<DateTime<FixedOffset>>()
                    .ok(),
                "2014-11-28T21:00:09+09:00"
                    .parse::<DateTime<FixedOffset>>()
                    .ok()
            )
        );
    }

    #[test]
    fn simple_put_transaction_with_tx_time() {
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
        let transactions = Transactions::builder()
            .tx_time(
                "2014-11-28T21:00:09+09:00"
                    .parse::<DateTime<FixedOffset>>()
                    .unwrap(),
            )
            .put(xtdb_id, document);

        assert_eq!(transactions, expected_transactions_with_tx_time());
    }

    fn expected_transactions(
        init_time: Option<DateTime<FixedOffset>>,
        end_time: Option<DateTime<FixedOffset>>,
    ) -> Transactions {
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

        Transactions {
            tx_time: None,
            transactions: vec![DatalogTransaction::Put {
                id: xtdb_id,
                document,
                valid_time: init_time,
                end_valid_time: end_time,
            }],
        }
    }

    fn expected_transactions_with_tx_time() -> Transactions {
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

        Transactions {
            tx_time: "2014-11-28T21:00:09+09:00"
                .parse::<DateTime<FixedOffset>>()
                .ok(),
            transactions: vec![DatalogTransaction::Put {
                id: xtdb_id,
                document,
                valid_time: None,
                end_valid_time: None,
            }],
        }
    }
}
