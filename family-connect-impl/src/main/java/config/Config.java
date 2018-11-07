package config;

import core.Family;
import core.FamilyGraph;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import validation.AgeValidator;
import validation.GenderValidator;
import validation.RelationshipValidator;
import validation.Validator;

@Configuration
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
    
    @Bean
    public Family getFamily(Validator validator){
        return new FamilyGraph(validator);
    }
}

