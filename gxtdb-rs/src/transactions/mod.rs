use chrono::prelude::*;
use tonic::Status;

use crate::proto_api::{SubmitRequest, Transaction};

use self::datalog::DatalogTransaction;

mod datalog;

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

/// Transactions to perform in XDTB. It is a builder struct to help you create a `Vec<DatalogTransaction>` for `tx_log`.
/// [Datalog Transactions](https://docs.xtdb.com/language-reference/datalog-transactions/#operations)
///
/// Allowed actions:
/// * `PUT` - Write a version of a document with its XT-ID. Functions are `append_put` and `append_put_timed`.
/// * `Delete` - Deletes the specific document at a given valid time. Functions are `append_delete` and `append_delete_timed`.
/// * `Evict` - Evicts a document entirely, including all historical versions (receives only the ID to evict). Function is `append_evict`.
/// * `Match` - Matches the current state of an entity, if the state doesn't match the provided document, the transaction will not continue. Functions are `append_match` and `append_match_timed`.
#[derive(Debug, PartialEq, Eq, Clone)]
pub struct Transactions {
    transactions: Vec<DatalogTransaction>,
    tx_time: Option<DateTime<FixedOffset>>,
}

impl TryFrom<Transactions> for SubmitRequest {
    type Error = Status;
    fn try_from(value: Transactions) -> Result<Self, Self::Error> {
        if value.is_empty() {
            Err(Status::invalid_argument(
                "Datalog Transactions cannot be empty".to_string(),
            ))
        } else {
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
}

impl Transactions {
    /// Starts the datalog transactions builder, similar to a  `::new()` or `::default()`
    #[must_use]
    pub const fn builder() -> Self {
        Self {
            transactions: Vec::new(),
            tx_time: None,
        }
    }

    // Verifies if `Transactions` is empty
    #[must_use]
    pub fn is_empty(&self) -> bool {
        self.transactions.is_empty()
    }

    pub(crate) fn build(self) -> Result<SubmitRequest, Status> {
        self.try_into()
    }

    #[must_use]
    #[allow(clippy::missing_const_for_fn)]
    /// Adds transaction time [`tx_time`](https://docs.xtdb.com/language-reference/datalog-transactions/#transaction-time) to `Transactions`
    pub fn tx_time(mut self, tx_time: DateTime<FixedOffset>) -> Self {
        self.tx_time = Some(tx_time);
        self
    }

    #[must_use]
    /// Appends an [`DatalogTransaction::Evict`](https://docs.xtdb.com/language-reference/datalog-transactions/#evict) enforcing types for `transaction`
    pub fn evict(mut self, id: XtdbID) -> Self {
        self.transactions.push(DatalogTransaction::Evict { id });
        self
    }

    #[must_use]
    /// Appends an [`DatalogTransaction::Delete`](https://docs.xtdb.com/language-reference/datalog-transactions/#delete) enforcing types for `transaction`
    pub fn delete(mut self, id: XtdbID) -> Self {
        self.transactions.push(DatalogTransaction::Delete {
            id,
            valid_time: None,
            end_valid_time: None,
        });
        self
    }

    #[must_use]
    /// Appends an [`DatalogTransaction::Delete`](https://docs.xtdb.com/language-reference/datalog-transactions/#delete) with [`valid_time`](https://docs.xtdb.com/language-reference/datalog-transactions/#valid-times) enforcing types for `transaction`
    pub fn delete_with_valid_time(mut self, id: XtdbID, valid_time: DateTime<FixedOffset>) -> Self {
        self.transactions.push(DatalogTransaction::Delete {
            id,
            valid_time: Some(valid_time),
            end_valid_time: None,
        });
        self
    }

    #[must_use]
    /// Appends an [`DatalogTransaction::Delete`](https://docs.xtdb.com/language-reference/datalog-transactions/#delete) with [`valid_time` and `end_valid_time`](https://docs.xtdb.com/language-reference/datalog-transactions/#valid-times) enforcing types for `transaction`
    pub fn delete_with_valid_time_range(
        mut self,
        id: XtdbID,
        valid_time: DateTime<FixedOffset>,
        end_valid_time: DateTime<FixedOffset>,
    ) -> Self {
        self.transactions.push(DatalogTransaction::Delete {
            id,
            valid_time: Some(valid_time),
            end_valid_time: Some(end_valid_time),
        });
        self
    }

    #[must_use]
    /// Appends an [`DatalogTransaction::Put`](https://docs.xtdb.com/language-reference/datalog-transactions/#put) enforcing types for `transaction` field to be a `T: Serialize`
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
    /// Appends an [`DatalogTransaction::Put`](https://docs.xtdb.com/language-reference/datalog-transactions/#put) with [`valid_time`](https://docs.xtdb.com/language-reference/datalog-transactions/#valid-times) enforcing types for `transaction` field to be a `T: Serialize`
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
    /// Appends an [`DatalogTransaction::Put`](https://docs.xtdb.com/language-reference/datalog-transactions/#put) with [`valid_time` and `end_valid_time`](https://docs.xtdb.com/language-reference/datalog-transactions/#valid-times) enforcing types for `transaction` field to be a `T: Serialize`
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

    #[must_use]
    /// Appends an [`DatalogTransaction::Match`](https://docs.xtdb.com/language-reference/datalog-transactions/#match)
    pub fn append_match(mut self, id: XtdbID, document: serde_json::Value) -> Self {
        self.transactions.push(DatalogTransaction::Match {
            id,
            document,
            valid_time: None,
        });
        self
    }

    #[must_use]
    pub fn append_match_timed(
        mut self,
        id: XtdbID,
        document: serde_json::Value,
        valid_time: DateTime<FixedOffset>,
    ) -> Self {
        self.transactions.push(DatalogTransaction::Match {
            id,
            document,
            valid_time: Some(valid_time),
        });
        self
    }
}

#[cfg(test)]
mod tests {
    use super::{DatalogTransaction, Transactions, XtdbID};
    use chrono::{DateTime, FixedOffset};
    use serde_json::json;

    #[test]
    #[should_panic(
        expected = "called `Result::unwrap()` on an `Err` value: Status { code: InvalidArgument, message: \"Datalog Transactions cannot be empty\", source: None }"
    )]
    fn empty_transaction_panics_when_converting_to_submit_tx() {
        let empty_transactions = Transactions::builder();

        empty_transactions.build().unwrap();
    }

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

