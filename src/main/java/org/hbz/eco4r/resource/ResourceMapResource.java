package org.hbz.eco4r.resource;

/**
 * <b>Package Name: org.hbz.eco4r.resource</b>
 * <b>Package Description: </b>
 * <p>This package includes classes which are related the main service application</p>
 *
 * -----------------------------------------------------------------------------
 * 
 * This file is part of the eco4r-Project funded by the German Research Foundation - DFG. 
 * It is created by Library Service Center North Rhine Westfalia (Cologne) and the University of Bielefeld.

 * <b>License and Copyright:</b> </br>
 * <p>The contents of this file are subject to the
 * D-FSL License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.dipp.nrw.de/dfsl/">http://www.dipp.nrw.de/dfsl/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>Portions created for the Fedora Repository System are Copyright &copy; 2002-2005
 * by The Rector and Visitors of the University of Virginia and Cornell
 * University. All rights reserved."</p>
 *
 * -----------------------------------------------------------------------------
 *
 * <b>Creator(s): @author Anouar Boulal, boulal@hbz-nrw.de</b>
 *
 * @version 1.0
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.dspace.foresite.ORESerialiser;
import org.dspace.foresite.ORESerialiserException;
import org.dspace.foresite.ORESerialiserFactory;
import org.dspace.foresite.ResourceMap;
import org.dspace.foresite.ResourceMapDocument;
import org.hbz.eco4r.main.ResourceMapGenerator;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * <b>Class Name</b>: ResourceMapResource</br>
 * <b>Class Definition</b>:
 * <p>This class represents a Resource-Map resource.</p>
 *
 * @author Anouar Boulal, boulal@hbz-nrw.de
 *
 */

public class ResourceMapResource extends ServerResource{
	
	private static Logger logger = Logger.getLogger(ResourceMapResource.class);
	
	private ResourceMap rem;
	private String pid;
	private String format;
	private Map<String, List<String>> formatMap;
	
	private final String CONFIGNAME = "configuration";

	@Override
	protected void doInit() throws ResourceException {
		Request request = getRequest();
		Map<String, Object> attributes = request.getAttributes();
		this.pid = (String) attributes.get("pid");
		this.format = (String) attributes.get("format");
		
		if (this.pid.contains("%3A"))
		{
			this.pid = this.pid.replace("%3A", ":");
		}
		
		logger.info("Getting pid: " + this.pid);
		logger.info("Getting format: " + this.format);
		
		ResourceMapGenerator generator = new ResourceMapGenerator(CONFIGNAME);
		this.setRem(generator.generateResourceMap(this.pid));
		
		this.initFormatMap();
	}

	@Get()
	public Representation generateReM() {
		String remSerializationFormat = this.getKeyToValue(this.formatMap, this.format);
		Representation	representation = this.generateReMRepresentation(remSerializationFormat);
		
		return representation;
	}
	
