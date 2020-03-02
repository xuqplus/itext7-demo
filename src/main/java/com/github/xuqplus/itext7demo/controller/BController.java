package com.github.xuqplus.itext7demo.controller;

import com.github.xuqplus.itext7demo.domain.Seal;
import com.github.xuqplus.itext7demo.repository.SealRepository;
import com.github.xuqplus.itext7demo.req.SignRequest;
import com.github.xuqplus.itext7demo.service.SignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping("api/v1")
public class BController {

	final static String dir = "d:/tmp/data/";
	final static String signedDir = "d:/tmp/data/signed/";

	@Autowired
	SealRepository sealRepository;

	@Autowired
	SignService signService;

	@GetMapping("seal/list")
	public ResponseEntity list(Pageable pageable) {
		Page<Seal> seals = sealRepository.findAll(pageable);
		return ResponseEntity.ok().body(seals);
	}

	@PostMapping("seal/add")
	public ResponseEntity add(@RequestBody Seal seal) {
		if (new File(dir + seal.getFilename()).isFile()) {
			sealRepository.save(seal);
		}
		return ResponseEntity.ok().body(seal);
	}

	@PostMapping("sign")
	public ResponseEntity sign(@RequestBody SignRequest signRequest) throws Exception {
		signService.sign(signRequest);
		return ResponseEntity.ok().body("签署成功");
	}

	@GetMapping("signed/list")
	public ResponseEntity signed() {
		return ResponseEntity.ok().body(new File(signedDir).list());
	}
}
