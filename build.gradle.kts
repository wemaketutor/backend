import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	idea
	base
	id("java")
	id("org.springframework.boot").version("3.4.0")
	id("io.spring.dependency-management").version("1.1.6")
}

group = "com.tutoras"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

tasks.withType<JavaCompile> {
	options.compilerArgs.add("-parameters")
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
	implementation("org.springframework.boot:spring-boot-starter-web")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	implementation("com.auth0:java-jwt:4.4.0")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.postgresql:postgresql")
	implementation("io.micrometer:micrometer-registry-prometheus")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.apache.pdfbox:pdfbox:2.0.28")
	implementation("org.apache.pdfbox:fontbox:2.0.28")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
	testImplementation("org.mockito:mockito-core")
	testImplementation("org.mockito:mockito-junit-jupiter")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("com.h2database:h2")
}

springBoot {
    mainClass.set("com.tutoras.tutoras.TutorasApplication")
}

tasks.test {
	filter {
		excludeTestsMatching("com.tutoras.tutoras.TutorasApplicationTests")
		excludeTestsMatching("com.tutoras.tutoras.service.MaterialServiceTest")
		excludeTestsMatching("com.tutoras.tutoras.service.RegistrationServiceTest")
	}
}

tasks.withType<BootJar>().configureEach {
  	archiveFileName = "service.jar"
}

tasks.named<Test>("test") {
	useJUnitPlatform()
}
