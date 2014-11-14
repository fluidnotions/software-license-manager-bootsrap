package com.groupfio.message.pojo;

public class LicFileMessage extends Message{

	public static final String VPASS = "VERFICATION_PASSED";
	public static final String VFAIL = "VERFICATION_FAILED";

	private int licfileByteSize;

	private String licfileCheckSum;

	public int getLicfileByteSize() {
		return licfileByteSize;
	}

	public void setLicfileByteSize(int licfileByteSize) {
		this.licfileByteSize = licfileByteSize;
	}

	public String getLicfileCheckSum() {
		return licfileCheckSum;
	}

	public void setLicfileCheckSum(String licfileCheckSum) {
		this.licfileCheckSum = licfileCheckSum;
	}

	
}
