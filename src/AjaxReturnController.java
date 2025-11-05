/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.emmanet.controllers;

/*

 */
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.emmanet.jobs.EmmaBiblioJOB;
import org.emmanet.jobs.EmmaBiblioJOB.FetchBiblio;
import org.emmanet.jobs.WebRequests;
import org.emmanet.model.BackgroundManager;
import org.emmanet.model.BibliosManager;
import org.emmanet.model.MutationsManager;
import org.emmanet.model.PeopleDAO;
import org.emmanet.model.PeopleManager;
import org.emmanet.model.SubmissionBibliosDAO;
import org.emmanet.model.SubmissionMutationsDAO;
import org.emmanet.model.SubmissionsManager;
import org.emmanet.util.Configuration;
import org.emmanet.util.DirFileList;
import org.emmanet.util.Encrypter;
import org.emmanet.util.Utils;
import org.json.simple.JSONObject;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 *
 * @author
 */
public class AjaxReturnController extends SimpleFormController {

    public static final String MAP_KEY = "returnedOut";
    private Map returnedOut = new HashMap();
    private WebRequests webRequest;
    private List returnedResults = null;
    private LinkedList JSONobjects;
    final static String SUBFORMUPLOAD = Configuration.get("SUBFORMUPLOAD");

    @SuppressWarnings("unchecked")
	@Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getParameter("q") != null) {

            //this is an jquery ajax call for autocomplete strain names from insert form
            if (request.getParameter("funct") != null && request.getParameter("funct").equals("esCellLineCall")) {
                returnedOut = new HashMap();
                returnedResults = null;
                //System.out.println("q value is::" + request.getParameter("q"));
                SubmissionsManager sm = new SubmissionsManager();
                returnedResults = sm.getesCellLines(request.getParameter("q"));
                returnedOut.put("ajaxReturn", returnedResults);
                System.out.println("returned out size==" + returnedResults.size());
            } else if (!request.getParameter("q").startsWith("em:") && request.getParameter("query") == null) {
                returnedOut = new HashMap();
                System.out.println("query will be from webrequests");
                webRequest = new WebRequests();
                Integer query = Utils.tryParseInt(request.getParameter("q"));
                if (query != null) {
                    returnedResults = webRequest.strainList(query.intValue());
                    System.out.println("returned results" + returnedResults.size());
                    returnedOut.put("ajaxReturn", returnedResults);
                }

            } // 
            else if (request.getParameter("query") != null && request.getParameter("query").equals("bg")) {
                returnedOut = new HashMap();
                System.out.println("query will be from backgrounds");
                BackgroundManager bm = new BackgroundManager();
                String backGround = request.getParameter("q");
                System.out.println("q value is::" + request.getParameter("q"));
                System.out.println("q values to string is::" + backGround);
                returnedResults = bm.getBGDAOByName(backGround);//.getBackgrounds();

                System.out.println("returned out size==" + returnedResults.size());
                returnedOut.put("ajaxReturn", returnedResults);
            } else {
                System.out.println("query will be from null");
                returnedOut.clear();
            }
        }

        if (request.getParameter("funct") != null && request.getParameter("funct").equals("peopleCall")) {
            returnedOut = new HashMap();
            System.out.println("AJAXCONTROLLERFUNCTIONCALLED " + request.getParameter("email"));
            if (request.getParameter("email") != null || !request.getParameter("email").equals("")) {

                PeopleDAO pd = new PeopleDAO();
                PeopleManager pm = new PeopleManager();
                List people = pm.getPeopleByEMail(request.getParameter("email"));
                List peopleDAOs = new LinkedList();
                JSONObject obj = new JSONObject();
                if (people.isEmpty()) {
                    //Do nothing
                } else {
                    System.out.println("PEOPLEDATA FUNCTION CALLED AND THE PERSON LIST SIZE IS " + people.size());
                    JSONobjects = new LinkedList();
                    for (Iterator it = people.listIterator(); it.hasNext();) {
                        obj = new JSONObject();
                        pd = (PeopleDAO) it.next();
                        peopleDAOs.add(pd);

                        //return a JSON String
                        obj.put("id", pd.getId_per());
                        obj.put("title", pd.getTitle());
                        obj.put("firstname", pd.getFirstname());
                        obj.put("surname", pd.getSurname());
                        obj.put("email", pd.getEmail());
                        obj.put("phone", pd.getPhone());
                        obj.put("fax", pd.getFax());
                        obj.put("ilar", pd.getId_ilar());
                        //laboratory details
                        obj.put("institution", pd.getLabsDAO().getName());
                        obj.put("dept", pd.getLabsDAO().getName());
                        obj.put("address1", pd.getLabsDAO().getAddr_line_1());
                        obj.put("address2", pd.getLabsDAO().getAddr_line_2());
                        obj.put("town", pd.getLabsDAO().getTown());
                        obj.put("county", pd.getLabsDAO().getProvince());
                        obj.put("postcode", pd.getLabsDAO().getPostcode());
                        obj.put("country", pd.getLabsDAO().getCountry());

                        System.out.println(obj.toString());
                        JSONobjects.add(obj);
                    }
                    returnedOut.put("JSON", JSONobjects);
                }
                returnedOut.put("peopleDAO", peopleDAOs);
            }
        }

        if (request.getParameter("funct") != null && request.getParameter("funct").equals("pubMed")) {
            int pmID = 0;
            String strID = request.getParameter("pubmedid");
            System.out.println("BIBLIO REFERENCE IS::- " + strID);
            returnedOut = new HashMap();
            List paper = new LinkedList();
            EmmaBiblioJOB ej = new EmmaBiblioJOB();
            //Use Mike's validator??
            Pattern replace = Pattern.compile("[^\\d+]");
            try {
                System.out.println("we are at try line 156");
                pmID = Integer.parseInt(strID);

                System.out.println("we are at try line 158");
            } catch (NumberFormatException ex) {
            	System.out.println("NumberFormatException at AJAXCONTROLLER" + ex);
            }
            Matcher matcher = replace.matcher(strID);

            while (matcher.find()) {
                System.out.println("we are at matcher find line 164");
                strID = matcher.replaceAll("");
                pmID = Integer.parseInt(strID);
            }
            System.out.println("BIBLIO REFERENCE CLEANED IS::- " + pmID);
            FetchBiblio fb = (FetchBiblio) ej.fetchPaper(pmID);
            paper.add(0, fb.title);
            paper.add(1, fb.author1);
            paper.add(2, fb.author2);
            paper.add(3, fb.journal);
            paper.add(4, fb.year);
            paper.add(5, fb.volume);
            paper.add(6, fb.issue);
            paper.add(7, fb.pages);
            paper.add(8, fb.paperid);
            paper.add(9, fb.paperid);
            paper.add(10, request.getParameter("notes"));
            paper.add(11, request.getParameter("notesadditional"));
           
            returnedOut.put("paper", paper);
        }
        if (request.getParameter("funct") != null && request.getParameter("funct").equals("bibliosEdit")) {
            returnedOut = new HashMap();
            List paper = new LinkedList();

            BibliosManager bm = new BibliosManager();
            int ID = Integer.parseInt(request.getParameter("biblioid"));
            SubmissionBibliosDAO sbd = bm.getSubBiblioBySubBiblioID(ID);
            System.out.println("I D + + " + ID);
            paper.add(0, sbd.getTitle());
            paper.add(1, sbd.getAuthor1());
            paper.add(2, sbd.getAuthor2());
            paper.add(3, sbd.getJournal());
            paper.add(4, sbd.getYear());
            paper.add(5, sbd.getVolume());
            paper.add(6, null);
            paper.add(7, sbd.getPages());
            paper.add(8, sbd.getPubmed_id());
            paper.add(9, ID);
            paper.add(10, sbd.getNotes());
            paper.add(11, sbd.getNotesadditional());
            //System.out.println("P A P E R  SIZE=" + paper.size());
            returnedOut.put("paper", paper);
        }

        ///to edit mutations
        if (request.getParameter("funct") != null && request.getParameter("funct").equals("mutationEdit")) {
            returnedOut = new HashMap();
            List mutations = new LinkedList();

            MutationsManager mm = new MutationsManager();
            int ID = Integer.parseInt(request.getParameter("mutid"));
            SubmissionMutationsDAO smd = mm.getSubMutationBySubMutID(ID);
            System.out.println("I D + + " + ID);
            mutations.add(0, smd.getMutation_allele_mgi_symbol());
            mutations.add(1, smd.getMutation_chrom());
            mutations.add(2, smd.getMutation_chrom_anomaly_descr());
            mutations.add(3, smd.getMutation_chrom_anomaly_name());
            mutations.add(4, smd.getMutation_dominance_pattern());
            mutations.add(5, smd.getMutation_es_cell_line());
            mutations.add(6, smd.getMutation_founder_line_number());
            mutations.add(7, smd.getMutation_gene_mgi_symbol());
            mutations.add(8, smd.getMutation_mutagen());
            mutations.add(9, smd.getMutation_original_backg());
            mutations.add(10, smd.getMutation_original_backg_text());
            mutations.add(11, smd.getMutation_plasmid());
            mutations.add(12, smd.getMutation_promoter());
            mutations.add(13, smd.getMutation_subtype());
            mutations.add(14, smd.getMutation_transgene_mgi_symbol());
            mutations.add(15, smd.getMutation_type());
            mutations.add(16, smd.getMutationCount());
            mutations.add(17, ID);
            returnedOut.put("mutations", mutations);
        }

        if (request.getParameter("funct") != null && request.getParameter("funct").equals("fileList")) {
            returnedOut = new HashMap();
            List<String> filenameList = buildFilenameList(request);

            returnedOut.put("fileListing", filenameList);
        }

        if (request.getParameter("funct") != null && request.getParameter("funct").equals("strainid")) {
            webRequest = new WebRequests();
            returnedResults = null;
            if (!request.getParameter("q").startsWith("em:")) {
                int query = Integer.parseInt(request.getParameter("q"));
                returnedResults = webRequest.strainList(query);
                System.out.println("returned results" + returnedResults.size());
                returnedOut.put("ajaxReturn", returnedResults);
            } else {

            }
        }
        return new ModelAndView("ajaxReturn", MAP_KEY, returnedOut);
    }

    @Deprecated
    public List<String> buildFilenameListOLD(HttpServletRequest request) {
        List<String> filenameList = new ArrayList();

        logger.debug("F I L E  L I S T I N G  R E A C H E D ! !");
        Encrypter enc = new Encrypter();
        String subID = enc.decrypt(request.getParameter("encID"));
        logger.debug("subID = " + subID);

        String fileType = request.getParameter("submissionFileType");
        logger.debug("fileType = " + fileType);

        String searchString = subID + "_" + fileType;
        logger.debug("searchString = " + searchString);

        if (!subID.isEmpty()) {
            DirFileList files = new DirFileList();
            String fileList[];
            logger.debug("calling DirFileList.filteredFileList(" + SUBFORMUPLOAD + ")");
            fileList = files.filteredFileList(SUBFORMUPLOAD, "pdf");
            logger.info("number of files found = " + fileList.length);

            for (int i = 0; i < fileList.length; i++) {
                logger.debug("fileList[" + i + "] = " + fileList[i]);
                if ((fileList[i] != null) && (fileList[i].startsWith(searchString))) {
                    String file = fileList[i].replaceAll(searchString, "");
                    file = file.replace("_", "");
                    logger.info("FILE MATCH. Adding file \"" + file + "\" (fileList[" + i + "])");
                    filenameList.add(file);
                }
            }
        }

        return filenameList;
    }

    /**
     * Builds a list of filenames found that match the pattern of submitted
     * attachments. The pattern is in the format: <i>xxx_yyy_</i>
     * where <i>xxx</i> is the submissions table's id_sub primary key and
     * <i>yyy</i>
     * is the submission file type.
     *
     * @param request <code>HttpServletRequest</code> request object
     * @return a list of filenames that match the pattern of submitted
     * attachments.
     */
    public List<String> buildFilenameList(HttpServletRequest request) {
        String[] filenames;
        logger.debug("submissions: processing uploaded attachments.");
        Encrypter enc = new Encrypter();
        String idSub = enc.decrypt(request.getParameter("encID"));              // Get this submission's submissions.id_sub primary key value.

        String filetype = request.getParameter("submissionFileType");           // Get the file type.
        logger.debug("filetype = " + filetype);

        String filenamePattern = idSub + "_" + filetype + "_";
        logger.debug("filenamePattern = " + filenamePattern);

        logger.debug("Uploading files from '" + SUBFORMUPLOAD + "'.");
        File directory = new File(SUBFORMUPLOAD);
        filenames = directory.list(new FilenameFilterImpl(filenamePattern));

        return Arrays.asList(filenames);
    }

    public class FilenameFilterImpl implements FilenameFilter {

        public String filenamePattern;

        public FilenameFilterImpl(String filenamePattern) {
            this.filenamePattern = filenamePattern;
        }

        /**
         * This method tests if a specified file should be included in a file
         * list. It is the implementation of the <code>FilenameFilter</code>
         * interface for the 'Additional Files' functionality.
         * <code>filenamePattern</code> is in the format:
         * <i>xxx</i>_ADDITIONAL_<i>yyy</i> where <i>xxx</i> is the submissions
         * table's id_sub primary key, and <i>yyy</i> is the remainder of the
         * filename, including any extension.
         *
         * @param dir the directory in which the file was found
         * @param name the name of the file
         * @return <code>true</code> if and only if the name should be included
         * in the file list; <code>false</code> otherwise.
         */
        @Override
        public boolean accept(File dir, String name) {
            return (name.startsWith(filenamePattern));
        }
    }
}
