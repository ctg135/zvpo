package mtusi.zvpo.controllers.requestEntities;

import java.util.Date;

public class SignatureRequestAdd {
    public String threatName;
    public String firstBytes;
    public String remainderHash;
    public int remainderLength;
    public String fileType;
    public int offsetStart;
    public int offsetEnd;
    public Date updatedAt;
    public String status;
}
