package com.gakshintala.mylabspace.familyconnectrest;


import com.gakshintala.mylabspace.familyconnectrest.api.RestApiController;
import config.Config;
import core.FamilyGraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RestApiController.class, Config.class})
class FamilyConnectRestApplicationTests {

    @Autowired   
    private RestApiController restApiController;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(restApiController);
        Assertions.assertNotNull(restApiController.getFamily());
        Assertions.assertNotNull(((FamilyGraph)restApiController.getFamily()).getValidator());
    }

}