package com.example.taskapi;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository repository;

    public TaskController(TaskRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Task> list(@RequestParam(required = false) TaskStatus status) {
        return status == null ? repository.findAll() : repository.findByStatusOrderByIdAsc(status);
    }

    @GetMapping("/{id}")
    public Task get(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    @PostMapping
    public ResponseEntity<Task> create(@Valid @RequestBody TaskRequest request) {
        TaskStatus status = request.status() == null ? TaskStatus.TODO : request.status();
        Task saved = repository.save(new Task(request.title().trim(), status));
        return ResponseEntity.created(URI.create("/tasks/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public Task update(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        Task task = repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        task.setTitle(request.title().trim());
        if (request.status() != null) {
            task.setStatus(request.status());
        }
        return repository.save(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
