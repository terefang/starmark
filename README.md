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

### for the maven-plugin

```
    <plugin>
        <groupId>com.github.terefang.starmark</groupId>
        <artifactId>starmark-maven-plugin</artifactId>
        <version>version</version>
        <executions>
            <execution>
                <id>make-html</id>
                <phase>package</phase>
                <goals>
                    <goal>starmark-to-html</goal>
                </goals>
                <configuration>
                    <documentClass>phb</documentClass>
                    <resourceDirectories>
                        <directory>src/main/style</directory>
                    </resourceDirectories>
                    <styles>
                        <!--style>css/reset.css</style-->
                        <style>css/bme-base.scss</style>
                    </styles>
                    <markupType>flexmark</markupType>
                    <documentDirectory>src/main/markdown</documentDirectory>
                    <documentIncludes>*.md</documentIncludes>
                    <documentExcludes></documentExcludes>
                    <outputDocument>${project.build.directory}/out/bme-%04d-%s.html</outputDocument>
                    <outputTemplate>src/main/templates/output-template.j2</outputTemplate>
                    <outputTemplateVariables>src/main/templates/output-variables.hson</outputTemplateVariables>
                    <coverFile>work/bme-cover.png</coverFile>
                </configuration>
            </execution>
        </executions>
    </plugin>
```

*coverFile, documentClass, resourceDirectories, styles, outputTemplate and outputTemplateVariables are optional*

**outputDocument can be any of:**

* a straight filename without '%' and '{' characters, all output is written to this single file.
* a String.format string, 1=number, 2=fullname, 3=pathname, 4=basename, 5=ext, each output-file is written from each input file.
* a MessageFormat.format string, 0=number, 1=fullname, 2=pathname, 3=basename, 4=ext, each output-file is written from each input file.

**supported markup types:**

* "flexmark" -- gh-markdown/commonmark with flexmark extensions
* "markdown+github/1.0" -- markdown/1.2 with gf-markdown
* "markdown/1.2" -- commonmark/0.28
* "xwiki/2.1" -- https://www.xwiki.org/xwiki/bin/view/Documentation/UserGuide/Features/XWikiSyntax/