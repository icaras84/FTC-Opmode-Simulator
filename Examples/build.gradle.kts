plugins {
    id("dev.frozenmilk.teamcode") version "11.1.0-1.1.1"
    id("dev.frozenmilk.sinister.sloth.load") version "0.2.4"
}

repositories {
    maven("https://www.jitpack.io")
    maven("https://central.sonatype.com/repository/maven-snapshots")
    maven("https://maven.brott.dev/")
}

ftc {
    sdk.TeamCode()

    dairy {
        implementation(Sloth)
        implementation(slothboard)
    }
}

dependencies {
    implementation("com.pedropathing:ivy:1.0.0")
    implementation("dev.frozenmilk.dairy:CachingHardware:1.0.0")
    testImplementation("junit:junit:4.13.2")
    // no version means it will get the local version
    testImplementation("org.codeblooded.ftcodesim:ftcodesim")
    testImplementation("org.codeblooded.ftcodesim:driverstation")

    implementation("com.pedropathing:revhub:3.0.1")

    implementation("org.psilynx.psikit:core:0.2.0")
    implementation("org.psilynx.psikit:ftc:0.2.0")

    implementation("com.acmerobotics.slothboard:dashboard:0.3.1+0.6.0")
}