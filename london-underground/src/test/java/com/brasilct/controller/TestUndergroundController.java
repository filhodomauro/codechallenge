package com.brasilct.controller;

import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.brasilct.App;

/**
 * 
 * @author mauro
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
public class TestUndergroundController {
	
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
	
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

	@Test
	public void findRoute() throws Exception{
		mockMvc.perform(get("/route/1/110"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(contentType))
			.andExpect(jsonPath("$[0].name", is("Acton Town")))
			.andExpect(jsonPath("$[(@.length-1)].name", is("Hammersmith")));
	}
	
	@Test
	public void getBestRouteTime() throws Exception{
		mockMvc.perform(get("/bestRoute/1/110"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(contentType))
			.andExpect(jsonPath("$", hasSize(3)))
			.andExpect(jsonPath("$[0].name", is("Acton Town")))
			.andExpect(jsonPath("$[1].name", is("Turnham Green")))
			.andExpect(jsonPath("$[2].name", is("Hammersmith")));
	}
	
	@Test
	public void findBestRoute() throws Exception{
		mockMvc.perform(get("/bestRouteTime/1/110/3/12"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(contentType))
			.andExpect(jsonPath("$.time", is(9)));
	}

}
