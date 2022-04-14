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

Any node may be individually validated to return a (possibly-empty) list of error messages.  For example:

```groovy
P.build {
    
}.validate()
```

Should return an empty list.

**Note - this functionality is currently quite basic and needs improvement!**


### Projects

The `DitaProject` class will generate a project structure that matches the 
[best practice](https://github.com/oxygenxml/dita-project-best-practices) guide here.

