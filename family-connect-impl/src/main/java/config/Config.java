package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import validation.AgeValidator;
import validation.GenderValidator;
import validation.RelationshipValidator;
import validation.Validator;

@Configuration
@ComponentScan(basePackages = {"validation", "core"})
public class Config {
    @Bean
    public Validator prepareValidator(){
        Validator genderValidator = new GenderValidator();
        Validator ageValidator = new AgeValidator();
        Validator relationShipValidator = new RelationshipValidator();

        genderValidator.setNextValidatorInChain(ageValidator);
        ageValidator.setNextValidatorInChain(relationShipValidator);

        return genderValidator;
    }
}

