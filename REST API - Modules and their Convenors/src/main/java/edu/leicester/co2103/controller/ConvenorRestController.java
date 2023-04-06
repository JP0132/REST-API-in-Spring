package edu.leicester.co2103.controller;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import edu.leicester.co2103.domain.Convenor;


import edu.leicester.co2103.repo.ConvenorRepository;
import edu.leicester.co2103.repo.ModuleRepository;


@RestController
@RequestMapping("/convenors")
public class ConvenorRestController {
	public static final Logger logger = LoggerFactory.getLogger(ConvenorRestController.class);
	
	@Autowired
	ConvenorRepository repo;
	
	@Autowired
	ModuleRepository mrepo;
	
	@GetMapping("/convenors")
	public ResponseEntity<List<Convenor>> listAllConvenors(){
		List<Convenor> convenors = repo.findAll();
		if(convenors.isEmpty()) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
			
		}
		return new ResponseEntity(convenors, HttpStatus.OK);
	}

	@PostMapping("/convenors")
	public ResponseEntity<?> createConvenor(@RequestBody Convenor convenor, UriComponentsBuilder ucBuilder){
		if(repo.existsById(convenor.getId())) {
			return new ResponseEntity("A convenor with ID " + convenor.getId() +" already extists", HttpStatus.CONFLICT);
		}
		repo.save(convenor);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/convenors/convenors/{id}").buildAndExpand(convenor.getId()).toUri());
		
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	
	}
	
	@GetMapping("/convenors/{id}")
	public ResponseEntity<?> getConvenor(@PathVariable("id") long id){
		Convenor convenor = repo.findById(id).orElse(null);
		if(convenor == null) {
			return new ResponseEntity("A convenor with id " + id +" not found", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Convenor>(convenor, HttpStatus.OK);
		
	}
	
	@PutMapping("/convenors/{id}")
	public ResponseEntity<?> updateConvenor(@PathVariable("id") long id, @RequestBody Convenor newConvenor){
		if(repo.findById(id).isPresent()) {
			Convenor currentConvenor = repo.findById(id).get();
			currentConvenor.setName(newConvenor.getName());
			//if(newConvenor.getPosition() != Position)
			currentConvenor.setPosition(newConvenor.getPosition());
			currentConvenor.getModules().clear();
			currentConvenor.getModules().addAll(newConvenor.getModules());
			repo.save(currentConvenor);
			return new ResponseEntity<Convenor>(currentConvenor, HttpStatus.OK);
		}
		else {
			return new ResponseEntity("Convenor with id "+ id + " not found", HttpStatus.NOT_FOUND);
		}
		
		
	}
	
	@DeleteMapping("/convenors/{id}")
	public ResponseEntity<?> deleteConvenor(@PathVariable("id") long id){
		Convenor convenor = repo.findById(id).orElse(null);
		if(repo.findById(id).isPresent()) {
			convenor.getModules().clear();
			repo.deleteById(id);
			return ResponseEntity.ok(null);
		}
		
		else {
			return new ResponseEntity("Convenor with id "+ id + " not found.", HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/convenors/{id}/modules")
	public ResponseEntity<?> getModules(@PathVariable("id") long id){
		Convenor convenor = repo.findById(id).orElse(null);
		if(convenor == null) {
			return new ResponseEntity("A convenor with id " + id +" not found", HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity(convenor.getModules(),HttpStatus.OK);
		
	}
	
	
	
	
	
}
