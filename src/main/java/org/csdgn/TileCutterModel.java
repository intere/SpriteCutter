package org.csdgn;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.ImageIO;

import org.csdgn.hash.FowlerNollVo;
import org.csdgn.io.UnsupportedFileTypeException;

public class TileCutterModel {
	private Dimension size = new Dimension(16, 16);
	private Dimension spacing = new Dimension(0, 0);
	private Insets margin = new Insets(0, 0, 0, 0);

	private int width;
	private int height;
	private BufferedImage raw;
	private Set<Tile> set = new HashSet<Tile>();

	public void loadImage(File file) throws Exception {
		raw = ImageIO.read(file);

		if (raw == null)
			throw new UnsupportedFileTypeException("File type not supported!");

		width = raw.getWidth();
		height = raw.getHeight();
	}

	public void setTileSize(int width, int height) {
		size.setSize(width, height);
	}

	public void setMargin(int top, int left, int bottom, int right) {
		margin.set(top, left, bottom, right);
	}

	public void setSpacing(int width, int height) {
		spacing.setSize(width, height);
	}

	public void cutTiles() {
		if(raw == null)
			return;
		
		int mw = width - margin.left - margin.right;
		int mh = height - margin.top - margin.bottom;
		int tw = mw / (size.width + spacing.width);
		int th = mh / (size.height + spacing.height);

		// for reference
		// int xoff = margin.left;
		// int yoff = margin.top;
		set.clear();

		for (int y = 0; y < th; ++y)
			for (int x = 0; x < tw; ++x) {
				Tile tile = new Tile(raw.getSubimage(
						x * (size.width + spacing.width) + margin.left,
						y * (size.height + spacing.height) + margin.top,
						size.width, size.height) );
				set.add(tile);
			}
		
		System.out.println("Count: "+set.size());
		System.out.println("Scale: "+set.size()*size.width*size.height);
		
		int count = set.size();
		Dimension output = new Dimension();
		
		//determine the optimal tileset size for the number of discovered tiles
		//if you have more then 16k tiles, I weep for you
		for(int i=2;i<128;i*=2) {
			int n = i*i;
			if(count <= n) {
				output.setSize(i,i);
				break;
			}
			if(count <= n*2) {
				output.setSize(i,i*2);
				break;
			}
		}
		
		output.width *= size.width;
		output.height *= size.height;
		
		BufferedImage image = new BufferedImage(output.width,output.height,raw.getType());
		Graphics2D g = image.createGraphics();
		int x = 0;
		int y = 0;
		Iterator<Tile> it = set.iterator();
		while(it.hasNext()) {
			Tile tile = it.next();
			g.drawImage(tile.image, x, y, null);
			x += size.width;
			if(x >= output.width) {
				y += size.height;
				x = 0;
			}
		}
		
		try {
			ImageIO.write(image, "png", new File("data.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class Tile {
		public BufferedImage image;
		private int hashCode;

		public Tile(BufferedImage image) {
			this.image = image;
			FowlerNollVo hash = new FowlerNollVo();
			hash.reset();
			for(int y = 0; y < size.height; ++y)
				for(int x = 0; x < size.width; ++x) {
					int rgb = image.getRGB(x, y);
					hash.update(rgb);
					hash.update(rgb >>> 8);
					hash.update(rgb >>> 16);
				}
			
			hashCode = (int)hash.getValue();
		}

		public int hashCode() {
			return hashCode;
		}

		public boolean equals(Object obj) {
			if (hashCode != obj.hashCode())
				return false;
			if (obj instanceof Tile) {
				Tile tile = (Tile) obj;
				
				for(int y = 0; y < size.height; ++y)
					for(int x = 0; x < size.width; ++x)
						if(tile.image.getRGB(x, y) != image.getRGB(x, y))
							return false;
				
				return true;
			}
			
			
			return false;
		}
	}
}
