package com.asisdroid.drawfun;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Point {
	public  float x, y;
	public  int col;
	public  int width;
	public Paint.Style style;
	public String brushStyle;
	public int bitmapSize;

	public Point(final float x, final float y, final int col, final int width, final Paint.Style style, final String brush_style,
				 final int bitmap_size) {
		this.x = x;
		this.y = y;
		this.col = col;
		this.width = width;
		this.style = style;
		this.brushStyle = brush_style;
		this.bitmapSize = bitmap_size;
	}
	
	public void draw(final Canvas canvas, final Paint paint) {
		paint.setColor(col);
		canvas.drawCircle(x, y, width/2, paint);
	}
	
	@Override
	public String toString() {
		return x + ", " + y + ", " + col;
	}
}
