fn main() -> Result<(), Box<dyn std::error::Error>> {
    tonic_build::configure()
        .build_server(true)
        .compile_well_known_types(true)
        .compile(
            &["../resources/service.proto", "../resources/transactions.proto"], 
            &["../resources/"])?;
    Ok(())
}
