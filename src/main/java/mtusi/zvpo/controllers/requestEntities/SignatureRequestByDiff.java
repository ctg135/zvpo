package mtusi.zvpo.controllers.requestEntities;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class SignatureRequestByDiff {
    public @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date since;
}
