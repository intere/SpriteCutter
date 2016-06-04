package org.csdgn.io;

public class UnsupportedFileTypeException extends RuntimeException {
	private static final long serialVersionUID = 8305533947564135208L;

	public UnsupportedFileTypeException() {
		
	}
	
	public UnsupportedFileTypeException(String message) {
		super(message);
	}
}
