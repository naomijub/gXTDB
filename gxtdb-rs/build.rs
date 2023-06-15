fn main() -> Result<(), Box<dyn std::error::Error>> {
    tonic_build::configure()
        .build_server(true)
        .compile_well_known_types(true)
        .include_file("mod.rs")
        .compile(
            &[
                "../resources/common.proto",
                "../resources/transactions.proto",
                "../resources/service.proto",
            ],
            &["../resources/"],
        )?;
    Ok(())
}
