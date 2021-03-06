/*
 * Copyright (C) 2013 Marten Gajda <marten@dmfs.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package org.dmfs.android.colorpicker;

import org.dmfs.android.colorpicker.palettes.AbstractPalette;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;


/**
 * A fragment that shows a color palette.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class PaletteFragment extends Fragment implements OnItemClickListener
{
	/**
	 * Key to store the palette in a {@link Bundle}.
	 */
	private final static String KEY_PALETTE = "org.dmfs.android.colorpicker.PALETTE";

	public interface OnColorSelectedListener
	{
		public void onColorSelected(int color, String colorName, String paletteName);
	}

	/**
	 * The palette to show.
	 */
	private AbstractPalette mPalette;

	/**
	 * An adapter for the palette.
	 */
	private PaletteGridAdapter mAdapter;


	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putParcelable(KEY_PALETTE, mPalette);
	}


	public void setPalette(AbstractPalette palette)
	{
		mPalette = palette;
	}


	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if (savedInstanceState != null)
		{
			// load the palette from the bundle
			mPalette = savedInstanceState.getParcelable(KEY_PALETTE);
		}

		/*
		 * TODO: build the layout programmatically to get rid of the resources, so we can distribute this in a single jar
		 */
		final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.org_dmfs_colorpickerdialog_palette_grid, container, false);
		final GridView gridview = (GridView) rootView.findViewById(android.R.id.content);

		mAdapter = new PaletteGridAdapter(getActivity(), mPalette);
		gridview.setAdapter(mAdapter);
		gridview.setOnItemClickListener(this);
		gridview.setNumColumns(mAdapter.getNumColumns());

		/*
		 * Adjust the layout of the gridview to a square.
		 * 
		 * Inspired by Bill Lahti, see http://blahti.wordpress.com/2012/07/23/three-variations-of-image-squares/
		 */
		gridview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
		{
			public void onGlobalLayout()
			{
				int parentHeight = rootView.getHeight() - rootView.getPaddingTop() - rootView.getPaddingBottom();
				int parentWidth = rootView.getWidth() - rootView.getPaddingLeft() - rootView.getPaddingRight();

				int gridWidth = Math.min(parentWidth, parentHeight);

				int columnSpacing;
				if (android.os.Build.VERSION.SDK_INT >= 16)
				{
					columnSpacing = gridview.getHorizontalSpacing() * (mAdapter.getNumColumns() - 1);
				}
				else
				{
					/*
					 * TODO: getHorizontalSpacing() has been introduced in SDK level 16. We need to find a way to get get the actual spacing. Until then we use
					 * a hard coded value of 8 dip.
					 * 
					 * One way would be to use a dimension in the layout. That would allow us to resolve the dimension here. However, that would be one step
					 * away from a library without resource dependencies. Maybe there is an Android dimension resource with a reasonable value?
					 */
					DisplayMetrics metrics = inflater.getContext().getResources().getDisplayMetrics();
					if (android.os.Build.VERSION.SDK_INT > 10)
					{
						columnSpacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, metrics) * (mAdapter.getNumColumns() - 1);
					}
					else
					{
						// Android 2 seems to add spacing around the entire gridview
						columnSpacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, metrics) * mAdapter.getNumColumns();
					}
				}

				// width of a single column
				int columnWidth = (gridWidth - columnSpacing) / mAdapter.getNumColumns();

				// estimated width of the grid
				int actualGridWidth = mAdapter.getNumColumns() * columnWidth + columnSpacing;

				// add padding to center the grid if we don't use the entire space due to rounding errors
				if (actualGridWidth < gridWidth - 1)
				{
					int padding = (gridWidth - actualGridWidth) / 2;
					if (padding > 0)
					{
						gridview.setPadding(padding, padding, padding, padding);

					}
				}
				else
				{
					// no padding needed
					gridview.setPadding(0, 0, 0, 0);
				}

				// set the column width
				gridview.setColumnWidth(columnWidth);

				android.view.ViewGroup.LayoutParams params = gridview.getLayoutParams();
				if (params == null || params.height != gridWidth) // avoid unnecessary updates
				{
					LayoutParams lparams = new LinearLayout.LayoutParams(gridWidth, gridWidth);
					gridview.setLayoutParams(lparams);
				}
			}
		});
		return rootView;
	}


	@Override
	public void onItemClick(AdapterView<?> gridView, View View, int position, long id)
	{
		// pass the click event to the parent fragment
		Fragment parent = getParentFragment();
		if (parent instanceof OnColorSelectedListener)
		{
			OnColorSelectedListener listener = (OnColorSelectedListener) parent;
			listener.onColorSelected(mPalette.getColor(position), mPalette.getColorName(position), mPalette.getName());
		}
	}
}
