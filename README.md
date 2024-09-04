
![Logo](https://i.imgur.com/9b2R2b6.png)



## Setup Project
 - Create a new project, prefer to use [IntelliJ Idea](https://www.jetbrains.com/pt-br/idea/) as your IDE
- Open your `pom.xml` or `build.gradle` and add the above dependency and repository

For Apache Maven: 
```xml
    	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>

    <dependencies>
        <dependency>
	        <groupId>com.github.syncwrld</groupId>
	        <artifactId>syncBooter</artifactId>
	        <version>v0.1.4.4</version>
	    </dependency>
    </dependencies>
```

For Gradle: 
```gradle   
    repositories {
		mavenCentral()
		maven { url 'https://jitpack.io' }
	}

    dependencies {
	    implementation 'com.github.syncwrld:syncBooter:v0.1.4.4'
	}
```
- Go to `plugin.yml` and add `syncBooter` as dependency
- Download the syncBooter JAR from [Releases Page](https://github.com/syncwrld/syncBooter/releases), put in your plugins folder and restart the server

## Commands

- None - syncBooter is a library and don't own any commands

## Docs
While `syncBooter` hasn't any official documentation, the JavaDocs are located [here](https://booterdocs.syncwrld.tech).
