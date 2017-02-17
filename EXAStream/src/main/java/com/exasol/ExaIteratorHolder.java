package com.exasol;

/**
 * 
 * Quick hack to make ExaIterator available globally
 *
 */
public class ExaIteratorHolder {

	private static ExaIterator iter;
	
	public static void setIterator(ExaIterator iterator) {
		iter = iterator;
	}
	
	public static ExaIterator getIterator() {
		return iter;
	}
	
}
