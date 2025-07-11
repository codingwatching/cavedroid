plugins {
    kotlin
    ksp
    kotlinxSerialization
}

java.sourceCompatibility = ApplicationInfo.sourceCompatibility
java.targetCompatibility = ApplicationInfo.sourceCompatibility

dependencies {
    useCommonModule()
    useLibgdx()
    useKotlinxSerializationJson()
    useDagger()

    useModule(":core:domain:assets")
    useModule(":core:domain:menu")
}
