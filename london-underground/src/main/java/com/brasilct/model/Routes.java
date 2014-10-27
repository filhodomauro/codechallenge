package com.brasilct.model;

/**
 * 
 * @author mauro
 *
 */
public class Routes {
	
	private Integer line;
	
	private String name;
	
	private String color;
	
	private String stripe;
	
	public Integer getLine() {
		return line;
	}

	public void setLine(Integer line) {
		this.line = line;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getStripe() {
		return stripe;
	}

	public void setStripe(String stripe) {
		this.stripe = stripe;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((line == null) ? 0 : line.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Routes other = (Routes) obj;
		if (line == null) {
			if (other.line != null)
				return false;
		} else if (!line.equals(other.line))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format(" %s - %s", getLine(), getName());
	}
}
