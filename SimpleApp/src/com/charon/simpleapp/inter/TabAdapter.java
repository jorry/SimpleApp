package com.charon.simpleapp.inter;

import android.view.View;

/**
 * Interface to get the tab view.
 * 
 * @author Charon Chui
 */
public interface TabAdapter {
	/**
	 * Tab View
	 * 
	 * @param position
	 *            Position of the tab view.
	 * @return View View of the tab.
	 */
	public View getView(int position);

	/**
	 * Segmentation view between two tab.
	 * 
	 * @return View, If don't use you this may return null.
	 */
	public View getSeparator();
}
