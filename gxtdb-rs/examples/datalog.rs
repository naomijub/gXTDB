use chrono::prelude::*;
use gxtdb_rs::transactions::{Transactions, XtdbID};
use serde_json::json;

fn main() {
    let datalog_tx = Transactions::builder()
        .put(XtdbID::String("gXTDB".to_string()), json!({"a": 1, "b": 2}))
        .delete(XtdbID::Int(3))
        .with_valid_time(
            "2014-11-28T21:00:09+09:00"
                .parse::<DateTime<FixedOffset>>()
                .unwrap(),
        )
        .with_end_valid_time(
            "2014-11-28T21:00:09+09:00"
                .parse::<DateTime<FixedOffset>>()
                .unwrap(),
        )
        .evict(XtdbID::Uuid(uuid::Uuid::nil()));
    println!("{datalog_tx:#?}");
}
