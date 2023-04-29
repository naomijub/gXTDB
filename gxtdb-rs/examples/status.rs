use gxtdb_rs::{client, status};

#[tokio::main]
async fn main() {
    let mut client = client("http://localhost", 8080).await.unwrap();
    let status = status(&mut client).await.unwrap();
    println!("{:?}", status)
}
