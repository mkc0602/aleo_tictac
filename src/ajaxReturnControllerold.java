/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.emmanet.controllers;

/*
 * #%L
 * InfraFrontier
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2015 EMBL-European Bioinformatics Institute
 * %%
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
 * #L%
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.emmanet.jobs.WebRequests;
import org.emmanet.model.BackgroundManager;
import org.emmanet.model.StrainsManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 *
 * @author phil
 */
public class ajaxReturnControllerold extends SimpleFormController {

    public static final String MAP_KEY = "returnedOut";
    private Map returnedOut = new HashMap();
    private WebRequests webRequest;
List returnedResults=null;
    @Override
    //  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
    //   protected Object formBackingObject(HttpServletRequest request) {
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getParameter("q") != null) {
            //this is an jquery ajax call for autocomplete strain names from insert form
            webRequest = new WebRequests();
            if (!request.getParameter("q").startsWith("em:") && request.getParameter("query") == null) {
                int query = Integer.parseInt(request.getParameter("q"));
                List returnedResults=null;
               // returnedResults = (List)webRequest.strainList(query);
                //System.out.println("returned results" + returnedResults.size());
            }
                 if(request.getParameter("query").equals("bg")) {
                     BackgroundManager bm = new BackgroundManager();

returnedResults = bm.getBackgrounds();
                 }
                
                
                returnedOut.put("ajaxReturn", returnedResults);

            }

    //    }
        
       
         if (request.getParameter("data") != null) {
             StrainsManager sr = new StrainsManager();
             List returnedResults=null;
             returnedResults = sr.getMutsSub("AND main_type = '" + request.getParameter("data") + "' ");
             
             if (returnedResults.size() == 0) {
                 returnedResults.add(0, "No associated mutation sub-type");
                 
             }
            
             returnedOut.put("ajaxReturn", returnedResults);
         }
         
        
        return new ModelAndView("ajaxReturn", MAP_KEY, returnedOut);
    //  return returnedResults;
    }
}
