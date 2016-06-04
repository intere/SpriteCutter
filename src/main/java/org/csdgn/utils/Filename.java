package org.csdgn.utils;

import java.io.File;

public class Filename implements Cloneable {
	private String dir;
	private String name;
	private String ext;
	
	public Filename(File file) {
		this(file.getAbsolutePath());
	}
	
	public Filename(String filename) {
		dir = ext = "";
		name = filename.replace('\\', '/');
		
		int sp = name.lastIndexOf('/');
		
		if(sp != -1) {
			dir = name.substring(0,sp+1);
			name = name.substring(sp+1);
		}
		
		int extp = name.lastIndexOf('.');
		
		if(extp != -1) {
			ext = name.substring(extp+1);
			name = name.substring(0,extp);
		}
		
		sanitizeName();
		sanitizeExtension();
	}
	
	public void setName(String name) {
		this.name = name;
		sanitizeName();
	}
	
	public void setExtension(String ext) {
		this.ext = ext;
		sanitizeExtension();
	}
	
	public void setDirectory(String dir) {
		this.dir = dir;
	}
	
	public String getName() {
		return name;
	}
	
	public String getExtension() {
		return ext;
	}
	
	public String getDirectory() {
		return dir;
	}
	
	private void sanitizeName() {		
		if(name.length() > 0) {
			//remove invalid characters
			name = name.replaceAll("[/\\?%*:|\"<>]", "");
			
			//check if last character is invalid
			while(name.charAt(name.length() - 1) == '.') {
				name = name.substring(0,name.length()-1);
			}
		}
	}
	
	private void sanitizeExtension() {
		if(ext.length() > 0) {
			ext = ext.replaceAll("[/\\?%*:|\"<>]", "");
			
			//remove all start and end periods
			while(ext.charAt(0) == '.') {
				ext = ext.substring(1);
			}
			while(ext.charAt(ext.length() - 1) == '.') {
				ext = ext.substring(0,ext.length()-1);
			}
		}
	}
	
	public Filename clone() {
		try {
			return (Filename)super.clone();
		} catch(CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void setNameIsDirectory() {
		if(name.length() == 0)
			return;
		dir += name + "/";
		name = "";
	}
	
	public File toFile() {
		return new File(toString());
	}
	
	public String toString() {
		return dir + File.separatorChar + name + "." + ext;
	}
}
