import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

plugins {
    id("org.springframework.boot") version "2.3.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("org.asciidoctor.convert") version "1.5.3"
    war
    kotlin("jvm") version "1.3.72"
    kotlin("kapt") version "1.3.61"
    kotlin("plugin.spring") version "1.3.72"
    kotlin("plugin.jpa") version "1.3.72"
}

group = "com.recipt"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

var snippetsDir = file("build/generated-snippets")
var outputDir = file("build/asciidoc")
var docDir = file("src/main/resources/static/docs")

ext["spring-security.version"] = "5.3.4.RELEASE"
ext["spring.version"] = "5.2.8.RELEASE"
val queryDslversion = "4.2.+"

buildscript {
    repositories {
        maven(url = "https://plugins.gradle.org/m2/")
        mavenCentral()
    }

    dependencies {
        val querydslPluginVersion = "1.0.10"
        classpath("gradle.plugin.com.ewerk.gradle.plugins:querydsl-plugin:${querydslPluginVersion}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.61")
    }

}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-configuration-processor")

    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate5")

    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // validator
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // jwt
    implementation("io.jsonwebtoken:jjwt:0.9.1")

    // jpa & querydsl
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    implementation("com.querydsl:querydsl-jpa:${queryDslversion}")
    kapt("com.querydsl:querydsl-apt:${queryDslversion}:jpa")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("mysql:mysql-connector-java")
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.security:spring-security-test")
    asciidoctor("org.springframework.restdocs:spring-restdocs-asciidoctor:2.0.3.RELEASE")
    testImplementation("org.springframework.restdocs:spring-restdocs-webtestclient:2.0.3.RELEASE")
    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("com.ninja-squad:springmockk:1.1.0")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }

    test {
        outputs.dir(snippetsDir)
        useJUnitPlatform()
    }

    asciidoctor {
        dependsOn(test)
        inputs.dir(snippetsDir)
        doLast {
            copy {
                from("${outputDir}/html5")
                into("$docDir")
            }
        }
    }
}

sourceSets["main"].withConvention(KotlinSourceSet::class) {
    kotlin.srcDir("$buildDir/generated/source/kapt/main")
}