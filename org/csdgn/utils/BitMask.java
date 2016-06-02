package org.csdgn.utils;

import java.util.BitSet;

public class BitMask {
	public final int x;
	public final int y;
	public final int width;
	public final int height;
	
	private final BitSet[] data;
	
	public BitMask(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		
		data = new BitSet[h];
		for(int i=0; i<height; ++i)
			data[i] = new BitSet(w);
	}
	
	public void set(int x, int y) {
		data[y].set(x);
	}
	
	public void clear(int x, int y) {
		data[y].clear(x);
	}
	
	public boolean get(int x, int y) {
		return data[y].get(x);
	}
	
	public boolean getOffset(int x, int y) {
		return data[y-this.y].get(x-this.x);
	}
	
	public BitMask getSubMask(int x, int y, int w, int h) {
		BitMask mask = new BitMask(x,y,w,h);
		for(int i = 0; i < h; ++i) {
			mask.data[i] = data[i+y].get(x, x+w);
		}
		return mask;
	}
}
