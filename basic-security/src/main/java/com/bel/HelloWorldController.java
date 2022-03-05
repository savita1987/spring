package com.bel;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

	//http://localhost:8080/hello?name=User
	
		@GetMapping("/hello")
		public String sayHello(@RequestParam("name") String name) {
			return "Hello "+name;
		}
}
