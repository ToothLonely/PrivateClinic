plugins {
    id("clinic.jvm")
    application
}

application {
    mainClass.set("ru.privatclinic.server.MainKt")
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    runtimeOnly(libs.logback.classic)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.ktor.server.test.host)
}
