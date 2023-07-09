use gxtdb_rs::Client;

#[tokio::main]
async fn main() {
    let mut client = Client::new("http://localhost", 8080).await.unwrap();
    let status = client.status().await.unwrap();
    println!("{status:#?}")
}
