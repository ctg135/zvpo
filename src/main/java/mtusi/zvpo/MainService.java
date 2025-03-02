package mtusi.zvpo;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@Component
@Service
@RestController
public class MainService {

    public MainService mainService() {
        return new MainService();
    }

    public String getMessage() {
        return "t e s t  f o r  a p i";
    }
}
