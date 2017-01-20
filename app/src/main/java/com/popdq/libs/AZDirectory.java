package com.popdq.libs;

import java.util.ArrayList;

public class AZDirectory {
	private String name;
	private String path;
	private int numImage;
	private int numImageSelected;
	private String date;

	private ArrayList<AZPhoto> listImage = new ArrayList<AZPhoto>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getNumImage() {
		return numImage;
	}

	public void setNumImage(int numImage) {
		this.numImage = numImage;
	}

	public int getNumImageSelected() {
		return numImageSelected;
	}

	public void setNumImageSelected(int numImageSelected) {
		this.numImageSelected = numImageSelected;
	}

	public ArrayList<AZPhoto> getListImage() {
		return listImage;
	}

	public void setListImage(ArrayList<AZPhoto> listImage) {
		this.listImage = listImage;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void addImage(AZPhoto image) {
		listImage.add(image);
	}

	public String getPathImage(int position) {
		return ((AZPhoto) getListImage().get(position)).getPath();
	}

	public AZPhoto getFirstImage() {
		if (!listImage.isEmpty()) {
			return listImage.get(0);
		}
		return null;
	}
}
