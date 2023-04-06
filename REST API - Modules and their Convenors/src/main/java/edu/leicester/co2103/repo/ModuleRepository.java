package edu.leicester.co2103.repo;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import edu.leicester.co2103.domain.Module;

public interface ModuleRepository extends CrudRepository<Module, String> {
	List<Module> findAll();



}
