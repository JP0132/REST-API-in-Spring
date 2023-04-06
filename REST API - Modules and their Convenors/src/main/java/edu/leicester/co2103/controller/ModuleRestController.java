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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import edu.leicester.co2103.domain.Convenor;
import edu.leicester.co2103.domain.Module;
import edu.leicester.co2103.domain.Session;
import edu.leicester.co2103.repo.ConvenorRepository;
import edu.leicester.co2103.repo.ModuleRepository;
import edu.leicester.co2103.repo.SessionRepository;


@RestController
@RequestMapping("/modules")
public class ModuleRestController {
	public static final Logger logger = LoggerFactory.getLogger(ConvenorRestController.class);
	
	@Autowired
	ModuleRepository repo;
	
	@Autowired
	SessionRepository sRepo;
	
	@Autowired
	ConvenorRepository crepo;
	
	@GetMapping("/modules")
	public ResponseEntity<List<Module>> listAllModules(){
		List<Module> modules = repo.findAll();
		if(modules.isEmpty()) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
			
		}
		return new ResponseEntity<List<Module>>(modules, HttpStatus.OK);
	}
	
	@PostMapping("/modules")
	public ResponseEntity<?> createModule(@RequestBody Module module, UriComponentsBuilder ucBuilder){
		if(repo.existsById(module.getCode())) {
			return new ResponseEntity("A module exists with code " + module.getCode(), HttpStatus.CONFLICT);
		}
		if(module.getLevel() > 4) {
			return new ResponseEntity("Value of level, " + module.getLevel() +" is not acceptable", HttpStatus.NOT_ACCEPTABLE);
		}
		else {
			repo.save(module);
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(ucBuilder.path("/modules/modules/{code}").buildAndExpand(module.getCode()).toUri());
			
			return new ResponseEntity<String>(headers, HttpStatus.CREATED);
		}
	
		
	}
	
	@GetMapping("/modules/{code}")
	public ResponseEntity<?> getModule(@PathVariable("code") String code){
		Module module = repo.findById(code).orElse(null);
		if(module == null) {
			return new ResponseEntity("A module with code " + code +" not found", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Module>(module, HttpStatus.OK);
		
	}
	
	@PatchMapping("/modules/{code}")
	public ResponseEntity<?> updateModule(@PathVariable("code") String code, @RequestBody Module updates){
		if(repo.findById(code).orElse(null) == null) {
			return new ResponseEntity("A module with code " + code +" not found", HttpStatus.NOT_FOUND);
		}
		Module currentModule = repo.findById(code).get();
		
		
		if(updates.getLevel() != 0) {
			if(updates.getLevel() <= 4) {
				currentModule.setLevel(updates.getLevel());
			}
			
			else{
				return new ResponseEntity("Value of level, " + updates.getLevel() +" is not acceptable", HttpStatus.NOT_ACCEPTABLE);
			}
			
		}
		
		if(updates.getTitle() != null) {
			currentModule.setTitle(updates.getTitle());
		}
		
		if(!updates.getSessions().isEmpty()) {
			currentModule.getSessions().clear();
			currentModule.getSessions().addAll(updates.getSessions());
		}
		
		if(updates.isOptional()) {
			currentModule.setOptional(updates.isOptional());
			
		}
		
		repo.save(currentModule);
		
		return new ResponseEntity("Module Updated", HttpStatus.OK);
		
		
	}

	
	@DeleteMapping("/modules/{code}")
	public ResponseEntity<?> deleteModule(@PathVariable("code") String code){
		if(repo.findById(code).isPresent()) {
			List<Convenor> convenors = crepo.findAll();
			for(Convenor c : convenors) {
				int counter = 0;
				for(Module m : c.getModules()) {
					if(code.equals(m.getCode())) {
						List<Module> mo = c.getModules();
						mo.remove(counter);
						break;
					}
					else {
						counter++;
					}
				}
				
			}
			
			repo.deleteById(code);
			return ResponseEntity.ok(null);
			
		}
		
		else {
			return new ResponseEntity("Module with code "+ code + " not found.", HttpStatus.NOT_FOUND);
		}
		
	}
	
    @GetMapping("/modules/{code}/sessions")
    public ResponseEntity<?> getSessions(@PathVariable("code") String code){
    	Module module = repo.findById(code).orElse(null);
    	if(module == null) {
    		return new ResponseEntity("Module with code "+ code + " not found.", HttpStatus.NOT_FOUND);
    	}
    	else {
    		return new ResponseEntity(module.getSessions(), HttpStatus.OK);
    	}
    	
    }
    
    @PostMapping("/modules/{code}/sessions")
    public ResponseEntity<?> createSession(@PathVariable("code") String code, @RequestBody Session session, UriComponentsBuilder ucBuilder ){
    	Module module = repo.findById(code).orElse(null);
    	if(module == null) {
    		return new ResponseEntity("Module with code "+ code + " not found.", HttpStatus.NOT_FOUND);
    	}
    	else {
    		module.getSessions().add(session);
    		repo.save(module);
    		HttpHeaders headers = new HttpHeaders();
    		headers.setLocation(ucBuilder.path("modules/modules/{code}/sessions").buildAndExpand(module.getCode()).toUri());
    		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    	}
    	
    }
    
    @GetMapping("/modules/{code}/sessions/{id}")
    public ResponseEntity<?> getSession(@PathVariable("code") String code, @PathVariable("id") long id){
    	Module module = repo.findById(code).orElse(null);
    	if(module == null) {
    		return new ResponseEntity("Module with code "+ code + " not found.", HttpStatus.NOT_FOUND);
    	}
    	else {
    		for(Session s : module.getSessions()) {
    			if(s.getId() == id) {
    				return new ResponseEntity(s,HttpStatus.OK);	
    			}
    		}
    		
    		return new ResponseEntity("Session with id "+ id + " not found.", HttpStatus.NOT_FOUND);
    	}
    	
    }
    
    @DeleteMapping("/modules/{code}/sessions/{id}")
	public ResponseEntity<?> deleteSession(@PathVariable("code") String code, @PathVariable("id") long id){
    	Module module = repo.findById(code).orElse(null);
    	if(module == null) {
    		return new ResponseEntity("Module with code "+ code + " not found.", HttpStatus.NOT_FOUND);
    	}
    	
    	else {
    		int counter = 0;
    		for(Session s : module.getSessions()) {
    			long sid = s.getId();
    			if(sid == id) {
    				List<Session> sl = module.getSessions();
    				sl.remove(counter);
    				sRepo.deleteById(sid);
    				return ResponseEntity.ok(null);	
    			}
    			else {
    				counter++;
    			}
    			
    		}
    		
    		return new ResponseEntity("Session with id "+ id + " not found.", HttpStatus.NOT_FOUND);
    	}
    	
		
		
	}
    
    @PutMapping("/modules/{code}/sessions/{id}")
	public ResponseEntity<?> updateSession(@PathVariable("code") String code, @PathVariable("id") long id, @RequestBody Session newSession){
    	Module module = repo.findById(code).orElse(null);
    	
    	if(module == null) {
    		return new ResponseEntity("Module with code "+ code + " not found", HttpStatus.NOT_FOUND);
    		
    	}
    	
    	
		else {
			for(Session s : module.getSessions()) {
				
    			if(s.getId() == id) {
    				Session currentSession = sRepo.findById(id).get();
    				currentSession.setDatetime(newSession.getDatetime());
    				currentSession.setDuration(newSession.getDuration());
    				currentSession.setTopic(newSession.getTopic());
    				sRepo.save(currentSession);
    				
    				return new ResponseEntity<Session>(currentSession, HttpStatus.OK);
    				

    				
    			}
    		}
			
			return new ResponseEntity("Session with id "+ id + " not found.", HttpStatus.NOT_FOUND);
    		
			
			
		}
		
		
	}
    
    @PatchMapping("/modules/{code}/sessions/{id}")
    public ResponseEntity<?> updatePartialSession(@PathVariable("code") String code, @PathVariable("id") long id, @RequestBody Session updates){
    	Module module = repo.findById(code).orElse(null);
    	if(module == null) {
			return new ResponseEntity("A module with code" + code +" not found", HttpStatus.NOT_FOUND);
		}
    	else {
    		for(Session s : module.getSessions()) {
    			if(s.getId() == id) {
    				Session currentSession = sRepo.findById(id).get();
    				Integer duration = updates.getDuration();
    				if(updates.getDatetime() != null) {
    					currentSession.setDatetime(updates.getDatetime());
    					
    				}
    				if(updates.getDuration() != 0) {
    					currentSession.setDuration(updates.getDuration());
    					
    				}
    				
    				if(updates.getTopic() != null) {
    					currentSession.setTopic(updates.getTopic());
    				}
    				
    				sRepo.save(currentSession);
    				
    				return new ResponseEntity<Session>(currentSession, HttpStatus.OK);
    			}
    		}
    		
    		return new ResponseEntity("Session with id "+ id + " not found.", HttpStatus.NOT_FOUND);
    	
    	}
		
    }
    
    
    
    
	
	

	
	
	
	
	
	
	
	
	

}
