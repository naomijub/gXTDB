use chrono::{DateTime, FixedOffset, Utc};
use gxtdb_rs::proto_api::{
    option_datetime::Value as DateTimeValue, option_string::Value as StrValue, Empty,
    OptionDatetime, OptionString,
};

#[test]
fn proto_option_string_to_rust_option_string() {
    let proto_opt_str_none = OptionString {
        value: Some(StrValue::None(Empty {})),
    };
    let proto_opt_str_some = OptionString {
        value: Some(StrValue::Some("hello world".to_string())),
    };

    let rs_opt_str_none: Option<String> = None;
    let rs_opt_str_some: Option<String> = Some("hello world".to_string());

    let proto_into_rs_none: Option<String> = proto_opt_str_none.into();
    let proto_into_rs_some: Option<String> = proto_opt_str_some.into();
    assert_eq!(proto_into_rs_none, rs_opt_str_none);
    assert_eq!(proto_into_rs_some, rs_opt_str_some);
}

#[test]
fn proto_option_datetime_to_rust_option_string() {
    let proto_opt_str_none = OptionDatetime {
        value: Some(DateTimeValue::None(Empty {})),
    };
    let proto_opt_str_some = OptionDatetime {
        value: Some(DateTimeValue::Some("A time".to_string())),
    };

    let rs_opt_str_none: Option<String> = None;
    let rs_opt_str_some: Option<String> = Some("A time".to_string());

    let proto_into_rs_none: Option<String> = proto_opt_str_none.into();
    let proto_into_rs_some: Option<String> = proto_opt_str_some.into();
    assert_eq!(proto_into_rs_none, rs_opt_str_none);
    assert_eq!(proto_into_rs_some, rs_opt_str_some);
}

#[test]
fn proto_option_datetime_to_rust_option_datetime_utc() {
    let date: DateTime<Utc> = "2014-11-28T21:00:09+09:00"
        .parse::<DateTime<Utc>>()
        .unwrap();
    let proto_opt_dt_none = OptionDatetime {
        value: Some(DateTimeValue::None(Empty {})),
    };
    let proto_opt_dt_some = OptionDatetime {
        value: Some(DateTimeValue::Some("2014-11-28T21:00:09+09:00".to_string())),
    };

    let rs_opt_dt_none: Option<DateTime<Utc>> = None;
    let rs_opt_dt_some: Option<DateTime<Utc>> = Some(date);

    let proto_into_rs_none: Option<DateTime<Utc>> = proto_opt_dt_none.into();
    let proto_into_rs_some: Option<DateTime<Utc>> = proto_opt_dt_some.into();
    assert_eq!(proto_into_rs_none, rs_opt_dt_none);
    assert_eq!(proto_into_rs_some, rs_opt_dt_some);
}

#[test]
fn proto_option_datetime_to_rust_option_datetime_fixed_offset() {
    let date: DateTime<FixedOffset> = "2014-11-28T21:00:09+09:00"
        .parse::<DateTime<FixedOffset>>()
        .unwrap();
    let proto_opt_dt_none = OptionDatetime {
        value: Some(DateTimeValue::None(Empty {})),
    };
    let proto_opt_dt_some = OptionDatetime {
        value: Some(DateTimeValue::Some("2014-11-28T21:00:09+09:00".to_string())),
    };

    let rs_opt_dt_none: Option<DateTime<FixedOffset>> = None;
    let rs_opt_dt_some: Option<DateTime<FixedOffset>> = Some(date);

    let proto_into_rs_none: Option<DateTime<FixedOffset>> = proto_opt_dt_none.into();
    let proto_into_rs_some: Option<DateTime<FixedOffset>> = proto_opt_dt_some.into();
    assert_eq!(proto_into_rs_none, rs_opt_dt_none);
    assert_eq!(proto_into_rs_some, rs_opt_dt_some);
}
