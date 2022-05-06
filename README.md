# dita-groovy-dsl

| Branch | Build Status                                                                                                                                                                                                                                     |
| ------ |--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| main | [![Build Status](https://jenkins.cs.ox.ac.uk/buildStatus/icon?job=Mauro+Data+Mapper%2Fdita-groovy-dsl%2Fmain)](https://jenkins.cs.ox.ac.uk/blue/organizations/jenkins/Mauro%20Data%20Mapper%2Fdita-groovy-dsl/branches)          |
| develop | [![Build Status](https://jenkins.cs.ox.ac.uk/buildStatus/icon?job=Mauro+Data+Mapper%2Fdita-groovy-dsl%2Fdevelop)](https://jenkins.cs.ox.ac.uk/blue/organizations/jenkins/Mauro%20Data%20Mapper2Fdita-groovy-dsl/branches) |

### Requirements

* Java 17 (Temurin)
* Gradle 7.3.3+
* Groovy 3.0.9+

All of the above can be installed and easily maintained by using [SDKMAN!](https://sdkman.io/install).

## Introduction

This library provides a clever [MarkupBuilder](https://docs.groovy-lang.org/latest/html/api/groovy/xml/MarkupBuilder.html) for editing 
[DITA](https://en.wikipedia.org/wiki/Darwin_Information_Typing_Architecture) XML from Groovy code.  Unlike the standard markup builders, this 
helps users generate correct DITA, by providing type-safe building methods and supporting validation to check that any generated XML is correctly 
formed.  The plugin also provides a 'project' class which can be used to compile topics, links and other resources to build a standard DITA 
project structure.

Finally, the project can be used alongside the [DITA Open Toolkit](https://www.dita-ot.org) to generate artifacts in formats such as HTML and PDF.

## Markup Language

### Example

The DSL provides an efficient mechanism for generating DITA content.  By way of example, here is how to create a simple topic:

```groovy
        Topic topic = Topic.build(id: "my_first_dita_topic") {
            title "Hello, World!"
            shortdesc "This is an example DITA topic, " +
                      "with some very simple content"
            body {
                p "Lorem ipsum dolor sit amet. Ut adipisci perspiciatis cum sunt vero est repudiandae " +
                  " et dicta nihil et optio repellendus aut omnis corporis. Ut sequi deleniti et " + 
                  "voluptatem recusandae quo Quis expedita. Et ducimus minus qui ipsa rerum et " +
                  "suscipit libero vel quibusdam reiciendis."
                p "Sed asperiores quibusdam rem modi porro sit velit quia qui quidem labore eum minus " +
                  "tenetur qui adipisci similique ut dicta omnis! Vel perferendis voluptate ut facilis " +
                  "laborum cum rerum impedit rem nemo corporis ea veniam possimus qui explicabo " +
                  "laboriosam et velit minima. Qui eveniet officiis sit unde deserunt nam animi accusamus."
            }
            relatedLinks {
                link (format: Format.HTML, href: "http://www.docbook.org/", scope: Scope.EXTERNAL) {
                    linktext "DocBook 5"
                }
                link (format: Format.HTML, href: "http://www.oasis-open.org/committees/tc_home.php?wg_abbrev=dita", scope: Scope.EXTERNAL) {
                    linktext "DITA"
                }
            }
        }
```
A call to `topic.toXmlString()` returns a String with the following content:

```xml
<topic id='my_first_dita_topic'>
  <title>Hello, World!</title>
  <shortdesc>This is an example DITA topic, with some very simple content</shortdesc>
  <body>
    <p>Lorem ipsum dolor sit amet. Ut adipisci perspiciatis cum sunt vero est repudiandae  et dicta nihil et optio repellendus aut omnis corporis. Ut sequi deleniti et voluptatem recusandae quo Quis expedita. Et ducimus minus qui ipsa rerum et suscipit libero vel quibusdam reiciendis.</p>
    <p>Sed asperiores quibusdam rem modi porro sit velit quia qui quidem labore eum minus tenetur qui adipisci similique ut dicta omnis! Vel perferendis voluptate ut facilis laborum cum rerum impedit rem nemo corporis ea veniam possimus qui explicabo laboriosam et velit minima. Qui eveniet officiis sit unde deserunt nam animi accusamus.</p>
  </body>
  <related-links>
    <link href='http://www.docbook.org/' format='html' scope='external'>
      <linktext>DocBook 5</linktext>
    </link>
    <link href='http://www.oasis-open.org/committees/tc_home.php?wg_abbrev=dita' format='html' scope='external'>
      <linktext>DITA</linktext>
    </link>
  </related-links>
</topic>
```

Some ways in which the language helps above ordinary groovy markup builders:

- Methods are only available in the appropriate context - for example inside the `relatedLinks` section, there is a `link` method, but no `p` method.
- Similarly, only appropriate attributes are available: `format` is available on the creation of a `link`, but not on a `topic`.
- Enumerations are available for attributes where appropriate - note the use of `Format.HTML` which again ensures correct attribute values.

### More Details

To create an initial node, use its static `build()` method.  This method takes an optional map of attributes, and a closure that defines its 
content.  Within the closure, methods are available to create new nodes in exactly the same way - for example the following two fragments of code 
create equivalent paragraphs:

```groovy
P exampleParagraph = P.build() {
    b {
        txt "Some bold text here"
    }
}
```
and
```groovy
B exampleBold = B.build() {
    txt "Some bold text here"
}

P exampleParagraph = P.build() {
    b exampleBold
}
```

As you will have seen, text content can be added using the `txt()` method.  For convenience, or styling, this method has synonyms `_()` and 
`str()`, which means the following definitions are equivalent:

```groovy
P.build {
    txt "Hello, World!"
}
```

```groovy
P.build {
    _ "Hello, World!"
}
```

```groovy
P.build {
    str "Hello, World!"
}
```

DITA nodes which may contain text content also have a simple constructor with a single String argument, so for example the following definition:

```groovy
P.build {
    b "Hello, World!"
}
```

is a shorter way of writing:

```groovy
P.build {
    b {
        txt "Hello, World!" 
    } 
}
```

In some cases, you may want to include content from elsewhere that has already been formatted into xml.  In this case, you can use the 
`ditaContent()` method which will accept some XML in a String field - this will be yielded *unescaped* (compared with the `txt()` content above 
which will be escaped)

```groovy
P.build {
    ditaContent "<b>Hello, World!</b>"
}
```
will generate the following xml:

```xml
<p>
  <b>Hello, World!</b>
</p>
```
Note that this content will not be properly validated (and may not be in the future)


### Validation

:warning: **Under construction**

Any node may be individually validated to return a (possibly-empty) list of error messages.  For example:

```groovy
P.build {
    
}.validate()
```

Should return an empty list.

**Note - this functionality is currently quite basic and needs improvement!**


### Projects

:warning: **Under construction**

The `DitaProject` class will generate a project structure that matches the 
[best practice](https://github.com/oxygenxml/dita-project-best-practices) guide here.

First create a DitaProject, and then add topics at given paths.  Internal links will be generated automatically.  You also add external links that 
are used in `xref` elements in your topics.  

```groovy
Topic topic = Topic.build(id: "myFirstTopic") {
    title "My first Topic"
    body {
        p {
            txt "Here is some text"
        }
    }

}

DitaProject ditaProject = new DitaProject(
    filename: "myFirstDitaProject",
    title: "My First DITA Project"
)
ditaProject.addTopic("", topic, Toc.YES)
```

Finally, you can output the project as a folder of XML files, or use the processor class to generate 
output. 

```groovy
ditaProject.writeToDirectory("/Documents/folder/myditaFolder")
```
Which will output a structure as follows:

```shell
DitaTest2
├── filters
├── images
├── links
│   ├── externalLinks.ditamap
│   └── internalLinks.ditamap
├── myFirstDitaProject.ditamap
├── reuse
├── tasks
├── temp
└── topics
    ├── myFirstTopic.dita
```

### Processor

:warning: **Under construction**

The `DitaProcessor` class can be used to generate publish artefacts from DitaProjects.  For example, given the DitaProject definition above, you 
can call:

```groovy
DitaProcessor.generatePdf(ditaProject, "/Documents/filename.pdf")
```

To generate a PDF document.

Other methods are available to support other dita `transtypes`, and generic methods to support the passing of other transform types:

```groovy
    static byte[] runTransform(DitaProject ditaProject, String transtype) {
        ...
    }

    static void runTransform(DitaProject ditaProject, String transtype, String filename) {
        ...
    }

    static void runTransform(DitaProject ditaProject, String transtype, File file) {
        ...
    }
```

## Dependencies

Some of the plugins require addtl dependencies at runtime. 
To keep the included package small we have these configured as `runtimeOnly` dependencies therefore if you want to use these plugins you will need to include the 
additional dependencies

## org.dita.pdf2.fop

Up to date dependencies can be found [here](https://github.com/dita-ot/dita-ot/blob/develop/src/main/plugins/org.dita.pdf2.fop/build.gradle)

```groovy
runtimeOnly(group: 'org.apache.xmlgraphics', name: 'fop', version: '2.6') {
    exclude group: 'xalan'
    exclude group: 'ant'
    exclude group: 'javax.servlet'
}
runtimeOnly(group: 'org.apache.xmlgraphics', name: 'batik-all', version: '1.14') {
    exclude group: 'xalan'
}
runtimeOnly group: 'xml-apis', name: 'xml-apis-ext', version: '1.3.04'
runtimeOnly group: 'org.slf4j', name: 'jcl-over-slf4j', version: '1.7.30'
runtimeOnly(group: 'org.apache.xmlgraphics', name: 'fop-pdf-images', version: '2.6') {
    exclude group: 'xalan'
}
runtimeOnly group: 'org.apache.pdfbox', name: 'pdfbox', version: '2.0.24'
```

### org.lwdita

Up to date dependencies can be found [here](https://github.com/jelovirt/org.lwdita/blob/master/build.gradle)

```groovy
runtimeOnly group: 'nu.validator.htmlparser', name: 'htmlparser', version: '1.4'
runtimeOnly group: 'org.nibor.autolink', name: 'autolink', version: '0.6.0'
runtimeOnly(group: "com.vladsch.flexmark", name: "flexmark-all", version: "0.50.18") {
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-profile-pegdown'
    //        exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-abbreviation'
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-admonition'
    //        exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-anchorlink'
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-aside'
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-emoji'
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-enumerated-reference'
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-attributes'
    //        exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-autolink'
    //        exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-definition'
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-html2md-converter'
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-escaped-character'
    //        exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-footnotes'
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-jira-converter'
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-youtrack-converter'
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-gfm-issues'
    //        exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-gfm-strikethrough'
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-gfm-tasklist'
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-gfm-users'
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-macros'
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-gitlab'
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-jekyll-front-matter'
    //        exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-jekyll-tag'
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-media-tags'
    //        exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-ins'
    //        exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-superscript'
    //        exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-tables'
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-toc'
    //        exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-typographic'
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-wikilink'
    //        exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-yaml-front-matter'
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-youtube-embedded'
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-ext-xwiki-macros'
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-util'
    //        exclude group: 'com.vladsch.flexmark', module: 'flexmark-util-format'
    //        exclude group: 'com.vladsch.flexmark', module: 'flexmark-util-ast'
    //        exclude group: 'com.vladsch.flexmark', module: 'flexmark-util-builder'
    //        exclude group: 'com.vladsch.flexmark', module: 'flexmark-util-dependency'
    //        exclude group: 'com.vladsch.flexmark', module: 'flexmark-util-html'
    exclude group: 'com.vladsch.flexmark', module: 'flexmark-util-options'
    //        exclude group: 'com.vladsch.flexmark', module: 'flexmark-util-sequence'
    //        exclude group: 'com.vladsch.flexmark', module: 'flexmark-util-collection'
    //        exclude group: 'com.vladsch.flexmark', module: 'flexmark-util-data'
    //        exclude group: 'com.vladsch.flexmark', module: 'flexmark-util-misc'
    //        exclude group: 'com.vladsch.flexmark', module: 'flexmark-util-visitor'
}
```

## Adding a plugin to the DSL

The following guide is how to add a plugin to be included in the DSL build.
There will be further documentation as to how to provide your own plugins into an already built DSL.

There are 2 ways to add the plugin we recommend using the DITA-OT CLI to make sure you get all the changes needed.

### DITA OT CLI

Install the [DITA OT](https://www.dita-ot.org/dev/topics/installing-client.html), 
the homebrew installation will install the base directory at `/usr/local/Cellar/dita-ot/3.7.1/libexec/` 

1. Identify the plugin to install from https://www.dita-ot.org/plugins
2. Install the plugin using the provided command `dita --install <plugin>`
3. Navigate to your dita installation base directory
4. Copy the installed plugin's directory from the `<base_dir>/plugins` to `src/main/resources/dita-ot-3.7/plugins`
5. Compare `<base_dir>/config/org.dita.dost.platform/plugin.properties` to `src/main/resources/org.dita.dost.platform/plugin.properties` 
   and add/update any lines relevant to the new plugin. At the very least the following will need adding/updating
   1. print_transtypes > update
   2. transtypes >  update
   3. plugin.<plugin>.dir=plugins/<plugin>
6. Compare `<base_dir>/plugins/org.dita.base/build.xml` to `src/main/resources/dita-ot-3.7/plugins/org.dita.base/build.xml`
   and add/update any lines relevant to the new plugin. At the very least the following will need adding/updating/
   1. `import` element to import the plugin's build.xml
   2. `property` element for the plugin's `dir`
7. If the plugin has a `lib` directory you need to determine the correct gradle jar dependency versions and add each to `dependencies.gradle` as a `runtimeOnly`
   dependency. 
   If the jar cannot be found at https://search.maven.org/ then it will need to be deployed to https://jenkins.cs.ox.ac.uk/ui/repos/tree/General/libs-release-local.
   Please make sure the jar is appropriately published in the correct group, artifact name and version.
8. Clean up the plugin directory of all jar files and any other obviously unnecessary files to ensure the DSL jar size remains as small as possible.

### Direct download/clone of Github Repo

This is similar to the above except you get the source code/zip file from github and place that (unzipped) into the `src/main/resources/dita-ot-3.7/plugins`
directory.
Then you will need to know all the various properties and changes that should be made to
* `src/main/resources/dita-ot-3.7/plugins/org.dita.base/build.xml`
* `src/main/resources/org.dita.dost.platform/plugin.properties` 