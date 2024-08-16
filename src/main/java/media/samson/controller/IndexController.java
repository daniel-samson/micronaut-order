package media.samson.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller
public class IndexController {
    @Get
    public String index() {
        return "Hello World!";
    }
}
