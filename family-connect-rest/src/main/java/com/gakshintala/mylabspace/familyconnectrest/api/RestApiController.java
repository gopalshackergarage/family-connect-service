package com.gakshintala.mylabspace.familyconnectrest.api;

import core.Family;
import lombok.Getter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RestApiController {

    @Getter
    private Family family;

    public RestApiController(Family family){
        super();
        this.family = family;
    }
    
}
