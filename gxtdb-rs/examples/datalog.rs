use chrono::prelude::*;
use gxtdb_rs::transactions::{Transactions, XtdbID};
use serde_json::json;

fn main() {
    let datalog_tx = Transactions::builder()
        .put(XtdbID::String("gXTDB".to_string()), json!({"a": 1, "b": 2}))
        .delete_with_valid_time_range(
            XtdbID::Int(3),
            "2014-11-28T21:00:09+09:00"
                .parse::<DateTime<FixedOffset>>()
                .unwrap(),
            "2014-11-28T21:00:09+09:00"
                .parse::<DateTime<FixedOffset>>()
                .unwrap(),
        )
        .evict(XtdbID::Uuid(uuid::Uuid::nil()));
    println!("{datalog_tx:#?}");
}
