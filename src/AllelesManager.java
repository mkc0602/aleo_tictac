/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.emmanet.model;

/*
 *
 */

import java.util.List;
import org.emmanet.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 *
 * @author
 */
public class AllelesManager {
    
     public List getAlleles() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List strains = null;
        try {
            strains = session.createQuery(
                    "FROM AllelesDAO").list();
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return strains;
    }
     
     //get alleles by id, use SQLQuery and return list.
     
          public List getAllelesByID(String alleleIDs) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List alleles = null;
        try {
            alleles = session.createSQLQuery(
                    "SELECT * FROM alleles where id_allel IN (?)").setParameter(0, alleleIDs).list();
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return alleles;
    }
          
    public AllelesDAO getAlleleByID(int id_allel) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        AllelesDAO ad = null;
        try {
            ad = (AllelesDAO) session.get(AllelesDAO.class,
                    id_allel);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return ad;
    }
     
     
    
    public void save(AllelesDAO aDAO) {

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        try {
            session.saveOrUpdate(aDAO);
            session.getTransaction().commit();

        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
    }
    
}
