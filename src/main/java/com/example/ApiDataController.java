package com.example;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/get_api_data")
public class ApiDataController{
	
	private Api_data apiData = new Api_data();
	
	@GetMapping
	public List<String> getApiData() {
	    return apiData.getApiData().stream()
	                  .map(JSONObject::toString)
	                  .collect(Collectors.toList());
	}
}