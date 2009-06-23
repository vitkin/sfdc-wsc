package com.jeffdouglas.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.jeffdouglas.model.Opportunity;

public class OpportunityValidator implements Validator {
	
	public boolean supports(Class aClass) {
		return com.jeffdouglas.model.Opportunity.class.equals(aClass);
	}	
 
	public void validate(Object obj, Errors errors) {
		
		Opportunity opp = (Opportunity) obj;
 
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "field.required", "Opportunity name is required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "amount", "field.required", "Required field");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "closeDate", "field.required", "A valid date is required in the format YYYY-MM-DD");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "orderNumber", "field.required", "Required field");
		
		if (!errors.hasFieldErrors("amount")) {
			if (opp.getAmount() == 0)
				errors.rejectValue("amount", "not_zero", "Please enter a price greater than $0");
		}
		
		if (!errors.hasFieldErrors("closeDate")) {
			if (!isDate(opp.getCloseDate()))
				errors.rejectValue("closeDate", "valid_date", "A valid date is required in the format YYYY-MM-DD");
		}
		
	}
	
    private boolean isDate(String s)
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        try
        {
            df.parse(s);
        }
        catch(Exception e)
        {
            return false;
        }
        return true;
    }

}
