package com.github.xuqplus.itext7demo.controller;

import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequestMapping("api/v1/file")
@RestController
public class AController {

	final static String dir = "d:/tmp/data/";

	static {
		new File(dir).mkdirs();
	}

	@PostMapping("upload")
	public ResponseEntity upload(MultipartFile[] files, MultipartFile file) throws IOException {
		if (null != files && files.length > 0) {
			List<String> filenames = new ArrayList<>(files.length);
			for (MultipartFile f : files) {
				String filename = f.getOriginalFilename();
				f.transferTo(new File(dir + filename));
				filenames.add(filename);
			}
			return ResponseEntity.ok().body(filenames);
		}
		if (null != file) {
			String filename = file.getOriginalFilename();
			file.transferTo(new File(dir + filename));
			return ResponseEntity.ok().body(filename);
		}
		return ResponseEntity.ok().build();
	}

	@GetMapping("download")
	public ResponseEntity download(String filename) throws IOException {
		File file = new File(dir + filename);
		if (file.exists() && file.isFile()) {
			Optional<MediaType> mediaType = MediaTypeFactory.getMediaType(filename);
			return ResponseEntity.ok()
					.contentType(mediaType.orElse(MediaType.APPLICATION_OCTET_STREAM))
					.header("Content-Disposition", "attachment;fileName=" + filename)
					.body(new UrlResource(file.toURI()));
		}
		return ResponseEntity.ok().build();
	}
}
