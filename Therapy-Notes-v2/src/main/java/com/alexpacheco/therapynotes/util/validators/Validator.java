package com.alexpacheco.therapynotes.util.validators;

@FunctionalInterface
public interface Validator
{
	boolean isValid(String text);
}