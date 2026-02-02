plugins {
    id("java")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter")
    implementation(files("libs/heroes_task_lib-1.0-SNAPSHOT.jar"))
    implementation(files("libs/Heroes Battle-1.0.0.jar"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}