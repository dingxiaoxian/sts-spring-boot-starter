buildscript {
    repositories {
        mavenLocal()
        maven("https://maven.aliyun.com/repository/public")
        mavenCentral()
    }
}

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("io.spring.dependency-management") version "1.0.14.RELEASE"
    id("org.springframework.boot") version "2.7.4"
    id("java-library")
    id("idea")
    id("maven-publish")
    id("signing")
}

tasks.withType<JavaCompile> {
    doFirst {
        println("当前Java版本为：$sourceCompatibility")
    }
    options.encoding = "UTF-8"
}

allprojects {
    group = "tech.xiaoxian.aliyun"
    version = "1.0.3"

    // Configure project's dependencies
    repositories {
        maven("https://maven.aliyun.com/repository/public")
        mavenCentral()
    }
}

dependencies {
    // Spring dependencies
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    api("org.springframework.boot:spring-boot-autoconfigure")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // 阿里云STS依赖
    compileOnly("com.aliyun:aliyun-java-sdk-sts:3.1.0")
    compileOnly("com.aliyun:aliyun-java-sdk-core:4.6.2")

    // 测试依赖
    compileOnly("com.google.code.gson:gson:2.10")
    testImplementation("com.google.code.gson:gson:2.10")
}

tasks.test {
    useJUnitPlatform()
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withJavadocJar()
    withSourcesJar()
}

nexusPublishing {
    repositories {
        sonatype {  //only for users registered in Sonatype after 24 Feb 2021
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            groupId = project.group as String
            artifactId = project.name
            version = project.version as String
            from(components["java"])
            pom {
                name.set(project.name)
                description.set("aliyun STS spring boot starter")
                url.set("https://github.com/dingxiaoxian/sts-spring-boot-starter")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://www.mit.edu/~amini/LICENSE.md")
                    }
                }
                developers {
                    developer {
                        id.set("dingxiaoxian")
                        name.set("Ding Xiaoxian")
                        email.set("dingxiaoxian@xiaoxian.tech")
                    }
                }
                scm {
                    connection.set("scm:git@github.com:dingxiaoxian/sts-spring-boot-starter.git")
                    developerConnection.set("scm:git@github.com:dingxiaoxian/sts-spring-boot-starter.git")
                    url.set("https://github.com/dingxiaoxian/sts-spring-boot-starter")
                }
            }
        }
    }
}

extra["isReleaseVersion"] = !version.toString().endsWith("-SNAPSHOT")

signing {
    setRequired {
        (project.extra["isReleaseVersion"] as Boolean) && gradle.taskGraph.hasTask("publishToSonatype")
    }
    useInMemoryPgpKeys(
        System.getenv("GPG_KEY_ID"),
        System.getenv("GPG_PRIVATE_KEY"),
        System.getenv("GPG_PRIVATE_KEY_PASSWORD")
    )
    sign(publishing.publications["mavenJava"])
}