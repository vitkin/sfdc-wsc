package com.jeffdouglas.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class AccountValidator implements Validator {
	
	public boolean supports(Class aClass) {
		return com.jeffdouglas.model.Account.class.equals(aClass);
	}	
 
	public void validate(Object obj, Errors errors) {
		 
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "field.required", "Required field");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "city", "field.required", "Required field");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "state", "field.required", "Required field");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phone", "field.required", "Required field");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "website", "field.required", "Required field");
		
	}

}
