use chrono::{DateTime, FixedOffset};
use tonic::Status;

use crate::{
    json_prost_helper::json_to_prost,
    proto_api::{Delete, Evict, Put, Transaction},
};

use super::XtdbID;

#[allow(clippy::module_name_repetitions)]
#[derive(Debug, PartialEq, Eq, Clone)]
/// [`DatalogTransaction`](https://docs.xtdb.com/language-reference/datalog-transactions/#operations)
/// * [`Put`](https://docs.xtdb.com/language-reference/datalog-transactions/#put)
/// * [`Delete`](https://docs.xtdb.com/language-reference/datalog-transactions/#delete)
/// * [`Evict`](https://docs.xtdb.com/language-reference/datalog-transactions/#evict)
pub enum DatalogTransaction {
    Put {
        id: XtdbID,
        document: serde_json::Value,
        valid_time: Option<DateTime<FixedOffset>>,
        end_valid_time: Option<DateTime<FixedOffset>>,
    },
    Delete {
        id: XtdbID,
        valid_time: Option<DateTime<FixedOffset>>,
        end_valid_time: Option<DateTime<FixedOffset>>,
    },
    Evict {
        id: XtdbID,
    },
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
                DatalogTransaction::Evict { id } => {
                    crate::proto_api::transaction::TransactionType::Evict(Evict {
                        id_type: (&id).into(),
                        document_id: id.to_string(),
                    })
                }
                DatalogTransaction::Delete {
                    id,
                    valid_time,
                    end_valid_time,
                } => crate::proto_api::transaction::TransactionType::Delete(Delete {
                    id_type: (&id).into(),
                    document_id: id.to_string(),
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
