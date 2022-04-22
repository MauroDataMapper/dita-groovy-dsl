/*
 * Copyright 2020-2022 University of Oxford and Health and Social Care Information Centre, also known as NHS Digital
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package uk.ac.ox.softeng.maurodatamapper.dita

import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.KeyDef
import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.Topic
import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.Map
import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.TopicRef
import uk.ac.ox.softeng.maurodatamapper.dita.enums.ProcessingRole
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Toc

class DitaProject {

    String filename
    String title

    DitaProjectOptions options = new DitaProjectOptions()

    java.util.Map<String, List<Tuple2<Topic, Toc>>> topicMap = [:]
    java.util.Map<String, String> externalKeyMap = [:]

    List<String> topLevelFolders = [
        "filters",
        "images",
        "links",
        "reuse",
        "tasks",
        "topics"
    ]


    static final String fileSeparator = System.getProperty("file.separator")

    File writeToDirectory(String directory) {

        File baseDirectory = createDirectory(directory, "")

        java.util.Map<String, File> folders = topLevelFolders.collectEntries {folderName ->
            [folderName, createDirectory(directory, folderName)]
        }
        topicMap.each {String path, List<Tuple2<Topic, Toc>> topicList ->
            String fullPath = "${directory}${fileSeparator}topics${fileSeparator}${path}"
            if(path == "") {
                fullPath = "${directory}${fileSeparator}topics"
            }
            outputTopics(fullPath, topicList)
        }
        createInternalLinks(directory)
        createExternalLinks(directory)
        File mapFile = writeDitaMap(directory)
        return mapFile
    }

    void outputTopics(String path, List<Tuple2<Topic, Toc>> topicList) {
        topicList.each {tuple ->
            Topic topic = tuple.getV1()
            Toc toc = tuple.getV2()

            String topicPath = "${path}${fileSeparator}${topic.id}.dita"
            if(path == "") {
                topicPath = "${topic.id}.dita"
            }
            if(options.filePerTopic) {
                topic.outputAsFile(topicPath)
                /*if(topic.subTopics) {
                    String newPath = "${path}${fileSeparator}${topic.id}"
                    if(path == "") {
                        newPath = "${topic.id}"
                    }
                    createDirectory(path, topic.id)
                    outputTopics(newPath, topic.subTopics)
                }
             */
            } else {
                topic.outputAsFile(topicPath)
            }
        }
    }

    private File createDirectory(String basePath, String dirName) {
        File newDir = new File("${basePath}${fileSeparator}${dirName}")
        if(!newDir.exists()) {
            newDir.mkdirs()
        }
        return newDir
    }

    File writeDitaMap (String directory) {
        Map ditaMap = Map.build {
            title this.title
            mapRef (
                href: "links/internalLinks.ditamap" ,
                processingRole: ProcessingRole.RESOURCE_ONLY
            ) {}
            mapRef (
                href: "links/externalLinks.ditamap",
                processingRole: ProcessingRole.RESOURCE_ONLY
            ) {}
        }

        topicMap.each { path, topicList ->
            topicList.each {tuple ->
                Topic topic = tuple.getV1()
                Toc toc = tuple.getV2()
                String newPath = "topics${fileSeparator}${path}"
                if(path == "") {
                    newPath = "topics"
                }
                ditaMap.topicRef getTopicRefForTopic(topic, toc, newPath)
            }
        }
        String ditamapFilename = "${directory}${fileSeparator}${filename}.ditamap"
        return ditaMap.outputAsFile(ditamapFilename)
    }

    void createInternalLinks (String directory) {
        Map ditaMap = Map.build {
            title "Keys for various linked topics"
        }

        topicMap.each { path, topicList ->
            topicList.each {tuple ->
                Topic topic = tuple.getV1()
                Toc toc = tuple.getV2()
                String newPath = "../topics${fileSeparator}${path}"
                if(path == "") {
                    newPath = "../topics"
                }
                ditaMap.keyDef getKeyDefForTopic(topic, newPath)

            }
        }
        String ditamapFilename = "${directory}${fileSeparator}links${fileSeparator}internalLinks.ditamap"
        ditaMap.outputAsFile(ditamapFilename)
    }


    void createExternalLinks (String directory) {
        Map ditaMap = Map.build {
            title "External Links Key Definitions"
        }
/*
        externalKeyMap.each {key, url ->
            ditaMap.keyDefs.add(new KeyDef(
                keys: new SpaceSeparatedStringList([key]),
                href: url,
                scope: Scope.EXTERNAL,
                format: Format.HTML))
        }
*/
        String ditamapFilename = "${directory}${fileSeparator}links${fileSeparator}externalLinks.ditamap"
        ditaMap.outputAsFile(ditamapFilename)
    }

    KeyDef getKeyDefForTopic(Topic topic, String path) {
        KeyDef keyDef = new KeyDef(
            keys: topic.id,
            href: "${path}${fileSeparator}${topic.id}.dita"
        )

/*
        if(topic.subTopics && options.filePerTopic) {
            topic.subTopics.each {subTopic ->
                keyDefList.addAll(addTopicKeyDefToDitaMap(subTopic, "${path}${fileSeparator}${topic.id}"))
            }
        }

 */
        return keyDef

    }

    TopicRef getTopicRefForTopic(Topic topic, Toc toc, String path) {
        TopicRef topicRef = TopicRef.build (
            href: "${path}${fileSeparator}${topic.id}.dita",
            toc: toc ) {


/*        if(topic.subTopics && options.filePerTopic) {
            topicRef.subTopicRefs = topic.subTopics.collect {subTopic ->
                addTopicRefToDitaMap(subTopic, "${path}${fileSeparator}${topic.id}")
            }
        }

 */
        }
        return topicRef


    }


    void addTopic(String path, Topic topic, Toc toc) {
        List<Topic> existingTopics = topicMap[path]
        if(existingTopics) {
            existingTopics.add(topic)
        } else {
            topicMap[path] = [new Tuple2(topic, toc)]
        }
    }

    void addExternalKey(String key, String url) {
        externalKeyMap[key] = url

    }


}
