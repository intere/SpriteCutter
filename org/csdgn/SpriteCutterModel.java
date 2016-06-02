package org.csdgn;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import org.csdgn.io.UnsupportedFileTypeException;
import org.csdgn.utils.BitMask;
import org.csdgn.utils.Filename;

/**
 * This class does the actual cutting and stuff.
 * @author Chase
 *
 */
public class SpriteCutterModel {
	private BitMask mask;
	
	protected boolean secondaryMask = false;
	protected boolean maskOutput = false;
	protected boolean eightWay = false;
	protected boolean wide = false;
	protected boolean multiSampleBG = false;
	
	/**
	 * do not use unless you actually need to debug (it is very laggy and verbose)
	 */
	private boolean debug = false;
	private boolean debugTiles = false;
	private boolean debugMask = false;
	private boolean debugPoints = false;
	
	private boolean secondaryMaskToggle = false;
	
	private int width, height, bg_color;
	private BufferedImage raw;
	private ArrayList<BitMask> shapes = new ArrayList<BitMask>();
	
	public BufferedImage[] images;
	
	public Filename sourceFilename;
	public Filename targetFilename;
	
	public void loadImage(File file) throws Exception {
		if(!file.exists()) {
			throw new FileNotFoundException("File Not Found!");
		}
		sourceFilename = new Filename(file.getAbsolutePath());
		
		raw = ImageIO.read(sourceFilename.toFile());
		
		if(raw == null)
			throw new UnsupportedFileTypeException("File type not supported!");
		
		width = raw.getWidth();
		height = raw.getHeight();
	}
	
	public void cutImage() throws Exception {
		bg_color = raw.getRGB(0, 0);
		
		if(multiSampleBG) {
			//sample from 8 edge positions and take the mode
			//if no mode exists, fallback to original
			final double[] samplePosition = new double[] {
					0,0,  0.5,0,  1,0,
					0,0.5,  1,0.5,
					0,1,  0.5,1,  1,1
			};
			
			int[] data = new int[samplePosition.length >> 1];
			
			for(int i=0; i < data.length; ++i) {
				int n = i << 1;
				int x = (int)(width*samplePosition[n]);
				int y = (int)(height*samplePosition[n+1]);
				if(x == width) --x;
				if(y == height) --y;
				data[i] = raw.getRGB(x,y);
			}
			
			int best = 1;
			for(int i = 0; i < data.length; ++i) {
				int n = 0;
				for(int j = 0; j < data.length; ++j)
					if(data[j] == data[i])
						++n;
				if(n > best) {
					best = n;
					bg_color = data[i];
				}
			}
			
		}
		
		shapes.clear();
		
		generateMask();
        
		targetFilename = sourceFilename.clone();
        
        int n = 0;
        for(int y=0;y<height;++y) {
        	for(int x=0;x<width;++x) {
        		if(mask.get(x, y)) {
        			Point p = new Point(x,y);
        			if(debugTiles) {
        				System.out.printf("Generating sprite %d. Found @ %d,%d.\n",n++,x,y);
        			}
        			BitMask mask = floodFind(p);
        			if(mask != null)
        				shapes.add(mask);
        		}
            }
        }
	}
	
	public void generateImages() throws Exception {
		images = new BufferedImage[shapes.size()];
		
		if(debug) {
			System.out.print("Generating images with options[ ");
			if(secondaryMask)
				System.out.print("2nd ");
			if(maskOutput)
				System.out.print("mask ");
			if(eightWay)
				System.out.print("8way ");
			if(wide)
				System.out.print("wide ");
			System.out.println("]");
		}
		
		for(int i=0;i<shapes.size();++i) {
        	BitMask shape = shapes.get(i);
        	
        	if(debugTiles) {
        		System.out.printf("Generating sprite %d @ %d %d %d %d\n",i,shape.x,shape.y,shape.width,shape.height);
        	}
        	
        	BufferedImage sub = null;
        	if(maskOutput) {
        		sub = new BufferedImage(shape.width, shape.height,BufferedImage.TYPE_INT_ARGB);
        		for(int y=0;y<shape.height;++y)
                	for(int x=0;x<shape.width;++x)
                		if(shape.get(x, y))
                			sub.setRGB(x, y, raw.getRGB(shape.x+x, shape.y+y));
        	} else {
        		sub = raw.getSubimage(shape.x, shape.y, shape.width, shape.height);
        	}
        	
        	images[i] = sub;
		}
		
	}
	
