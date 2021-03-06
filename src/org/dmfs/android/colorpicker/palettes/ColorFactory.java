package org.dmfs.android.colorpicker.palettes;

import android.graphics.Color;


/**
 * A factory for colors.
 */
public interface ColorFactory
{

	/**
	 * A Factory that returns colors with a specific HUE value.
	 * 
	 * @author Marten Gajda <marten@dmfs.org>
	 */
	public class ColorShadeFactory implements ColorFactory
	{
		private final float[] mHSL = new float[] { 0, 0, 0 };


		public ColorShadeFactory(float hue)
		{
			mHSL[0] = hue;
		}


		@Override
		public int getColor(int index, int count)
		{
			index++;
			count++;
			float[] hsl = mHSL;

			if (index <= count / 2)
			{
				mHSL[1] = 1f;
				mHSL[2] = index * 2f / count;
			}
			else
			{
				mHSL[1] = 2f - index * 2f / count;
				mHSL[2] = 1f;
			}
			return Color.HSVToColor(255, hsl);
		}
	}

	/**
	 * A factory that returns the entire palette with a specific saturation and lightness value.
	 * 
	 * @author Marten Gajda <marten@dmfs.org>
	 */
	public class RainbowColorFactory implements ColorFactory
	{
		private final float[] mHSL = new float[] { 0, 0, 0 };


		public RainbowColorFactory(float saturation, float lightness)
		{
			mHSL[1] = saturation;
			mHSL[2] = lightness;
		}


		@Override
		public int getColor(int index, int count)
		{
			count += 1;
			float[] hsl = mHSL;

			hsl[0] = index * 360f / count;

			return Color.HSVToColor(255, hsl);
		}
	}

	/**
	 * A Factory that combines multiple factories into one, by concatenation of the palettes.
	 * 
	 * @author Marten Gajda <marten@dmfs.org>
	 */
	public class CombinedColorFactory implements ColorFactory
	{

		private final ColorFactory[] mFactories;


		public CombinedColorFactory(ColorFactory... factories)
		{
			mFactories = factories;
		}


		@Override
		public int getColor(int index, int count)
		{
			int factoryCount = mFactories.length;
			return mFactories[(index * factoryCount) / count].getColor(index % (count / factoryCount), count / factoryCount);
		}

	}

	/**
	 * Shades of red (0°).
	 */
	public final static ColorFactory RED = new ColorShadeFactory(0);

	/**
	 * Shades of orange (37°).
	 */
	public final static ColorFactory ORANGE = new ColorShadeFactory(37f);

	/**
	 * Shades of yellow (60°).
	 */
	public final static ColorFactory YELLOW = new ColorShadeFactory(60f);

	/**
	 * Shades of green (120°).
	 */
	public final static ColorFactory GREEN = new ColorShadeFactory(120f);

	/**
	 * Shades of cyan (180°).
	 */
	public final static ColorFactory CYAN = new ColorShadeFactory(180f);

	/**
	 * Shades of blue (240°).
	 */
	public final static ColorFactory BLUE = new ColorShadeFactory(240f);

	/**
	 * Shades of purple (280°).
	 */
	public final static ColorFactory PURPLE = new ColorShadeFactory(280f);

	/**
	 * Shades of pink (320°).
	 */
	public final static ColorFactory PINK = new ColorShadeFactory(320f);

	/**
	 * Rainbow colors.
	 */
	public final static ColorFactory RAINBOW = new RainbowColorFactory(1f, 1f);

	/**
	 * Pastel colors (same as {@link #RAINBOW} just with a saturation of 50%).
	 */
	public final static ColorFactory PASTEL = new RainbowColorFactory(0.5f, 1f);


	/**
	 * Return a color for the given index into a palette of <code>count</code> colors.
	 * 
	 * @param index
	 *            The index of the color.
	 * @param count
	 *            The total number of colors in this palette.
	 * @return The color.
	 */
	public int getColor(int index, int count);
}