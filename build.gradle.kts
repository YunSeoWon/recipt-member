import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "2.3.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("org.asciidoctor.convert") version "1.5.3"
    kotlin("jvm") version "1.3.72"
    kotlin("kapt") version "1.3.61"
    kotlin("plugin.spring") version "1.3.72"
    kotlin("plugin.jpa") version "1.3.72"
}

group = "com.recipt"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

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

tasks {
    getByName<BootJar>("bootJar") {
        enabled = false
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.springframework.boot")

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

        // test
        testImplementation("io.projectreactor:reactor-test")
        testImplementation("io.mockk:mockk:1.9.3")
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xjsr305=strict")
                jvmTarget = "1.8"
            }
        }
    }

    // 스프링 종속성 ㄱㄱ
    if (project.name != "core") {
        apply(plugin = "kotlin-spring")
        apply(plugin = "kotlin-kapt")
        apply(plugin = "kotlin-jpa")
        apply(plugin = "org.asciidoctor.convert")

        dependencies {
            implementation(project(":core"))

            // spring
            implementation("org.springframework.boot:spring-boot-starter-webflux")

            // validator
            implementation("org.springframework.boot:spring-boot-starter-validation")

            // jackson
            implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
            implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
            implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate5")

            // jpa & querydsl
            implementation("org.springframework.boot:spring-boot-starter-data-jpa")
            kapt("org.springframework.boot:spring-boot-configuration-processor")
            implementation("com.querydsl:querydsl-jpa:${queryDslversion}")
            kapt("com.querydsl:querydsl-apt:${queryDslversion}:jpa")

            developmentOnly("org.springframework.boot:spring-boot-devtools")
            runtimeOnly("mysql:mysql-connector-java")

            // test
            testImplementation("org.springframework.boot:spring-boot-starter-test") {
                exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
            }
            asciidoctor("org.springframework.restdocs:spring-restdocs-asciidoctor:2.0.3.RELEASE")
            testImplementation("org.springframework.restdocs:spring-restdocs-webtestclient:2.0.3.RELEASE")
            testImplementation("com.ninja-squad:springmockk:1.1.0")
        }

        tasks {
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

            test {
                outputs.dir(snippetsDir)
                useJUnitPlatform()
            }
        }

        sourceSets["main"].withConvention(KotlinSourceSet::class) {
            kotlin.srcDir("$buildDir/generated/source/kapt/main")
        }
    }
}
