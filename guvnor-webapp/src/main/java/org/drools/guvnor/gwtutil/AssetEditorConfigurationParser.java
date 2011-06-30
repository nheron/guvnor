/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.gwtutil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetEditorConfigurationParser {
	private static final Logger log = LoggerFactory
			.getLogger(AssetEditorConfigurationParser.class);
	public static final String DROOLS_ASSETEDITOR_CONFIG = "/drools-asseteditors.xml";
	static final String ASSET_EDITOR = "asseteditor";
	static final String TITLE = "title";
	static final String CLASS = "class";
	static final String ICON = "icon";
	static final String FORMAT = "format";

	public List<AssetEditorConfiguration> readConfig() {
		List<AssetEditorConfiguration> assetEditors = new ArrayList<AssetEditorConfiguration>();
		try {
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			InputStream in = getClass().getResourceAsStream(
					DROOLS_ASSETEDITOR_CONFIG);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			AssetEditorConfiguration configuration = null;

			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();

				if (event.isStartElement()) {
					final AssetEditorConfigElement element = AssetEditorConfigElement.forName(event.asStartElement().getName().getLocalPart());
					switch (element) {
					case ASSET_EDITOR: {
						configuration = new AssetEditorConfiguration();
						break;
					}
					case FORMAT: {
						event = eventReader.nextEvent();
						if(event.isCharacters()) {
						    configuration.setFormat(event.asCharacters().getData());							
						} else if(event.isEndElement()) {
							configuration.setFormat("");	
						}
						break;
					}
					case TITLE: {
						event = eventReader.nextEvent();
						if(event.isCharacters()) {
						    configuration.setTitle(event.asCharacters().getData());							
						} else if(event.isEndElement()) {
							configuration.setTitle("");	
						}

						break;
					}
					case ICON: {
						event = eventReader.nextEvent();
						if(event.isCharacters()) {
						    configuration.setIcon(event.asCharacters().getData());							
						} else if(event.isEndElement()) {
							configuration.setIcon("");	
						}
						break;
					}
					case CLASS: {
						event = eventReader.nextEvent();
						if(event.isCharacters()) {
						    configuration.setEditorClass(event.asCharacters().getData());							
						} else if(event.isEndElement()) {
							configuration.setEditorClass("");	
						}
						break;
					}
					}
				}
				if (event.isEndElement()) {
					final AssetEditorConfigElement element = AssetEditorConfigElement.forName(event
							.asEndElement().getName().getLocalPart());
					if (element == AssetEditorConfigElement.ASSET_EDITOR) {
						assetEditors.add(configuration);
					}
				}

			}
		} catch (XMLStreamException e) {
			log.error("Failed to parse Asset editor configuration file", e);
			e.printStackTrace();
		}
		return assetEditors;
	}

	public static void main(String[] agrs) {
		AssetEditorConfigurationParser a = new AssetEditorConfigurationParser();
		a.readConfig();
	}
}