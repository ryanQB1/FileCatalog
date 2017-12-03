package server.model;

import Common.DeprUserif;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@NamedQueries({
    @NamedQuery(
        name = "deleteDeprUserByName",
        query = "DELETE FROM DeprUser dus WHERE dus.accountId=:accountId"
    )
        ,
    @NamedQuery(
            name = "findDeprUserForRead",
            query = "SELECT dus FROM DeprUser dus"
    )
})

@Entity(name="DeprUser")
public class DeprUser implements DeprUserif{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long accountId;
    
    @Column(name = "username")
    private String usern;
    
    
    @Override
    public String getUsern(){
        return usern;
    }
    
    public DeprUser() {
        this("");
    }
    
    public DeprUser(String usern){
        this.usern=usern;
    }
    
    public long getId(){
        return accountId;
    }
}
