package edu.leicester.co2103.controller;

import java.util.ArrayList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.leicester.co2103.domain.Convenor;
import edu.leicester.co2103.domain.Module;
import edu.leicester.co2103.domain.Session;
import edu.leicester.co2103.repo.ConvenorRepository;
import edu.leicester.co2103.repo.ModuleRepository;
import edu.leicester.co2103.repo.SessionRepository;

@RestController
@RequestMapping("/sessions")
public class SessionRestController {
	
	public static final Logger logger = LoggerFactory.getLogger(ConvenorRestController.class);
	
	@Autowired
	ModuleRepository mrepo;
	
	@Autowired
	ConvenorRepository crepo;
	
	@Autowired
	SessionRepository repo;
	
	@DeleteMapping("/sessions")
	public ResponseEntity<?> deleteAllSession(){
		Iterable<Session> sessions = repo.findAll();
		int counter = 0;
		for(Session s : sessions) {
			 counter++;
		}

		if(counter == 0) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		else {
			repo.deleteAll();
			return ResponseEntity.ok(null);	
		}
		
	}
	
	@GetMapping("/sessions")
	public ResponseEntity<?> filterSession(@RequestParam(required =  false, name = "convenor", defaultValue="0") long id, @RequestParam(required =  false, name = "module") String code) {
		
		//Request Parameters are not given
		if(code == null && id == 0) {
			return new ResponseEntity("No request parameters", HttpStatus.NOT_FOUND);
		}
		
		//Convenor id was given but module code wasn't
		if(id != 0 && code == null) {
			Convenor convenor = crepo.findById(id).orElse(null);
			if(convenor == null) {
				return new ResponseEntity("A convenor with id " + id +" not found", HttpStatus.NOT_FOUND);
			}
			
			else {
				List<Module> m = convenor.getModules();
				List<Session> s = new ArrayList<Session>();
				for(Module module : convenor.getModules()) {
					s.addAll(module.getSessions());
					
				}
	
				return new ResponseEntity<List<Session>>(s,HttpStatus.OK);
					
			}

			
			
		}
		
		//Module code was given but convenor id wasn't
		else if(id == 0 && code !=  null) {
			Module module = mrepo.findById(code).orElse(null);
			if(module == null) {
				return new ResponseEntity("A convenor with id " + code +" not found", HttpStatus.NOT_FOUND);
			}
			
			return new ResponseEntity<List<Session>>(module.getSessions(),HttpStatus.OK);
			
		}
		

		
		//Both were given
		else {
			Convenor convenor = crepo.findById(id).orElse(null);
			if(convenor == null) {
				return new ResponseEntity("A convenor with id " + id +" not found", HttpStatus.NOT_FOUND);
			}
			for(Module m : convenor.getModules()) {
				String c = m.getCode();
				if(code.equals(m.getCode())) {
					return new ResponseEntity(m.getSessions(),HttpStatus.OK);
					
				}
			
			}
			
			return new ResponseEntity("A Module with code " + code +" not found", HttpStatus.NOT_FOUND);
		}
		
	
		
		
		
		
		
		
	}

}
