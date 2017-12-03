package Common;

import java.io.Serializable;
import java.util.List;

public interface FileHandleif extends Serializable{
    
    public String getName();
    
    public List<? extends DeprUserif> getWritePermissions();
    
    public List<? extends DeprUserif> getReadPermissions();
    
    public String getOwner();
    
    public long getSize();
    
    public boolean getNotify();
    
    public boolean publicRead();
    
    public boolean publicWrite();
}
