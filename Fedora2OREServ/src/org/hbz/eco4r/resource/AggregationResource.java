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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.data.Reference;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.apache.log4j.Logger;

/**
 * <b>Class Name</b>: AggregationResource</br>
 * <b>Class Definition</b>:
 * <p>This class represents an Aggregation resource.</p>
 *
 * @author Anouar Boulal, boulal@hbz-nrw.de
 *
 */

public class AggregationResource extends ServerResource{

	private static Logger logger = Logger.getLogger(AggregationResource.class);
	
	private Map<String, String> mediaTypeMap;
	private String incommingMediaType;
	private String outgoingMediaType;
	
	
	@Override
	protected void doInit() throws ResourceException {
		this.initMediaTypeMap();
		List<Preference<MediaType>> acceptMediaTypes = getRequest().getClientInfo().getAcceptedMediaTypes();
		this.setIncommingMediaType(acceptMediaTypes.get(0).getMetadata().getName());
		this.setOutgoingMediaType(this.getMediaType(this.incommingMediaType));
		
		logger.info("Incoming media type: " + this.getIncommingMediaType());
		logger.info("Outgoing media type: " + this.getOutgoingMediaType());
	}
	
	
	private String getMediaType(String incommingMediaType) {
		String mediaType = this.mediaTypeMap.get(incommingMediaType);
		
		if (mediaType == null) {
			mediaType = "rdf";
		}
		
		return mediaType;
	}

	private void initMediaTypeMap() {
		this.mediaTypeMap = new HashMap<String, String>();
		this.mediaTypeMap.put("application/rdf+xml", "rdf");
		this.mediaTypeMap.put("application/atom+xml", "atom");
		this.mediaTypeMap.put("application/x-turtle", "turtle");
		this.mediaTypeMap.put("text/n3", "ntriples");
		this.mediaTypeMap.put("text/n3", "n3");
		this.mediaTypeMap.put("text/xml", "rdfxmlabbrev"); // too general
	}
	
	/**
	 * Performs a 303 redirection to the appropriate ReM
	 * depending on the accept header information
	 */
	@Get
	public void redirect303() {
		
		Reference reference = getReference();
		String refString = reference.toString();
		String target = refString + "/rem." + this.outgoingMediaType;
		getResponse().redirectSeeOther(target);
	}

	public String getIncommingMediaType() {
		return incommingMediaType;
	}

	public void setIncommingMediaType(String incommingMediaType) {
		this.incommingMediaType = incommingMediaType;
	}

	public String getOutgoingMediaType() {
		return outgoingMediaType;
	}

	public void setOutgoingMediaType(String outgoingMediaType) {
		this.outgoingMediaType = outgoingMediaType;
	}
}
