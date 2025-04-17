plugins {
	java
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.sonarqube") version "6.0.1.5171"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"


java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

sonar {
	properties {
		property("sonar.projectKey", "RePlan-Backend")
		property("sonar.projectName", "RePlan Backend")
		property("sonar.host.url", "http://localhost:9000")
		property("sonar.token", "sqp_3d2af99752301f76dd0cce3504772fdccc2197cb")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
