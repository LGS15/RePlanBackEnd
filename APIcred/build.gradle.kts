plugins {
	java
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
	id("jacoco")
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
		property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")

		property("sonar.sources", "src/main/java")
		property("sonar.tests", "src/test/java")

		// compiled class
		property("sonar.java.binaries", "build/classes/java/main")
		property("sonar.java.test.binaries", "build/classes/java/test")

		// test results
		property("sonar.junit.reportPaths", "build/test-results/test")




		// test package as inclusion
		property("sonar.test.inclusions", "**/*Test.java")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

jacoco {
	toolVersion = "0.8.11"
}

tasks.test {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
		html.required.set(true)
	}
}
