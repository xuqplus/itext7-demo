package com.github.xuqplus.itext7demo.req;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SignRequest implements Serializable {
	private List<Document> documents;
	private List<Stamper> stampers;
}
