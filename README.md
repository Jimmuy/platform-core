# 使用方式

## Gradle

### Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```
 allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}
```
	
### Step 2. Add the dependency
```
dependencies {
	        implementation 'com.github.Jimmuy:platform:v1.0.0'
	}
```
## Marven
### Step 1. Add the JitPack repository to your build file
```
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://www.jitpack.io</url>
		</repository>
	</repositories>
```
### Step 2. Add the dependency
```
  <dependency>
	    <groupId>com.github.Jimmuy</groupId>
	    <artifactId>platform</artifactId>
	    <version>v1.0.0</version>
	</dependency>
```