	private Representation generateReMRepresentation(String remSerializationFormat) {
		
		Representation representation = null;
		try {
			if (remSerializationFormat != null) {
				
					ORESerialiser serialiser = ORESerialiserFactory.getInstance(remSerializationFormat);
					ResourceMapDocument remDoc = null;
					
					if (remSerializationFormat.compareTo("ATOM-1.0") == 0) {
						remDoc = serialiser.serialise(rem);
					}
					else {
						remDoc = serialiser.serialiseRaw(rem);
					}
					
					String remString = remDoc.getSerialisation();
					
					if (remSerializationFormat.compareTo("RDF/XML") == 0 || remSerializationFormat.compareTo("RDF/XML-ABBREV") == 0) {
						byte[] bytes = remString.getBytes();
						InputStream is = new ByteArrayInputStream(bytes);
						representation = new DomRepresentation(MediaType.APPLICATION_RDF_XML);
						Document doc = this.loadXMLFrom(is);
						doc.normalizeDocument();
						((DomRepresentation) representation).setDocument(doc);
					}
					
					if (remSerializationFormat.compareTo("ATOM-1.0") == 0) {
						representation = new StringRepresentation(remString, MediaType.APPLICATION_ATOM);
					}
					
					if (remSerializationFormat.compareTo("TURTLE") == 0) {
						representation = new StringRepresentation(remString, MediaType.TEXT_ALL);
					}
					if (remSerializationFormat.compareTo("N-TRIPLE") == 0) {
						representation = new StringRepresentation(remString, MediaType.TEXT_ALL);
					}
					if (remSerializationFormat.compareTo("N3") == 0) {
						representation = new StringRepresentation(remString, MediaType.TEXT_ALL);
					}
			}
			else {
				ORESerialiser serialiser = ORESerialiserFactory.getInstance("RDF/XML");
				ResourceMapDocument remDoc = serialiser.serialiseRaw(rem);
				String remString = remDoc.getSerialisation();
				
				byte[] bytes = remString.getBytes();
				InputStream is = new ByteArrayInputStream(bytes);
				
				representation = new DomRepresentation(MediaType.APPLICATION_RDF_XML);
				Document doc = this.loadXMLFrom(is);
				
				doc.normalizeDocument();
				
				((DomRepresentation) representation).setDocument(doc);
			}
		} 
		catch (ORESerialiserException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return representation;
	}
	
	public Document loadXMLFrom(InputStream is) {

		Document doc = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = null;
			builder = factory.newDocumentBuilder();
			doc = builder.parse(is);
			is.close();
		} 
		catch (SAXException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return doc;
	}

	private String getKeyToValue(Map<String, List<String>> formatMap,
			String format) {
		String key = null;
		
		for (Map.Entry<String, List<String>> entry : formatMap.entrySet()) {
			String k = entry.getKey();
			List<String> v = entry.getValue();
			if (v.contains(format)) {
				key = k;
				break;
			}
		}
		
		return key;
	}
	
	private void initFormatMap() {
		this.formatMap = new HashMap<String, List<String>>();
		List<String> rdfxmlList = this.getRDFXMLList();
		List<String> atomList = this.getAtomList();
		List<String> turtleList = this.getTurtleList();
		List<String> ntripleList = this.getNTriples();
		List<String> n3List = this.getN3List();
		List<String> rdfxmlAbbList = this.getRDFXMLABBList();
		
		this.formatMap.put("RDF/XML", rdfxmlList);
		this.formatMap.put("ATOM-1.0", atomList);
		this.formatMap.put("TURTLE", turtleList);
		this.formatMap.put("N-TRIPLE", ntripleList);
		this.formatMap.put("N3", n3List);
		this.formatMap.put("RDF/XML-ABBREV", rdfxmlAbbList);
	}
	
	private List<String> getRDFXMLABBList() {
		List<String> list = new ArrayList<String>();
		list.add("rdfxmlabbrev");
		list.add("rdfxmlabb");
		return list;
	}

	private List<String> getN3List() {
		List<String> list = new ArrayList<String>();
		list.add("n3");
		list.add("N3");
		return list;
	}

	private List<String> getNTriples() {
		List<String> list = new ArrayList<String>();
		list.add("ntriples");
		list.add("nTriples");
		list.add("NTRIPLES");
		return list;
	}

	private List<String> getTurtleList() {
		List<String> list = new ArrayList<String>();
		list.add("turtle");
		list.add("TURTLE");
		return list;
	}

	private List<String> getAtomList() {
		List<String> list = new ArrayList<String>();
		list.add("atom");
		list.add("ATOM");
		return list;
	}

	private List<String> getRDFXMLList() {
		List<String> list = new ArrayList<String>();
		list.add("rdf");
		list.add("RDF");
		list.add("rdfxml");
		list.add("RDFXML");
		return list;
	}
	
	
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public ResourceMap getRem() {
		return rem;
	}

	public void setRem(ResourceMap rem) {
		this.rem = rem;
	}
}
