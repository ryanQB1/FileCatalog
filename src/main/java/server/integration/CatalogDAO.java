package server.integration;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import server.model.DeprUser;
import server.model.FileHandle;
import server.model.Usero;

public class CatalogDAO {
    
    private final EntityManagerFactory emFactory;
    private final ThreadLocal<EntityManager> threadLocalEntityManager = new ThreadLocal<>();
    
    public CatalogDAO() {
        emFactory = Persistence.createEntityManagerFactory("FileCatalogPU");
    }
    
    public FileHandle findFile(String fileName, boolean commitAfterSearching) {
        if(fileName==null) return null;
        try {
            EntityManager em = beginTransaction();
            try {
                return em.createNamedQuery("findFileByName", FileHandle.class).
                        setParameter("fileName", fileName).getSingleResult();
            } catch (NoResultException noSuchFile) {
                return null;
            }
        } finally {
            if(commitAfterSearching){
                commitTransaction();
            }
        }
    }
    
    public Usero findUsero(String username){
        if(username==null) return null;
        try {
            EntityManager em = beginTransaction();
            try {
                return em.createNamedQuery("findUserByName", Usero.class).
                        setParameter("username", username).getSingleResult();
            } catch (NoResultException noSuchFile) {
                return null;
            }
        } finally {
            commitTransaction();
        }
    }
    
    public DeprUser findDeprUsero(String username){
        if(username==null) return null;
        try {
            EntityManager em = beginTransaction();
            try {
                Usero temp = em.createNamedQuery("findUserByName", Usero.class).
                        setParameter("username", username).getSingleResult();
                if(temp==null) return null;
                return new DeprUser(temp.getUsern());
            } catch (NoResultException noSuchFile) {
                return null;
            }
        } finally {
            commitTransaction();
        }
    }
    
    public void createNewUsero(Usero us) {
        try {
            EntityManager em = beginTransaction();
            em.persist(us);
        } finally {
            commitTransaction();
        }
    }
    
    public void deleteUsero(String username) {
        try {
            EntityManager em = beginTransaction();
            em.createNamedQuery("deleteUserByName", Usero.class).
                    setParameter("username", username).executeUpdate();
        } finally {
            commitTransaction();
        }
    }
    
    public void createNewFile(FileHandle file) {
        EntityManager em = beginTransaction();
        em.persist(file);
    }
    
    public void updateFile() {
        commitTransaction();
    }
    
    public void deleteFile(String fileName){
        try {
            EntityManager em = beginTransaction();
            em.createNamedQuery("deleteFileByName", FileHandle.class).
                    setParameter("fileName", fileName).executeUpdate();
        } finally {
            commitTransaction();
        }
    }
    
    public void deleteDepr(List<DeprUser> duss){
        for(DeprUser dus : duss){
        try {
            EntityManager em = beginTransaction();
            em.createNamedQuery("deleteDeprUserByName", DeprUser.class).
                    setParameter("accountId", dus.getId()).executeUpdate();
        } finally {
            commitTransaction();
        }
        }
    }
    
    private void commitTransaction() {
        threadLocalEntityManager.get().getTransaction().commit();
    }
    
    private EntityManager beginTransaction() {
        EntityManager em = emFactory.createEntityManager();
        threadLocalEntityManager.set(em);
        EntityTransaction transaction = em.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        return em;
    }
    
    public List<FileHandle> findMyFiles(String username, boolean commit) {
        if(username==null) return null;
        try {
            EntityManager em = beginTransaction();
            try {
                return em.createNamedQuery("findFileByOwner", FileHandle.class).
                        setParameter("fileOwner", username).getResultList();
            } catch (NoResultException noSuchFile) {
                return null;
            }
        } finally {
            if(commit){
                commitTransaction();
            }
        }
    }
    
    public List<FileHandle> findAllFiles() {
        try {
            EntityManager em = beginTransaction();
            try {
                return em.createNamedQuery("findAllFiles", FileHandle.class).getResultList();
            } catch (NoResultException noSuchFile) {
                return null;
            }
        } finally {
            commitTransaction();
        }
    }
    
    public List<FileHandle> findWriteFiles(String username) {
        if(username==null) return null;
        try {
            EntityManager em = beginTransaction();
            try {
                return em.createNamedQuery("findFileInWrite", FileHandle.class).
                        setParameter("username", username).getResultList();
            } catch (NoResultException noSuchFile) {
                return null;
            }
        } finally {
            commitTransaction();
        }
    }
    
    public boolean checkPassw(String toCheckUsern, String toCheckPassw){
        if(toCheckUsern==null || toCheckPassw==null) return false;
        Usero temp;
        EntityManager em = beginTransaction();
        try {
            temp = em.createNamedQuery("findUserByName", Usero.class).
                    setParameter("username", toCheckUsern).getSingleResult();
        } catch (NoResultException noSuchUser) {
            return false;
        }
        commitTransaction();
        return temp.getPassw().equals(toCheckUsern);
    }
}
