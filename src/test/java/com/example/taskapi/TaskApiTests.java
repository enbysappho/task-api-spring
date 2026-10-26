package com.example.taskapi;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TaskApiTests {

    @Autowired MockMvc mvc;
    @Autowired TaskRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    private String json(String body) { return body; }

    @Test
    void createThenGet() throws Exception {
        mvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON).content(json("{\"title\":\"learn spring\"}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.title").value("learn spring"));
        mvc.perform(get("/tasks")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void blankTitleIsRejected() throws Exception {
        mvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON).content(json("{\"title\":\"  \"}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("title is required"));
    }

    @Test
    void titleOver100CharsIsRejected() throws Exception {
        String longTitle = "a".repeat(101);
        mvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON).content(json("{\"title\":\"" + longTitle + "\"}")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unknownIdReturns404() throws Exception {
        mvc.perform(get("/tasks/999")).andExpect(status().isNotFound());
        mvc.perform(delete("/tasks/999")).andExpect(status().isNotFound());
    }

    @Test
    void filterByStatusAndUpdate() throws Exception {
        Task saved = repository.save(new Task("a", TaskStatus.TODO));
        repository.save(new Task("b", TaskStatus.DONE));
        mvc.perform(get("/tasks").param("status", "DONE")).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(put("/tasks/" + saved.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(json("{\"title\":\"a2\",\"status\":\"DOING\"}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DOING"));
    }

    @Test
    void invalidStatusFilterReturns400() throws Exception {
        mvc.perform(get("/tasks").param("status", "nope")).andExpect(status().isBadRequest());
    }

    @Test
    void deleteReturns204() throws Exception {
        Task saved = repository.save(new Task("a", TaskStatus.TODO));
        mvc.perform(delete("/tasks/" + saved.getId())).andExpect(status().isNoContent());
    }
}
