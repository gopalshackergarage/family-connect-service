package validation;

import config.Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {Config.class})
class ValidatorTest {
    @Autowired
    Validator validator;
    
    @Test
    void testValidatorAutowiring(){
        assertNotNull(validator);
        assertTrue(validator instanceof GenderValidator);
    }
}
