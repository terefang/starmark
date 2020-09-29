# STARMARK

An opinionated flexmark (commonmark) processor and maven plugin.

* process commonmark and extensions to html
* provide style classes based on markdown attributes
* build a sane html-document with embedded css and resources (images, svg, fonts)

## How to

To get project into your build:

### Step 1. Add the JitPack repository to your build file

```
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```

### Step 2. Add the dependency

```
	<dependency>
	    <groupId>com.github.terefang.starmark</groupId>
        <artifactId>starmark-processor</artifactId>
	    <version>Version</version>
	</dependency>
```
