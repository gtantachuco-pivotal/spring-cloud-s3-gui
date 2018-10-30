package com.gtphonehome.spring.cloud.aws.s3;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3ObjectView extends S3ObjectSummary {
	private String uri;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getText() {
		return getKey() + 
				"  [" + 
				getSize() + 
				"  bytes]";
	}

}
