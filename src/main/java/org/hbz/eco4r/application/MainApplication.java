package org.hbz.eco4r.application;

/**
 * <b>Package Name: org.hbz.eco4r.application</b>
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

import org.hbz.eco4r.resource.AggregationResource;
import org.hbz.eco4r.resource.ResourceMapResource;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 * <b>Class Name</b>: MainApplication</br>
 * <b>Class Definition</b>:
 * <p>This is the main application that receives the request to the Fedora2OREServ service and 
 * forwards them to to the appropriate resources.</p>
 *
 * @author Anouar Boulal, boulal@hbz-nrw.de
 *
 */

public class MainApplication extends Application {

	@Override
    public synchronized Restlet createInboundRoot() {
    	Router router = new Router(getContext());
    	
        router.attach("/{pid}", AggregationResource.class);
        router.attach("/{pid}/rem.{format}", ResourceMapResource.class);
        
        return router;
    }
}