	public void writeImages() throws Exception {
		 //for every shape create a subimage and save it
        for(int i=0;i<shapes.size();++i) {
        	targetFilename.setName(String.format("%s_%04d", sourceFilename.getName(),i));
        	targetFilename.setExtension("png");
        	ImageIO.write(images[i], "png", targetFilename.toFile());
        }
	}
	
	private final void generateMask() {
		mask = new BitMask(0,0,width,height);
		for(int y=0;y<height;++y) {
        	for(int x=0;x<width;++x) {
        		if(raw.getRGB(x, y) != bg_color) {
        			mask.set(x, y);
        		}
        	}
		}
		
		if(secondaryMask) {
			secondaryMaskToggle = secondaryMask;
			for(int y=0;y<height;++y) {
	        	for(int x=0;x<width;++x) {
	        		//find first subentry
	        		if(secondaryMaskToggle && mask.get(x, y)) {
	        			bg_color = raw.getRGB(x, y);
	        			secondaryMaskToggle = false;
	        		}
	        		if(secondaryMaskToggle)
	        			continue;
	        		if(mask.get(x, y) && raw.getRGB(x, y) == bg_color) {
	        			mask.clear(x, y);
	        		}
	        	}
			}
		}
	}
	
	private  final BitMask floodFind(Point p) {
		ArrayDeque<Point> queue = new ArrayDeque<Point>();
		BitMask out_mask = new BitMask(0,0,width,height);
		queue.add(p);
		
		Rectangle r = new Rectangle(Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE);
		
		while(!queue.isEmpty()) {
			Point n = queue.pollLast();
			if(n.x <= 0 || n.x >= width || n.y <= 0 || n.y >= height)
				continue;
			
			if(mask.get(n.x, n.y)) {
				out_mask.set(n.x, n.y);
				
				if(debugPoints) {
					System.out.println("Adding point to mask... " + n);
				}
				
				if(n.x < r.x) r.x = n.x;
	    		if(n.y < r.y) r.y = n.y;
	    		if(n.x > r.width) r.width = n.x;
	    		if(n.y > r.height) r.height = n.y;
				
				mask.clear(n.x, n.y);
				
				int w = n.x;
				int e = n.x;
				
				while(w > 0 && mask.get(--w, n.y));
				while(e < width-1 && mask.get(++e, n.y));
				
				for(int i=w;i<=e;++i) {
					queue.add(new Point(i,n.y));
					
					queue.add(new Point(i,n.y-1));
					queue.add(new Point(i,n.y+1));
					
					if(wide) {
						queue.add(new Point(i,n.y-2));
						queue.add(new Point(i,n.y+2));
					}
					
					if(eightWay && (i==w||i==e)) {
						queue.add(new Point(i-1,n.y-1));
						queue.add(new Point(i+1,n.y-1));
						queue.add(new Point(i-1,n.y+1));
						queue.add(new Point(i+1,n.y+1));
						
						if(wide) {
							queue.add(new Point(i-1,n.y-2));
							queue.add(new Point(i+1,n.y-2));
							queue.add(new Point(i-1,n.y+2));
							queue.add(new Point(i+1,n.y+2));
							
							queue.add(new Point(i-2,n.y-1));
							queue.add(new Point(i+2,n.y-1));
							queue.add(new Point(i-2,n.y+1));
							queue.add(new Point(i+2,n.y+1));
							
							queue.add(new Point(i-2,n.y-2));
							queue.add(new Point(i+2,n.y-2));
							queue.add(new Point(i-2,n.y+2));
							queue.add(new Point(i+2,n.y+2));
						}
					}
				}
			}
		}
		
		if(r.x == Integer.MAX_VALUE || r.width == 0 || r.height == 0)
			return null;
		
		//do not change this to -= it will not work the way you expect ;3
		// as in short the + 1 would have to become - 1 if you do that, which could be confusing to read
		r.width = r.width - r.x + 1;
		r.height = r.height - r.y + 1;
    	
    	if(debugMask) {
    		System.out.printf("Generating mask %d %d %d %d\n",r.x,r.y,r.width,r.height);
    	}
    	
		return out_mask.getSubMask(r.x, r.y, r.width, r.height);
	}
}
