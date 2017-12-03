package server.model;

import Common.Userif;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

@NamedQueries({
    @NamedQuery(
        name = "deleteUserByName",
        query = "DELETE FROM Usero us WHERE us.usern LIKE :username"
    )
    ,
    @NamedQuery(
        name = "findUserByName",
        query = "SELECT us FROM Usero us WHERE us.usern LIKE :username",
        lockMode = LockModeType.OPTIMISTIC
    )
})

@Entity(name="Usero")
public class Usero implements Userif{
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long accountId;
    
    @Column(name = "username", nullable = false)
    private String usern;
    
    @Column(name = "passw", nullable = false)
    private String passw;
    
    @Version
    @Column(name = "OPTLOCK")
    private int versionNum;
    
    public Usero() {
        this("","");
    }
    
    public Usero(String usern, String passw){
        this.usern=usern;
        this.passw=passw;
    }
    
    @Override
    public String getPassw(){
        return passw;
    }
    
    @Override
    public String getUsern(){
        return usern;
    }
}
