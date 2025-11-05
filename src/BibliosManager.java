/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.emmanet.model;

/*
 *
 */

/**
 *
 * @author
 */
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.emmanet.util.HibernateUtil;
import org.emmanet.util.PubmedID;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class BibliosManager implements BibliosManagerIO {
    
    public BibliosStrainsDAO getBibStrainsByID(int id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        BibliosStrainsDAO bsd = null;
        try {

            bsd = (BibliosStrainsDAO) session.get(BibliosStrainsDAO.class,
                    id);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }

        return bsd;
    }

    public BibliosDAO getBibByID(int id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        BibliosDAO bd = null;
        try {

            bd = (BibliosDAO) session.get(BibliosDAO.class,
                    id);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }

        return bd;
    }

    /**
     * 
     * @param pmid
     * @return
     * @deprecated This method has been replaced by getBibliosByPubmed_id(), which
     *             allows the caller to pass in any pmid, including null, and which
     *             returns a list of matches. Remember, biblios.pubmed_id is defined
     *             in the database as a varchar, with no unique constraint, and
     *             thus may return multiple BibliosDAO records.
     */
    @Deprecated
    public BibliosDAO getPubmedIDByID(int pmid) {
        BibliosDAO bd = null;
        if (pmid > 0) {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            //setParameter(0, pmid)
            try {
                //NOTE: had to remove setparameter and ? from query as unable to solve 
                //org.springframework.web.util.NestedServletException: Request processing failed; nested exception is java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.String
                //error from org.emmanet.model.BibliosManager.getPubmedIDByID(BibliosManager.java:58)
                // and org.emmanet.controllers.SubmissionFormController.processFinish(SubmissionFormController.java:579) despite an integer being used

                bd = (BibliosDAO) session.createQuery("FROM BibliosDAO WHERE pubmed_id =" + pmid).uniqueResult();
                session.getTransaction().commit();
            } catch (HibernateException e) {
                session.getTransaction().rollback();
                throw e;
            }
        }
        return bd;
    }
    
////    public String getNotes(int pubmed_id) {
////        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
////        session.beginTransaction();
////        List<BibliosDAO> bibliosList = new ArrayList<>();
////
////        try {
////            if (pubmed_id == null) {
////                bibliosList = (List<BibliosDAO>) session.createQuery("FROM BibliosDAO WHERE pubmed_id IS NULL").list();
////            } else {
////                bibliosList = (List<BibliosDAO>) session.createQuery("FROM BibliosDAO WHERE pubmed_id = ?").setParameter(0, pubmed_id).list();
////            }
////        } catch (HibernateException e) {
////            session.getTransaction().rollback();
////            throw e;
////        }
////
////        return bibliosList;
////    }
    
    /**
     * Given a pubmed_id string that may be null, this method returns a list of
     * matching <code>BibliosDAO</code> objects matching the pubmed_id. When the
     * pubmed_id is an integer &gt; 0, a match will always return exactly one element
     * in the list. If pubmed_id is null, 0, or not a number, the list may return
     * any number of matching elements, including zero. <strong>NOTE: The returned
     * list is guaranteed to not be null.</strong>
     * @param pubmed_id the pubmed id against which to search
     * @return a <code>List&lt;BibliosDAO&gt</code> of matches. The list is guaranteed never to be null.
     */
    public List<BibliosDAO> getBibliosByPubmed_id(String pubmed_id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<BibliosDAO> bibliosList = new ArrayList<>();

        try {
            if (pubmed_id == null) {
                bibliosList = (List<BibliosDAO>) session.createQuery("FROM BibliosDAO WHERE pubmed_id IS NULL").list();
            } else {
                bibliosList = (List<BibliosDAO>) session.createQuery("FROM BibliosDAO WHERE pubmed_id = ?").setParameter(0, pubmed_id).list();
            }
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }

        return bibliosList;
    }

    @Override
    public List<BibliosDAO> getUpdateCandidateBiblios() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List bd = null;
        try {
            bd = session.createQuery("FROM BibliosDAO WHERE (updated IS NULL OR updated != 'Y') "
                    + "AND (pubmed_id IS NOT NULL AND pubmed_id !='')").list();
            session.getTransaction().commit();
            System.out.println("committed transaction");
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
System.out.println("bd :: " + bd.size());
        return bd;
    }
 
   public List<BibliosDAO> biblios(int id) {

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List bibIDs = null;
        try {
            bibIDs = session.createQuery(
                    "FROM BibliosDAO WHERE id_biblio=?").setParameter(0, id).list();
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return bibIDs;
    }

    public List<BibliosDAO> submissionBiblios(int id) {

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List biblios = null;
        try {
            biblios = session.createQuery(
                    "FROM SubmissionBibliosDAO WHERE sub_id_sub=?").setParameter(0, id).list();
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return biblios;
    }
    
    public List<BibliosDAO> bibliosStrains(int id) {

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List bibIDs = null;
        try {
            bibIDs = session.createQuery(
                    "FROM BibliosStrainsDAO WHERE str_id_str=?").setParameter(0, id).list();
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return bibIDs;
    }

    public BigInteger bibliosStrainCount(Integer id) {

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        BigInteger count;
        try {
            count = (BigInteger) session.createSQLQuery(
                    "SELECT COUNT(*) FROM biblios_strains WHERE str_id_str=?").setParameter(0, id).uniqueResult();
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }

        return count;
    }

    public SubmissionBibliosDAO getSubBiblioBySubBiblioID(int subBibID) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        SubmissionBibliosDAO sbd = null;
        try {

            sbd = (SubmissionBibliosDAO) session.get(SubmissionBibliosDAO.class,
                    subBibID);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }

        return sbd;
    }

    @Override
    public void save(BibliosDAO bDAO) {
System.out.println("S A V I N G ! !");
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        try {
            session.saveOrUpdate(bDAO);
            session.getTransaction().commit();

        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    public void save(SubmissionBibliosDAO sbDAO) {

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        try {
            session.saveOrUpdate(sbDAO);
            session.getTransaction().commit();

        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    public void delete(int ID) {
        SubmissionBibliosDAO sbDAO = this.getSubBiblioBySubBiblioID(ID);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        try {
            session.delete(sbDAO);
            session.getTransaction().commit();

        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    public void save(BibliosStrainsDAO bsDAO) {

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        try {
            //session.saveOrUpdate(bsDAO);
            session.save(bsDAO);
            session.getTransaction().commit();

        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
    }
    
    public boolean exists(PubmedID pubmed_id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Integer count;
        try {
            count = (Integer) session.createSQLQuery(
                    "SELECT COUNT(*) FROM biblios WHERE pubmed_id = ?").setParameter(0, pubmed_id.toString()).uniqueResult();
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }

        return ((count != null) && (count.intValue() > 0) ? true : false);
    }
}