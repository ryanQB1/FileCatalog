package server.model;

import Common.FileHandleif;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.LockModeType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Version;

@NamedQueries({
    @NamedQuery(
        name = "deleteFileByName",
        query = "DELETE FROM FileHandle file WHERE file.fileName LIKE :fileName"
    )
    ,
    @NamedQuery(
        name = "findFileByName",
        query = "SELECT file FROM FileHandle file WHERE file.fileName LIKE :fileName",
        lockMode = LockModeType.OPTIMISTIC
    )
    ,
    @NamedQuery(
        name = "findFileByOwner",
        query = "SELECT file FROM FileHandle file WHERE file.ownerName LIKE :fileOwner",
        lockMode = LockModeType.OPTIMISTIC
    )
    ,
    @NamedQuery(
        name = "findFileInRead",
        query = "SELECT file FROM FileHandle file WHERE :username MEMBER OF file.allowRead",
        lockMode = LockModeType.OPTIMISTIC
    )
    ,
    @NamedQuery(
        name = "findFileInWrite",
        query = "SELECT file FROM FileHandle file WHERE :username MEMBER OF file.allowWrite",
        lockMode = LockModeType.OPTIMISTIC
    )
    ,
    @NamedQuery(
        name = "findAllFiles",
        query = "SELECT file FROM FileHandle file"
    )
})

@Entity(name="FileHandle")
public class FileHandle implements FileHandleif{
    
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long fileId;
    
    
    @Column(name = "name", nullable = false)
    private String fileName;
    
    @Column(name = "filesize", nullable = false)
    private long fileSize;
    
    @Column(name = "ispublicread", nullable = false)
    private boolean isPublicRead;
    
    @Column(name = "ispublicwrite", nullable = false)
    private boolean isPublicWrite;
    
    @Column(name = "owner", nullable = false)
    private String ownerName;
    
    @Column(name = "notifyme", nullable = false)
    private boolean notifyme;
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "writePermissions")
    private List<DeprUser> allowWrite;
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "readPermissions")
    private List<DeprUser> allowRead;
    
    @Version
    @Column(name = "OPTLOCK")
    private int versionNum;
    
    public FileHandle(){
        this.fileName="";
        this.fileSize=0;
        this.isPublicRead=false;
        this.isPublicWrite=false;
        this.ownerName="";
        this.notifyme=false;
    }
    
    public FileHandle(String fileName, long fileSize, boolean isPublicRead, boolean isPublicWrite, String ownerName, boolean notifyme){
        this.fileName=fileName;
        this.fileSize=fileSize;
        this.isPublicRead=isPublicRead;
        this.isPublicWrite=isPublicWrite;
        this.ownerName=ownerName;
        this.notifyme=notifyme;
    }
    
    @Override
    public String getName(){
        return fileName;
    }
    
    @Override
    public List<DeprUser> getWritePermissions(){
        return allowWrite;
    }
    
    @Override
    public List<DeprUser> getReadPermissions(){
        return allowRead;
    }
    
    public void setOwner(String newOwner){
        this.ownerName=newOwner;
    }
    
    @Override
    public String getOwner(){
        return ownerName;
    }
    
    @Override
    public boolean publicRead(){
        return isPublicRead;
    }
    
    @Override
    public boolean publicWrite(){
        return isPublicWrite;
    }
    
    @Override
    public long getSize(){
        return fileSize;
    }
    
    @Override
    public boolean getNotify(){
        return notifyme;
    }
    
    public void giveWritePermissions(Usero us){
        DeprUser dus = new DeprUser(us.getUsern());
        allowWrite.add(dus);
    }
    
    public void giveWritePermissions(DeprUser dus){
        allowWrite.add(dus);
    }
    
    public void giveReadPermissions(Usero us) {
        DeprUser dus = new DeprUser(us.getUsern());
        allowRead.add(dus);
    }
    
    public void giveReadPermissions(DeprUser dus) {
        allowRead.add(dus);
    }
    
    public void setNotfiyMe(boolean setTo) {
        notifyme=setTo;
    }
    
    public void setPublicWrite(boolean setTo) {
        isPublicWrite = setTo;
    }
    
    public void setPublicRead(boolean setTo) {
        isPublicRead = setTo;
    }
}