    #[test]
    fn simple_evict_transaction() {
        let xtdb_id = XtdbID::String(String::from("gxtdb"));
        let transactions = Transactions::builder().evict(xtdb_id);
        let expected = Transactions {
            transactions: vec![DatalogTransaction::Evict {
                id: XtdbID::String("gxtdb".to_string()),
            }],
            tx_time: None,
        };

        assert_eq!(transactions, expected);
    }

    #[test]
    fn simple_delete_transaction() {
        let xtdb_id = XtdbID::String(String::from("gxtdb"));
        let transactions = Transactions::builder().delete(xtdb_id);
        let expected = Transactions {
            transactions: vec![DatalogTransaction::Delete {
                id: XtdbID::String("gxtdb".to_string()),
                valid_time: None,
                end_valid_time: None,
            }],
            tx_time: None,
        };

        assert_eq!(transactions, expected);
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

    #[test]
    fn simple_match_transaction() {
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
        let transactions = Transactions::builder().append_match(xtdb_id, document);
        let expected = Transactions {
            transactions: vec![DatalogTransaction::Match {
                id: XtdbID::String(String::from("gxtdb")),
                document: json!({
                    "code": 200,
                    "success": true,
                    "payload": {
                        "features": [
                            "gxtdb-rs"
                        ]
                    }
                }),
                valid_time: None,
            }],
            tx_time: None,
        };
        assert_eq!(transactions, expected);
    }
}
