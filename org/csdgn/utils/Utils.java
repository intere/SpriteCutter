package org.csdgn.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class Utils {
	private Utils() {}
	
	public static BufferedImage createNearestNeighborThumbnail(Image img, int thumbWidth, int thumbHeight) {
		return createThumbnail(img,thumbWidth,thumbHeight,0);
	}
	
	public static BufferedImage createBilinearThumbnail(Image img, int thumbWidth, int thumbHeight) {
		return createThumbnail(img,thumbWidth,thumbHeight,1);
	}
	
	public static BufferedImage createBicubicThumbnail(Image img, int thumbWidth, int thumbHeight) {
		return createThumbnail(img,thumbWidth,thumbHeight,2);
	}
	
	private static BufferedImage createThumbnail(Image img, int thumbWidth, int thumbHeight, int quality) {
		BufferedImage out = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gx = out.createGraphics();
		
		switch(quality) {
		case 0:
			gx.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			break;
		case 1:
			gx.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			break;
		case 2:
			gx.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		}
		
		int tW = thumbWidth, tH = thumbHeight;
		double iamgeRatio = img.getWidth(null) / (double)img.getHeight(null);
		if(iamgeRatio >= 1) {
			tH = (int)(thumbWidth / iamgeRatio + 0.5);
		} else {
			tW = (int)(thumbHeight * iamgeRatio + 0.5);
		}
		
		int xOffset = (thumbWidth - tW) >> 1;
		int yOffset = (thumbHeight - tH) >> 1;
		
		gx.drawImage(img, xOffset, yOffset, tW, tH, null);
		
		return out;
	}
}
